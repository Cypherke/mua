package be.cypherke.mua.messages.chat;

import be.cypherke.mua.Mua;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class TimeMessage extends ChatMessageBase {

    private boolean timeTriggered;

    public TimeMessage(Mua mua) {
        super(mua);
    }

    @Override
    public boolean handleChat(String chat, String player) throws IOException {
        if (chat.equalsIgnoreCase("!time")) {
            getMua().getOutput().sendCommand("time query daytime");
            timeTriggered = true;

            return true;
        }

        return false;
    }

    @Override
    public boolean handle(String function, String message) throws IOException {
        String pattern = "Time is (?<time>[0-9]+)";
        Matcher m = Pattern.compile(pattern).matcher(message);
        if (m.matches() && timeTriggered) {
            Integer time = Integer.valueOf(m.group("time"));
            if (time > 23999) {
                time = time % 24000;
            }
            DateTime dateTime = new DateTime(2016, 1, 1, 6, 0, 0);
            dateTime = dateTime.plusSeconds(Math.toIntExact((long) (time * 3.6)));
            DateTimeFormatter fmt = DateTimeFormat.forPattern("HH:mm:ss");
            getMua().getOutput().sendMessage("@a", "Time is " + dateTime.toString(fmt));

            timeTriggered = false;

            return true;
        }

        return false;
    }
}
