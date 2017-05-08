package be.cypherke.mua;

import be.cypherke.mua.messages.ChatMessage;
import be.cypherke.mua.messages.DeathMessage;
import be.cypherke.mua.messages.InfoMessage;
import be.cypherke.mua.messages.LogoffMessage;
import be.cypherke.mua.messages.LogonMessage;
import be.cypherke.mua.messages.MessageBase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageHandler {

    private List<MessageBase> messages = new ArrayList<>();

    /**
     * Constructor.
     *
     * @param mua {@link Mua}
     */
    public MessageHandler(Mua mua) {
        messages.add(new InfoMessage(mua));
        messages.add(new LogonMessage(mua));
        messages.add(new LogoffMessage(mua));
        messages.add(new DeathMessage(mua));
        messages.add(new ChatMessage(mua));
    }

    /**
     * Handles every line from the server.
     *
     * @param line the line to handle
     * @throws IOException exception by BufferedReader
     */
    public void dispatch(String line) throws IOException {
        String pattern = "\\[(?<timestamp>[0-9\\:]*)\\]\\s\\[(?<function>[\\w\\s/\\#]*)\\]:\\s(?<message>.*)";
        Matcher m = Pattern.compile(pattern).matcher(line);
        // just the default matching
        if (m.matches()) {
            String timestamp = m.group("timestamp");
            String function = m.group("function");
            String message = m.group("message");

            boolean handled;
            for (MessageBase msg : messages) {
                handled = msg.handle(function, message);

                if (handled) {
                    break;
                }
            }
        }
    }
}
