import java.util.List;
import java.util.Scanner;

/**
 * Represents a general type of user input. Stores a prompt and a scanner that
 * can be used by different types of input.
 *
 * @author Muhammad Zenoor Hayat Bhatti
 * @version 2026.09.21
 */
public abstract class Input {
    /**
     * The message displayed to the user.
     */
    private java.lang.String prompt;

    /**
     * The scanner used to read input from the user.
     */
    private Scanner scanner;


    /**
     * Creates a new Input with the given prompt.
     *
     * @param prompt
     *     the message shown to the user
     */
    public Input(java.lang.String prompt) {
        this.prompt = prompt;
        this.scanner = new Scanner(System.in);
    }


    /**
     * Returns the prompt for this input.
     *
     * @return the prompt
     */
    public java.lang.String getPrompt() {
        return prompt;
    }


    /**
     * Returns the scanner used to read user input.
     *
     * @return the scanner
     */
    public Scanner getScanner() {
        return scanner;
    }


    /**
     * Represents an input that reads text entered by the user.
     */
    public static class String extends Input {
        /**
         * Creates a new String input with the given prompt.
         *
         * @param prompt
         *     the message shown to the user
         */
        public String(java.lang.String prompt) {
            super(prompt);
        }


        /**
         * Displays the prompt and returns the text entered by the user.
         *
         * @return the text entered by the user
         */
        public java.lang.String get() {
            System.out.print(getPrompt() + ":\n\t");
            return getScanner().nextLine();
        }
    }




    /**
     * Represents an input where the user selects from a list of possible
     * options.
     */
    public static class Option extends Input {
        /**
         * The available choices for the user.
         */
        private List<java.lang.String> options;


        /**
         * Creates a new Option input with the given prompt and list of
         * choices.
         *
         * @param prompt
         *     the message shown to the user
         * @param options
         *     the available choices
         */
        public Option(java.lang.String prompt, List<java.lang.String> options) {
            super(prompt);
            this.options = options;
        }


        /**
         * Displays the available options and asks the user to choose one.
         *
         * Continues asking until the user enters a valid number corresponding
         * to one of the options.
         *
         * @return the number of the selected option
         */
        public int get() {
            while (true) {
                System.out.println(getPrompt() + ":");

                for (int i = 0; i < options.size(); i++) {
                    System.out.println("\t" + (i + 1) + ". " + options.get(i));
                }

                System.out.print("Enter your choice:\n\t");

                java.lang.String input = getScanner().nextLine();

                try {
                    int choice = Integer.parseInt(input);

                    if (choice >= 1 && choice <= options.size()) {
                        return choice;
                    }

                    System.out.println(
                        "Invalid choice. Please Enter a number between 1 and " + options.size() + ".");
                }
                catch (NumberFormatException e) {
                    System.out.println(
                        "Invalid input. Please Enter a number between 1 and " + options.size() + ".");
                }
            }
        }
    }
}
