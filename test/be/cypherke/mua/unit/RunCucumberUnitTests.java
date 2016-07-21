package be.cypherke.mua.unit;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(monochrome = true, format = {"pretty"}, features = "test/be/cypherke/mua/unit/features/", glue = "be.cypherke.mua.unit")
public class RunCucumberUnitTests {

}
