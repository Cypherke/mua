package be.cypherke.mua;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        monochrome = true,
        format = {"pretty"},
        features = "test/be/cypherke/mua/features/",
        glue = "be.cypherke.mua"
)
public class RunCucumberTest {

}
