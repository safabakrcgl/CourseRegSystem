import os
from JsonMethods import JsonMethods
from Student import Student
from Advisor import Advisor
from DepartmentScheduler import DepartmentScheduler
from DepartmentHead import DepartmentHead
from CourseRegistrationSystem import CourseRegistrationSystem
import logging

# Configure logging
logging.basicConfig(
    filename="error_logs.txt",
    level=logging.ERROR,
    format="%(asctime)s - %(levelname)s - %(message)s"
)

class LoginSystem:
    def __init__(self):
        self.json_methods = JsonMethods()
        self.students = []
        self.advisors = []
        self.staff_members = []
        self.courses = []
        self.load_data()

    def load_data(self):
        try:
            self.students = self.json_methods.load_all_students()
            self.advisors = self.json_methods.load_all_advisors()

            self.staff_members = []
            print("Loading Department Heads...")
            self.staff_members.extend(DepartmentHead.load_depthead())
            print("Loading Department Schedulers...")
            self.staff_members.extend(DepartmentScheduler.load_deptscheduler())

            self.courses = self.json_methods.load_course_json()

            print(f"{len(self.staff_members)} staff members loaded successfully.")
            print("All data loaded successfully.")
        except Exception as e:
            logging.error("Error loading data", exc_info=True)  # Log the error
            print(f"Error loading data: {e}")

    def start_login(self):
        """
        Starts the login process and directs to the appropriate menu based on the user role.
        """
        while True:
            username = input("Enter your username: ").strip()
            password = input("Enter your password: ").strip()

            # Student Login
            if username.startswith("o"):
                for student in self.students:
                    if student.get_username() == username and student.get_password() == password:
                        print(f"Welcome, {student.get_name()} {student.get_surname()}! (Student)")
                        self.student_menu(student)
                        return
                print("Invalid student username or password. Please try again.")

            # Advisor Login
            elif username.startswith("advisor"):
                for advisor in self.advisors:
                    if advisor.get_username() == username and advisor.get_password() == password:
                        print(f"Welcome, {advisor.get_name()} {advisor.get_surname()}! (Advisor)")
                        self.advisor_menu(advisor)
                        return
                print("Invalid advisor username or password. Please try again.")

            # Department Head Login
            elif username == "deptHead":
                for staff in self.staff_members:
                    if isinstance(staff, DepartmentHead) and staff.get_username() == username and staff.get_password() == password:
                        print(f"Welcome, {staff.get_name()} {staff.get_surname()}! (Department Head)")
                        self.department_head_menu(staff)
                        return
                print("Invalid department head username or password. Please try again.")

            # Department Scheduler Login
            elif username == "deptScheduler":
                for staff in self.staff_members:
                    if isinstance(staff, DepartmentScheduler) and staff.get_username() == username and staff.get_password() == password:
                        print(f"Welcome, {staff.get_name()} {staff.get_surname()}! (Department Scheduler)")
                        self.department_scheduler_menu(staff)
                        return
                print("Invalid department scheduler username or password. Please try again.")

            else:
                print("Invalid username. Please try again.")

    def student_menu(self, student):
        crs = CourseRegistrationSystem(self.json_methods, student, self.courses)

        while True:
            student.display_menu()
            student_choice = input("Enter your choice (or 'q' to quit): ").strip()

            if student_choice.lower() == 'b':
                break
            if student_choice.lower() == 'q':
                print("Exiting the program...")
                exit()

            if student_choice == "1":
                available_courses = crs.list_available_courses(student)
                for course in available_courses:
                    print(f"{course.get_course_id()} - {course.get_course_name()}")
            elif student_choice == "2":
                course_id = input("Enter the Course ID to request: ").strip()
                course = next((c for c in self.courses if c.get_course_id() == course_id), None)

                if not course:
                    print("Invalid Course ID. Please try again.")
                    continue

                crs.request_in_course(course, student)
            elif student_choice == "3":
                student.view_notifications(self.json_methods)
            elif student_choice == "4":
                student.display_schedule(self.json_methods)
            else:
                print("Invalid choice. Please try again.")

    def advisor_menu(self, advisor):
        advisor.refresh_advised_students()
        requests_map = {}

        while True:
            advisor.refresh_advised_students()
            requests_map = {}  # Reset requests_map each time
            advisor.display_menu()
            advisor_choice = input("Enter your choice (or 'q' to quit): ").strip()

            if advisor_choice.lower() == 'b':
                break
            if advisor_choice.lower() == 'q':
                print("Exiting the program...")
                exit()

            if advisor_choice == "1":
                requests_map = advisor.view_requests()
            elif advisor_choice == "2":
                requests_map = advisor.view_requests()
                if not requests_map:
                    print("No requests available to approve. Please try again.")
                    continue
                try:
                    request_index = int(input("Enter the request number to approve: ").strip()) - 1
                    advisor.approve_request_by_index(requests_map, request_index)
                except (ValueError, IndexError):
                    print("Invalid request number. Please try again.")
                    logging.error("Invalid request number. Please try again.", exc_info=True)   
            elif advisor_choice == "3":
                requests_map = advisor.view_requests()
                if not requests_map:
                    print("No requests available to reject. Please try again.")
                    continue
                try:
                    request_index = int(input("Enter the request number to reject: ").strip()) - 1
                    advisor.reject_request_by_index(requests_map, request_index)
                except (ValueError, IndexError):
                    print("Invalid request number. Please try again.")
                    logging.error("Invalid request number. Please try again.", exc_info=True)   
            else:
                print("Invalid choice. Please try again.")

    def department_head_menu(self, department_head):
        while True:
            department_head.display_menu()
            head_choice = input("Enter your choice (or 'q' to quit): ").strip()

            if head_choice.lower() == 'b':
                break
            if head_choice.lower() == 'q':
                print("Exiting the program...")
                exit()

            if head_choice == "1":
                department_head.add_course()
            elif head_choice == "2":
                department_head.update_course()
            elif head_choice == "3":
                department_head.display_all_courses()
            elif head_choice == "4":
                print("Logging out...")
                break
            else:
                print("Invalid choice. Please try again.")

    def department_scheduler_menu(self, scheduler):
        while True:
            scheduler.display_menu()
            scheduler_choice = input("Enter your choice (or 'q' to quit): ").strip()

            if scheduler_choice.lower() == 'b':
                break
            if scheduler_choice.lower() == 'q':
                print("Exiting the program...")
                exit()

            if scheduler_choice == "1":
                scheduler.print_all_courses()
            elif scheduler_choice == "2":
                scheduler.update_course_sections()
            elif scheduler_choice == "3":
                scheduler.reset_course_section()
            elif scheduler_choice == "4":
                scheduler.reset_all_course_sections()
            elif scheduler_choice == "5":
                print("Logging out...")
                break
            else:
                print("Invalid choice. Please try again.")

if __name__ == "__main__":
    login_system = LoginSystem()
    while True:
        login_system.start_login()
