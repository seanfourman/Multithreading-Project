public class Client {
    private String username;
    private String password;
    private boolean isStudent;
    private boolean isHappy;

    public Client() {
        username = null;
        password = null;
        isStudent = false;
        isHappy = false;
    }

    public Client(String username, String password, boolean isStudent, boolean isHappy) {
        setUsername(username);
        setPassword(password);
        setStudent(isStudent);
        setHappy(isHappy);
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean isStudent() {
        return isStudent;
    }

    public boolean isHappy() {
        return isHappy;
    }

    public void setUsername(String username) {
        // check for if the username is taken is done in the protocol with "isUsernameTaken"
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        this.username = username;
    }

    public void setPassword(String password) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        this.password = password;
    }

    public void setStudent(boolean isStudent) {
        this.isStudent = isStudent;
    }

    public void setHappy(boolean isHappy) {
        this.isHappy = isHappy;
    }

    @Override
    public String toString() {
        return "Client{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", isStudent=" + isStudent +
                ", isHappy=" + isHappy +
                '}';
    }
}
