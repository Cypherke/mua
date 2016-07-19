package be.cypherke.mua.messages;

import be.cypherke.mua.Mua;

import java.io.IOException;

public abstract class MessageBase {
    private final Mua mua;

    public MessageBase(Mua mua) {
        this.mua = mua;
    }

    protected Mua getMua() {
        return mua;
    }

    public abstract boolean handle(String function, String message) throws IOException;
}

