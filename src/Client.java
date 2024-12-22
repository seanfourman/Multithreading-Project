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

    public Client(String username, String password) {
        this.username = username;
        this.password = password;
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
        // check for if the username is taken is done in the protocol with "checkUser"
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty. ");
        }
        this.username = username;
        }

    public void setPassword(String password) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty. ");
        }
        if (!isValidPassword(password)) {
            throw new IllegalArgumentException("Password must be at least 9 characters long, contain at least one uppercase letter, one lowercase letter, and one number. ");
        }
        this.password = password;
    }

    public void setStudent(boolean isStudent) {
        this.isStudent = isStudent;
    }

    public void setHappy(boolean isHappy) {
        this.isHappy = isHappy;
    }

    // check whether we need to use contains here
    private boolean isValidPassword(String password) {
        if (password.length() < 9) {
            return false;
        }
        boolean hasUppercase = !password.equals(password.toLowerCase());
        boolean hasLowercase = !password.equals(password.toUpperCase());
        boolean hasNumber = password.matches(".*\\d.*");
        return hasUppercase && hasLowercase && hasNumber;
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
