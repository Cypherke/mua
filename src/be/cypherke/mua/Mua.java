package be.cypherke.mua;

import be.cypherke.mua.db.TeleportsDb;
import be.cypherke.mua.db.UsersDb;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Scanner;

public class Mua {

    private UsersDb usersDb;
    private Output output;
    private TeleportsDb teleportsDb;

    private Mua() throws IOException {
        Config config = new Config("mua", "xml");
        usersDb = new UsersDb(config.getString("mua_usersfile"));
        teleportsDb = new TeleportsDb(config.getString("mua_teleportsfile"));
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

    UsersDb getUsersDb() {
        return this.usersDb;
    }

    TeleportsDb getTeleportsDb() {
        return this.teleportsDb;
    }

    Output getOutput() {
        return output;
    }
}
