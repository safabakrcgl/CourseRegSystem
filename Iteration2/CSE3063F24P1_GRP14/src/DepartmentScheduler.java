import java.io.File;
import java.io.IOException;
import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;

public class DepartmentScheduler extends User {
    private List<Course> courses;
    private static final String COURSE_JSON_PATH = "CSE3063F24P1_GRP14/src/resources/course.json";

    private static final String[] DAYS = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
    private static final String[] HOURS = {
            "8:30-9:20", "9:30-10:20", "10:30-11:20", "11:30-12:20",
            "13:00-13:50", "14:00-14:50", "15:00-15:50", "16:00-16:50",
            "17:00-17:50", "18:00-18:50"
    };
    private static final String[] PLACES = {"M1-Z01", "M1-Z06", "M1-110", "M2-Z11", "M2-Z06", "M2-110", "M2-103"};

    public DepartmentScheduler() {
        super(null, null, null, null);
        this.courses = loadCoursesFromJson();
    }

    public DepartmentScheduler(String username, String name, String surname, String password) {
        super(username, name, surname, password);
        this.courses = loadCoursesFromJson();
    }

    @Override
    protected void getMenu() {
        System.out.println("1. View All Courses");
        System.out.println("2. Update Course Sections");
        System.out.println("3. Reset All Course Sections");
        System.out.println("4. Logout");
        System.out.print("Please choose an operation (or 'q' to go back): ");
    }

    @Override
    protected String getUsername() {
        return getUsernameField(); // Access via the getter method in User
    }

    @Override
    protected String getName() {
        return getNameField(); // Access via the getter method in User
    }

    @Override
    protected String getSurname() {
        return getSurnameField(); // Access via the getter method in User
    }

    @Override
    protected String getPassword() {
        return getPasswordField(); // Access via the getter method in User
    }

    public void printAllCourses() {
        System.out.printf("%-10s %-30s\n", "Course Code", "Course Name");
        for (Course course : courses) {
            System.out.printf("%-10s %-30s\n", course.getCourseId(), course.getCourseName());
        }
    }

    public void resetAllCourseSections() {
        System.out.println("Resetting all course sections...");
        try {
            for (Course course : courses) {
                updateCourseSectionsInJson(course.getCourseId(), new ArrayList<>());
            }
            this.courses = loadCoursesFromJson();
            System.out.println("All course sections have been reset.");
        } catch (Exception e) {
            System.err.println("Error while resetting course sections: " + e.getMessage());
        }
    }

    public boolean updateCourseSections(String courseId) {
        Course selectedCourse = findCourseById(courseId);
        if (selectedCourse == null) {
            System.out.println("Course not found!");
            return false;
        }

        System.out.println("Updating sections for course: " + selectedCourse.getCourseName());
        List<CourseSection> sections = new ArrayList<>();
        updateCourseSectionsInJson(courseId, new ArrayList<>());

        for (int i = 0; i < selectedCourse.getWeeklyCourseCount(); i++) {
            System.out.println("Section " + (i + 1) + ":");
            String day = selectOptionWithRetry(DAYS, "Select a day:");
            String hour = selectAvailableHour(day, sections, selectedCourse);
            if (hour == null) {
                System.out.println("No available hours for this day!");
                return false;
            }

            String place = selectAvailablePlaceWithRetry(day, hour);
            if (place == null) {
                System.out.println("No available classrooms for this time slot!");
                return false;
            }
            sections.add(new CourseSection(day, place, hour));
        }

        updateCourseSectionsInJson(courseId, sections);
        this.courses = loadCoursesFromJson();
        System.out.println("Sections updated successfully for course " + selectedCourse.getCourseName());
        return true;
    }

    private String selectAvailableHour(String day, List<CourseSection> currentSections, Course currentCourse) {
        Set<String> occupiedHours = new HashSet<>();
        int currentCourseYear = currentCourse.getYear();

        for (CourseSection section : currentSections) {
            if (section.getDay().equals(day)) {
                occupiedHours.add(section.getHour());
            }
        }

        for (Course course : courses) {
            if (course.getYear() == currentCourseYear) {
                for (CourseSection section : course.getCourseSection()) {
                    if (section.getDay().equals(day)) {
                        occupiedHours.add(section.getHour());
                    }
                }
            }
        }

        List<String> availableHours = new ArrayList<>();
        for (String hour : HOURS) {
            if (!occupiedHours.contains(hour)) {
                availableHours.add(hour);
            }
        }

        if (availableHours.isEmpty()) {
            return null;
        }

        return selectOptionWithRetry(availableHours.toArray(new String[0]), "Available hours (no conflict with same year courses):");
    }

    private String selectAvailablePlaceWithRetry(String day, String hour) {
        while (true) {
            Set<String> occupiedPlaces = new HashSet<>();
            for (Course course : courses) {
                for (CourseSection section : course.getCourseSection()) {
                    if (section.getDay().equals(day) && section.getHour().equals(hour)) {
                        occupiedPlaces.add(section.getPlace());
                    }
                }
            }

            List<String> availablePlaces = new ArrayList<>();
            for (String place : PLACES) {
                if (!occupiedPlaces.contains(place)) {
                    availablePlaces.add(place);
                }
            }

            if (availablePlaces.isEmpty()) {
                return null;
            }

            String place = selectOptionWithRetry(availablePlaces.toArray(new String[0]), "Available places:");
            if (place != null) {
                return place;
            }
        }
    }

    private String selectOptionWithRetry(String[] options, String prompt) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println(prompt);
            for (int i = 0; i < options.length; i++) {
                System.out.printf("%d. %s\n", i + 1, options[i]);
            }
            System.out.print("Enter your choice: ");
            try {
                int choice = scanner.nextInt();
                if (choice >= 1 && choice <= options.length) {
                    return options[choice - 1];
                } else {
                    System.out.println("Invalid choice. Please select a valid option.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.next();
            }
        }
    }

    private Course findCourseById(String courseId) {
        for (Course course : courses) {
            if (course.getCourseId().equals(courseId)) {
                return course;
            }
        }
        return null;
    }

    private List<Course> loadCoursesFromJson() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            File file = new File(COURSE_JSON_PATH);
            Course[] coursesArray = objectMapper.readValue(file, Course[].class);
            return Arrays.asList(coursesArray);
        } catch (IOException e) {
            System.err.println("Error loading courses from JSON: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private void updateCourseSectionsInJson(String courseId, List<CourseSection> updatedSections) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            File file = new File(COURSE_JSON_PATH);
            List<Course> currentCourses = Arrays.asList(objectMapper.readValue(file, Course[].class));

            List<Course> updatedCourses = new ArrayList<>();
            for (Course course : currentCourses) {
                if (course.getCourseId().equals(courseId)) {
                    updatedCourses.add(new Course(
                            course.getCourseId(),
                            course.getCourseName(),
                            course.getCredit(),
                            course.hasPrerequisite(),
                            course.getPrerequisiteLessonId(),
                            updatedSections,
                            course.getWeeklyCourseCount(),
                            course.getYear(),
                            course.getInstructor(),
                            course.getEnrollmentCapacity(),
                            course.getCurrentCapacity(),
                            course.getStatus(),
                            course.getWaitList()
                    ));
                } else {
                    updatedCourses.add(course);
                }
            }

            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, updatedCourses);
            System.out.println("Course JSON updated successfully!");
        } catch (IOException e) {
            System.err.println("Error updating course JSON: " + e.getMessage());
        }
    }
}