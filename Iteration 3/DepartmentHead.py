import logging
from Staff import Staff
from JsonMethods import JsonMethods
from Course import Course
import json
import os

# Configure logging
logging.basicConfig(
    filename="error_logs.txt",
    level=logging.ERROR,
    format="%(asctime)s - %(levelname)s - %(message)s"
)

class DepartmentHead(Staff):
    def __init__(self, username, name, surname, password):
        super().__init__(username, name, surname, password)
        self.json_methods = JsonMethods()

    def display_menu(self):
        """
        Displays the menu options for the DepartmentHead.
        """
        print("1. Add a Course")
        print("2. Update Course Information")
        print("3. Display All Courses")
        print("4. Logout")

    def get_int_input(self, prompt, error_message="Invalid input. Please enter a number."):
        while True:
            try:
                return int(input(prompt).strip())
            except ValueError:
                print(error_message)
                logging.error("Invalid input. Please enter a number.", exc_info=True)   

    def get_choice_input(self, prompt, valid_choices, error_message="Invalid input. Please enter a valid choice."):
        while True:
            choice = input(prompt).strip().lower()
            if choice in valid_choices:
                return choice
            print(error_message)

    def display_all_courses(self):
        """
        Displays all courses in a formatted table.
        """
        self.courses = self.json_methods.load_course_json()
        if not self.courses:
            print("No courses available.")
            return

        print(f"{'Course ID':<10} {'Course Name':<45} {'Credits':<10} {'Year':<10} {'Instructor':<25} {'Type':<10}")
        print("-" * 110)
        for course in self.courses:
            print(f"{course.get_course_id():<10} {course.get_course_name():<45} {course.get_credit():<10} {course.get_year():<10} {course.get_instructor():<25} {course.get_type():<10}")

    def add_course(self):
        """
        Adds a new course to the JSON file.
        """
        self.courses = self.json_methods.load_course_json()

        prerequisite = self.get_choice_input("Does the course have prerequisites? (yes/no): ", ["yes", "no"]) == 'yes'

        new_course = Course(
            courseId=input("Enter the Course ID: ").strip(),
            courseName=input("Enter the Course Name: ").strip(),
            credit=self.get_int_input("Enter the Credit Hours: "),
            prerequisite=prerequisite,
            prerequisiteLessonId=input("Enter Prerequisite Lesson ID (or None): ").strip() if prerequisite else "None",
            courseSection=[],
            weeklyCourseCount=self.get_int_input("Enter the Weekly Course Count: "),
            year=self.get_choice_input("Enter the Year of the Course (1, 2, 3, or 4): ", ["1", "2", "3", "4"]),
            instructor=input("Enter the Instructor Name: ").strip(),
            enrollmentCapacity=self.get_int_input("Enter the Enrollment Capacity: "),
            currentCapacity=0,
            type=self.get_choice_input("Enter the Course Type (Mandatory/Elective): ", ["mandatory", "elective"]),
            waitList=[]
        )

        self.courses.append(new_course)
        self.json_methods.update_course_json(self.courses)
        print(f"Course {new_course.get_course_name} added successfully.")



    def update_course(self):
        """
        Updates all information of a specific course.
        """
        self.courses = self.json_methods.load_course_json()
        course_id = input("Enter the Course ID to update: ").strip()
        course = next((c for c in self.courses if c.get_course_id() == course_id), None)
        if not course:
            print(f"Course with ID {course_id} not found.")
            return

        print(f"Updating course: {course.get_course_name()}")

        while True:
            print("1. Update Course Name")
            print("2. Update Credit Hours")
            print("3. Update Prerequisites")
            print("4. Update Weekly Course Count")
            print("5. Update Year")
            print("6. Update Instructor Name")
            print("7. Update Enrollment Capacity")
            print("8. Update Type")
            print("9. Exit Update Menu")

            choice = input("Enter your choice: ").strip()

            if choice == "1":
                course.set_course_name(input("Enter the new Course Name: ").strip())
            elif choice == "2":
                course.set_credit(self.get_int_input("Enter the new Credit Hours: "))
            elif choice == "3":
                prerequisite = self.get_choice_input("Does the course have prerequisites? (yes/no): ", ["yes", "no"]) == 'yes'
                course.set_prerequisite(prerequisite)
                course.set_prerequisite_lesson_id(input("Enter Prerequisite Lesson ID (or None): ").strip() if prerequisite else "None")
            elif choice == "4":
                course.set_weekly_course_count(self.get_int_input("Enter the Weekly Course Count: "))
            elif choice == "5":
                course.set_year(self.get_choice_input("Enter the Year of the Course (1, 2, 3, or 4): ", ["1", "2", "3", "4"]))
            elif choice == "6":
                course.set_instructor(input("Enter the new Instructor Name: ").strip())
            elif choice == "7":
                course.set_enrollment_capacity(self.get_int_input("Enter the new Enrollment Capacity: "))
            elif choice == "8":
                course.set_type(self.get_choice_input("Enter the new Course Type (Mandatory/Elective): ", ["mandatory", "elective"]))
            elif choice == "9":
                print("Exiting update menu.")
                break
            else:
                print("Invalid choice. Please try again.")

        self.json_methods.update_course_json(self.courses)
        print(f"Course with ID {course.get_course_id()} updated successfully.")

    @classmethod
    def load_depthead(cls, file_path="./resources/department_head.json"):
        """
        Loads all department heads from the given JSON file.

        Args:
            file_path (str): Path to the JSON file containing department head data.

        Returns:
            list: A list of DepartmentHead objects.
        """
        dept_heads = []

        try:
            if not os.path.exists(file_path):
                print(f"Error: File {file_path} not found.")
                return []

            with open(file_path, "r", encoding="utf-8") as file:
                raw_data = json.load(file)

            for head_data in raw_data:
                try:
                    dept_heads.append(cls(**head_data))
                except TypeError as e:
                    print(f"Error initializing DepartmentHead with data {head_data}: {e}")
                    logging.error(f"Error initializing DepartmentHead with data {head_data}", exc_info=True)
        except Exception as e:
            print(f"Unexpected error while loading department heads: {e}")
            logging.error("Unexpected error while loading department heads", exc_info=True)

        return dept_heads