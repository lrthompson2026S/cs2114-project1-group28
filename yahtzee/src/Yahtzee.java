import java.util.Arrays;
import java.util.List;

/**
 * Controls the overall Yahtzee program flow, including the main menu,
 * game setup, the shared dice set, and the player list.
 *
 * @author Kaustubh Pasumarthi
 * @version 2026.09.16.1
 */
public class Yahtzee {
    private List<Player> players;
    private DiceSet dice;

    /**
     * Creates a new Yahtzee game with an empty player list and a shared
     * dice set.
     */
    public Yahtzee() {
        this.dice = new DiceSet();
    }

    /**
     * Starts the program and runs the top-level loop until the user quits.
     * Choosing play runs setup and then the game loop; choosing quit ends
     * the program.
     */
    public void start() {
        boolean running = true;
        while (running) {
            int choice = mainMenu();
            // change input when OptionInput is implemented
            if (choice == 1) {
                gameSetup();
                gameLoop();
            } else {
                running = false;
            }
        }
    }

    /**
     * Displays the main menu options and reads the user's selection.
     *
     * @return 1 if the user chooses Play, or 2 if the user chooses Quit
     */
    private int mainMenu() {
        List<String> options = Arrays.asList("Play", "Quit");
        return new OptionInput("Choose an option", options).get();
    }

    private void gameSetup() {

    }

    private void gameLoop() {

    }

    private boolean gameComplete() {
        return false;
    }
}
