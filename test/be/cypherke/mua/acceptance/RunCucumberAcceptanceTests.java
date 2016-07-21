package be.cypherke.mua.acceptance;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(monochrome = true, format = {"pretty"}, features = "test/be/cypherke/mua/acceptance/features/", glue = "be.cypherke.mua.acceptance")
public class RunCucumberAcceptanceTests {

}
