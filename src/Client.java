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
        addUser(username);
        changePassword(password);
        setStudent(isStudent);
        setHappy(isHappy);
    }

    public String checkUser() {
        return username;
    }

    public String checkPassword() {
        return password;
    }

    public boolean isStudent() {
        return isStudent;
    }

    public boolean isHappy() {
        return isHappy;
    }

    public void addUser(String username) {
        // check for if the username is taken is done in the protocol with "isUsernameTaken"
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        this.username = username;
    }

    public void changePassword(String password) {
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

    @Override
    public boolean equals(Object checkedClient) {
        if (this == checkedClient) return true;
        if (checkedClient == null || getClass() != checkedClient.getClass()) return false;
    
        Client client = (Client) checkedClient;
    
        if (username != null ? !username.equals(client.username) : client.username != null) return false;
        return password != null ? password.equals(client.password) : client.password == null;
    }
}
