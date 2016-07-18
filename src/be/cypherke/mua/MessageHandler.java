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
            if (function.matches("User Authenticator #[0-9]*/INFO")) {
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
                    u.setOnline(true);
                    mua.getOutput().sendMotd(u.getUsername());
                }
                pattern = "(?<player>[\\w]*) left the game";
                m = Pattern.compile(pattern).matcher(message);
                // match logoff message
                if (m.matches()) {
                    User u = mua.getUsersDb().getUser(m.group("player"));
                    u.setLastseen(DateTime.now().toString());
                    u.setOnline(false);
                }
                //count number of deaths
                String [] deathMessages = {"was slain by Zombie", "was blown up by Creeper","was slain by Enderman","tried to swim in lava", "hit the ground too hard", "was shot by Skeleton"};
                for ( String deathMessage : deathMessages) {
                    if (message.contains(deathMessage)) {
                        String user = message.split(" ")[0];
                        mua.getUsersDb().getUser(user).addDeath();
                        mua.getOutput().sendMessage("@a", "Congrats " + message.split(" ")[0] + ", this brings your total death count to: " + mua.getUsersDb().getUser(user).getNumberOfDeaths());
                    }
                }
                if (message.contains(""))
                pattern = "<(?<player>[\\w]*)> (?<chat>.*)";
                m = Pattern.compile(pattern).matcher(message);
                // match chat message
                if (m.matches()) {
                    if (m.group("chat").equalsIgnoreCase("!help")) {
                        mua.getOutput().sendMessage("@a", "Available commands: !sc !time !tp");
                    }
                    if (m.group("chat").equalsIgnoreCase("!time")) {
                        mua.getOutput().sendCommand("time query daytime");
                        timeTriggered = true;
                    }
                    if (m.group("chat").equalsIgnoreCase("!sc")) {
                        mua.getOutput().sendGetCoordinates(m.group("player"));
                        coordTriggered = true;
                    }
                    if (m.group("chat").startsWith("!tp")) {
                        String[] params = m.group("chat").split(" ");
                        if (params.length == 1) {
                            mua.getOutput().sendMessage("@a", "Usage !tp add/delete/username/teleportname/list");
                            return;
                        }
                        if (params.length == 2) {
                            if (params[1].equalsIgnoreCase("add")) {
                                mua.getOutput().sendMessage("@a","Usage !tp add name_for_tele");
                                return;
                            }
                            if (params[1].equalsIgnoreCase("delete")) {
                                mua.getOutput().sendMessage("@a","Usage !tp delete name_for_tele");
                                return;
                            }
                            if (params[1].equalsIgnoreCase("list")) {
                                String tps = mua.getTeleportsDb().getUserTps(m.group("player"));
                                if (tps != null) {
                                    mua.getOutput().sendMessage(m.group("player"), "Your teleports are: " + tps);
                                } else {
                                    mua.getOutput().sendMessage(m.group("player"), "You don't have any teleports yet, use !tp add to set");
                                }
                                return;
                            }
                            if (mua.getTeleportsDb().getUserTps(m.group("player")) != null && mua.getTeleportsDb().getUserTps(m.group("player")).contains(params[1])) {
                                mua.getOutput().sendTeleport(m.group("player"),mua.getTeleportsDb().getTp(params[1]).getCoordinate());
                                return;
                            }
                            if (mua.getUsersDb().getUserNames() != null && mua.getUsersDb().getUserNames().contains(params[1]) && mua.getUsersDb().getUser(params[1]).isOnline()) {
                                mua.getOutput().sendTeleport(m.group("player"), params[1]);
                                return;
                            }
                            mua.getOutput().sendMessage(m.group("player"), "Usage !tp add/username/teleportname/list");
                            return;
                        }
                        if (params.length == 3) {
                            if (params[1].equalsIgnoreCase("add")) {
                                mua.getOutput().sendGetCoordinates(m.group("player"));
                                mua.getTeleportsDb().add(new Teleport(params[2], m.group("player"), DateTime.now().toString(), mua.getUsersDb().getUser(m.group("player")).getCoordinate()));
                            }
                            if (params[1].equalsIgnoreCase("delete")) {
                                if (mua.getTeleportsDb().getUserTps(m.group("player")).contains(params[2])) {
                                    mua.getTeleportsDb().removeTeleport(params[2]);
                                    mua.getOutput().sendMessage(m.group("player"),"The teleport has been removed.");
                                }
                                else {
                                    mua.getOutput().sendMessage(m.group("player"), "This teleport doesn't exist, use !tp list to check");
                                }
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
                    mua.getOutput().sendMessage("@a", "Time is " + dateTime.toString(fmt));
                    timeTriggered = false;
                }
                //match teleport message
                pattern = "Teleported (?<player>[\\w]*) to (?<x>\\-?[0-9\\.]*), (?<y>\\-?[0-9\\.]*), (?<z>\\-?[0-9\\.]*)";
                m = Pattern.compile(pattern).matcher(message);
                if (m.matches()) {
                    User u = mua.getUsersDb().getUser(m.group("player"));
                    u.setCoordinate(new Coordinate(Double.valueOf(m.group("x")), Double.valueOf(m.group("y")), Double.valueOf(m.group("z"))));
                    if (coordTriggered) {
                        mua.getOutput().sendMessage("@a", "coordinates of " + u.getUsername() + ": " + u.getCoordinate().toString());
                        coordTriggered = false;
                    }
                }
            }
        }
    }
}
