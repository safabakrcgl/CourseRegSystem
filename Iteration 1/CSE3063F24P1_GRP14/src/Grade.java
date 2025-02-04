import com.fasterxml.jackson.annotation.JsonProperty;

public class Grade {
    @JsonProperty("course")
    private Course course;

    @JsonProperty("gradeValue")
    private String gradeValue;

    public Grade(Course course, String gradeValue) {
        this.course = course;
        this.gradeValue = gradeValue;
    }
    public Grade() {}
    // Getters and setters
    public Course getCourse() {
        return course;
    }

    public String getGradeValue() {
        return gradeValue;
    }


    @Override
    public String toString() {
        return String.format("%-10s %-30s %-10s\n", course.getCourseId(), course.getCourseName(),gradeValue);
    }


}