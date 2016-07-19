package be.cypherke.mua.messages.chat;

import be.cypherke.mua.Mua;

import java.io.IOException;

public class HelpMessage extends ChatMessageBase {

    public HelpMessage(Mua mua) {
        super(mua);
    }

    @Override
    public boolean handleChat(String chat, String player) throws IOException {
        if (chat.equalsIgnoreCase("!help")) {
            getMua().getOutput().sendMessage("@a", "Available messages: !sc !time !tp");

            return true;
        }

        return false;
    }

    @Override
    public boolean handle(String function, String message) throws IOException {
        return false;
    }
}
