package MasterMind;

import MasterMind.client.BotClient;
import MasterMind.client.ChatClient;
import MasterMind.server.ServerMain;

public class ConnectionHandler {

    static ServerThread serverThread;
    static ServerMain server;

    static ClientThread clientThread;
    static ChatClient client;

    static BotThread botThread;
    static BotClient bot;

    public static boolean serverReady = false;

    public static boolean clientAttemptingToConnect = false;
    public static boolean clientConnected = false;

    public static boolean botAttemptingToConnect = false;
    public static boolean botConnected = false;

    public static void startServer() {
        serverThread = new ServerThread("server");
        serverThread.start();
    }

    private static class ServerThread implements Runnable {
        private Thread t;
        private String threadName;

        ServerThread(String name) {
            threadName = name;
        }

        public void run() {
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
        if (!clientAttemptingToConnect) {
            clientAttemptingToConnect = true;
            clientThread = new ClientThread("client", ip);
            clientThread.start();
        }
    }

    private static class ClientThread implements Runnable {
        private Thread t;
        private String threadName;
        private String ip;

        ClientThread(String name, String ip) {
            this.threadName = name;
            this.ip = ip;
        }

        public void run() {
            client = new ChatClient(this.ip);
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

    public static void startBotIfNull(String ip) {
        if (!botAttemptingToConnect) {
            botAttemptingToConnect = true;
            botThread = new BotThread("bot", ip);
            botThread.start();
        }
    }

    private static class BotThread implements Runnable {
        private Thread t;
        private String threadName;
        private String ip;

        BotThread(String name, String ip) {
            this.threadName = name;
            this.ip = ip;
        }

        public void run() {
            bot = new BotClient(this.ip);
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
}