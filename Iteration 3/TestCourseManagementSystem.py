import unittest
from Course import Course
from Student import Student
from JsonMethods import JsonMethods
from CourseRegistrationSystem import CourseRegistrationSystem
from Advisor import Advisor
from DepartmentHead import DepartmentHead
from DepartmentScheduler import DepartmentScheduler

class TestCourseManagementSystem(unittest.TestCase):
    def setUp(self):
        self.json_methods = JsonMethods(courses_file="./resources/course.json")
        self.student = Student("o150121003", "Umut", "Çelik", "mar21003", "150121003", [], [], None, None, [])
        self.advisor = Advisor("advisoro2", "Borahan", "Tümer", "maro2", "120121047")
        self.department_head = DepartmentHead("deptHead", "Mehmet", "Kaya", "pass321")
        self.department_scheduler = DepartmentScheduler("deptScheduler", "Serap", "Korkmaz", "pass123")
        self.courses = self.json_methods.load_course_json()
        self.crs = CourseRegistrationSystem(self.json_methods, self.student, self.courses)

    def test_load_courses(self):
        self.assertGreater(len(self.courses), 0)


    def test_advisor_approve_course(self):
        course = self.courses[2]
        self.student.get_requested_courses().append(course)
        self.advisor.approve_requested_course(self.student, course)
        self.assertIn(course, self.student.get_enrolled_courses())


    def test_advisor_reject_course(self):
        course = self.courses[3]
        self.student.get_requested_courses().append(course)
        requests_map = [(self.student, course)]
        self.advisor.reject_request_by_index(requests_map, 0)
        self.assertNotIn(course, self.student.get_requested_courses())

    def test_list_available_courses(self):
        available_courses = self.crs.list_available_courses(self.student)
        self.assertGreater(len(available_courses), 0)


    def test_remove_from_waitlist_on_enrollment(self):
        course = self.courses[0]
        course.set_current_capacity(1)
        course.get_wait_list().append(self.student.get_studentID())
        self.crs.request_in_course(course, self.student)
        updated_course = next(c for c in self.json_methods.load_course_json() if c.get_course_id() == course.get_course_id())
        self.assertNotIn(self.student.get_studentID(), updated_course.get_wait_list())

if __name__ == "__main__":
    unittest.main()