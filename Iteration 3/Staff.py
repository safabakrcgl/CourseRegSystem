from abc import ABC, abstractmethod

class Staff(ABC):
    def __init__(self, username, name, surname, password):
        print(f"Initializing Staff: {username}, {name} {surname}")
        self.__username = username
        self.__name = name
        self.__surname = surname
        self.__password = password

    @abstractmethod
    def display_menu(self):
        """
        Abstract method for displaying a menu specific to the role.
        """
        pass

    def get_username(self):
        return self.__username

    def get_name(self):
        return self.__name

    def get_surname(self):
        return self.__surname

    def get_password(self):
        return self.__password

    def __str__(self):
        return f"Username: {self.username}, Name: {self.name} {self.surname}"
