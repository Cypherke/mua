package be.cypherke.mua;

import be.cypherke.mua.gsonobjects.Coordinate;

import java.io.BufferedWriter;
import java.io.IOException;


class Output {
    private final BufferedWriter writer;

    Output(BufferedWriter writer) {
        this.writer = writer;
    }

    void sendCommand(String command) throws IOException {
        writer.write(command + "\n");
        writer.flush();
    }

    void sendTeleport(String who, Coordinate coordinate) throws IOException {
        writer.write("tp " + who + " " + coordinate.getX() + " " + coordinate.getY() + " " + coordinate.getZ() + "\n");
        writer.flush();
    }

    void sendTeleport(String who, String where) throws IOException {
        writer.write("tp " + who + " " + where + "\n");
        writer.flush();
    }

    void sendGetCoordinates(String who) throws IOException {
        writer.write("tp " + who + " ~ ~ ~\n");
        writer.flush();
    }

    void sendMessage(String who, String what) throws IOException {
        writer.write("tellraw " + who + " {\"text\": \"[Server] \", \"color\": \"dark_red\", \"extra\": [{\"text\": \"" + what + "\", \"color\": \"dark_green\"}]}\n");
        writer.flush();
    }

    void sendMotd(String username) throws IOException {
        sendMessage(username, "Hey " + username + ", welcome to the server!");
        sendMessage(username, "Some Guidelines: ");
        sendMessage(username, "Caving: always place signs at the entry of a dead end, only place torches on the left wall when entering, so you can follow the torches to the exit on your right");
    }
}
