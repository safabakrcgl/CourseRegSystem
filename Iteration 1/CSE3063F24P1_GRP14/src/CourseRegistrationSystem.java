import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CourseRegistrationSystem {
    private Student student;
    //private List<Student> students;
    private List<Course> courses;
    private JSONMethods jsonMethods = new JSONMethods(); // Assuming JSONMethods class handles JSON operations

    // Constructor
    public CourseRegistrationSystem(Student student, List<Course> courses) {
        this.student = student;
        //this.students = students;
        this.courses = courses;
    }


    // Method to add a student to a course
    public void addToEnrollList(Course course, Student student) throws IOException {
        // Check if the student is already enrolled in the course by comparing course IDs
        boolean isAlreadyEnrolled = false;
        for (Course enrolledCourse : student.getEnrolledCourses()) {
            if (enrolledCourse.getCourseId().equals(course.getCourseId())) {
                isAlreadyEnrolled = true;
                break;
            }
        }

        if (isAlreadyEnrolled) {
            System.out.println("Student is already enrolled in this course.");
            return;
        }

        // Check if the course has enough capacity
        CourseSection section = course.getCourseSection(); // Assumes each course has a single section for simplicity
        if (section.getEnrollmentCapacity() <= 0) {
            System.out.println("Course enrollment capacity reached.");
            return;
        }

        // Enroll the student in the course
        student.getEnrolledCourses().add(course);

        // Update the course's enrollment capacity
        // section.setEnrollmentCapacity(section.getEnrollmentCapacity() - 1);

        // Save the updated student object to JSON file
        jsonMethods.saveStudentToFile(student);

        System.out.println("Student enrolled in course successfully.");
    }

    //Remove the course from the students request list
    public boolean removeCourseFromRequestList(Student student, Course course) throws IOException {
        if(student.getRequestedCourses().size() > 0 && student.getRequestedCourses().contains(course)){
            student.getRequestedCourses().remove(course);
            jsonMethods.saveStudentToFile(student);
            return true; //succesfully removed

        }
        else{
            System.out.println("You did not take any course. Firstly, please request at least one course.");
            return false; //removal failed
        }
    }

    public void removeCourseFromEnrolledList(Student student, Course course) throws IOException {
        if(student.getEnrolledCourses().size() > 0 && student.getEnrolledCourses().contains(course)){
            student.getEnrolledCourses().remove(course);
            jsonMethods.saveStudentToFile(student);

        }
        else{
            System.out.println("You are not enrolled to any course. Firstly, please request at least one course.");
        }
    }

    // Method to list available course sections for a given student
    public List<Course> listAvailableCourses() {
        List<Course> availableCourses = new ArrayList<>();
        List<Course> takenCourses = new ArrayList<>();
        List<String> failedCourses = new ArrayList<>();

        
        for (Grade grade : student.getTranscript().allGrades()) {
            String gradeValue = grade.getGradeValue();
            Course course = grade.getCourse();


            if (gradeValue.equals("FF") || gradeValue.equals("FD")) {
                failedCourses.add(course.getCourseId());
                availableCourses.add(course);
            }

            else if (gradeValue.equals("DD") || gradeValue.equals("DC")) {

                if (!student.getEnrolledCourses().contains(course)) {
                    availableCourses.add(course);
                    takenCourses.add(course);
                }

            }

            else {
                takenCourses.add(course);
            }
        }


        for (Course course : courses) {

            if (student.getEnrolledCourses().contains(course) || takenCourses.contains(course) || availableCourses.contains(course))  {
                continue;
            }

            boolean hasPassedPrerequisite = !course.hasPrerequisite();


            if (course.hasPrerequisite()) {
                String prerequisiteId = course.getPrerequisiteLessonId();


                if (failedCourses.contains(prerequisiteId)) {
                    hasPassedPrerequisite = false;
                } else {

                    for (Course takenCourse : takenCourses) {
                        if (takenCourse.getCourseId().equals(prerequisiteId)) {
                            hasPassedPrerequisite = true;
                            break;
                        }
                    }
                }
            }


            if (hasPassedPrerequisite) {
                availableCourses.add(course);
            }
        }

        return availableCourses;
    }





    public String availableCoursesToString(List<Course> availableCourses) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-10s %-40s\n", "Course ID", "Course Name"));
        sb.append("------------------------------------------------------------\n");

        for (Course course : availableCourses) {
            sb.append(String.format("%-10s %-40s\n",
                    course.getCourseId(),
                    course.getCourseName()));
        }

        return sb.toString();
    }





    // Method to request a course for a student
    public void requestInCourse(Course course, Student student) throws IOException {

        if (student.getRequestedCourses().stream().anyMatch(requestedCourse -> requestedCourse.getCourseId().equals(course.getCourseId()))) {
            System.out.println("Student is already requested to this course.");
        }
        else{
            student.getRequestedCourses().add(course);
            System.out.println("Course " + course.getCourseId() + " requested successfully.");
            jsonMethods.saveStudentToFile(student);
        }
    }



}
