class Course:
    def __init__(self, courseId, courseName, credit, prerequisite, prerequisiteLessonId, courseSection,
                 weeklyCourseCount, year, instructor, enrollmentCapacity, currentCapacity, type, waitList=None):
        self.__courseId = courseId
        self.__courseName = courseName
        self.__credit = credit
        self.__prerequisite = prerequisite
        self.__prerequisiteLessonId = prerequisiteLessonId
        self.__courseSection = courseSection
        self.__weeklyCourseCount = weeklyCourseCount
        self.__year = year
        self.__instructor = instructor
        self.__enrollmentCapacity = enrollmentCapacity
        self.__currentCapacity = currentCapacity
        self.__type = type
        self.__waitList = waitList if waitList else []

    # Getter methods
    def get_course_id(self):
        return self.__courseId

    def get_course_name(self):
        return self.__courseName

    def get_credit(self):
        return self.__credit

    def get_prerequisite(self):
        return self.__prerequisite

    def get_prerequisite_lesson_id(self):
        return self.__prerequisiteLessonId

    def get_course_section(self):
        return self.__courseSection

    def get_weekly_course_count(self):
        return self.__weeklyCourseCount

    def get_year(self):
        return self.__year

    def get_instructor(self):
        return self.__instructor

    def get_enrollment_capacity(self):
        return self.__enrollmentCapacity

    def get_current_capacity(self):
        return self.__currentCapacity

    def get_type(self):
        return self.__type

    def get_wait_list(self):
        return self.__waitList

    # Setter methods
    def set_course_name(self, courseName):
        self.__courseName = courseName

    def set_credit(self, credit):
        self.__credit = credit

    def set_prerequisite(self, prerequisite):
        self.__prerequisite = prerequisite

    def set_prerequisite_lesson_id(self, prerequisiteLessonId):
        self.__prerequisiteLessonId = prerequisiteLessonId

    def set_course_section(self, courseSection):
        self.__courseSection = courseSection

    def set_weekly_course_count(self, weeklyCourseCount):
        self.__weeklyCourseCount = weeklyCourseCount

    def set_year(self, year):
        self.__year = year

    def set_instructor(self, instructor):
        self.__instructor = instructor

    def set_enrollment_capacity(self, enrollmentCapacity):
        self.__enrollmentCapacity = enrollmentCapacity

    def set_current_capacity(self, currentCapacity):
        self.__currentCapacity = currentCapacity

    def set_type(self, type):
        self.__type = type

    def set_wait_list(self, waitList):
        self.__waitList = waitList

    # Convert object to dictionary
    def to_dict(self):
        return {
            "courseId": self.__courseId,
            "courseName": self.__courseName,
            "credit": self.__credit,
            "prerequisite": self.__prerequisite,
            "prerequisiteLessonId": self.__prerequisiteLessonId,
            "courseSection": self.__courseSection,
            "weeklyCourseCount": self.__weeklyCourseCount,
            "year": self.__year,
            "instructor": self.__instructor,
            "enrollmentCapacity": self.__enrollmentCapacity,
            "currentCapacity": self.__currentCapacity,
            "type": self.__type,
            "waitList": self.__waitList
        }
