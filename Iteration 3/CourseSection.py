from abc import ABC, abstractmethod
from Course import Course
from User import User

class CourseSection:
    def __init__(self, day, place, hour):
        self.day = day
        self.place = place
        self.hour = hour

    def get_day(self):
        return self.day

    def get_place(self):
        return self.place

    def get_hour(self):
        return self.hour

    def __str__(self):
        return f"CourseSection{{place='{self.place}', day='{self.day}', hour='{self.hour}'}}"


