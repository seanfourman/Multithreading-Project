public class KnockKnockProtocol implements ProtocolInterface {
    private static final int WAITING = 0;
    private static final int SENT_KNOCK_KNOCK = 1;
    private static final int SENT_CLUE = 2;
    private static final int ANOTHER = 3;
    private static final int NUM_JOKES = 5;

    private int state = WAITING;
    private int currentJoke = 0;

    private final String[] clues = { "Turnip", "Little Old Lady", "Atch", "Who", "Who" };
    private final String[] answers = {
        "Turnip the heat, it's cold in here!",
        "I didn't know you could yodel!",
        "Bless you!",
        "Is there an owl in here?",
        "Is there an echo in here?"
    };

    public String processInput(String theInput) {
        String theOutput = null;

        switch (state) {
            case WAITING:
                theOutput = "Knock! Knock!";
                state = SENT_KNOCK_KNOCK;
                break;

            case SENT_KNOCK_KNOCK:
                if ("Who's there?".equalsIgnoreCase(theInput)) {
                    theOutput = clues[currentJoke];
                    state = SENT_CLUE;
                } else {
                    theOutput = "You're supposed to say \"Who's there?\"! Try again. Knock! Knock!";
                }
                break;

            case SENT_CLUE:
                if ((clues[currentJoke] + " who?").equalsIgnoreCase(theInput)) {
                    theOutput = answers[currentJoke] + " Want another? (y/n)";
                    state = ANOTHER;
                } else {
                    theOutput = "You're supposed to say \"" + clues[currentJoke] + " who?\"! Try again. Knock! Knock!";
                    state = SENT_KNOCK_KNOCK;
                }
                break;

            case ANOTHER:
                if ("y".equalsIgnoreCase(theInput)) {
                    theOutput = "Knock! Knock!";
                    currentJoke = (currentJoke + 1) % NUM_JOKES;
                    state = SENT_KNOCK_KNOCK;
                } else {
                    theOutput = "Bye.";
                    state = WAITING;
                }
                break;
        }
        return theOutput;
    }
}