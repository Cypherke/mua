package be.cypherke.mua.messages;

import be.cypherke.mua.Mua;
import be.cypherke.mua.gsonobjects.User;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.joda.time.DateTime;

/** Handles player logout events. */
public class LogoffMessage extends MessageBase {

  /**
   * Creates a new LogoffMessage handler.
   *
   * @param mua the {@link Mua} instance
   */
  public LogoffMessage(Mua mua) {
    super(mua);
  }

  @Override
  public boolean handle(String function, String message) throws IOException {
    String pattern = "(?<player>[a-zA-Z0-9§]*) left the game";
    Matcher m = Pattern.compile(pattern).matcher(message);

    if (m.matches()) {
      String player = m.group("player");

      // Player is in a team with colours: "§9Nickname§r joined the game"
      if (!player.isEmpty() && player.charAt(0) == '\u00a7' // § Minecraft color code prefix
          && player.charAt(player.length() - 2) == '\u00a7') { // § Minecraft color code prefix
        player = player.substring(2, player.length() - 2);
      }

      User u = getMua().getUsersDb().getUser(player);
      u.setLastseen(DateTime.now().toString());
      u.setOnline(false);

      getMua().printToIrc(u.getUsername() + " went offline...");

      return true;
    }

    return false;
  }
}
