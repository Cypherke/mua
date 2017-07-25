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

    public ChatMessage(Mua mua) {
        super(mua);

        messages.add(new TimeMessage(mua));
        messages.add(new HelpMessage(mua));
        messages.add(new TeleportMessage(mua));
    }

    @Override
    public boolean handle(String function, String message) throws IOException {
        if (message.contains("")) {
            String pattern = "<(?<player>[a-zA-Z0-9§]*)> (?<chat>.*)";
            Matcher m = Pattern.compile(pattern).matcher(message);

            if (m.matches()) {
                String player = m.group("player");
                String chat = m.group("chat");

                // Player is in a team with colours: "§9Nickname§r joined the game"
                if (player.length() > 0 && player.charAt(0) == '§' && player.charAt(player.length() - 2) == '§') {
                    player = player.substring(2, player.length() - 2);
                }

                boolean handled = false;
                for (ChatMessageBase msg : messages) {
                    handled = msg.handleChat(chat, player);

                    if (handled) {
                        break;
                    }
                }

                // Don't bridge valid server commands to IRC
                if (!handled && getMua().isIrcBridgeActive()) {
                    getMua().printToIRC("<" + player + "> " + chat);
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
