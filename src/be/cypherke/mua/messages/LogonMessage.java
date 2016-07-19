package be.cypherke.mua.messages;

import be.cypherke.mua.Mua;
import be.cypherke.mua.gsonobjects.Coordinate;
import be.cypherke.mua.gsonobjects.User;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogonMessage extends MessageBase {

    public LogonMessage(Mua mua) {
        super(mua);
    }

    @Override
    public boolean handle(String function, String message) throws IOException {
        String pattern = "(?<player>[\\w]*)\\[/(?<ip>[0-9\\.]*):[0-9]*\\] logged in with entity id (?<entity>[0-9]*) at \\((?<x>\\-?[0-9\\.]*), (?<y>\\-?[0-9\\.]*), (?<z>\\-?[0-9\\.]*)\\)";
        Matcher m = Pattern.compile(pattern).matcher(message);

        if (m.matches()) {
            User u = getMua().getUsersDb().getUser(m.group("player"));
            u.setIp(m.group("ip"));
            u.setEntity(m.group("entity"));
            u.setCoordinate(new Coordinate(Double.valueOf(m.group("x")), Double.valueOf(m.group("y")), Double.valueOf(m.group("z"))));
            u.setOnline(true);

            getMua().getOutput().sendMotd(u.getUsername());

            return true;
        }

        return false;
    }
}
