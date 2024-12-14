import java.util.ArrayList;

public class RuppinProtocol implements ProtocolInterface {
    // DEFAULT PROTOCOL STATES
    private static final int WAITING = 0;
    private static final int ASK_USER_TYPE = 1;
    // NEW USER STATES
    private static final int ASK_USERNAME_NEW = 2;
    private static final int ASK_PASSWORD_NEW = 3;
    private static final int ASK_IS_STUDENT = 4;
    private static final int ASK_IS_HAPPY = 5;
    // EXISTING USER STATES
    private static final int ASK_USERNAME_EXISTING = 6;
    private static final int ASK_PASSWORD_EXISTING = 7;

    private int state = WAITING;
    private ArrayList<Client> users;
    private String tempUsername;
    private String tempPassword;
    private boolean tempIsStudent;
    private boolean tempIsHappy;

    public RuppinProtocol(ArrayList<Client> users) {
        this.users = users;
    }

    public String processInput(String theInput) {
        String theOutput;

        switch (state) {
            case WAITING:
                theOutput = "New User? [Y/N]";
                state = ASK_USER_TYPE;
                break;

            case ASK_USER_TYPE:
                if ("Y".equalsIgnoreCase(theInput)) {
                    theOutput = "Enter new username:";
                    state = ASK_USERNAME_NEW;
                } else if ("N".equalsIgnoreCase(theInput)) {
                    theOutput = "Enter your username:";
                    state = ASK_USERNAME_EXISTING;
                } else {
                    theOutput = "Invalid Input. New User? [Y/N]";
                }
                break;

            // TYPE -> NEW USER
            case ASK_USERNAME_NEW:
                tempUsername = theInput.trim();
                if (isUsernameTaken(tempUsername)) {
                    theOutput = "Username already exists. Try another username:";
                } else {
                    theOutput = "Enter password:";
                    state = ASK_PASSWORD_NEW;
                }
                break;

            case ASK_PASSWORD_NEW:
                tempPassword = theInput.trim();
                if (isValidPassword(tempPassword)) {
                    theOutput = "Are you a student in Ruppin? [Y/N]";
                    state = ASK_IS_STUDENT;
                } else {
                    theOutput = "Password must be at least 9 characters long and include at least one uppercase letter, one lowercase letter, and one number. Please enter a valid password:";
                    state = ASK_PASSWORD_NEW;
                }
                break;

            case ASK_IS_STUDENT:
                if ("Y".equalsIgnoreCase(theInput)) {
                    tempIsStudent = true;
                } else if ("N".equalsIgnoreCase(theInput)) {
                    tempIsStudent = false;
                } else {
                    theOutput = "Invalid input. Are you a student in Ruppin? [Y/N]";
                    break;
                }
                theOutput = "Are you happy? [Y/N]";
                state = ASK_IS_HAPPY;
                break;

            case ASK_IS_HAPPY:
                if ("Y".equalsIgnoreCase(theInput)) {
                    tempIsHappy = true;
                } else if ("N".equalsIgnoreCase(theInput)) {
                    tempIsHappy = false;
                } else {
                    theOutput = "Invalid input. Are you happy? [Y/N]";
                    break;
                }
                try {
                    // create new user and add to users list
                    Client newUser = new Client(tempUsername, tempPassword, tempIsStudent, tempIsHappy);
                    users.add(newUser);
                    theOutput = "Disconnecting..."; // message to indicate disconnection from server
                } catch (IllegalArgumentException e) {
                    theOutput = "Error creating user: " + e.getMessage() + " Please try again.";
                    state = ASK_USERNAME_NEW;
                }
                break;

            // TYPE -> EXISTING USER
            case ASK_USERNAME_EXISTING:
                tempUsername = theInput.trim();
                if (isUsernameTaken(tempUsername)) {
                    theOutput = "Username recognized. Please enter your password:";
                    state = ASK_PASSWORD_EXISTING;
                } else {
                    theOutput = "Username not found. Try again:";
                }
                break;

            case ASK_PASSWORD_EXISTING:
                String password = theInput.trim();
                if (validateUser(tempUsername, password)) {
                    theOutput = "Login successful. Welcome back, " + tempUsername + "! ";
                } else {
                    theOutput = "Incorrect password. Please try again:";
                }
                break;

            default:
                theOutput = "Unexpected input. Please try again.";
                state = WAITING;
                break;
        }

        return theOutput;
    }

    private boolean isUsernameTaken(String username) {
        for (Client user : users) {
            if (user.checkUser().equalsIgnoreCase(username)) {
                return true;
            }
        }
        return false;
    }

    private boolean isValidPassword(String password) {
        if (password.length() < 9) {
            return false;
        }
        boolean hasUppercase = !password.equals(password.toLowerCase());
        boolean hasLowercase = !password.equals(password.toUpperCase());
        boolean hasNumber = password.matches(".*\\d.*");
        return hasUppercase && hasLowercase && hasNumber;
    }

    private boolean validateUser(String username, String password) {
        for (Client user : users) {
            if (user.checkUser().equalsIgnoreCase(username) &&
                user.checkPassword().equals(password)) {
                return true;
            }
        }
        return false;
    }

    private Client getClientByUsername(String username) {
        for (Client user : users) {
            if (user.checkUser().equalsIgnoreCase(username)) {
                return user;
            }
        }
        return null;
    }
}