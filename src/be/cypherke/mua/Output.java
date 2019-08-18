package be.cypherke.mua;

import be.cypherke.mua.gsonobjects.Coordinate;

import java.io.BufferedWriter;
import java.io.IOException;


public class Output {
    private final BufferedWriter writer;

    Output(BufferedWriter writer) {
        this.writer = writer;
    }

    public void sendCommand(String command) throws IOException {
        writer.write(command + "\n");
        writer.flush();
    }

    public void sendTeleport(String who, Coordinate coordinate) throws IOException {
        writer.write("execute as " + who + " run teleport " + who + " " + coordinate.getX() + " " + coordinate.getY() + " " + coordinate.getZ() + "\n");
        writer.flush();
    }

    public void sendTeleport(String who, String where) throws IOException {
        writer.write("execute as " + who + " run teleport " + who + " " + where + "\n");
        writer.flush();
    }

    public void sendGetCoordinates(String who) throws IOException {
        writer.write("execute as " + who + " run teleport " + who + " ~ ~ ~\n");
        writer.flush();
    }

    /**
     * Sends a message to a user.
     *
     * @param who  the user to send it to
     * @param what the message to send
     * @throws IOException exception for BufferedWriter
     */
    public void sendMessage(String who, String what) throws IOException {
        writer.write("tellraw " + who + " {\"text\": \"[Server] \", \"color\": \"dark_red\", \"extra\": [{\"text\": \""
            + what + "\", \"color\": \"dark_green\"}]}\n");
        writer.flush();
    }

    /**
     * Sends the motd to the server.
     *
     * @param username the user that joined
     * @throws IOException exception from BufferedWriter
     */
    public void sendMotd(String username) throws IOException {
        sendMessage(username, "Hey " + username + ", welcome to the server!");
        sendMessage(username, "Some Guidelines: ");
        sendMessage(username, "Caving: always place signs at the entry of a dead end, only place torches on the left wall when entering, "
            + "so you can follow the torches to the exit on your right");
    }
}
