import java.util.ArrayList;

public class RuppinProtocol implements ProtocolInterface {
    private static final int WAITING = 0;
    private static final int ASK_NEW_USER = 1;
    private static final int ASK_USERNAME_NEW = 2;
    private static final int ASK_PASSWORD_NEW = 3;
    private static final int NEW_USER_CREATED = 4;
    private static final int ASK_USERNAME_EXISTING = 5;
    private static final int ASK_PASSWORD_EXISTING = 6;
    private static final int USER_LOGGED_IN = 7;

    private int state = WAITING;
    private ArrayList<Client> users;
    private String tempUsername;
    private String tempPassword;

    public RuppinProtocol(ArrayList<Client> users) {
        this.users = users;
    }

    public String processInput(String theInput) {
        String theOutput;

        switch (state) {
            case WAITING:
                theOutput = "New User? [Y/N]";
                state = ASK_NEW_USER;
                break;

            case ASK_NEW_USER:
                if ("Y".equalsIgnoreCase(theInput)) {
                    theOutput = "Enter desired username:";
                    state = ASK_USERNAME_NEW;
                } else if ("N".equalsIgnoreCase(theInput)) {
                    theOutput = "Enter your username:";
                    state = ASK_USERNAME_EXISTING;
                } else {
                    theOutput = "Invalid input. New User? [Y/N]";
                }
                break;

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
                    try {
                        // create new user and add to users list
                        Client newUser = new Client(tempUsername, tempPassword, false, false);
                        users.add(newUser);
                        theOutput = "User created successfully! Welcome, " + tempUsername;
                        state = NEW_USER_CREATED;
                    } catch (IllegalArgumentException e) {
                        theOutput = "Error creating user: " + e.getMessage() + " Please try again.";
                        state = ASK_PASSWORD_NEW;
                    }
                } else {
                    theOutput = "Password must be at least 9 characters long and include at least one uppercase letter, one lowercase letter, and one number. Please enter a valid password:";
                    state = ASK_PASSWORD_NEW;
                }
                break;

            case NEW_USER_CREATED:
                theOutput = "You are now logged in. Thank you!";
                // Optionally reset the state to WAITING or end the session
                state = WAITING;
                break;

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
                    theOutput = "Login successful! Welcome back, " + tempUsername;
                    state = USER_LOGGED_IN;
                } else {
                    theOutput = "Incorrect password. Please try again:";
                }
                break;

            case USER_LOGGED_IN:
                theOutput = "You are now logged in. Thank you!";
                // Optionally reset the state to WAITING or end the session
                state = WAITING;
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
}