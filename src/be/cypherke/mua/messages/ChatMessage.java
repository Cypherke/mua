package be.cypherke.mua.messages;

import be.cypherke.mua.Mua;
import be.cypherke.mua.messages.chat.ChatMessageBase;
import be.cypherke.mua.messages.chat.HelpMessage;
import be.cypherke.mua.messages.chat.TeleportMessage;
import be.cypherke.mua.messages.chat.TimeMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatMessage extends MessageBase {

    private final List<ChatMessageBase> messages = new ArrayList<>();

    /**
     * Constructor.
     * @param mua {@link Mua}
     */
    public ChatMessage(Mua mua) {
        super(mua);

        messages.add(new TimeMessage(mua));
        messages.add(new HelpMessage(mua));
        messages.add(new TeleportMessage(mua));
    }

    @Override
    public boolean handle(String function, String message) throws IOException {
        if (message.contains("")) {
            String pattern = "<(?<player>[\\w]*)> (?<chat>.*)";
            Matcher m = Pattern.compile(pattern).matcher(message);

            if (m.matches()) {
                String chat = m.group("chat");
                String player = m.group("player");

                boolean handled;
                for (ChatMessageBase msg : messages) {
                    handled = msg.handleChat(chat, player);

                    if (handled) {
                        break;
                    }
                }
            } else {
                boolean handled;
                for (ChatMessageBase msg : messages) {
                    handled = msg.handle(function, message);

                    if (handled) {
                        break;
                    }
                }
            }
        }

        return false;
    }
}
