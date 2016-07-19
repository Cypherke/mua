package be.cypherke.mua.messages.chat;

import be.cypherke.mua.Mua;
import be.cypherke.mua.gsonobjects.Coordinate;
import be.cypherke.mua.gsonobjects.Teleport;
import be.cypherke.mua.gsonobjects.User;
import org.joda.time.DateTime;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TeleportMessage extends ChatMessageBase {

    private boolean coordTriggered;

    public TeleportMessage(Mua mua) {
        super(mua);
    }

    @Override
    public boolean handleChat(String chat, String player) throws IOException {
        if (chat.equalsIgnoreCase("!sc")) {
            getMua().getOutput().sendGetCoordinates(player);
            coordTriggered = true;

            return true;
        }
        if (chat.startsWith("!tp")) {
            String[] params = chat.split(" ");
            if (params.length == 1) {
                getMua().getOutput().sendMessage("@a", "Usage !tp add/delete/username/teleportname/list");
                return true;
            }
            if (params.length == 2) {
                if (params[1].equalsIgnoreCase("add")) {
                    getMua().getOutput().sendMessage("@a", "Usage !tp add name_for_tele");
                    return true;
                }
                if (params[1].equalsIgnoreCase("delete")) {
                    getMua().getOutput().sendMessage("@a", "Usage !tp delete name_for_tele");
                    return true;
                }
                if (params[1].equalsIgnoreCase("list")) {
                    String tps = getMua().getTeleportsDb().getUserTps(player);
                    if (tps != null) {
                        getMua().getOutput().sendMessage(player, "Your teleports are: " + tps);
                    } else {
                        getMua().getOutput().sendMessage(player, "You don't have any teleports yet, use !tp add to set");
                    }
                    return true;
                }
                if (getMua().getTeleportsDb().getUserTps(player) != null && getMua().getTeleportsDb().getUserTps(player).contains(params[1])) {
                    getMua().getOutput().sendTeleport(player, getMua().getTeleportsDb().getTp(player, params[1]).getCoordinate());
                    return true;
                }
                if (getMua().getUsersDb().getUserNames() != null && getMua().getUsersDb().getUserNames().contains(params[1]) && getMua().getUsersDb().getUser(params[1]).isOnline()) {
                    getMua().getOutput().sendTeleport(player, params[1]);
                    return true;
                }
                getMua().getOutput().sendMessage(player, "Usage !tp add/username/teleportname/list");

                return true;
            }
            if (params.length == 3) {
                if (params[1].equalsIgnoreCase("add")) {
                    getMua().getOutput().sendGetCoordinates(player);
                    getMua().getTeleportsDb().add(new Teleport(params[2], player, DateTime.now().toString(), getMua().getUsersDb().getUser(player).getCoordinate()));
                    getMua().getOutput().sendMessage(player, "The teleport has been added.");

                    return true;
                }
                if (params[1].equalsIgnoreCase("delete")) {
                    if (getMua().getTeleportsDb().getUserTps(player).contains(params[2])) {
                        getMua().getTeleportsDb().removeTeleport(player, params[2]);
                        getMua().getOutput().sendMessage(player, "The teleport has been removed.");
                    } else {
                        getMua().getOutput().sendMessage(player, "This teleport doesn't exist, use !tp list to check");
                    }

                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean handle(String function, String message) throws IOException {
        String pattern = "Teleported (?<player>[\\w]*) to (?<x>\\-?[0-9\\.]*), (?<y>\\-?[0-9\\.]*), (?<z>\\-?[0-9\\.]*)";
        Matcher m = Pattern.compile(pattern).matcher(message);
        if (m.matches()) {
            User u = getMua().getUsersDb().getUser(m.group("player"));
            u.setCoordinate(new Coordinate(Double.valueOf(m.group("x")), Double.valueOf(m.group("y")), Double.valueOf(m.group("z"))));
            if (coordTriggered) {
                getMua().getOutput().sendMessage("@a", "coordinates of " + u.getUsername() + ": " + u.getCoordinate().toString());
                coordTriggered = false;
            }

            return true;
        }

        return false;
    }
}