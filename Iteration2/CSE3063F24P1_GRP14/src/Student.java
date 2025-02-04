import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class Student extends User{
    @JsonProperty("studentID")
    private String studentID;

    @JsonProperty("enrolledCourses")
    private List<Course> enrolledCourses;

    @JsonProperty("requestedCourses")
    private List<Course> requestedCourses;

    @JsonProperty("transcript")
    private Transcript transcript;
    @JsonProperty("advisor")
    private Advisor advisor;


    public Student(@JsonProperty("username") String username,@JsonProperty("name") String name,@JsonProperty("surname") String surname,@JsonProperty("password") String password,@JsonProperty("studentID") String studentID,
                   @JsonProperty("transcript") Transcript transcript,@JsonProperty("advisor") Advisor advisor) {
        super(username, name, surname, password);
        this.requestedCourses = new ArrayList<>();
        this.enrolledCourses = new ArrayList<>();
        this.advisor = advisor;
        this.transcript = transcript;
        this.studentID = studentID;
    }
  // private Grade grade;

    // Getters
    @Override
    protected void getMenu() {
        System.out.println("1. View Transcript");
        System.out.println("2. Request Course");
        System.out.println("3. View Enrolled Courses");
        System.out.println("4. Display Schedule");
        System.out.println("5. Logout");
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

    public String getStudentID() {
        return studentID;
    }


    public Advisor getAdvisor() {
        return advisor;
    }


    public List<Course> getEnrolledCourses() {
        return enrolledCourses;
    }
    public List<Course> getRequestedCourses() {
        return requestedCourses;
    }
    public Transcript getTranscript() {
        return transcript;
    }

    
    //listAvailableCourses metodu yazılacak
    // toString() method
    @Override
    public String toString() {
        return "Student{" +
                "username='" + getUsername() + '\'' +
                ", name='" + getName() + '\'' +
                ", surname='" + getSurname() + '\'' +
                ", studentID='" + studentID + '\'' +
                ", enrolledCourses=" + enrolledCourses +
                ", requestedCourses=" + requestedCourses +
                ", transcript=" + transcript +
                '}';
    }


    public void displaySchedule(Student student) {
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
        String[] hours = {
                "8:30-9:20", "9:30-10:20", "10:30-11:20", "11:30-12:20",
                "13:00-13:50", "14:00-14:50", "15:00-15:50", "16:00-16:50",
                "17:00-17:50", "18:00-18:50"
        };

        // Tablonun başlıklarını yazdır
        System.out.println(String.format("%-15s %-25s %-25s %-25s %-25s %-25s",
                "Hour", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday"));
        System.out.println("-----------------------------------------------------------------------------------------------------------------------------");

        // Tablonun içeriği
        for (String hour : hours) {
            StringBuilder row = new StringBuilder(String.format("%-15s", hour));
            for (String day : days) {
                String courseInfo = "";
                for (Course course : student.getEnrolledCourses()) {
                    for (CourseSection section : course.getCourseSection()) {
                        if (section.getDay().equals(day) && section.getHour().equals(hour)) {
                            courseInfo = course.getCourseId() + " (" + section.getPlace() + ")";
                        }
                    }
                }
                row.append(String.format("%-25s", courseInfo));
            }
            System.out.println(row.toString());
        }
    }


    // Gün indeks dönüşümü
    private int getDayIndex(String day) {
        switch (day) {
            case "Monday": return 0;
            case "Tuesday": return 1;
            case "Wednesday": return 2;
            case "Thursday": return 3;
            case "Friday": return 4;
            default: return -1;
        }
    }

    // Saat indeks dönüşümü
    private int getHourIndex(String hour) {
        switch (hour) {
            case "8:30-9:20": return 0;
            case "9:30-10:20": return 1;
            case "10:30-11:20": return 2;
            case "11:30-12:20": return 3;
            case "13:00-13:50": return 4;
            case "14:00-14:50": return 5;
            case "15:00-15:50": return 6;
            case "16:00-16:50": return 7;
            case "17:00-17:50": return 8;
            case "18:00-18:50": return 9;
            default: return -1;
        }
    }

    // Saat etiketi dönüşümü
    private String getHourLabel(int index) {
        String[] hourLabels = {
                "8:30-9:20 ", "9:30-10:20 ", "10:30-11:20 ", "11:30-12:20 ",
                "13:00-13:50", "14:00-14:50", "15:00-15:50", "16:00-16:50",
                "17:00-17:50", "18:00-18:50"
        };
        return hourLabels[index];
    }

}
