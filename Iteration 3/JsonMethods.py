import os
import json
from Course import Course
from Student import Student
from Transcript import Transcript
from Grade import Grade
import logging

# Configure logging
logging.basicConfig(
    filename="error_logs.txt",
    level=logging.ERROR,
    format="%(asctime)s - %(levelname)s - %(message)s"
)


class JsonMethods:
    def __init__(self, courses_file="./resources/course.json", students_folder="./resources/Students", advisors_folder="./resources/Advisors"):
        self.courses_file = courses_file
        self.students_folder = students_folder
        self.advisors_folder = advisors_folder
        #print(f"Advisors folder set to: {self.advisors_folder}")  # Debugging

    def load_course_json(self):
        try:
            with open(self.courses_file, "r", encoding="utf-8") as file:
                courses_data = json.load(file)

            courses = []
            for course_data in courses_data:
                wait_list = [
                    self.load_student(student["studentID"]) if isinstance(student, dict) else student
                    for student in course_data.get("waitList", [])
                ]
                course_data["waitList"] = wait_list
                courses.append(Course(**course_data))

            return courses
        except Exception as e:
            print(f"Error loading courses: {e}")
            logging.error("Error loading courses", exc_info=True)
            return []

    def update_course_json(self, courses):
        """
        Updates the courses JSON file with the provided list of Course objects.
        If a course with the same ID exists, it will be updated instead of creating a duplicate.

        Args:
            courses (list): A list of Course objects to save back to the JSON file.
        """
        try:
            # Mevcut JSON dosyasını oku
            current_data = []
            if os.path.exists(self.courses_file):
                with open(self.courses_file, "r", encoding="utf-8") as file:
                    try:
                        current_data = json.load(file)
                    except json.JSONDecodeError:
                        current_data = []

            # Yeni kurs verilerini dictionary'e dönüştür
            updated_courses = [course.to_dict() for course in courses]
            
            # Her bir güncel kurs için
            for updated_course in updated_courses:
                # Mevcut verilerde aynı ID'ye sahip kurs var mı kontrol et
                found = False
                for i, existing_course in enumerate(current_data):
                    if existing_course["courseId"] == updated_course["courseId"]:
                        if not updated_course.get("courseSection"):
                            updated_course["courseSection"] = existing_course.get("courseSection", [])
                        # Varsa, mevcut kursu güncelle
                        current_data[i] = updated_course
                        found = True
                        break
                
                # Eğer kurs mevcut değilse, yeni olarak ekle
                if not found:
                    current_data.append(updated_course)

            # Güncellenmiş veriyi dosyaya yaz
            with open(self.courses_file, "w", encoding="utf-8") as file:
                json.dump(current_data, file, indent=4, ensure_ascii=False)
            
            print("Courses updated successfully in JSON file.")
        except Exception as e:
            print(f"Error while updating course.json: {e}")
            logging.error("Error while updating course.json", exc_info=True)    


    def load_student(self, username):
        """
        Loads a student from the corresponding JSON file based on their username.
        Handles 'o' prefix in usernames.
        """
        
        # Remove 'o' prefix to get the student ID
        student_id = username[1:] if username.startswith("o") else username
        student_file = os.path.join(self.students_folder, f"{student_id}.json")

        try:
            with open(student_file, "r", encoding="utf-8") as file:
                student_data = json.load(file)

            # Parsing nested objects (e.g., Transcript and Grades)
            transcript_data = student_data.get("transcript", {})
            grades = transcript_data.get("grades", [])

            # Convert grades into Grade objects with Course data
            parsed_grades = [
                Grade(Course(**grade["course"]), grade["gradeValue"])
                for grade in grades
            ]

            # Create a Transcript object with parsed grades
            student_data["transcript"] = Transcript(parsed_grades)

            # Convert enrolledCourses and requestedCourses into Course objects
            student_data["enrolledCourses"] = [Course(**course) for course in student_data.get("enrolledCourses", [])]
            student_data["requestedCourses"] = [Course(**course) for course in student_data.get("requestedCourses", [])]
            student_data["notifications"] = student_data.get("notifications", [])
            if not isinstance(student_data["notifications"], list):
                student_data["notifications"] = []


            # Create and return the Student object
            return Student(**student_data)
        except FileNotFoundError:
            print(f"Error: Student file for ID {student_id} not found.")
            logging.error(f"Error: Student file for ID {student_id} not found.")    
            return None
        except Exception as e:
            print(f"Unexpected error while loading student ID {student_id}: {e}")
            logging.error(f"Unexpected error while loading student ID {student_id}: {e}")
            return None

    def load_all_students(self):
        """
        Loads all students from the student folder and converts them into Student objects.

        Returns:
            list: A list of Student objects.
        """
        students = []
        try:
            for student_file in os.listdir(self.students_folder):
                if student_file.endswith(".json"):
                    student_id = os.path.splitext(student_file)[0]
                    student = self.load_student(student_id)
                    if student:
                        students.append(student)
            return students
        except Exception as e:
            print(f"Error while loading all students: {e}")
            logging.error(f"Error while loading all students: {e}")
            return []

    def load_advisor(self, username):
        """
        Loads an advisor from the corresponding JSON file based on their username.
        """
        advisors_folder = self.advisors_folder
        #print(f"Looking for advisor with username: {username}")  # Debugging

        try:
            from Advisor import Advisor  # Lazy import for Advisor

            for filename in os.listdir(advisors_folder):
                if filename.endswith(".json"):
                    advisor_file = os.path.join(advisors_folder, filename)
                    with open(advisor_file, "r", encoding="utf-8") as file:
                        advisor_data = json.load(file)

                    if advisor_data.get("username") == username:
                        advised_students = [
                            self.load_student(student["username"]) for student in advisor_data.get("advisedStudents", [])
                        ]

                        return Advisor(
                            username=advisor_data["username"],
                            name=advisor_data["name"],
                            surname=advisor_data["surname"],
                            password=advisor_data["password"],
                            advisorID=advisor_data["advisorID"],
                            advisedStudents=advised_students
                        )

            print(f"Error: Advisor with username {username} not found.")
            return None
        except FileNotFoundError:
            print("Error: Advisors folder not found.")
            logging.error("Error: Advisors folder not found.")  
            return None
        except Exception as e:
            print(f"Unexpected error loading advisor with username {username}: {e}")
            logging.error(f"Unexpected error loading advisor with username {username}: {e}")    
            return None

        
    def load_all_advisors(self):
        """
        Loads all advisors from the advisors folder.

        Returns:
            list: A list of Advisor objects.
        """
        advisors = []
        try:
            # Import Advisor lazily to avoid circular imports
            from Advisor import Advisor  

            for filename in os.listdir(self.advisors_folder):
                if filename.endswith(".json"):
                    advisor_file = os.path.join(self.advisors_folder, filename)
                    with open(advisor_file, "r", encoding="utf-8") as file:
                        advisor_data = json.load(file)

                    # Parse advised students using username
                    advised_students = [
                        self.load_student(student["username"]) for student in advisor_data.get("advisedStudents", [])
                    ]

                    advisors.append(Advisor(
                        username=advisor_data["username"],
                        name=advisor_data["name"],
                        surname=advisor_data["surname"],
                        password=advisor_data["password"],
                        advisorID=advisor_data["advisorID"],
                        advisedStudents=advised_students
                    ))

            print(f"{len(advisors)} advisors loaded successfully.")
            return advisors
        except FileNotFoundError:
            logging.error("Error: Advisors folder not found.")
            print("Error: Advisors folder not found.")
            return []
        except Exception as e:
            print(f"Unexpected error loading advisors: {e}")
            logging.error(f"Unexpected error loading advisors: {e}")    
            return []

    def load_student_from_folder(self, student_id):
        """
        Loads a student from the students folder based on their student ID.

        Args:
            student_id (str): The ID of the student to load.

        Returns:
            Student: A Student object if the file exists and loads successfully, otherwise None.
        """
        student_file = os.path.join(self.students_folder, f"{student_id}.json")

        try:
            with open(student_file, "r", encoding="utf-8") as file:
                student_data = json.load(file)

            # Parse enrolled and requested courses
            enrolled_courses = [Course(**course) for course in student_data.get("enrolledCourses", [])]
            requested_courses = [Course(**course) for course in student_data.get("requestedCourses", [])]

            # Create Student object
            return Student(
                username=student_data.get("username"),
                name=student_data.get("name"),
                surname=student_data.get("surname"),
                password=student_data.get("password"),
                studentID=student_data.get("studentID"),
                enrolledCourses=enrolled_courses,
                requestedCourses=requested_courses,
                transcript=student_data.get("transcript"),
                advisor=student_data.get("advisor")
            )
        except FileNotFoundError:
            print(f"Error: Student file for ID {student_id} not found.")
            return None
        except json.JSONDecodeError:
            print(f"Error: Failed to parse the JSON file for student ID {student_id}.")
            return None
        except Exception as e:
            print(f"Unexpected error while loading student ID {student_id}: {e}")
            return None

    def save_student_to_file(self, student):
        """
        Saves a Student object to its corresponding JSON file.

        Args:
            student (Student): The Student object to save.
        """
        student_file = os.path.join(self.students_folder, f"{student.get_studentID()}.json")

        try:
            student_data = {
                "username": student.get_username(),
                "name": student.get_name(),
                "surname": student.get_surname(),
                "password": student.get_password(),
                "studentID": student.get_studentID(),
                "transcript": {
                    "grades": [
                        {"course": grade.get_course().to_dict(), "gradeValue": grade.get_grade_value()}
                        for grade in student.get_transcript().all_grades()
                    ]
                },
                "enrolledCourses": [course.to_dict() for course in student.get_enrolled_courses()],
                "requestedCourses": [course.to_dict() for course in student.get_requested_courses()],
                "advisor": student.get_advisor(),
                "notifications": student.get_notifications()  # Save notifications field
            }

            with open(student_file, "w", encoding="utf-8") as file:
                json.dump(student_data, file, indent=4, ensure_ascii=False)

            print(f"Student data for ID {student.get_studentID()} saved successfully.")
        except Exception as e:
            print(f"Error while saving student data for ID {student.get_studentID()}: {e}")
   
