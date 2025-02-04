from User import User  # Import User
from typing import List
from Student import Student  # Import Student class
from Course import Course  # Import Course class
from JsonMethods import JsonMethods  # Import JsonMethods for JSON handling
import logging

# Configure logging
logging.basicConfig(
    filename="error_logs.txt",
    level=logging.ERROR,
    format="%(asctime)s - %(levelname)s - %(message)s"
)

class Advisor(User):
    def __init__(self, username, name, surname, password, advisorID, advisedStudents=None):
        super().__init__(username, name, surname, password)
        self.__advisorID = advisorID
        self.__advisedStudents = advisedStudents if advisedStudents else []
        self.json_methods = JsonMethods()  # JsonMethods nesnesi eklendi


    def get_advisor_id(self):
        """
        Returns the advisor ID.
        """
        return self.__advisorID

    def get_advised_students(self):
        """
        Returns the list of advised students.
        """
        return self.__advisedStudents

    def refresh_advised_students(self):
        """
        Reloads the advised students for this advisor from the JSON file.
        """
        try:
            json_methods = JsonMethods()
            advisor_data = json_methods.load_advisor(self.get_username())
            if advisor_data:
                self.__advisedStudents = advisor_data.get_advised_students()
        except Exception as e:
            print(f"Error refreshing advised students: {e}")
            logging.error("Error refreshing advised students", exc_info=True)


    def view_requests(self):
        self.json_methods.load_advisor(self.get_username())
        print("\nYour advised student list:")
        has_requests = False
        table = []
        table.append(f"{'No':<5} {'Student Name':<20} {'Surname':<20} {'Course ID':<10} {'Course Name':<40}")
        table.append("-" * 95)

        request_no = 1
        requests_map = []

        for student in self.__advisedStudents:
            for course in student.get_requested_courses():
                has_requests = True
                table.append(
                    f"{request_no:<5} {student.get_name():<20} {student.get_surname():<20} {course.get_course_id():<10} {course.get_course_name():<40}"
                )
                requests_map.append((student, course))
                request_no += 1

        if has_requests:
            print("\n".join(table))
        else:
            print("There are no course requests at the moment.")

        return requests_map

    def approve_requested_course(self, student: Student, course: Course):
        """
        Approves the requested course for a student.
        """
        # Load all courses to ensure consistency
        json_methods = JsonMethods()
        courses = json_methods.load_course_json()
        
        # Find the corresponding course in the JSON file
        master_course = next((c for c in courses if c.get_course_id() == course.get_course_id()), None)
        if not master_course:
            print(f"Course {course.get_course_name()} not found in course.json.")
            return



        # Save updated course data
        json_methods.update_course_json(courses)

        # Add the course to the student's enrolled courses
        if course in student.get_requested_courses():
            student.get_requested_courses().remove(course)
            student.get_enrolled_courses().append(course)
            
            # Add notification for the student
            notification_message = f"Your requested {course.get_course_name()} course has been approved."
            student.get_notifications().append(notification_message)
            
            # Save updated student data
            json_methods.save_student_to_file(student)
            
            print(f"Course {course.get_course_name()} approved for {student.get_name()} {student.get_surname()}.")
        else:
            print(f"Course {course.get_course_name()} is not in the requested courses of {student.get_name()} {student.get_surname()}.")


            
    def approve_request_by_index(self, requests_map, index):
        """
        Approves a course request based on the table index.
        """
        if 0 <= index < len(requests_map):
            student, course = requests_map[index]
            self.approve_requested_course(student, course)
        else:
            print("Invalid request number.")

    def reject_request_by_index(self, requests_map, index):
        """
        Rejects a course request based on the table index.
        Uses courses.json for waitlist data while maintaining the student's course reference.
        """
        if 0 <= index < len(requests_map):
            student, student_course = requests_map[index]
            
            # Load all courses to find the matching one with correct waitlist
            courses = self.json_methods.load_course_json()
            master_course = next((c for c in courses if c.get_course_id() == student_course.get_course_id()), None)
            
            if master_course:
                # Copy waitlist from master course to student's course instance
                student_course.set_wait_list(master_course.get_wait_list())
                
                # Now reject using student's course instance (which now has the correct waitlist)
                self.reject_requested_course(student, student_course)
                
                # After rejection, update the master course in courses.json with the new waitlist
                master_course.set_wait_list(student_course.get_wait_list())
                self.json_methods.update_course_json([master_course])
            else:
                print(f"Error: Could not find course {student_course.get_course_id()} in courses.json")
        else:
            print("Invalid request number.")




    def reject_requested_course(self, student: Student, course: Course):
        if course in student.get_requested_courses():
            # Requested courses'tan kursu kaldır
            student.get_requested_courses().remove(course)
            self.json_methods.save_student_to_file(student)
            print(f"Course {course.get_course_name()} removed from {student.get_name()} {student.get_surname()}'s requested courses.")

            # Kurs kapasitesini artır
            course.set_current_capacity(course.get_current_capacity() + 1)

            # Kurs bilgisini güncelle (Reject'ten önce kaydet)
            self.json_methods.update_course_json([course])
            print(f"Course {course.get_course_name()} JSON data updated before processing waitlist.")

            # WaitList'i kontrol et
            wait_list = course.get_wait_list()
            if wait_list:
                next_student_id = wait_list.pop(0)  # İlk öğrenciyi al
                next_student_username = f"o{next_student_id}"  # 'o' ekle

                try:
                    next_student = self.json_methods.load_student(next_student_username)
                    if not next_student:
                        raise ValueError(f"Student with ID {next_student_username} could not be loaded.")

                    # Bekleme listesindeki öğrencinin requestedCourses kısmına kursu ekle
                    next_student.get_requested_courses().append(course)
                     # Öğrenciye bildirim ekle
                    notification_message = f"You have been added to the course {course.get_course_name()} from the waitlist."
                    next_student.get_notifications().append(notification_message)

                    # Waitlist'ten öğrenci çıkar
                    course.set_wait_list(wait_list)

                    self.json_methods.save_student_to_file(next_student)  # Güncellenmiş öğrenciyi kaydet

                    # Kurs kapasitesini düşür
                    course.set_current_capacity(course.get_current_capacity() - 1)
                    print(f"Course {course.get_course_name()} added to {next_student.get_name()} {next_student.get_surname()}'s requested courses.")

                except Exception as e:
                    print(f"Error processing waitlist student: {e}")
                    logging.error("Error processing waitlist student", exc_info=True)
            else:
                print(f"No students in waitlist for course {course.get_course_name()}.")

            # Güncellenmiş kurs bilgilerini kaydet
            self.json_methods.update_course_json([course])
            print(f"Course {course.get_course_name()} JSON data updated successfully.")
        else:
            print("The course is not in the student's requested courses.")



    def display_menu(self):
        print("\nAdvisor Menu:")
        print("1. View Requests")
        print("2. Approve a Request")
        print("3. Reject a Request")
        print("b. Back")
