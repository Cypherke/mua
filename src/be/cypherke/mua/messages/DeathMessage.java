package be.cypherke.mua.messages;

import be.cypherke.mua.Mua;

import java.io.IOException;

public class DeathMessage extends MessageBase {

    public DeathMessage(Mua mua) {
        super(mua);
    }

    @Override
    public boolean handle(String function, String message) throws IOException {
        String[] deathMessages = {"was slain by Zombie", "was blown up by Creeper", "was slain by Enderman", "tried to swim in lava", "hit the ground too hard", "was shot by Skeleton"};
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
