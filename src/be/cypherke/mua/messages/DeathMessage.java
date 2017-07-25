package be.cypherke.mua.messages;

import be.cypherke.mua.Mua;

import java.io.IOException;

public class DeathMessage extends MessageBase {

    public DeathMessage(Mua mua) {
        super(mua);
    }

    @Override
    public boolean handle(String function, String message) throws IOException {
        String username = message.split(" ")[0];

        // User did a "/me ..." command which starts with a "*"
        //     [10:56:21] [Server thread/INFO]: * Username withered away
        if (username.equals("*")) {
            return false;
        }

        // http://minecraft.gamepedia.com/Health#Death_messages
        String[] deathMessages = {
            "was shot by",

            "was pricked to death",
            "hugged a cactus",
            "walked into a cactus while trying to escape",
            "was stabbed to death",

            "drowned",
            "drowned whilst trying to escape",

            "experienced kinetic energy",
            "removed an elytra while flying",

            "blew up",
            "was blown up by",

            "hit the ground too hard",
            "fell from a high place",
            "fell off a ladder",
            "fell off some vines",
            "fell out of the water",
            "fell into a patch of fire",
            "fell into a patch of cacti",
            "was doomed to fall by",
            "was shot off some vines by",
            "was shot off a ladder by",
            "was blown from a high place by",

            "was squashed by a falling anvil",
            "was squashed by a falling block",

            "went up in flames",
            "burned to death",
            "was burnt to a crisp whilst fighting",
            "walked into a fire whilst fighting",

            "went off with a bang",

            "tried to swim in lava",

            "was struck by lightning",

            "discovered floor was lava",

            "was slain by",
            "got finished off by",
            "was fireballed by",

            "was killed by",

            "starved to death",

            "suffocated in a wall",
            "was squished too much",

            "was killed while trying to hurt",

            "fell out of the world",
            "fell from a high place and fell out of the world",
            "has been demolished",

            "withered away",

            "was pummeled by",
            "died"
        };

        for (String deathMessage : deathMessages) {
            if (message.contains(deathMessage)) {
                String user = message.split(" ")[0];
                getMua().getUsersDb().getUser(user).addDeath();
                getMua().getOutput().sendMessage("@a", "Congrats " + message.split(" ")[0] + ", this brings your total death count to: " + getMua().getUsersDb().getUser(user).getNumberOfDeaths());

                getMua().printToIRC(user.getUsername() + " just died (#" + user.getNumberOfDeaths() + "): " + message);

                return true;
            }
        }

        return false;
    }
}
