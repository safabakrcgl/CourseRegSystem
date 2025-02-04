from typing import List
from Grade import Grade

class Transcript:
    def __init__(self, grades: List[Grade]):
        """
        Initializes a Transcript object with a list of Grade objects.

        Args:
            grades (List[Grade]): A list of Grade objects.
        """
        self.__grades = grades if grades is not None else []

    def all_grades(self) -> List[Grade]:
        """
        Returns all grades in the transcript.

        Returns:
            List[Grade]: A list of Grade objects.
        """
        return self.__grades

    def add_grade(self, grade: Grade):
        """
        Adds a grade to the transcript.

        Args:
            grade (Grade): The Grade object to add.
        """
        self.__grades.append(grade)

    def __str__(self):
        """
        Returns a string representation of the transcript.

        Returns:
            str: A formatted string of all grades.
        """
        return "\n".join(str(grade) for grade in self.__grades)
