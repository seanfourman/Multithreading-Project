public class Main {
    public static void main(String[] args) {
        int protocolFlag;

        if (args.length > 0) {
            if ("1".equals(args[0]) || "2".equals(args[0])) {
                protocolFlag = Integer.parseInt(args[0]);
            } else {
                System.err.println("[SERVER] Invalid Protocol. Please provide 1 or 2 as an argument.");
                System.exit(1);
                return;
            }
        } else {
            System.err.println("[SERVER] No protocol specified. Please provide 1 or 2 as an argument.");
            System.exit(1);
            return;
        }

        if (protocolFlag == 1) {
            KnockKnockServer server = new KnockKnockServer();
            server.startServer(1);
        } else if (protocolFlag == 2) {
            RuppinServer server = new RuppinServer();
            server.startServer(2);
        }
    }
}