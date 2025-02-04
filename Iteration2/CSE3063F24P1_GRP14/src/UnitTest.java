import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UnitTest {

    private Transcript testTranscript;
    private Advisor testAdvisor;
    private Student testStudent;
    private Course testCourse;

    @BeforeEach
    void setUp() {
        // Test Course
        testCourse = new Course("C101", "Test Course", 3, false, null,
                new ArrayList<>(), 1, 2023, "Dr. Smith", 30, 0, "active");

        // Test Transcript
        List<Grade> grades = new ArrayList<>();
        grades.add(new Grade(testCourse, "AA"));
        testTranscript = new Transcript(grades);

        // Test Student
        testStudent = new Student("student1", "Test", "Student", "password123", "S123",
                testTranscript, null);

        // Test Advisor
        testAdvisor = new Advisor("advisor1", "Test", "Advisor", "password123", "A101");
        testAdvisor.getAdvisedStudents().add(testStudent);
    }

    @Test
    void testTranscriptToString() {
        String transcriptString = testTranscript.toString();
        assertNotNull(transcriptString);
        assertTrue(transcriptString.contains("C101"));
        assertTrue(transcriptString.contains("AA"));
    }

    @Test
    void testAdvisorToString() {
        String advisorString = testAdvisor.toString();
        assertNotNull(advisorString);
        assertTrue(advisorString.contains("advisor1"));
        assertTrue(advisorString.contains("A101"));
    }

    @Test
    void testStudentInitialization() {
        assertEquals("student1", testStudent.getUsername());
        assertEquals("S123", testStudent.getStudentID());
        assertNotNull(testStudent.getTranscript());
    }

    @Test
    void testAddGradeToTranscript() {
        Grade newGrade = new Grade(new Course("C102", "New Course", 3, false, null, new ArrayList<>(), 1, 2023, "Dr. Brown", 30, 0, "active"), "BB");
        testTranscript.getGrades().add(newGrade);
        assertEquals(2, testTranscript.getGrades().size());
        assertTrue(testTranscript.getGrades().stream().anyMatch(g -> g.getGradeValue().equals("BB")));
    }

    @Test
    void testAdvisorAdvisedStudents() {
        assertEquals(1, testAdvisor.getAdvisedStudents().size());
        assertEquals("student1", testAdvisor.getAdvisedStudents().get(0).getUsername());
    }

    @Test
    void testStudentAddRequestedCourse() {
        testStudent.getRequestedCourses().add(testCourse);
        assertEquals(1, testStudent.getRequestedCourses().size());
        assertTrue(testStudent.getRequestedCourses().contains(testCourse));
    }

    @Test
    void testStudentRemoveRequestedCourse() {
        testStudent.getRequestedCourses().add(testCourse);
        testStudent.getRequestedCourses().remove(testCourse);
        assertEquals(0, testStudent.getRequestedCourses().size());
        assertFalse(testStudent.getRequestedCourses().contains(testCourse));
    }


}
