import com.fasterxml.jackson.annotation.JsonProperty;

class Course {
    @JsonProperty("courseId")
    private String courseId;

    @JsonProperty("courseName")
    private String courseName;

    @JsonProperty("credit")
    private int credit;

    @JsonProperty("prerequisite")
    private boolean prerequisite;

    @JsonProperty("prerequisiteLessonId")
    private String prerequisiteLessonId;

    @JsonProperty("courseSection")
    private CourseSection courseSection;

    // Constructor
    public Course(String courseId, String courseName, int credit, boolean prerequisite, String prerequisiteLessonId, CourseSection courseSection) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.credit = credit;
        this.prerequisite = prerequisite;
        this.prerequisiteLessonId = prerequisiteLessonId;
        this.courseSection = courseSection;
    }
    public Course() {}

    // Getters and Setters
    public String getCourseName() {
        return courseName;
    }
    public String getCourseId() {
        return courseId;
    }
    public CourseSection getCourseSection() {
        return courseSection;
    }
    public String getPrerequisiteLessonId() {
        return prerequisiteLessonId;
    }

    public boolean hasPrerequisite() {
        return prerequisite;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Course course = (Course) obj;
        return courseId.equals(course.courseId);
    }

    @Override
    public int hashCode() {
        return courseId.hashCode();
    }
    // toString method for printing
    @Override
    public String toString() {
        return "Course{" +
                "courseId='" + courseId + '\'' +
                ", courseName='" + courseName + '\'' +
                ", credit=" + credit +
                ", prerequisite=" + prerequisite +
                ", prerequisiteLessonId='" + prerequisiteLessonId + '\'' +
                ", courseSection=" + courseSection +
                '}';
    }

}


