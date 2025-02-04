import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

class CourseSection {

    @JsonProperty("day")
    private String day;

    @JsonProperty("place")
    private String place;

    @JsonProperty("hour")
    private String hour;

    public CourseSection() {

    }

    // Constructor
    public CourseSection(String day, String place, String hour) {
        this.day = day;
        this.place = place;
        this.hour = hour;

    }


    // Getters
    public String getDay() {
        return day;
    }

    public String getPlace() {
        return place;
    }

    public String getHour() {
        return hour;
    }


    // toString method for printing
    @Override
    public String toString() {
        return "CourseSection{" +
                "place='" + place + '\'' +
                ", day='" + day + '\'' +
                ", hour=" + hour +
                '}';
    }
}