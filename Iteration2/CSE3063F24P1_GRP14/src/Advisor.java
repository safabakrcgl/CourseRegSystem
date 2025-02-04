import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class Advisor extends User {

    JSONMethods jsonMethods = new JSONMethods();
    @JsonProperty("advisorID")
    private String advisorID;

    @JsonProperty("advisedStudents")
    private List<Student> advisedStudents;

    @JsonProperty("requestedStudents")
    private List<Student> requestedStudents;

    public Advisor(@JsonProperty("username") String username, @JsonProperty("name") String name, @JsonProperty("surname") String surname, @JsonProperty("password") String password, @JsonProperty("advisorID") String advisorID) {
        super(username, name, surname, password);
        this.advisedStudents = new ArrayList<>();
        this.requestedStudents = new ArrayList<>();
        this.advisorID = advisorID;
    }

    // Getters
    @Override
    protected void getMenu() {
        System.out.println("1. See requests");
        System.out.println("2. Approve request");
        System.out.println("3. Reject request");
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


    public String getAdvisorID() {
        return advisorID;
    }

    public List<Student> getAdvisedStudents() {
        return advisedStudents;
    }


    public void approveRequestedCourse(Student student, Course course) {
        try {
            // Requested Courses listesinden kursu kaldır
            boolean removed = student.getRequestedCourses().removeIf(c -> c.getCourseId().equals(course.getCourseId()));

            if (!removed) {
                System.out.println("The course was not found in the requested list.");
                return;
            }

            // Enrolled Courses listesinde kursun zaten mevcut olup olmadığını kontrol et

            // Tüm kursları JSON'dan yükle ve tam kurs bilgilerini bul
            List<Course> allCourses = jsonMethods.loadAllCourses();
            Course fullCourse = allCourses.stream()
                    .filter(c -> c.getCourseId().equals(course.getCourseId()))
                    .findFirst()
                    .orElse(null);

            if (fullCourse == null) {
                System.err.println("Full course data could not be found.");
                return;
            }

            // Enrolled Courses listesine tam kurs bilgilerini ekle
            student.getEnrolledCourses().add(fullCourse);

            // Öğrenci bilgilerini JSON dosyasına güncelle
            jsonMethods.updateStudentInJson(student);

            System.out.println("Course approved successfully for " + student.getName());
        } catch (Exception e) {
            System.err.println("Error approving the course: " + e.getMessage());
        }
    }

    public void rejectRequestedCourse(List<Student> allStudents,CourseRegistrationSystem crs, Student student, Course course) throws IOException {
        // Remove the course from the student's requested course list
        boolean removedSuccessfully = student.getRequestedCourses().removeIf(c -> c.getCourseId().equals(course.getCourseId()));

        if (removedSuccessfully) {
            // Update the student in the JSON file
            jsonMethods.updateStudentInJson(student);

            // Check if the waitlist is not empty
            if (!course.getWaitList().isEmpty()) {
                // Get the first student ID from the waitlist
                String firstStudentId = course.getWaitList().get(0);

                // Find the Student object by ID from the CRS student list
                Student nextStudent = allStudents.stream()
                        .filter(s -> s.getStudentID().equals(firstStudentId))
                        .findFirst()
                        .orElse(null);

                if (nextStudent != null) {
                    // Add the course to the student's requested course list
                    nextStudent.getRequestedCourses().add(course);

                    // Remove the student ID from the course's waitlist
                    course.getWaitList().remove(0);

                    // Update the Student and Course in the JSON files
                    jsonMethods.updateStudentInJson(nextStudent);
                    jsonMethods.updateCourseInJson(course);

                    System.out.println("The course " + course.getCourseName() +
                            " has been assigned to the next student in the waitlist: " + nextStudent.getName());
                } else {
                    System.out.println("Failed to find the student with ID: " + firstStudentId);
                }
            } else {
                System.out.println("The waitlist for the course " + course.getCourseName() + " is empty.");
            }
        } else {
            System.out.println("Failed to reject the course. Course might not exist in the requested list.");
        }
    }

    private void removeFromWaitList(Student student, Course course) throws IOException {
        if (course.getWaitList().remove(student.getStudentID())) {
            jsonMethods.updateCourseInJson(course); // Update course in JSON
            System.out.println("Student removed from waitlist for course: " + course.getCourseId());
        }
    }


    // toString() method
    @Override
    public String toString() {
        return "Advisor{" +
                "username='" + getUsername() + '\'' +
                ", name='" + getName() + '\'' +
                ", surname='" + getSurname() + '\'' +
                ", advisorID='" + advisorID + '\'' +
                ", advisedStudents=" + advisedStudents +
                '}';
    }
}
