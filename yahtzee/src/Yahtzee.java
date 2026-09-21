import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Controls the overall Yahtzee program flow, including the main menu, game
 * setup, the shared dice set, and the player list.
 *
 * @author Kaustubh Pasumarthi
 * @version 2026.09.21.1
 */
public class Yahtzee {
    /**
     * The ordered list of players in the current game.
     */
    private List<Player> players;

    /**
     * The shared dice set reused for every player turn.
     */
    private DiceSet dice;


    /**
     * Creates a new Yahtzee game with an empty player list and a shared dice
     * set.
     */
    public Yahtzee() {
        this.players = new ArrayList<>();
        this.dice = new DiceSet();
    }


    /**
     * Starts the program and runs the top-level loop until the user quits.
     * Choosing play runs setup, the game loop, and the final score display;
     * choosing quit ends the program.
     */
    public void start() {
        boolean running = true;
        while (running) {
            int choice = mainMenu();
            if (choice == 1) {
                gameSetup();
                gameLoop();
                displayResults();
            }
            else {
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
        return new Input.Option("Welcome", options).get();
    }


    /**
     * Collects setup information for a new game, creates the player, and
     * prepares the shared dice set.
     */
    private void gameSetup() {
        players.clear();
        String name = new Input.String("Enter player name").get();
        players.add(new Player(name));
        dice.reset();
    }


    /**
     * Gives each player a turn with the shared dice set until every player's
     * board is complete.
     */
    private void gameLoop() {
        if (players.isEmpty()) {
            return;
        }
        while (!gameComplete()) {
            for (Player player : players) {
                if (!player.isComplete()) {
                    player.takeTurn(dice);
                }
            }
        }
    }


    /**
     * Prints each player's final scorecard after the game ends.
     */
    private void displayResults() {
        System.out.println("Game over!");
        for (Player player : players) {
            System.out.println(player);
        }
    }


    /**
     * Checks whether the current game has finished.
     *
     * @return true if the player list is not empty and every player's board is
     *     complete; false otherwise
     */
    private boolean gameComplete() {
        if (players.isEmpty()) {
            return false;
        }
        for (Player player : players) {
            if (!player.isComplete()) {
                return false;
            }
        }
        return true;
    }
}
