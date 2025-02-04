import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Path;
import java.nio.file.Paths;

public class JSONMethods {

    public List<Course> loadAllCourses() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            InputStream inputStream = JSONMethods.class.getClassLoader().getResourceAsStream("./resources/course.json");
            return List.of(objectMapper.readValue(inputStream, Course[].class));
        } catch (IOException e) {
            e.printStackTrace();
            return List.of(); // Return empty list in case of error
        }
    }

    public Student loadStudent(String studentId) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String resourcePath = "./resources/Students/" + studentId + ".json";
        InputStream inputStream = JSONMethods.class.getClassLoader().getResourceAsStream(resourcePath);

        if (inputStream == null) {
            throw new IOException("Resource not found: " + resourcePath);
        }

        return objectMapper.readValue(inputStream, Student.class);
    }


    public Advisor loadAdvisor(String advisorId) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String resourcePath = "./resources/Advisors/" + advisorId + ".json";
        InputStream inputStream = JSONMethods.class.getClassLoader().getResourceAsStream(resourcePath);

        if (inputStream == null) {
            throw new IOException("Resource not found: " + resourcePath);
        }

        return objectMapper.readValue(inputStream, Advisor.class);
    }

    private final String STUDENT_JSON_PATH = "CSE3063F24P1_GRP14/src/resources/Students/";

    public List<Advisor> loadAllAdvisors() throws IOException {
        File advisorsDir = new File("CSE3063F24P1_GRP14/src/resources/Advisors/");
        List<Advisor> advisors = new ArrayList<Advisor>();

        // Check if directory exists and list files
        if (advisorsDir.exists() && advisorsDir.isDirectory()) {
            File[] advisorFiles = advisorsDir.listFiles();
            if (advisorFiles != null) {
                for (File file : advisorFiles) {
                    if (file.isFile() && file.getName().endsWith(".json")) {
                        String fileName = file.getName();
                        String advisorId = fileName.substring(0, fileName.indexOf('.'));
                        advisors.add(loadAdvisor(advisorId));
                    }
                }

            }
        } else {
            System.out.println("The directory does not exist or is not a directory.");

        }
        return advisors;

    }

    public List<Student> loadAllStudents() throws IOException {
        File studentsDir = new File("CSE3063F24P1_GRP14/src/resources/Students/");
        List<Student> students = new ArrayList<Student>();

        // Check if directory exists and list files
        if (studentsDir.exists() && studentsDir.isDirectory()) {
            File[] studentFiles = studentsDir.listFiles();
            if (studentFiles != null) {
                for (File file : studentFiles) {
                    if (file.isFile() && file.getName().endsWith(".json")) {
                        String fileName = file.getName();
                        String studentId = fileName.substring(0, fileName.indexOf('.'));
                        students.add(loadStudent(studentId));
                    }
                }

            }
        } else {
            System.out.println("The directory does not exist or is not a directory.");

        }
        return students;

    }


    public void saveStudentToFile(Student student) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String studentFileName = STUDENT_JSON_PATH + student.getStudentID() + ".json";
        mapper.writeValue(new File(studentFileName), student);
    }


}
