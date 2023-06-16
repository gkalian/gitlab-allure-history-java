package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Story;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class ExampleTest {

    @Test
    @Story("This is a success Story.")
    @Description("This is a success story description.")
    public void testSuccess() {
        assertEquals(2 + 2, 4);
    }

    @Test
    @Story("This is a fail Story.")
    @Description("This is a fail story description.")
    public void testFailure() {
        assertEquals(2 + 2, 5);
    }

    @Test
    @Story("This is a second success Story.")
    @Description("This is a second success story description.")
    public void testSuccess2() {
        assertEquals(2 + 2, 4);
    }
}