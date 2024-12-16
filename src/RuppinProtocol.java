import java.util.ArrayList;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RuppinProtocol implements ProtocolInterface {
    // DEFAULT PROTOCOL STATES
    private static final int WAITING = 0;
    private static final int ASK_USER_TYPE = 1;
    // NEW CLIENT STATES
    private static final int ASK_USERNAME_NEW = 2;
    private static final int ASK_PASSWORD_NEW = 3;
    private static final int ASK_IS_STUDENT = 4;
    private static final int ASK_IS_HAPPY = 5;
    // EXISTING CLIENT STATES
    private static final int ASK_USERNAME_EXISTING = 6;
    private static final int ASK_PASSWORD_EXISTING = 7;
    private static final int ASK_IF_UPDATE = 8;
    private static final int ASK_PASSWORD_UPDATE = 9;
    private static final int ASK_PASSWORD_UPDATE_NEW = 10;

    private int state = WAITING;
    private ArrayList<Client> clientState;
    private String tempUsername;
    private String tempPassword;
    private boolean tempIsStudent;
    private boolean tempIsHappy;
    private boolean isUpdate;

    public RuppinProtocol(ArrayList<Client> clientState) {
        this.clientState = clientState;
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
                    isUpdate = false;
                } else if ("N".equalsIgnoreCase(theInput)) {
                    theOutput = "Enter your username:";
                    state = ASK_USERNAME_EXISTING;
                } else {
                    theOutput = "Invalid input. New User? [Y/N]";
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
                theOutput = "Are you a student in Ruppin? [Y/N]";
                state = ASK_IS_STUDENT;
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
                    if (isUpdate) {
                        Client client = getClientByUsername(tempUsername);
                        client.setStudent(tempIsStudent);
                        client.setHappy(tempIsHappy);
                        theOutput = "Do you want to change your password? [Y/N]";
                        state = ASK_PASSWORD_UPDATE;
                    }
                    else {
                        // create new user and add to clientState list
                        Client newClient = new Client(tempUsername, tempPassword, tempIsStudent, tempIsHappy);
                        // synchronized may not be needed here (***)
                        synchronized (clientState) {
                            clientState.add(newClient);
                            // check if we need to save the clients to a CSV file
                            if (clientState.size() % 3 == 0) {
                                saveClientsToCSV(clientState);
                            }
                        }
                        theOutput = "Disconnecting..."; // message to indicate disconnection from server
                    }
                } catch (IllegalArgumentException e) {
                    theOutput = e.getMessage() + "Try entering a new username:";
                    state = ASK_USERNAME_NEW;
                }
                break;

            // TYPE -> EXISTING USER
            case ASK_USERNAME_EXISTING:
                tempUsername = theInput.trim();
                if (isUsernameTaken(tempUsername)) {
                    theOutput = "Enter your password:";
                    state = ASK_PASSWORD_EXISTING;
                } else {
                    theOutput = "Username not found. Try again:";
                }
                break;

            case ASK_PASSWORD_EXISTING:
                tempPassword = theInput.trim();
                Client tempClient = new Client(tempUsername, tempPassword);
                if (findClient(tempClient)) {
                    Client client = getClientByUsername(tempUsername);
                    theOutput = "Last time you gave me the following information: " +
                                "You are " + (client.isStudent() ? "a student at Ruppin" : "not a student at Ruppin") +
                                " and you are " + (client.isHappy() ? "Happy." : "not Happy.") + " Any changes since last time? [Y/N]";
                    state = ASK_IF_UPDATE;
                } else {
                    theOutput = "Incorrect password. Please try again:";
                    state = ASK_PASSWORD_EXISTING;
                }
                break;

            case ASK_IF_UPDATE:
                if ("Y".equalsIgnoreCase(theInput)) {
                    theOutput = "Are you a student in Ruppin? [Y/N]";
                    state = ASK_IS_STUDENT;
                    isUpdate = true;
                } else if ("N".equalsIgnoreCase(theInput)) {
                    theOutput = "Disconnecting..."; // message to indicate disconnection from server
                } else {
                    theOutput = "Invalid input. Any changes since last time? [Y/N]";
                }
                break;

            case ASK_PASSWORD_UPDATE:
                if ("Y".equalsIgnoreCase(theInput)) {
                    theOutput = "Choose your new password:";
                    state = ASK_PASSWORD_UPDATE_NEW;
                } else if ("N".equalsIgnoreCase(theInput)) {
                    theOutput = "Disconnecting..."; // message to indicate disconnection from server
                } else {
                    theOutput = "Invalid input. Do you want to change your password? [Y/N]";
                }
                break;

            case ASK_PASSWORD_UPDATE_NEW:
                tempPassword = theInput.trim();
                Client client = getClientByUsername(tempUsername);
                try {
                    client.changePassword(tempPassword);
                    theOutput = "Disconnecting..."; // message to indicate disconnection from server
                } catch (IllegalArgumentException e) {
                    theOutput = e.getMessage() + " Try again:";
                    state = ASK_PASSWORD_UPDATE_NEW;
                }
                break;
            
            default:
                theOutput = "Unexpected input. Please try again.";
                state = WAITING;
                break;
        }

        return theOutput;
    }

    // synchronized may not be needed here in all the methods (***)

    private synchronized boolean isUsernameTaken(String username) {
        for (Client client : clientState) {
            if (client.checkUser().equalsIgnoreCase(username)) {
                return true;
            }
        }
        return false;
    }

    private synchronized boolean findClient(Client tempClient) {
        for (Client client : clientState) {
            if (client.equals(tempClient)) {
                return true;
            }
        }
        return false;
    }

    private synchronized Client getClientByUsername(String username) {
        for (Client client : clientState) {
            if (client.checkUser().equalsIgnoreCase(username)) {
                return client;
            }
        }
        return null;
    }

    private void saveClientsToCSV(ArrayList<Client> users) {
        String date = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String filename = "csv." + date + "_backup.csv";

        synchronized (clientState) {
            try (FileWriter writer = new FileWriter(filename)) {
                writer.append("Username,Password,IsStudent,IsHappy\n");
                for (Client user : users) {
                    writer.append(user.checkUser())
                        .append(',')
                        .append(user.checkPassword())
                        .append(',')
                        .append(Boolean.toString(user.isStudent()))
                        .append(',')
                        .append(Boolean.toString(user.isHappy()))
                        .append('\n');
                }
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}