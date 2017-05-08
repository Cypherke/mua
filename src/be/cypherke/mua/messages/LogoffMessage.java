package be.cypherke.mua.messages;

import be.cypherke.mua.Mua;
import be.cypherke.mua.gsonobjects.User;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;

public class LogoffMessage extends MessageBase {

    public LogoffMessage(Mua mua) {
        super(mua);
    }

    @Override
    public boolean handle(String function, String message) throws IOException {
        String pattern = "(?<player>[\\w]*) left the game";
        Matcher m = Pattern.compile(pattern).matcher(message);

        if (m.matches()) {
            User u = getMua().getUsersDb().getUser(m.group("player"));
            u.setLastseen(DateTime.now().toString());
            u.setOnline(false);

            return true;
        }

        return false;
    }
}
