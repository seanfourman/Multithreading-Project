import java.util.ArrayList;

public class RuppinProtocol implements ProtocolInterface {
    private static final int WAITING = 0;
    private static final int IF_USER_EXISTS = 1;
    private static final int NEW_USER = 2;
    private static final int EXISTING_USER = 3;

    private static final int ASK_PASSWORD = 4;
    private static final int USER_CREATED = 5;
    private static final int USER_EXISTS = 6;

    private int state = WAITING;
    private ArrayList<Client> clientState;
    private Client newClient;
    private String username;
    private String password;

    public RuppinProtocol(ArrayList<Client> clientState) {
        this.clientState = clientState;
    }

    public String processInput(String theInput) {
        String theOutput = "";

        switch (state) {
            case WAITING:
                theOutput = "New User? [Y/N]";
                state = IF_USER_EXISTS;
                break;

            case IF_USER_EXISTS:
                if ("Y".equalsIgnoreCase(theInput)) {
                    newClient = new Client();
                    theOutput = "Enter username:";
                    state = NEW_USER;
                } else if ("N".equalsIgnoreCase(theInput)) {
                    theOutput = "Enter existing username:";
                    state = EXISTING_USER;
                } else {
                    theOutput = "Invalid Input. New User? [Y/N]";
                }
                break;

            case NEW_USER:
                username = theInput.trim();
                if (isUsernameTaken(username)) {
                    theOutput = "Username already exists. Try another username:";
                    state = NEW_USER; // re-run the state to get a new username
                } else {
                    newClient.setUsername(username);
                    theOutput = "Enter password:";
                    state = ASK_PASSWORD;
                }
                break;

            case EXISTING_USER:
                username = theInput.trim();
                if (isUsernameTaken(username)) {
                    theOutput = "Username recognized. Please enter your password:";
                    state = ASK_PASSWORD; // reuse ASK_PASSWORD state for simplicity
                } else {
                    theOutput = "Username not found. Try again:";
                    state = EXISTING_USER; // re-run the state to get a new username
                }
                break;

            case ASK_PASSWORD:
                password = theInput.trim();
                if (!isPasswordStrong(password)) {
                    theOutput = "Password does not meet requirements. Try another password:";
                    state = ASK_PASSWORD; // re-run the state to get a new username
                } else {
                    newClient.setPassword(password);
                    state = USER_CREATED;
                }
                break;

            case USER_EXISTS:
                // check if the password matches the username
                break;

            default:
                theOutput = "Unexpected input. Please try again.";
                state = WAITING;
                break;
        }

        return theOutput;
    }

    private boolean isUsernameTaken(String username) {
        for (Client user : clientState) {
            if (user.getUsername().equalsIgnoreCase(username)) {
                return true;
            }
        }
        return false;
    }

    private boolean isPasswordStrong(String password) {
        // check for password strength
        return true;
    }
}