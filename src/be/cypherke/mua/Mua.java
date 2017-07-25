package be.cypherke.mua;

import be.cypherke.mua.db.TeleportsDb;
import be.cypherke.mua.db.UsersDb;
import be.cypherke.mua.io.IRCListener;
import be.cypherke.mua.io.LocalFileManager;

import java.io.*;
import java.util.Scanner;

import org.apache.commons.io.input.Tailer;

public class Mua {

    private UsersDb usersDb;
    private Output output;
    private TeleportsDb teleportsDb;
    private Scheduler scheduler;

    private PrintWriter ircwriter;
    private boolean isIRCActive = false;
    private boolean ircBridgeActive = false;

    private Mua() throws IOException {
        Config config = new Config("mua", "xml");
        usersDb = new UsersDb(new LocalFileManager(config.getString("mua_usersfile")));
        teleportsDb = new TeleportsDb(new LocalFileManager(config.getString("mua_teleportsfile")));
        scheduler = new Scheduler(this);
        MessageHandler messageHandler = new MessageHandler(this);

        ProcessBuilder builder = new ProcessBuilder("java", "-Xmx2048M", "-Xms2048M", "-d64", "-XX:+UseConcMarkSweepGC", "-XX:+UseParNewGC", "-XX:+CMSIncrementalPacing", "-XX:ParallelGCThreads=2", "-XX:+AggressiveOpts", "-jar", config.getString("server_jar"), "nogui");
        builder.directory(new File(config.getString("server_workdir")));

        Process process = builder.start();

        Scanner scan = new Scanner(System.in);

        OutputStream stdout = process.getOutputStream();
        InputStream stderr = process.getErrorStream();
        InputStream stdin = process.getInputStream();

        BufferedReader reader = new BufferedReader(new InputStreamReader(stdin));
        BufferedReader error = new BufferedReader(new InputStreamReader(stderr));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stdout));

        output = new Output(writer);

        if (!config.getString("irc_channel_file").equals("none")) {
            isIRCActive = true;

            // Open writer to (ii irc bot) pipe
            ircwriter = new PrintWriter(new BufferedOutputStream(new FileOutputStream(config.getString("irc_channel_file") + "/in", true)));

            // Open reader from (ii irc bot) output file
            IRCListener ircListener = new IRCListener();
            ircListener.setMua(this);
            File ircOutput = new File(config.getString("irc_channel_file") + "/out");
            Tailer ircTailer = new Tailer(ircOutput, ircListener, 500, true);

            Thread ircReaderThread = new Thread(ircTailer);
            ircReaderThread.start();
        }

        Thread inputThread = new Thread(() -> {
            while (true) {
                String input = scan.nextLine();
                input += "\n";
                try {
                    writer.write(input);
                    writer.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (input.equalsIgnoreCase("stop\n")) {
                    usersDb.save();
                    teleportsDb.save();
                    try {
                        process.waitFor();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.exit(0);
                }
            }
        });
        inputThread.start();

        Thread errorThread = new Thread(() -> {
            String line = null;
            try {
                line = error.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (line != null) {
                System.out.println("Error: " + line);
                try {
                    line = reader.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        errorThread.start();

        String line = reader.readLine();
        while (line != null && !line.trim().equals("stop")) {
            System.out.println(line);
            messageHandler.dispatch(line);
            line = reader.readLine();
        }
    }

    public static void main(final String[] args) {
        try {
            new Mua();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public UsersDb getUsersDb() {
        return this.usersDb;
    }

    public TeleportsDb getTeleportsDb() {
        return this.teleportsDb;
    }

    public Output getOutput() {
        return output;
    }

    public void printToIRC(String msg) {
        if (!isIRCActive) return;

        ircwriter.println("[MineCraft Server] " + msg);
        ircwriter.flush();
    }

    public boolean isIrcBridgeActive() {
        return this.ircBridgeActive;
    }

    public void setIrcBridgeActive(boolean active) {
        this.ircBridgeActive = active;
    }
}
