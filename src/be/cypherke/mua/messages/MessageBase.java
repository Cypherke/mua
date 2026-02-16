package be.cypherke.mua.messages;

import be.cypherke.mua.Mua;
import java.io.IOException;

/** Base class for server message handlers. */
public abstract class MessageBase {
  private final Mua mua;

  /**
   * Creates a new message handler.
   *
   * @param mua the {@link Mua} instance
   */
  public MessageBase(Mua mua) {
    this.mua = mua;
  }

  protected Mua getMua() {
    return mua;
  }

  /**
   * Handles a server message.
   *
   * @param function the server thread/function identifier
   * @param message the message content
   * @return true if the message was handled
   * @throws IOException if writing to the server fails
   */
  public abstract boolean handle(String function, String message) throws IOException;
}

