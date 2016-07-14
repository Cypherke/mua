package be.cypherke.mua;


import be.cypherke.mua.gsonobjects.Coordinate;
import be.cypherke.mua.gsonobjects.Teleport;
import be.cypherke.mua.gsonobjects.User;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class MessageHandler {

    private final Mua mua;
    // needed so it only shows time message if user requests it, not when admin requests it from console
    private boolean timeTriggered = false;
    private boolean coordTriggered = false;

    MessageHandler(Mua mua) {
        this.mua = mua;
    }

    void dispatch(String line) throws IOException {
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
                    User u = mua.getUsersDb().getUser(m.group("player"));
                    if (u != null) {
                        u.setUuid(m.group("uuid"));
                    } else {
                        u = new User(m.group("player"));
                        u.setUuid(m.group("uuid"));
                        mua.getUsersDb().addUser(u);
                    }
                }
            } else {
                pattern = "(?<player>[\\w]*)\\[/(?<ip>[0-9\\.]*):[0-9]*\\] logged in with entity id (?<entity>[0-9]*) at \\((?<x>\\-?[0-9\\.]*), (?<y>\\-?[0-9\\.]*), (?<z>\\-?[0-9\\.]*)\\)";
                m = Pattern.compile(pattern).matcher(message);
                // match logon message
                if (m.matches()) {
                    User u = mua.getUsersDb().getUser(m.group("player"));
                    u.setIp(m.group("ip"));
                    u.setEntity(m.group("entity"));
                    u.setCoordinate(new Coordinate(Double.valueOf(m.group("x")), Double.valueOf(m.group("y")), Double.valueOf(m.group("z"))));
                }
                pattern = "(?<player>[\\w]*) left the game";
                m = Pattern.compile(pattern).matcher(message);
                // match logoff message
                if (m.matches()) {
                    User u = mua.getUsersDb().getUser(m.group("player"));
                    u.setLastseen(DateTime.now().toString());
                }
                pattern = "<(?<player>[\\w]*)> (?<chat>.*)";
                m = Pattern.compile(pattern).matcher(message);
                // match chat message
                if (m.matches()) {
                    if (m.group("chat").equalsIgnoreCase("!help")) {
                        mua.getWriter().write("tellraw @a {\"text\": \"[Server] \", \"color\": \"dark_red\", \"extra\": [{\"text\": \"Available commands: !sc !time !tp\", \"color\": \"dark_green\"}]}\n");
                        mua.getWriter().flush();
                    }
                    if (m.group("chat").equalsIgnoreCase("!time")) {
                        mua.getWriter().write("time query daytime\n");
                        mua.getWriter().flush();
                        timeTriggered = true;
                    }
                    if (m.group("chat").equalsIgnoreCase("!sc")) {
                        mua.getWriter().write("tp " + m.group("player") + " ~ ~ ~\n");
                        mua.getWriter().flush();
                        coordTriggered = true;
                    }
                    if (m.group("chat").startsWith("!tp")) {
                        String[] params = m.group("chat").split(" ");
                        if (params.length == 1) {
                            mua.getWriter().write("tellraw @a {\"text\": \"[Server] \", \"color\": \"dark_red\", \"extra\": [{\"text\": \"Usage !tp add/username/teleportname/list\", \"color\": \"dark_green\"}]}\n");
                            mua.getWriter().flush();
                        }
                        if (params.length == 2) {
                            if (params[1].equalsIgnoreCase("add")) {
                                mua.getWriter().write("tellraw @a {\"text\": \"[Server] \", \"color\": \"dark_red\", \"extra\": [{\"text\": \"Usage !tp register name_for_tele\", \"color\": \"dark_green\"}]}\n");
                                mua.getWriter().flush();
                                return;
                            }
                            if (params[1].equalsIgnoreCase("list")) {
                                String tps = mua.getTeleportsDb().getUserTps(m.group("player"));
                                if (tps != null) {
                                    mua.getWriter().write("tellraw @a {\"text\": \"[Server] \", \"color\": \"dark_red\", \"extra\": [{\"text\": \"Your teleports are: " + tps + "\", \"color\": \"dark_green\"}]}\n");
                                    mua.getWriter().flush();
                                } else {
                                    mua.getWriter().write("tellraw @a {\"text\": \"[Server] \", \"color\": \"dark_red\", \"extra\": [{\"text\": \"You dont have any teleports yet, use !tp add to set\", \"color\": \"dark_green\"}]}\n");
                                    mua.getWriter().flush();
                                }
                                return;
                            }
                            if (mua.getTeleportsDb().getUserTps(m.group("player")) != null && mua.getTeleportsDb().getUserTps(m.group("player")).contains(params[1])) {
                                mua.getWriter().write("tp " + m.group("player") + " " + mua.getTeleportsDb().getTp(params[1]).getCoordinate().toString() + "\n");
                                mua.getWriter().flush();
                                return;
                            }
                            if (mua.getUsersDb().getUserNames() != null && mua.getUsersDb().getUserNames().contains(params[1])) {
                                mua.getWriter().write("tp " + params[1] + " ~ ~ ~\n");
                                mua.getWriter().flush();
                                mua.getWriter().write("tp " + m.group("player") + " " + mua.getUsersDb().getUser(params[1]).getCoordinate().toString() + "\n");
                                mua.getWriter().flush();
                                return;
                            }
                            mua.getWriter().write("tellraw @a {\"text\": \"[Server] \", \"color\": \"dark_red\", \"extra\": [{\"text\": \"Usage !tp add/username/teleportname/list\", \"color\": \"dark_green\"}]}\n");
                            mua.getWriter().flush();
                            return;
                        }
                        if (params.length == 3) {
                            if (params[1].equalsIgnoreCase("register")) {
                                mua.getWriter().write("tp " + m.group("player") + " ~ ~ ~\n");
                                mua.getWriter().flush();
                                mua.getTeleportsDb().add(new Teleport(params[2], m.group("player"), DateTime.now().toString(), mua.getUsersDb().getUser(m.group("player")).getCoordinate()));
                            }
                        }
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
                    mua.getWriter().write("tellraw @a {\"text\": \"[Server] \", \"color\": \"dark_red\", \"extra\": [{\"text\": \"Time is " + dateTime.toString(fmt) + " \", \"color\": \"dark_green\"}]}\n");
                    mua.getWriter().flush();
                    timeTriggered = false;
                }
                //match teleport message
                pattern = "Teleported (?<player>[\\w]*) to (?<x>\\-?[0-9\\.]*), (?<y>\\-?[0-9\\.]*), (?<z>\\-?[0-9\\.]*)";
                m = Pattern.compile(pattern).matcher(message);
                if (m.matches()) {
                    User u = mua.getUsersDb().getUser(m.group("player"));
                    u.setCoordinate(new Coordinate(Double.valueOf(m.group("x")), Double.valueOf(m.group("y")), Double.valueOf(m.group("z"))));
                    if (coordTriggered) {
                        mua.getWriter().write("tellraw @a {\"text\": \"[Server] \", \"color\": \"dark_red\", \"extra\": [{\"text\": \"coordinates of " + u.getUsername() + ": " + u.getCoordinate().toString() + " \", \"color\": \"dark_green\"}]}\n");
                        mua.getWriter().flush();
                        coordTriggered = false;
                    }
                }
            }
        }
    }
}
