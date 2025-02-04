import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CourseRegistrationSystem {
    private Student student;
    private List<Course> courses;
    private JSONMethods jsonMethods = new JSONMethods(); // Assuming JSONMethods class handles JSON operations

    // Constructor
    public CourseRegistrationSystem(Student student, List<Course> courses) {
        this.student = student;
        this.courses = courses;
    }

    // Method to add a student to a course
    public void addToEnrollList(Course course, Student student) throws IOException {
        boolean isAlreadyEnrolled = student.getEnrolledCourses().stream()
                .anyMatch(enrolledCourse -> enrolledCourse.getCourseId().equals(course.getCourseId()));

        if (isAlreadyEnrolled) {
            System.out.println("Student is already enrolled in this course.");
            return;
        }

        student.getEnrolledCourses().add(course);
        jsonMethods.saveStudentToFile(student);
        System.out.println("Student enrolled in course successfully.");
    }

    // Remove the course from the student's request list
    public boolean removeCourseFromRequestList(Student student, Course course) throws IOException {
        if (student.getRequestedCourses().contains(course)) {
            student.getRequestedCourses().remove(course);
            jsonMethods.saveStudentToFile(student);
            return true; // Successfully removed
        } else {
            System.out.println("Course is not in the request list.");
            return false; // Removal failed
        }
    }

    // Remove the course from the student's enrolled list
    public void removeCourseFromEnrolledList(Student student, Course course) throws IOException {
        if (student.getEnrolledCourses().contains(course)) {
            student.getEnrolledCourses().remove(course);
            jsonMethods.saveStudentToFile(student);
        } else {
            System.out.println("Course is not in the enrolled list.");
        }
    }

    // List available course sections for a given student
    public List<Course> listAvailableCourses(Student loggedInStudent) {

        List<Course> availableCourses = new ArrayList<>();
        List<Course> takenCourses = new ArrayList<>();
        List<String> failedCourses = new ArrayList<>();
        List<String> passedCourses = new ArrayList<>();
        List<String> passingGrades = Arrays.asList("AA", "BA", "BB", "CB", "CC");

        for (Grade grade : loggedInStudent.getTranscript().allGrades()) {
            String gradeValue = grade.getGradeValue();
            Course course = grade.getCourse();

            if (gradeValue.equals("FF") || gradeValue.equals("FD")) {
                failedCourses.add(course.getCourseId());
            } else if (passingGrades.contains(gradeValue)) {
                passedCourses.add(course.getCourseId());
            } else if (gradeValue.equals("DD") || gradeValue.equals("DC")) {
                takenCourses.add(course);
            }
        }

        for (Course course : courses) {
            if (passedCourses.contains(course.getCourseId()) ||
                    takenCourses.contains(course) ||
                    loggedInStudent.getRequestedCourses().stream().anyMatch(c -> c.getCourseId().equals(course.getCourseId())) ||
                    loggedInStudent.getEnrolledCourses().stream().anyMatch(c -> c.getCourseId().equals(course.getCourseId()))) {
                continue;
            }

            if (course.hasPrerequisite() && !passedCourses.contains(course.getPrerequisiteLessonId())) {
                continue;
            }

            availableCourses.add(course);
        }

        return availableCourses;
    }

    public String availableCoursesToString(List<Course> availableCourses) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-10s %-40s\n", "Course ID", "Course Name"));
        sb.append("------------------------------------------------------------\n");

        for (Course course : availableCourses) {
            sb.append(String.format("%-10s %-40s\n", course.getCourseId(), course.getCourseName()));
        }

        return sb.toString();
    }

    // Check for schedule conflicts
    public boolean checkScheduleConflict(Course newCourse, Student student) throws IOException {
        List<Course> allCourses = jsonMethods.loadAllCourses();
        Course fullCourse = allCourses.stream()
                .filter(c -> c.getCourseId().equals(newCourse.getCourseId()))
                .findFirst()
                .orElse(null);

        if (fullCourse == null) {
            System.err.println("Full course data could not be found.");
            return false;
        }
        for (Course enrolledCourse : student.getEnrolledCourses()) {
            if (isScheduleConflict(enrolledCourse, fullCourse)) {
                System.out.println("Schedule conflict with enrolled course: " + enrolledCourse.getCourseName());
                return true;
            }
        }

        for (Course requestedCourse : student.getRequestedCourses()) {
            if (isScheduleConflict(requestedCourse, fullCourse)) {
                System.out.println("Schedule conflict with requested course: " + requestedCourse.getCourseName());
                System.out.println("Please choose one course to keep:");
                System.out.println("1. " + requestedCourse.getCourseName());
                System.out.println("2. " + newCourse.getCourseName());

                int choice = getUserChoice();
                if (choice == 1) {
                    System.out.println("Keeping " + requestedCourse.getCourseName() + " and rejecting " + fullCourse.getCourseName());
                    return true;
                } else if (choice == 2) {
                    student.getRequestedCourses().remove(requestedCourse);
                    jsonMethods.saveStudentToFile(student);
                    System.out.println("Removed " + requestedCourse.getCourseName() + " and added " + fullCourse.getCourseName());
                    return false;
                } else {
                    System.out.println("Invalid choice. No action taken.");
                    return true;
                }
            }
        }

        return false;
    }

    // Handle the logic of requesting a course
    public void requestInCourse(Course course, Student student) throws IOException {
        // Çakışma kontrolü
        if (checkScheduleConflict(course, student)) {
            return; // Çakışma varsa işlem sonlanır
        }

        // Kapasite kontrolü
        if (course.getCurrentCapacity() >= course.getEnrollmentCapacity()) {
            System.out.println("This course is full and cannot accept more students.");
            return;
        }

        // Talep edilmiş mi kontrolü
        if (student.getRequestedCourses().contains(course)) {
            System.out.println("You have already requested this course.");
            return;
        }
        List<Course> allCourses = jsonMethods.loadAllCourses();
        Course fullCourse = allCourses.stream()
                .filter(c -> c.getCourseId().equals(course.getCourseId()))
                .findFirst()
                .orElse(null);

        if (fullCourse == null) {
            System.err.println("Full course data could not be found.");
            return ;
        }

        // Öğrencinin requestedCourses listesine ekle
        student.getRequestedCourses().add(fullCourse);

        // JSON'da güncelleme
        jsonMethods.updateStudentInJson(student);

        System.out.println("Successfully requested the course: " + course.getCourseName());
    }

    private void addToWaitList(Student student, Course course) throws IOException {
        if (!course.getWaitList().contains(student.getStudentID())) {
            course.getWaitList().add(student.getStudentID());
            jsonMethods.updateCourseInJson(course); // Update course in JSON
            System.out.println("Student added to waitlist for course: " + course.getCourseId());
        }
    }




    private int countRequestedStudents(List<Student> allStudents, Course course) {
        int count = 0;
        for (Student student : allStudents) {
            for (Course requestedCourse : student.getRequestedCourses()) {
                if (requestedCourse.getCourseId().equals(course.getCourseId())) {
                    count++;
                    break;
                }
            }
        }
        return count;
    }

    private int countEnrolledStudents(List<Student> allStudents, Course course) {
        int count = 0;
        for (Student student : allStudents) {
            for (Course enrolledCourse : student.getEnrolledCourses()) {
                if (enrolledCourse.getCourseId().equals(course.getCourseId())) {
                    count++;
                    break;
                }
            }
        }
        return count;
    }

    // Utility method to check schedule conflict between two courses
    private boolean isScheduleConflict(Course course1, Course course2) {
       for (CourseSection section1 : course1.getCourseSection()) {
            for (CourseSection section2 : course2.getCourseSection()) {
                if (section1.getDay().equals(section2.getDay()) && section1.getHour().equals(section2.getHour())) {
                    return true;
                }
            }
        }
        return false;
    }

    // Utility method to get user's choice
    private int getUserChoice() {
        java.util.Scanner scanner = new java.util.Scanner(System.in);
        System.out.print("Enter your choice (1 or 2): ");
        return scanner.nextInt();
    }
}
