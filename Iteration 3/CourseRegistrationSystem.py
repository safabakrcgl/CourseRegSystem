from typing import List
from Student import Student
from Course import Course
from JsonMethods import JsonMethods

import logging

# Configure logging
logging.basicConfig(
    filename="error_logs.txt",
    level=logging.ERROR,
    format="%(asctime)s - %(levelname)s - %(message)s"
)

class CourseRegistrationSystem:
    def __init__(self, json_methods, student=None, courses=None):
        """
        Initializes the CourseRegistrationSystem.

        Args:
            json_methods (JsonMethods): The instance of JsonMethods.
            student (Student, optional): The student using the system. Defaults to None.
            courses (list, optional): List of courses. Defaults to None.
        """
        self.json_methods = json_methods
        self._student = student  # Allow None for advisor operations
        self._courses = courses if courses is not None else []


    def add_to_enroll_list(self, course: Course, student: Student):
        """
        Enrolls the student in the course if not already enrolled.
        """
        # Reload student data
        student = self.json_methods.load_student(student.get_studentID())

        is_already_enrolled = any(enrolled_course.get_course_id() == course.get_course_id()
                                  for enrolled_course in student.get_enrolled_courses())

        if is_already_enrolled:
            print("Student is already enrolled in this course.")
            return

        # Add course to the student's enrolled courses
        student.get_enrolled_courses().append(course)

        # Save student data to JSON
        self.json_methods.save_student_to_file(student)

        print("Student enrolled in course successfully.")

    def remove_course_from_request_list(self, student: Student, course: Course) -> bool:
        """
        Removes the course from the student's requested courses.
        """
        # Reload student data
        student = self.json_methods.load_student(student.get_student_id())

        requested_courses = student.get_requested_courses()
        if course in requested_courses:
            requested_courses.remove(course)
            self.json_methods.save_student_to_file(student)  # Save updated data
            print("Course removed from requested list.")
            return True
        else:
            print("Course is not in the request list.")
            return False

    def remove_course_from_enrolled_list(self, student: Student, course: Course):
        """
        Removes the course from the student's enrolled courses.
        """
        # Reload student data
        student = self.json_methods.load_student(student.get_student_id())

        enrolled_courses = student.get_enrolled_courses()
        if course in enrolled_courses:
            enrolled_courses.remove(course)
            self.json_methods.save_student_to_file(student)  # Save updated data
            print("Course removed from enrolled list.")
        else:
            print("Course is not in the enrolled list.")

    def list_available_courses(self, logged_in_student: Student) -> List[Course]:
        """
        Returns a list of courses available for the student to enroll in.
        """
        # Reload student data
        logged_in_student = self.json_methods.load_student(logged_in_student.get_studentID())

        available_courses = []
        taken_courses = []
        failed_courses = []
        passed_courses = []
        passing_grades = {"AA", "BA", "BB", "CB", "CC"}

        # Ensure transcript is a proper object
        transcript = logged_in_student.get_transcript()
        if isinstance(transcript, dict):
            from Transcript import Transcript
            transcript = Transcript(**transcript)

        # Process grades to categorize courses
        for grade in transcript.all_grades():
            grade_value = grade.get_grade_value()
            course = grade.get_course()

            if grade_value in {"FF", "FD"}:
                failed_courses.append(course.get_course_id())
            elif grade_value in passing_grades:
                passed_courses.append(course.get_course_id())
            elif grade_value in {"DD", "DC"}:
                taken_courses.append(course)

        # List courses that are available to enroll
        for course in self._courses:
            if (course.get_course_id() in passed_courses or
                    course in taken_courses or
                    any(c.get_course_id() == course.get_course_id() for c in
                        logged_in_student.get_requested_courses()) or
                    any(c.get_course_id() == course.get_course_id() for c in logged_in_student.get_enrolled_courses())):
                continue

            # Check prerequisites
            if course.get_prerequisite() and course.get_prerequisite_lesson_id() not in passed_courses:
                continue

            available_courses.append(course)

        return available_courses

    def is_schedule_conflict(self, course1: Course, course2: Course) -> bool:
        """
        Checks if there is a schedule conflict between two courses.
        """
        for section1 in course1.get_course_section():
            for section2 in course2.get_course_section():
                if section1["day"] == section2["day"] and section1["hour"] == section2["hour"]:
                    return True
        return False


    def get_user_choice(self) -> int:
        """
        Utility method to get the user's choice via input.
        """
        while True:
            try:
                choice = int(input("Enter your choice (1 or 2): "))
                if choice in {1, 2}:
                    return choice
                else:
                    print("Invalid choice. Please enter 1 or 2.")
            except ValueError:
                print("Invalid input. Please enter a number.")
                logging.error("Invalid input. Please enter a number.", exc_info=True)
        
            
    
    
    
    def request_in_course(self, course: Course, student: Student):
        """
        Adds a course to the student's requested courses if eligible.
        """
        # Reload student data
        student = self.json_methods.load_student(student.get_studentID())
        
        # Load the most up-to-date course list
        json_methods = JsonMethods()
        courses = json_methods.load_course_json()

        course = next((c for c in courses if c.get_course_id() == course.get_course_id()), None)

        # Check if the course has already been requested
        is_already_requested = any(
            requested_course.get_course_id() == course.get_course_id()
            for requested_course in student.get_requested_courses()
        )
        
        # Check if the course is already enrolled
        is_already_enrolled = any(
            enrolled_course.get_course_id() == course.get_course_id()
            for enrolled_course in student.get_enrolled_courses()
        )

        if is_already_requested:
            print("You have already requested this course.")
            return
            
        if is_already_enrolled:
            print("You are already enrolled in this course.")
            return

        # Check for schedule conflicts with both requested and enrolled courses
        for existing_course in student.get_requested_courses() + student.get_enrolled_courses():
            if self.is_schedule_conflict(course, existing_course):
                print(f"\nSchedule conflict detected between courses!")
                print(f"1. Keep {existing_course.get_course_id()} ({existing_course.get_course_name()})")
                print(f"2. Choose {course.get_course_id()} ({course.get_course_name()})")
                
                choice = self.get_user_choice()
                
                if choice == 1:
                    print(f"\nKeeping {existing_course.get_course_id()}")
                    return
                else:
                    if existing_course in student.get_requested_courses():
                        # Remove the old course from requested courses
                        student.get_requested_courses().remove(existing_course)
                        # Increase the capacity of the removed course
                        existing_course.set_current_capacity(existing_course.get_current_capacity() + 1)
                        # Update the removed course in JSON
                        self.json_methods.update_course_json([existing_course])
                        print(f"\nRemoved {existing_course.get_course_id()} from requested courses")
                    else:
                        print("Cannot remove enrolled course. Please drop it first.")
                        return
                    break

        # Capacity check
        if course.get_current_capacity() <= 0:
            print("This course is full. Adding to the waitlist.")
            
            # Add student to the course's waitlist
            waitlist = course.get_wait_list()
            if student.get_studentID() not in waitlist:
                waitlist.append(student.get_studentID())
                course.set_wait_list(waitlist)
                self.json_methods.update_course_json([course])
                print(f"Student {student.get_name()} {student.get_surname()} added to the waitlist for course: {course.get_course_name()}.")
            else:
                print("Student is already in the waitlist for this course.")
            return

        # Add the course to the student's requested courses list
        student.get_requested_courses().append(course)

        # Reduce the course's current capacity by 1
        course.set_current_capacity(course.get_current_capacity() - 1)

        # Save updated student data
        self.json_methods.save_student_to_file(student)

        # Update the course data in course.json
        self.json_methods.update_course_json([course])

        print(f"Successfully requested the course: {course.get_course_name()}")
