package be.cypherke.mua.unit.steps;

import be.cypherke.mua.db.TeleportsDb;
import be.cypherke.mua.gsonobjects.Coordinate;
import be.cypherke.mua.gsonobjects.Teleport;
import be.cypherke.mua.io.FileManager;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.mockito.Mockito;

public class TeleportsDbSteps {
  private TeleportsDb teleportsDb;

  @Given("^an empty teleport database$")
  public void anEmptyTeleportDatabase() {
    FileManager fileManager = Mockito.mock(FileManager.class);
    Mockito.when(fileManager.load(Mockito.any())).thenReturn(null);

    teleportsDb = new TeleportsDb(fileManager);
  }

  @When("^I add a new teleport location with the name '(.+)'$")
  public void iAddANewTeleportLocationWithTheNameTest(String locationName) {
    String playerName = "user";
    String created = DateTime.now().toString();
    Coordinate coordinate = new Coordinate(42.0, 42.0, 42.0);

    Teleport teleport = new Teleport(locationName, playerName, created, coordinate);

    teleportsDb.add(teleport);
  }

  @Then("^the new teleport location '(.+)' is added to the database$")
  public void theNewTeleportLocationIsAddedToTheDatabase(String locationName) {
    String playerName = "user";
    Teleport teleport = teleportsDb.getTp(playerName, locationName);

    Assert.assertNotNull(teleport);
  }

  @Given("^the player '(.+)' has only one teleport location named '(.+)'$")
  public void thePlayerUserHasOnlyOneTeleportLocationNamedTest(String playerName, String locationName) {
    FileManager fileManager = Mockito.mock(FileManager.class);
    Mockito.when(fileManager.load(Mockito.any())).thenReturn(null);

    teleportsDb = new TeleportsDb(fileManager);

    String created = DateTime.now().toString();
    Coordinate coordinate = new Coordinate(42.0, 42.0, 42.0);

    Teleport teleport = new Teleport(locationName, playerName, created, coordinate);

    teleportsDb.add(teleport);
  }

  @When("^I remove the teleport location '(.+)' for the player '(.+)'$")
  public void iRemoveTheTeleportLocationTest(String locationName, String playerName) {
    teleportsDb.removeTeleport(playerName, locationName);
  }

  @Then("^the player '(.+)' has no more teleport locations$")
  public void thePlayerUsersHasNoMoreTeleportLocations(String playerName) {
    String locations = teleportsDb.getUserTps(playerName);

    Assert.assertNull(locations);
  }
}
