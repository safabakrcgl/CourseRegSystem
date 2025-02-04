from Course import Course


class Grade:
    def __init__(self, course : Course, grade_value):
        self.__course = course 
        self.__grade_value = grade_value 

    # Getter methods
    def get_course(self):
        return self.__course

    def get_grade_value(self):
        return self.__grade_value

    # toString method (or __str__ in Python) to provide a string representation of the grade
    def __str__(self):
        # Check if the course is a valid Course object
        course_info : Course = self.__course
        grade_info = self.__grade_value if self.__grade_value else "No grade assigned"

        if course_info:
            # Ensure that the course_info is a Course object, then get course_name
            return f"Grade - Course: {course_info.get_course_name()}, Grade: {grade_info}"
        else:
            return f"Grade - Course: No course assigned, Grade: {grade_info}"



