import java.util.Arrays;
import java.util.List;

public class Yahtzee {
    private List<Player> players;
    private DiceSet dice;

    public Yahtzee() {
        this.dice = new DiceSet();
    }

    public void start() {
        boolean running = true;
        while (running) {
            int choice = mainMenu();
            //change input when OptionInput is implemented 
            if (choice == 1){ 
                gameSetup();
                gameLoop();
            } else {
                running = false;
            }
        }
    }

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
