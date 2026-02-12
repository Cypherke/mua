package be.cypherke.mua.unit;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(monochrome = true, plugin = {"pretty"},
    features = "test/be/cypherke/mua/unit/features/", glue = "be.cypherke.mua.unit")
public class RunCucumberUnitTests {

}
