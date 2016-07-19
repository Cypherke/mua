package be.cypherke.mua.messages.chat;

import be.cypherke.mua.Mua;
import be.cypherke.mua.messages.MessageBase;

import java.io.IOException;

public abstract class ChatMessageBase extends MessageBase {

    public ChatMessageBase(Mua mua) {
        super(mua);
    }

    public abstract boolean handleChat(String chat, String player) throws IOException;
}
