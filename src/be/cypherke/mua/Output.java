package be.cypherke.mua;

import be.cypherke.mua.gsonobjects.Coordinate;

import java.io.BufferedWriter;
import java.io.IOException;


public class Output {
    private final BufferedWriter writer;

    public Output(BufferedWriter writer) {
        this.writer = writer;
    }

    public void sendCommand(String command) throws IOException {
        writer.write(command + "\n");
        writer.flush();
    }

    public void sendTeleport(String who, Coordinate coordinate) throws IOException {
        writer.write("tp " + who + " " + coordinate.toString() + "\n");
        writer.flush();
    }

    public void sendTeleport(String who, String where) throws IOException {
        writer.write("tp " + who + " " + where + "\n");
        writer.flush();
    }

    public void sendGetCoordinates(String who) throws IOException {
        writer.write("tp " + who + " ~ ~ ~\n");
        writer.flush();
    }

    public void sendMessage(String who, String what) throws IOException {
        writer.write("tellraw " + who + " {\"text\": \"[Server] \", \"color\": \"dark_red\", \"extra\": [{\"text\": \"" + what + "\", \"color\": \"dark_green\"}]}\n");
        writer.flush();
    }
}
