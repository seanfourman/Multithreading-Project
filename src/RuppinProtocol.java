public class RuppinProtocol implements ProtocolInterface {
    private static final int WAITING = 0;
    private static final int SENT_GREETING = 1;
    private static final int SENT_RESPONSE = 2;

    private int state = WAITING;

    public String processInput(String theInput) {
        String theOutput = null;

        switch (state) {
            case WAITING:
                theOutput = "Welcome to Ruppin Protocol! Say 'Hello' to start.";
                state = SENT_GREETING;
                break;

            case SENT_GREETING:
                if ("Hello".equalsIgnoreCase(theInput)) {
                    theOutput = "Ruppin: How can I assist you today?";
                    state = SENT_RESPONSE;
                } else {
                    theOutput = "You should say 'Hello'! Try again.";
                }
                break;

            case SENT_RESPONSE:
                if ("Bye".equalsIgnoreCase(theInput)) {
                    theOutput = "Goodbye!";
                    state = WAITING;
                } else {
                    theOutput = "You said: " + theInput;
                }
                break;
        }
        return theOutput;
    }
}