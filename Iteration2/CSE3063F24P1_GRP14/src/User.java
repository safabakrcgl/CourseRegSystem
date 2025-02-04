import com.fasterxml.jackson.annotation.JsonProperty;

abstract class User {
    @JsonProperty("username")
    private String username;

    @JsonProperty("name")
    private String name;

    @JsonProperty("surname")
    private String surname;

    @JsonProperty("password")
    private String password;

    public User(@JsonProperty("username") String username,
                @JsonProperty("name") String name,
                @JsonProperty("surname") String surname,
                @JsonProperty("password") String password) {
        this.username = username;
        this.name = name;
        this.surname = surname;
        this.password = password;
    }

    // Getter methods for private fields
    protected String getUsernameField() {
        return username;
    }

    protected String getNameField() {
        return name;
    }

    protected String getSurnameField() {
        return surname;
    }

    protected String getPasswordField() {
        return password;
    }

    // Abstract methods for subclasses
    protected abstract void getMenu();

    protected abstract String getUsername();

    protected abstract String getName();

    protected abstract String getSurname();

    protected abstract String getPassword();
}
