package be.cypherke.mua.messages;

import be.cypherke.mua.Mua;
import be.cypherke.mua.gsonobjects.User;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InfoMessage extends MessageBase {
    public InfoMessage(Mua mua) {
        super(mua);
    }

    @Override
    public boolean handle(String function, String message) {
        if (function.matches("User Authenticator #[0-9]*/INFO")) {
            String pattern = "UUID of player (?<player>[\\w]*) is (?<uuid>[a-z0-9\\-]*)";
            Matcher m = Pattern.compile(pattern).matcher(message);
            if (m.matches()) {
                User u = getMua().getUsersDb().getUser(m.group("player"));
                if (u != null) {
                    u.setUuid(m.group("uuid"));
                } else {
                    u = new User(m.group("player"));
                    u.setUuid(m.group("uuid"));
                    getMua().getUsersDb().addUser(u);
                }
            }

            return true;
        }

        return false;
    }
}
