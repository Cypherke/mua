package be.cypherke.mua.acceptance.steps;

import be.cypherke.mua.MessageHandler;
import be.cypherke.mua.Mua;
import be.cypherke.mua.Output;
import be.cypherke.mua.db.TeleportsDb;
import be.cypherke.mua.db.UsersDb;
import be.cypherke.mua.gsonobjects.Teleport;
import be.cypherke.mua.gsonobjects.User;
import be.cypherke.mua.io.FileManager;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class TeleportSteps {
  private String playerName;
  private MessageHandler messageHandler;
  private TeleportsDb teleportsDb;

  @Given("^a player '(.+)'$")
  public void aPlayerUser(String playerName) throws Exception {
    this.playerName = playerName;

    FileManager fileManager = Mockito.mock(FileManager.class);
    Output output = Mockito.mock(Output.class);

    Mua mua = Mockito.mock(Mua.class);

    UsersDb usersDb = Mockito.mock(UsersDb.class);
    User user = new User(this.playerName);

    this.teleportsDb = new TeleportsDb(fileManager);
    this.messageHandler = new MessageHandler(mua);

    Mockito.when(mua.getOutput()).thenReturn(output);
    Mockito.when(mua.getTeleportsDb()).thenReturn(this.teleportsDb);
    Mockito.when(mua.getUsersDb()).thenReturn(usersDb);

    Mockito.when(usersDb.getUser(this.playerName)).thenReturn(user);
    Mockito.when(fileManager.load(Mockito.any())).thenReturn(null);

    Mockito.doAnswer(new Answer<Void>() {
      @Override
      public Void answer(InvocationOnMock invocationOnMock) throws Exception {
        messageHandler.dispatch("[00:00:00] [Server thread/INFO]: Teleported "
            + invocationOnMock.getArguments()[0] + " to 1, 1, 1");

        return null;
      }
    }).when(output).sendGetCoordinates(playerName);
  }

  @When("^the player adds a teleport location$")
  public void thePlayerAddsATeleportLocation() throws Exception {
    messageHandler.dispatch("[00:00:00] [Server thread/INFO]: <"
        + this.playerName + "> !tp add home");
  }

  @Then("^the location is added to the teleport list$")
  public void theLocationIsAddedToTheTeleportList() {
    Teleport teleport = this.teleportsDb.getTp(this.playerName, "home");

    Assert.assertNotNull(teleport);
  }

  @And("^the player has a teleport location '(.+)'$")
  public void thePlayerHasATeleportLocationHome(String locationName) throws Exception {
    messageHandler.dispatch("[00:00:00] [Server thread/INFO]: <" + this.playerName
        + "> !tp add " + locationName);
  }

  @When("^the player deletes the teleport location '(.+)'$")
  public void thePlayerDeletesTheTeleportLocationHome(String locationName) throws Exception {
    messageHandler.dispatch("[00:00:00] [Server thread/INFO]: <"
        + this.playerName + "> !tp delete home");
  }

  @Then("^the location is no longer present$")
  public void theLocationIsNoLongerPresent() {
    Teleport teleport = this.teleportsDb.getTp(this.playerName, "home");

    Assert.assertNull(teleport);
  }
}
