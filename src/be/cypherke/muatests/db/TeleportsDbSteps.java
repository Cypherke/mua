package be.cypherke.muatests.db;

import be.cypherke.mua.db.TeleportsDb;
import be.cypherke.mua.gsonobjects.Coordinate;
import be.cypherke.mua.gsonobjects.Teleport;
import be.cypherke.mua.io.FileManager;
import be.cypherke.muatests.mocks.EmptyFileManager;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.joda.time.DateTime;
import org.junit.Assert;

public class TeleportsDbSteps {
    private TeleportsDb teleportsDb;

    @Given("^an empty teleport database$")
    public void anEmptyTeleportDatabase() throws Throwable {
        FileManager fileManager = new EmptyFileManager();
        teleportsDb = new TeleportsDb(fileManager);
    }

    @When("^I add a new teleport location with the name '(.+)'$")
    public void iAddANewTeleportLocationWithTheNameTest(String locationName) throws Throwable {
        String playerName = "user";
        String created = DateTime.now().toString();
        Coordinate coordinate = new Coordinate(42.0, 42.0, 42.0);

        Teleport teleport = new Teleport(locationName, playerName, created, coordinate);

        teleportsDb.add(teleport);
    }

    @Then("^the new teleport location '(.+)' is added to the database$")
    public void theNewTeleportLocationIsAddedToTheDatabase(String locationName) throws Throwable {
        String playerName = "user";
        Teleport teleport = teleportsDb.getTp(playerName, locationName);

        Assert.assertNotNull(teleport);
    }

    @Given("^the player '(.+)' has only one teleport location named '(.+)'$")
    public void thePlayerUserHasOnlyOneTeleportLocationNamedTest(String playerName, String locationName) throws Throwable {
        FileManager fileManager = new EmptyFileManager();
        teleportsDb = new TeleportsDb(fileManager);

        String created = DateTime.now().toString();
        Coordinate coordinate = new Coordinate(42.0, 42.0, 42.0);

        Teleport teleport = new Teleport(locationName, playerName, created, coordinate);

        teleportsDb.add(teleport);
    }

    @When("^I remove the teleport location '(.+)' for the player '(.+)'$")
    public void iRemoveTheTeleportLocationTest(String locationName, String playerName) throws Throwable {
        teleportsDb.removeTeleport(playerName, locationName);
    }

    @Then("^the player '(.+)' has no more teleport locations$")
    public void thePlayerUsersHasNoMoreTeleportLocations(String playerName) throws Throwable {
        String locations = teleportsDb.getUserTps(playerName);

        Assert.assertNull(locations);
    }
}