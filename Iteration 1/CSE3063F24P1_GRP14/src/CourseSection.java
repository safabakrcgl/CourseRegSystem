import com.fasterxml.jackson.annotation.JsonProperty;

class CourseSection {
    @JsonProperty("term")
    private String term;

    @JsonProperty("day")
    private String day;

    @JsonProperty("hour")
    private String hour;

    @JsonProperty("semester")
    private int semester;

    @JsonProperty("instructor")
    private String instructor;

    @JsonProperty("enrollmentCapacity")
    private int enrollmentCapacity;

    @JsonProperty("status")
    private String status;

    // Getters
    public int getEnrollmentCapacity() {
        return enrollmentCapacity;
    }

    public CourseSection() {}


    public CourseSection(String fall, String sanemArslan, int i, String active) {
        term = fall;
        day = sanemArslan;
    }

    // toString method for printing
    @Override
    public String toString() {
        return "CourseSection{" +
                "term='" + term + '\'' +
                ", day='" + day + '\'' +
                ", hour='" + hour + '\'' +
                ", semester=" + semester +
                ", instructor='" + instructor + '\'' +
                ", enrollmentCapacity=" + enrollmentCapacity +
                ", status='" + status + '\'' +
                '}';
    }

}