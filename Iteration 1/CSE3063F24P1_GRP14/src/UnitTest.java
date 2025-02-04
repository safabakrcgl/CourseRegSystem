import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UnitTest {
    private CourseRegistrationSystem registrationSystem;
    private Student student;
    private Advisor advisor;
    private List<Course> courses;
    private Course programmingCourse;
    private Course circuitsCourse;
    private Course dataStructuresCourse;
    private Transcript transcript;
    private JSONMethods jsonMethods;

    @BeforeEach
    void setUp() {
        CourseSection programmingSection = new CourseSection("fall", "Sanem Arslan", 5, "Active");
        programmingCourse = new Course("CSE1241", "Programming 1", 6, false, "None", programmingSection);

        CourseSection circuitsSection = new CourseSection("fall", "Borahan Tümer", 5, "Active");
        circuitsCourse = new Course("EE2031", "Electric Circuits", 5, false, "None", circuitsSection);

        CourseSection dsSection = new CourseSection("spring", "Ali Veli", 5, "Active");
        dataStructuresCourse = new Course("CSE2225", "Data Structures", 6, true, "CSE1241", dsSection);

        List<Grade> grades = new ArrayList<>();
        grades.add(new Grade(programmingCourse, "CB"));
        grades.add(new Grade(circuitsCourse, "CC"));
        transcript = new Transcript(grades);

        student = new Student("o150121065", "Azra", "ÇetinTürk", "mar21065", "150121065", transcript, null);

        advisor = new Advisor("advisor123", "Ali", "Veli", "password123", "A123");

        courses = new ArrayList<>();
        courses.add(programmingCourse);
        courses.add(circuitsCourse);
        courses.add(dataStructuresCourse);

        registrationSystem = new CourseRegistrationSystem(student, courses);
    }



    @Test
    void testRequestInCourse() throws IOException {
        assertTrue(student.getRequestedCourses().isEmpty());

        registrationSystem.requestInCourse(dataStructuresCourse, student);
        assertTrue(student.getRequestedCourses().contains(dataStructuresCourse));
        assertEquals(1, student.getRequestedCourses().size());
    }



    @Test
    void testRemoveCourseFromRequestList() throws IOException {
        // Given: Öğrenci bir ders talep etmiş durumda
        registrationSystem.requestInCourse(dataStructuresCourse, student);
        assertTrue(student.getRequestedCourses().contains(dataStructuresCourse));
        boolean result = registrationSystem.removeCourseFromRequestList(student, dataStructuresCourse);

        assertTrue(result);
        assertFalse(student.getRequestedCourses().contains(dataStructuresCourse));
    }



    @Test
    void testTranscriptToString() {
        String expected = String.format("%-10s %-30s %-10s\n", "Course ID", "Course Name", "Grade Value")
                + "CSE1241    Programming 1                  CB        \n"  // Programming 1 dersi
                + "EE2031     Electric Circuits              CC        \n"; // Electric Circuits dersi


        String actual = transcript.toString();
        assertEquals(expected, actual);
    }

    @Test
    void testAdvisorToString() {

        advisor.getAdvisedStudents().add(student);

        String expected = "Advisor{" +
                "username='advisor123', " +
                "name='Ali', " +
                "surname='Veli', " +
                "advisorID='A123', " +
                "advisedStudents=[" +
                "Student{username='o150121065', name='Azra', surname='ÇetinTürk', " +
                "studentID='150121065', " +
                "enrolledCourses=[], " +
                "requestedCourses=[], " +
                "transcript=Course ID  Course Name                    Grade Value\n" +
                "CSE1241    Programming 1                  CB        \n" +
                "EE2031     Electric Circuits              CC        \n" +
                "}]}";

        String actual = advisor.toString();
        assertEquals(expected, actual);
    }



    @Test
    void testCourseToString() {
        CourseSection courseSection = new CourseSection("fall", "Sanem Arslan", 5, "Active");
        Course course = new Course("CSE1241", "Programming 1", 6, false, "None", courseSection);

        String expected = "Course{" +
                "courseId='CSE1241'" +
                ", courseName='Programming 1'" +
                ", credit=6" +
                ", prerequisite=false" +
                ", prerequisiteLessonId='None'" +
                ", courseSection=" + courseSection +
                '}';

        assertEquals(expected, course.toString());
    }

    @Test
    void testStudentToString() {
        String expected = "Student{" +
                "username='o150121065', " +
                "name='Azra', " +
                "surname='ÇetinTürk', " +
                "studentID='150121065', " +
                "enrolledCourses=" + student.getEnrolledCourses() +
                ", requestedCourses=" + student.getRequestedCourses() +
                ", transcript=" + transcript +
                '}';

        assertEquals(expected, student.toString());
    }


}