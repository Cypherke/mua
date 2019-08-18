package be.cypherke.mua.messages.chat;

import be.cypherke.mua.Mua;
import be.cypherke.mua.gsonobjects.Coordinate;
import be.cypherke.mua.gsonobjects.Teleport;
import be.cypherke.mua.gsonobjects.User;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;

public class TeleportMessage extends ChatMessageBase {

    private boolean coordTriggered;
    private Map<String, AddRequest> teleportAddTriggered = new HashMap<>();

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
                if (getMua().getTeleportsDb().getUserTps(player) != null && getMua().getTeleportsDb().getTp(player, params[1]) != null) {
                    getMua().getOutput().sendTeleport(player, getMua().getTeleportsDb().getTp(player, params[1]).getCoordinate());
                    return true;
                }

                // Can we teleport to another valid online user?
                if (
                    getMua().getUsersDb().getUserNames() != null
                    && getMua().getUsersDb().getUserNames().contains(params[1])
                    && getMua().getUsersDb().getUser(params[1]) != null
                    && getMua().getUsersDb().getUser(params[1]).isOnline()
                ) {
                    getMua().getOutput().sendTeleport(player, params[1]);
                    return true;
                }
                getMua().getOutput().sendMessage(player, "Usage !tp add/username/teleportname/list");

                return true;
            }

            if (params.length == 3) {
                if (params[1].equalsIgnoreCase("add")) {
                    String locationName = params[2];
                    AddRequest request = new AddRequest(true, locationName);

                    teleportAddTriggered.put(player, request);

                    getMua().getOutput().sendGetCoordinates(player);

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
        String pattern = "Teleported (?<player>[a-zA-Z0-9§]*) to (?<x>\\-?[0-9\\.]*), (?<y>\\-?[0-9\\.]*), (?<z>\\-?[0-9\\.]*)";
        Matcher m = Pattern.compile(pattern).matcher(message);

        if (m.matches()) {
            String player = m.group("player");

            // Player is in a team with colours: "§9Nickname§r joined the game"
            if (player.length() > 0 && player.charAt(0) == '\u00a7' && player.charAt(player.length() - 2) == '\u00a7') {
                player = player.substring(2, player.length() - 2);
            }

            User u = getMua().getUsersDb().getUser(player);

            u.setCoordinate(new Coordinate(Double.valueOf(m.group("x")), Double.valueOf(m.group("y")), Double.valueOf(m.group("z"))));

            if (coordTriggered) {
                getMua().getOutput().sendMessage("@a", "coordinates of " + u.getUsername() + ": " + u.getCoordinate().toString());
                coordTriggered = false;
            }

            if (teleportAddTriggered.containsKey(player)) {
                AddRequest request = teleportAddTriggered.get(player);
                if (request.getRequestedAdd()) {
                    String locationName = request.getLocationName();
                    if (locationName != null) {
                        getMua().getTeleportsDb().add(new Teleport(locationName, player, DateTime.now().toString(), getMua().getUsersDb().getUser(player).getCoordinate()));
                        getMua().getOutput().sendMessage(player, "The teleport has been added.");

                        AddRequest emptyRequest = new AddRequest(false, null);
                        teleportAddTriggered.put(player, emptyRequest);
                    }
                }
            }

            return true;
        }

        return false;
    }

    private class AddRequest {
        private boolean requestedAdd;
        private String locationName;

        AddRequest(boolean requestedAdd, String locationName) {
            this.requestedAdd = requestedAdd;
            this.locationName = locationName;
        }

        boolean getRequestedAdd() {
            return requestedAdd;
        }

        String getLocationName() {
            return locationName;
        }
    }
}
