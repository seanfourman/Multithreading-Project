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

    @Override
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
                if (checkUser(tempUsername)) {
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
                        Client client = findClient(tempUsername);
                        client.setStudent(tempIsStudent);
                        client.setHappy(tempIsHappy);
                        theOutput = "Do you want to change your password? [Y/N]";
                        state = ASK_PASSWORD_UPDATE;
                    }
                    else {
                        // need to re-check here if the username is still available because there could be multiple clients trying to add a new user at the same time
                        if (checkUser(tempUsername)) {
                            theOutput = "Username already exists. Try another username:";
                            state = ASK_USERNAME_NEW;
                        } else {
                            addUser(tempUsername, tempPassword, tempIsStudent, tempIsHappy);
                            theOutput = "Disconnecting..."; // message to indicate disconnection from server
                        }
                    }
                } catch (IllegalArgumentException e) {
                    theOutput = e.getMessage() + "Try entering a new username:";
                    state = ASK_USERNAME_NEW;
                }
                break;

            // TYPE -> EXISTING USER
            case ASK_USERNAME_EXISTING:
                tempUsername = theInput.trim();
                if (checkUser(tempUsername)) {
                    theOutput = "Enter your password:";
                    state = ASK_PASSWORD_EXISTING;
                } else {
                    theOutput = "Username not found. Try again:";
                }
                break;

            case ASK_PASSWORD_EXISTING:
                tempPassword = theInput.trim();
                if (checkPassword(tempUsername, tempPassword)) {
                    Client client = findClient(tempUsername);
                    theOutput = "Last time you gave me the following information: " +
                                "You are " + (client.isStudent() ? "a student at Ruppin" : "not a student at Ruppin") +
                                " and you are " + (client.isHappy() ? "Happy." : "not Happy.") + " Any changes since last time? [Y/N]";
                    state = ASK_IF_UPDATE;
                } else {
                    theOutput = "Incorrect password. Try again:";
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
                    // backup the user with the new isStudent, isHappy values to the CSV -> NOT ONLY WHEN DIVISIBLE BY 3 BUT NEEDED FOR NOT LOSING DATA
                    saveUsersToCSV(clientState);
                    theOutput = "Disconnecting..."; // message to indicate disconnection from server
                } else {
                    theOutput = "Invalid input. Do you want to change your password? [Y/N]";
                }
                break;

            case ASK_PASSWORD_UPDATE_NEW:
                tempPassword = theInput.trim();
                try {
                    changePassword(tempUsername, tempPassword);
                    // backup the user with the new password to the CSV -> NOT ONLY WHEN DIVISIBLE BY 3 BUT NEEDED FOR NOT LOSING DATA
                    saveUsersToCSV(clientState);
                    theOutput = "Disconnecting..."; // message to indicate disconnection from server
                } catch (IllegalArgumentException e) {
                    theOutput = e.getMessage() + " Try again:";
                    state = ASK_PASSWORD_UPDATE_NEW;
                }
                break;
            
            default:
                theOutput = "Unexpected input. Try again:";
                state = WAITING;
                break;
        }

        return theOutput;
    }

    // synchronized may not be needed here in all the methods (***)
    public synchronized boolean checkUser(String username) {
        for (Client client : clientState) {
            if (client.getUsername().equalsIgnoreCase(username)) {
                return true;
            }
        }
        return false;
    }

    public synchronized boolean checkPassword(String username, String password) {
        for (Client client : clientState) {
            if (client.getUsername().equalsIgnoreCase(username) && client.getPassword().equals(password)) {
                return true;
            }
        }
        return false;
    }

    public synchronized Client findClient(String username) {
        for (Client client : clientState) {
            if (client.getUsername().equalsIgnoreCase(username)) {
                return client;
            }
        }
        return null;
    }

    public synchronized void changePassword(String username, String newPassword) {
        Client client = findClient(username);
        if (client != null) {
            client.setPassword(newPassword);
        } else {
            throw new IllegalArgumentException("User not found. ");
        }
    }

    public void addUser(String username, String password, boolean isStudent, boolean isHappy) {
        Client newClient = new Client(username, password, isStudent, isHappy);
        synchronized (clientState) {
            clientState.add(newClient);
            if (clientState.size() % 3 == 0) {
                saveUsersToCSV(clientState);
            }
        }
    }

    public void saveUsersToCSV(ArrayList<Client> users) {
        String date = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String filename = date + "_backup.csv";

        synchronized (clientState) {
            try (FileWriter writer = new FileWriter(filename)) {
                writer.append("Username,Password,IsStudent,IsHappy\n");
                for (Client user : users) {
                    writer.append(user.getUsername())
                        .append(',')
                        .append(user.getPassword())
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