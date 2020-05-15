package MasterMind;

import MasterMind.client.ChatClient;
import MasterMind.server.ServerMain;

public class ConnectionHandler {

    static ServerThread serverThread;
    static ServerMain server;
    static ChatClient client;

    public static boolean serverReady = false;

    public static boolean clientConnected = false;

    public static void startServer() {
        serverThread = new ServerThread("server thread");
        serverThread.start();
    }

    private static class ServerThread implements Runnable {
        private Thread t;
        private String threadName;

        ServerThread(String name) {
            threadName = name;
            System.out.println("Creating " + threadName);
        }

        public void run() {
            System.out.println("Running " + threadName);
            server = new ServerMain();
            System.out.println("Thread " + threadName + " exiting.");
        }

        public void start() {
            System.out.println("Starting " + threadName);
            if (t == null) {
                t = new Thread(this, threadName);
                t.start();
            }
        }
    }

    public static void startClientIfNull(String ip) {
        client = new ChatClient(ip);
    }
}