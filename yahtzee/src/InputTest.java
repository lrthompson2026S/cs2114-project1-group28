import java.io.ByteArrayInputStream;
import java.util.Arrays;

/**
 * Tests the Input class and its nested String and Option classes.
 *
 * @author Muhammad Zenoor Hayat Bhatti
 * @version 2026.09.22
 */
public class InputTest extends student.TestCase
{
    /**
     * Tests that a String input stores its prompt correctly.
     */
    public void testStringPrompt()
    {
        System.setIn(
            new ByteArrayInputStream("test\n".getBytes()));

        Input.String input =
            new Input.String("Enter your name: ");

        assertEquals("Enter your name: ", input.getPrompt());
    }


    /**
     * Tests normal text input.
     */
    public void testStringGet()
    {
        java.lang.String fakeInput = "Zenoor\n";

        System.setIn(
            new ByteArrayInputStream(fakeInput.getBytes()));

        Input.String input =
            new Input.String("Enter your name: ");

        assertEquals("Zenoor", input.get());
    }


    /**
     * Tests empty text input.
     */
    public void testStringEmptyInput()
    {
        java.lang.String fakeInput = "\n";

        System.setIn(
            new ByteArrayInputStream(fakeInput.getBytes()));

        Input.String input =
            new Input.String("Enter text: ");

        assertEquals("", input.get());
    }


    /**
     * Tests a valid option choice.
     */
    public void testOptionValidChoice()
    {
        java.lang.String fakeInput = "2\n";

        System.setIn(
            new ByteArrayInputStream(fakeInput.getBytes()));

        Input.Option input =
            new Input.Option(
                "Choose an option:",
                Arrays.asList("Roll", "Score", "Quit"));

        assertEquals(2, input.get());
    }


    /**
     * Tests non-numeric input followed by a valid choice.
     */
    public void testOptionBadTextInput()
    {
        java.lang.String fakeInput = "hello\n1\n";

        System.setIn(
            new ByteArrayInputStream(fakeInput.getBytes()));

        Input.Option input =
            new Input.Option(
                "Choose an option:",
                Arrays.asList("Roll", "Score", "Quit"));

        assertEquals(1, input.get());
    }


    /**
     * Tests an option number that is too large,
     * followed by a valid choice.
     */
    public void testOptionTooHigh()
    {
        java.lang.String fakeInput = "9\n3\n";

        System.setIn(
            new ByteArrayInputStream(fakeInput.getBytes()));

        Input.Option input =
            new Input.Option(
                "Choose an option:",
                Arrays.asList("Roll", "Score", "Quit"));

        assertEquals(3, input.get());
    }


    /**
     * Tests an option number that is too small,
     * followed by a valid choice.
     */
    public void testOptionTooLow()
    {
        java.lang.String fakeInput = "0\n2\n";

        System.setIn(
            new ByteArrayInputStream(fakeInput.getBytes()));

        Input.Option input =
            new Input.Option(
                "Choose an option:",
                Arrays.asList("Roll", "Score", "Quit"));

        assertEquals(2, input.get());
    }


    /**
     * Tests that an Option input stores its prompt correctly.
     */
    public void testOptionPrompt()
    {
        System.setIn(
            new ByteArrayInputStream("1\n".getBytes()));

        Input.Option input =
            new Input.Option(
                "Select:",
                Arrays.asList("Yes", "No"));

        assertEquals("Select:", input.getPrompt());
    }


    /**
     * Tests that a scanner is created for the input.
     */
    public void testGetScanner()
    {
        System.setIn(
            new ByteArrayInputStream("test\n".getBytes()));

        Input.String input =
            new Input.String("Enter text: ");

        assertNotNull(input.getScanner());
    }
}
