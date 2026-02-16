package be.cypherke.mua.acceptance;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(monochrome = true, plugin = {"pretty"},
    features = "test/be/cypherke/mua/acceptance/features/",
    glue = "be.cypherke.mua.acceptance")
public class RunCucumberAcceptanceTests {

}
