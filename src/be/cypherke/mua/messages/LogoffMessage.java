package be.cypherke.mua.messages;

import be.cypherke.mua.Mua;
import be.cypherke.mua.gsonobjects.User;
import org.joda.time.DateTime;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogoffMessage extends MessageBase {

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
            if (player.length() > 0 && player.charAt(0) == '§' && player.charAt(player.length() - 2) == '§') {
                player = player.substring(2, player.length() - 2);
            }

            User u = getMua().getUsersDb().getUser(player);
            u.setLastseen(DateTime.now().toString());
            u.setOnline(false);

            getMua().printToIRC(u.getUsername() + " went offline...");

            return true;
        }

        return false;
    }
}
