import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JSONMethods {

    private static final String COURSE_JSON_PATH = "CSE3063F24P1_GRP14/src/resources/course.json";
    private static final String STUDENT_JSON_PATH = "CSE3063F24P1_GRP14/src/resources/Students/";
    private static final String DEPARTMENT_SCHEDULER_JSON_PATH = "CSE3063F24P1_GRP14/src/resources/department_scheduler.json";
    private static final String ADVISORS_JSON_PATH = "CSE3063F24P1_GRP14/src/resources/Advisors/";

    // Tüm kursları JSON'dan yükler
    public List<Course> loadAllCourses() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            File file = new File(COURSE_JSON_PATH);
            return Arrays.asList(objectMapper.readValue(file, Course[].class));
        } catch (IOException e) {
            System.err.println("Error loading courses from JSON: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // JSON'dan öğrenci yükler
    public Student loadStudent(String studentId) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            File studentFile = new File(STUDENT_JSON_PATH + studentId + ".json");
            return objectMapper.readValue(studentFile, Student.class);
        } catch (IOException e) {
            System.err.println("Error loading student from JSON: " + e.getMessage());
            return null;
        }
    }

    // Öğrenciyi JSON dosyasına kaydeder
    public void saveStudentToFile(Student student) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            File studentFile = new File(STUDENT_JSON_PATH + student.getStudentID() + ".json");
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(studentFile, student);
            System.out.println("Student saved to JSON file successfully!");
        } catch (IOException e) {
            System.err.println("Error saving student to JSON file: " + e.getMessage());
        }
    }

    // Öğrenciyi JSON dosyasında günceller
    public void updateStudentInJson(Student updatedStudent) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            File studentFile = new File(STUDENT_JSON_PATH + updatedStudent.getStudentID() + ".json");

            if (studentFile.exists()) {
                // JSON dosyasından mevcut öğrenciyi oku
                Student existingStudent = objectMapper.readValue(studentFile, Student.class);

                // Enrolled Courses'u tamamen güncelle
                existingStudent.getEnrolledCourses().clear();
                existingStudent.getEnrolledCourses().addAll(updatedStudent.getEnrolledCourses());

                // Requested Courses'u tamamen güncelle
                existingStudent.getRequestedCourses().clear();
                existingStudent.getRequestedCourses().addAll(updatedStudent.getRequestedCourses());

                // Güncellenmiş nesneyi JSON'a yaz
                objectMapper.writerWithDefaultPrettyPrinter().writeValue(studentFile, existingStudent);
                System.out.println("Student JSON updated successfully!");
            } else {
                // Eğer JSON dosyası yoksa, yeni dosya oluştur
                objectMapper.writerWithDefaultPrettyPrinter().writeValue(studentFile, updatedStudent);
                System.out.println("Student JSON created successfully!");
            }
        } catch (IOException e) {
            System.err.println("Error updating student JSON: " + e.getMessage());
        }
    }




    // JSON'dan belirli bir kursu yükler
    public Course loadCourseFromJson(String courseId) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            File file = new File(COURSE_JSON_PATH);
            List<Course> courses = Arrays.asList(objectMapper.readValue(file, Course[].class));
            return courses.stream().filter(course -> course.getCourseId().equals(courseId)).findFirst().orElse(null);
        } catch (IOException e) {
            System.err.println("Error loading course from JSON: " + e.getMessage());
            return null;
        }
    }

    // Danışmanları JSON dosyasından yükler
    public List<Advisor> loadAllAdvisors() {
        List<Advisor> advisors = new ArrayList<>();
        try {
            File advisorsDir = new File(ADVISORS_JSON_PATH);
            File[] advisorFiles = advisorsDir.listFiles((dir, name) -> name.endsWith(".json"));

            if (advisorFiles != null) {
                ObjectMapper objectMapper = new ObjectMapper();
                for (File advisorFile : advisorFiles) {
                    Advisor advisor = objectMapper.readValue(advisorFile, Advisor.class);
                    advisors.add(advisor);
                }
            } else {
                System.out.println("No advisor files found in the directory.");
            }
        } catch (IOException e) {
            System.err.println("Error loading advisors from JSON: " + e.getMessage());
        }
        return advisors;
    }

    // Bölüm planlayıcısını JSON dosyasından yükler
    public DepartmentScheduler loadDepartmentScheduler() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            File schedulerFile = new File(DEPARTMENT_SCHEDULER_JSON_PATH);

            if (schedulerFile.exists()) {
                return objectMapper.readValue(schedulerFile, DepartmentScheduler.class);
            } else {
                System.err.println("Department scheduler JSON file not found.");
                return null;
            }
        } catch (IOException e) {
            System.err.println("Error loading department scheduler from JSON: " + e.getMessage());
            return null;
        }
    }

    // Belirli bir kursu günceller ve JSON'a kaydeder
    public void updateCourseInJson(Course updatedCourse) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            File file = new File(COURSE_JSON_PATH);
            List<Course> courses = Arrays.asList(objectMapper.readValue(file, Course[].class));

            List<Course> updatedCourses = new ArrayList<>();
            for (Course course : courses) {
                if (course.getCourseId().equals(updatedCourse.getCourseId())) {
                    updatedCourses.add(updatedCourse);
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

    // Tüm öğrencileri JSON'dan yükler
    public List<Student> loadAllStudents() {
        try {
            File studentsDir = new File(STUDENT_JSON_PATH);
            File[] studentFiles = studentsDir.listFiles((dir, name) -> name.endsWith(".json"));
            List<Student> students = new ArrayList<>();

            if (studentFiles != null) {
                ObjectMapper objectMapper = new ObjectMapper();
                for (File studentFile : studentFiles) {
                    students.add(objectMapper.readValue(studentFile, Student.class));
                }
            }
            return students;
        } catch (IOException e) {
            System.err.println("Error loading all students from JSON: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
