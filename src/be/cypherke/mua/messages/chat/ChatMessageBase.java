package be.cypherke.mua.messages.chat;

import be.cypherke.mua.Mua;
import be.cypherke.mua.messages.MessageBase;
import java.io.IOException;

/** Base class for chat command handlers. */
public abstract class ChatMessageBase extends MessageBase {

  /**
   * Creates a new chat message handler.
   *
   * @param mua the {@link Mua} instance
   */
  public ChatMessageBase(Mua mua) {
    super(mua);
  }

  /**
   * Handles a chat message from a player.
   *
   * @param chat the chat text
   * @param player the player who sent the message
   * @return true if the message was handled
   * @throws IOException if writing to the server fails
   */
  public abstract boolean handleChat(String chat, String player) throws IOException;
}
