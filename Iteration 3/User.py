from abc import ABC, abstractmethod

class User(ABC):
    def __init__(self, username, name, surname, password):
        self.username = username
        self.name = name
        self.surname = surname
        self.password = password

    @abstractmethod
    def display_menu(self):
        """
        Abstract method to display the menu options for the user.
        Must be implemented by subclasses.
        """
        pass

    def get_username(self):
        return self.username

    def get_name(self):
        return self.name

    def get_surname(self):
        return self.surname

    def get_password(self):
        return self.password

    def __str__(self):
        return f"Username: {self.username}, Name: {self.name} {self.surname}"
