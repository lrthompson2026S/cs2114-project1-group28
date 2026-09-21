import java.util.ArrayList;
import java.util.List;

/**
 * Represents one player as a name plus a scorecard, and runs that player's turn
 * with the shared dice set.
 *
 * @author Kaustubh Pasumarthi
 * @version 2026.09.21.1
 */
public class Player {
    /**
     * The maximum number of rolls allowed in one turn.
     */
    private static final int MAX_ROLLS = 3;

    /**
     * The number of dice the player can hold or release.
     */
    private static final int NUM_DICE = 5;

    /**
     * This player's display name.
     */
    private final String name;

    /**
     * This player's scorecard.
     */
    private final Board board;


    /**
     * Creates a player with the given name and an empty scorecard.
     *
     * @param name
     *     the player's name
     */
    public Player(String name) {
        this.name = name;
        this.board = new Board();
    }


    /**
     * Runs this player's turn using the shared dice. The player may hold,
     * release, and roll up to three times, then must successfully score or
     * scratch one available section before the turn ends.
     *
     * @param dice
     *     the shared dice set used for this turn
     */
    public void takeTurn(DiceSet dice) {
        if (isComplete()) {
            return;
        }

        dice.reset();
        dice.roll();

        boolean finished = false;
        while (!finished) {
            System.out.println(this);
            System.out.println(dice);
            System.out.println("Rolls used: " + dice.getRolls());

            String action = chooseAction(dice.getRolls());
            switch (action) {
                case "Hold" -> holdDie(dice);
                case "Release" -> releaseDie(dice);
                case "Roll" -> dice.roll();
                case "Score" -> finished = tryScore(dice);
                case "Scratch" -> finished = tryScratch();
                default -> throw new UnknownError();
            }
        }
    }


    /**
     * Returns whether this player's scorecard is complete.
     *
     * @return true if every section has been scored or scratched
     */
    public boolean isComplete() {
        return board.isComplete();
    }


    /**
     * Returns this player's name and scorecard display.
     *
     * @return the name followed by the board text
     */
    @Override
    public String toString() {
        return name + "\n" + board;
    }


    /**
     * Asks the player to choose the next turn action.
     *
     * @param rollsUsed
     *     how many rolls have already been used this turn
     * @return the selected action name
     */
    private String chooseAction(int rollsUsed) {
        List<String> options = new ArrayList<>();
        if (rollsUsed < MAX_ROLLS) {
            options.add("Hold");
            options.add("Release");
            options.add("Roll");
        }
        options.add("Score");
        options.add("Scratch");
        int choice = new Input.Option("Choose an action", options).get();
        return options.get(choice - 1);
    }


    /**
     * Holds one die chosen from a numbered menu.
     *
     * @param dice
     *     the shared dice set
     */
    private void holdDie(DiceSet dice) {
        int choice = chooseDie("Choose a die to hold");
        dice.hold(choice - 1);
    }


    /**
     * Releases one die chosen from a numbered menu.
     *
     * @param dice
     *     the shared dice set
     */
    private void releaseDie(DiceSet dice) {
        int choice = chooseDie("Choose a die to release");
        dice.release(choice - 1);
    }


    /**
     * Displays a menu of the five dice and returns the player's 1-based
     * selection.
     *
     * @param prompt
     *     the input prompt
     * @return a die number from 1 through 5
     */
    private int chooseDie(String prompt) {
        List<String> options = new ArrayList<>();
        for (int i = 1; i <= NUM_DICE; i++) {
            options.add("Die " + i);
        }
        return new Input.Option(prompt, options).get();
    }


    /**
     * Attempts to score one available section with the current dice. A failed
     * attempt does not end the turn.
     *
     * @param dice
     *     the shared dice set
     * @return true if the section was scored
     */
    private boolean tryScore(DiceSet dice) {
        Board.Section section =
            chooseSection("Choose a section to score", dice);
        if (section == null) {
            return false;
        }
        if (!board.scoreSection(section, dice)) {
            System.out.println(
                "That section cannot be scored with the current dice.");
            return false;
        }
        return true;
    }


    /**
     * Attempts to scratch one available section. A failed attempt does not end
     * the turn.
     *
     * @return true if the section was scratched
     */
    private boolean tryScratch() {
        Board.Section section =
            chooseSection("Choose a section to scratch", null);
        if (section == null) {
            return false;
        }
        if (!board.scratch(section)) {
            System.out.println("That section cannot be scratched.");
            return false;
        }
        return true;
    }


    /**
     * Asks the player to pick one unused scorecard section.
     *
     * @param prompt
     *     the input prompt
     * @return the chosen section, or null if none are available
     */
    private Board.Section chooseSection(String prompt, DiceSet dice) {
        List<Board.Section> available = board.getAvailableSections();
        List<Board.Section> scoreable = available;

        if (dice != null) {
            scoreable = dice.getValidSections();
        }

        if (available == null || available.isEmpty()) {
            return null;
        }

        List<String> options = new ArrayList<>();
        for (Board.Section section : available) {
            if (!scoreable.contains(section)) {
                continue; // don't add
            }
            options.add(section.toString());
        }

        int choice = new Input.Option(prompt, options).get();
        return available.get(choice - 1);
    }
}
