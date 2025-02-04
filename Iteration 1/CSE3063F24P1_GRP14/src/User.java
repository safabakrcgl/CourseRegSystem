import com.fasterxml.jackson.annotation.JsonProperty;

abstract class  User {
    @JsonProperty("username")
    private String username;

    @JsonProperty("name")
    private String name;

    @JsonProperty("surname")
    private String surname;

    @JsonProperty("password")
    private String password;

    public User(@JsonProperty("username") String username,@JsonProperty("name")  String name, @JsonProperty("surname") String surname, @JsonProperty("password") String password) {
        this.username = username;
        this.name = name;
        this.password = password;
        this.surname = surname;
    }

    protected String getUsername() {
        return username;
    }

    protected String getName() {
        return name;
    }

    protected String getSurname() {
        return surname;
    }

    protected String getPassword() {
        return password;
    }

}
