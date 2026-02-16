package be.cypherke.mua.io;

import be.cypherke.mua.Mua;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.input.TailerListenerAdapter;

/**
 * Listens to IRC channel output and dispatches commands to the server.
 */
public class IrcListener extends TailerListenerAdapter {
  private Mua mua;

  /**
   * Handles a line from the IRC channel output file.
   *
   * @param line the line to process
   */
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
            System.err.println("Failed to send IRC message to server: " + ex.getMessage());
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

          this.mua.printToIrc("Available options: msg [msg to mc]; chatbridge [on/off]");

          break;
      }
    }
  }

  public void setMua(Mua mua) {
    this.mua = mua;
  }
}
