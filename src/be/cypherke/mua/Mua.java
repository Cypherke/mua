package be.cypherke.mua;

import be.cypherke.mua.db.UsersDb;
import be.cypherke.mua.gsonobjects.Coordinate;
import be.cypherke.mua.gsonobjects.User;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Mua {

    private Mua() throws IOException {
        Config config = new Config("mua", "xml");
        UsersDb usersDb = new UsersDb(config.getString("mua_usersfile"));

        // needed so it only shows time message if user requests it, not when admin requests it from console
        boolean timeTriggered = false;
        boolean coordTriggered = false;

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
            String pattern = "\\[(?<timestamp>[0-9\\:]*)\\]\\s\\[(?<function>[\\w\\s/\\#]*)\\]:\\s(?<message>.*)";
            Matcher m = Pattern.compile(pattern).matcher(line);
            // just the default matching
            if (m.matches()) {
                String timestamp = m.group("timestamp");
                String function = m.group("function");
                String message = m.group("message");
                // match authenticator message
                if (function.equalsIgnoreCase("User Authenticator #1/INFO")) {
                    pattern = "UUID of player (?<player>[\\w]*) is (?<uuid>[a-z0-9\\-]*)";
                    m = Pattern.compile(pattern).matcher(message);
                    if (m.matches()) {
                        User u = usersDb.getUser(m.group("player"));
                        if (u != null) {
                            u.setUuid(m.group("uuid"));
                        } else {
                            u = new User(m.group("player"));
                            u.setUuid(m.group("uuid"));
                            usersDb.addUser(u);
                        }
                    }
                } else {
                    pattern = "(?<player>[\\w]*)\\[/(?<ip>[0-9\\.]*):[0-9]*\\] logged in with entity id (?<entity>[0-9]*) at \\((?<x>\\-?[0-9\\.]*), (?<y>\\-?[0-9\\.]*), (?<z>\\-?[0-9\\.]*)\\)";
                    m = Pattern.compile(pattern).matcher(message);
                    // match logon message
                    if (m.matches()) {
                        User u = usersDb.getUser(m.group("player"));
                        u.setIp(m.group("ip"));
                        u.setEntity(m.group("entity"));
                        u.setCoordinate(new Coordinate(Double.valueOf(m.group("x")), Double.valueOf(m.group("y")), Double.valueOf(m.group("z"))));
                    }
                    pattern = "(?<player>[\\w]*) left the game";
                    m = Pattern.compile(pattern).matcher(message);
                    // match logoff message
                    if (m.matches()) {
                        User u = usersDb.getUser(m.group("player"));
                        u.setLastseen(DateTime.now().toString());
                    }
                    pattern = "<(?<player>[\\w]*)> (?<chat>.*)";
                    m = Pattern.compile(pattern).matcher(message);
                    // match chat message
                    if (m.matches()) {
                        if (m.group("chat").equalsIgnoreCase("!help")) {
                            writer.write("tellraw @a {\"text\": \"[Server] \", \"color\": \"dark_red\", \"extra\": [{\"text\": \"Available commands: !sc !time\", \"color\": \"dark_green\"}]}\n");
                            writer.flush();
                        }
                        if (m.group("chat").equalsIgnoreCase("!time")) {
                            writer.write("time query daytime\n");
                            writer.flush();
                            timeTriggered = true;
                        }
                        if (m.group("chat").equalsIgnoreCase("!sc")) {
                            writer.write("tp " + m.group("player") + " ~ ~ ~\n");
                            writer.flush();
                            coordTriggered = true;
                        }
                    }
                    //match time message
                    pattern = "Time is (?<time>[0-9]+)";
                    m = Pattern.compile(pattern).matcher(message);
                    if (m.matches() && timeTriggered) {
                        Integer time = Integer.valueOf(m.group("time"));
                        if (time > 23999) {
                            time = time % 24000;
                        }
                        DateTime dateTime = new DateTime(2016, 1, 1, 6, 0, 0);
                        dateTime = dateTime.plusSeconds(Math.toIntExact((long) (time * 3.6)));
                        DateTimeFormatter fmt = DateTimeFormat.forPattern("HH:mm:ss");
                        writer.write("tellraw @a {\"text\": \"[Server] \", \"color\": \"dark_red\", \"extra\": [{\"text\": \"Time is " + dateTime.toString(fmt) + " \", \"color\": \"dark_green\"}]}\n");
                        writer.flush();
                        timeTriggered = false;
                    }
                    //match teleport message
                    pattern = "Teleported (?<player>[\\w]*) to (?<x>\\-?[0-9\\.]*), (?<y>\\-?[0-9\\.]*), (?<z>\\-?[0-9\\.]*)";
                    m = Pattern.compile(pattern).matcher(message);
                    if (m.matches() && coordTriggered) {
                        User u = usersDb.getUser(m.group("player"));
                        u.setCoordinate(new Coordinate(Double.valueOf(m.group("x")), Double.valueOf(m.group("y")), Double.valueOf(m.group("z"))));
                        writer.write("tellraw @a {\"text\": \"[Server] \", \"color\": \"dark_red\", \"extra\": [{\"text\": \"coordinates of " + u.getUsername() + ": " + u.getCoordinate().toString() + " \", \"color\": \"dark_green\"}]}\n");
                        writer.flush();
                        coordTriggered = false;
                    }

                }
            }
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
}
