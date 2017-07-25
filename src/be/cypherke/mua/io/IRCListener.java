package be.cypherke.mua.io;

import be.cypherke.mua.Mua;
import org.apache.commons.io.input.TailerListenerAdapter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IRCListener extends TailerListenerAdapter {
    private Mua mua;

    public void handle(String line) {
        String pattern = "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2} <(?<nickname>.*)> !mc (?<command>.*)";
        Matcher m = Pattern.compile(pattern).matcher(line);

        if (m.matches()) {
            String nickname = m.group("nickname");
            String[] commands = m.group("command").split(" ");

            switch (commands[0]) {
                case "msg":
                    String msg = "";
                    for (int i = 1; i < commands.length; i++) {
                        msg += commands[i] + " ";
                    }

                    try {
                        this.mua.getOutput().sendMessage("@a", "[IRC] <" + nickname + "> " + msg);
                    } catch (Exception ex) {
                        // ...
                    }

                    break;

                case "chatbridge":
                    if (commands[1].equals("on")) {
                        this.mua.setIrcBridgeActive(true);
                    } else {
                        this.mua.setIrcBridgeActive(false);
                    }

                    break;

                default:
                    System.out.println("[IRC] User " + nickname + " issued command: " + m.group("command"));

                    this.mua.printToIRC("Available options: msg [msg to mc]; chatbridge [on/off]");

                    break;
            }
        }
    }

    public void setMua(Mua mua) {
        this.mua = mua;
    }
}
