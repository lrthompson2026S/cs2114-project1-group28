import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Owns the five dice, the current turn's roll count, and analysis of which
 * scoring sections the current roll satisfies.
 *
 * @author Kaustubh Pasumarthi
 * @version 2026.09.21.1
 */
public class DiceSet {
    /**
     * The number of dice in a Yahtzee set.
     */
    private static final int NUM_DICE = 5;

    /**
     * The maximum number of rolls allowed in one turn.
     */
    private static final int MAX_ROLLS = 3;

    /**
     * The lowest valid die face.
     */
    private static final int MIN_FACE = 1;

    /**
     * The highest valid die face.
     */
    private static final int MAX_FACE = 6;

    /**
     * The number of consecutive faces required for a small straight.
     */
    private static final int SMALL_STRAIGHT_LENGTH = 4;

    /**
     * The number of consecutive faces required for a large straight.
     */
    private static final int LARGE_STRAIGHT_LENGTH = 5;

    /**
     * The number of matching dice required for three of a kind.
     */
    private static final int THREE_OF_A_KIND = 3;

    /**
     * The number of matching dice required for four of a kind.
     */
    private static final int FOUR_OF_A_KIND = 4;

    /**
     * The triplet count required for a full house.
     */
    private static final int FULL_HOUSE_THREE = 3;

    /**
     * The pair count required for a full house.
     */
    private static final int FULL_HOUSE_TWO = 2;

    /**
     * The five dice used for every turn.
     */
    private Die[] dice;

    /**
     * The number of rolls used in the current turn.
     */
    private int rolls;

    /**
     * The random source used when rolling unheld dice.
     */
    private Random random;


    /**
     * Creates a set of five unrolled dice with a roll count of zero.
     */
    public DiceSet(Random random) {
        this.dice = new Die[NUM_DICE];
        for (int i = 0; i < NUM_DICE; i++) {
            this.dice[i] = new Die();
        }
        this.rolls = 0;
        this.random = random;
    }


    /**
     * Rolls every unheld die and increases the roll count by one. Does nothing
     * if this turn has already used three rolls.
     */
    public void roll() {
        if (rolls >= MAX_ROLLS) {
            return;
        }
        for (int i = 0; i < NUM_DICE; i++) {
            dice[i].roll(random);
        }
        rolls++;
    }


    /**
     * Holds the die at the given index so later rolls skip it.
     *
     * @param index
     *     the zero-based die index, from 0 through 4
     * @return true if the index is valid and the die is held; false if the
     *     index is out of range and nothing changes
     */
    public boolean hold(int index) {
        if (!isValidIndex(index)) {
            return false;
        }
        dice[index].hold();
        return true;
    }


    /**
     * Releases the die at the given index so later rolls can change it.
     *
     * @param index
     *     the zero-based die index, from 0 through 4
     * @return true if the index is valid and the die is released; false if the
     *     index is out of range and nothing changes
     */
    public boolean release(int index) {
        if (!isValidIndex(index)) {
            return false;
        }
        dice[index].release();
        return true;
    }


    /**
     * Releases all five dice.
     */
    public void releaseAll() {
        for (int i = 0; i < NUM_DICE; i++) {
            dice[i].release();
        }
    }


    /**
     * Prepares this set for a new turn by clearing the roll count and releasing
     * every die.
     */
    public void reset() {
        rolls = 0;
        for (int i = 0; i < NUM_DICE; i++) {
            dice[i].reset();
        }
    }


    /**
     * Returns how many rolls have been used in the current turn.
     *
     * @return the current roll count, from 0 through 3
     */
    public int getRolls() {
        return rolls;
    }


    /**
     * Examines the current dice and returns the scoring sections this roll
     * satisfies.
     *
     * @return a list of valid upper and lower sections; empty if the dice have
     *     not been rolled into legal face values
     */
    public List<Board.Section> getValidSections() {
        List<Board.Section> valid = new ArrayList<>();
        int[] counts = faceCounts();

        addValidUpperSections(valid, counts);
        addValidLowerSections(valid, counts);

        return valid;
    }


    /**
     * Returns the die values that contribute to the requested scoring section.
     * The result is a primitive {@code int[]} so callers can use
     * {@code IntStream.of(...).sum()} or check {@code .length == 0}.
     *
     * @param section
     *     the scorecard category to analyze
     * @return the contributing face values, or an empty array if the current
     *     roll does not satisfy that section
     */
    public int[] getScoringValues(Board.Section section) {
        List<Integer> values = new ArrayList<>();
        if (section == null || !getValidSections().contains(section)) {
            return new int[0];
        }

        if (section instanceof Board.Upper.Section) {
            int face = faceFor((Board.Upper.Section)section);
            for (int i = 0; i < NUM_DICE; i++) {
                if (dice[i].value() == face) {
                    values.add(dice[i].value());
                }
            }
            return toIntArray(values);
        }

        for (int i = 0; i < NUM_DICE; i++) {
            values.add(dice[i].value());
        }
        return toIntArray(values);
    }


    /**
     * Returns a display representation of all five dice in order, including
     * held state.
     *
     * @return the five dice separated by spaces
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < NUM_DICE; i++) {
            if (i > 0) {
                result.append(" ");
            }
            result.append(dice[i].toString());
        }
        return result.toString();
    }


    /**
     * Checks whether the given index refers to one of the five dice.
     *
     * @param index
     *     the index to check
     * @return true if the index is between 0 and 4 inclusive
     */
    private boolean isValidIndex(int index) {
        return index >= 0 && index < NUM_DICE;
    }


    /**
     * Counts how many dice currently show each face from 1 through 6.
     *
     * @return an array whose indexes 1 through 6 store face counts
     */
    private int[] faceCounts() {
        int[] counts = new int[MAX_FACE + 1];
        for (int i = 0; i < NUM_DICE; i++) {
            int face = dice[i].value();
            if (face >= MIN_FACE && face <= MAX_FACE) {
                counts[face]++;
            }
        }
        return counts;
    }


    /**
     * Adds each unused-pattern upper section that the current counts satisfy.
     *
     * @param valid
     *     the list receiving matching sections
     * @param counts
     *     face counts for values 1 through 6
     */
    private void addValidUpperSections(
        List<Board.Section> valid,
        int[] counts) {
        Board.Upper.Section[] sections = Board.Upper.Section.values();
        for (int i = 0; i < sections.length; i++) {
            int face = faceFor(sections[i]);
            if (counts[face] > 0) {
                valid.add(sections[i]);
            }
        }
    }


    /**
     * Adds each lower section that the current counts satisfy.
     *
     * @param valid
     *     the list receiving matching sections
     * @param counts
     *     face counts for values 1 through 6
     */
    private void addValidLowerSections(
        List<Board.Section> valid,
        int[] counts) {
        if (hasOfAKind(counts, THREE_OF_A_KIND)) {
            valid.add(Board.Lower.Section.ThreeOfKind);
        }
        if (hasOfAKind(counts, FOUR_OF_A_KIND)) {
            valid.add(Board.Lower.Section.FourOfKind);
        }
        if (isFullHouse(counts)) {
            valid.add(Board.Lower.Section.FullHouse);
        }
        if (isSmallStraight(counts)) {
            valid.add(Board.Lower.Section.SmallStraight);
        }
        if (isLargeStraight(counts)) {
            valid.add(Board.Lower.Section.LargeStraight);
        }
        if (hasOfAKind(counts, NUM_DICE)) {
            valid.add(Board.Lower.Section.Yahtzee);
        }
        if (allDiceRolled(counts)) {
            valid.add(Board.Lower.Section.Chance);
        }
    }


    /**
     * Maps an upper-section category to its die face.
     *
     * @param section
     *     the upper-section category
     * @return the matching face value from 1 through 6
     */
    private int faceFor(Board.Upper.Section section) {
        return section.ordinal() + MIN_FACE;
    }


    /**
     * Copies a list of boxed integers into a primitive array.
     *
     * @param values
     *     the boxed face values
     * @return a primitive {@code int[]} of the same values
     */
    private int[] toIntArray(List<Integer> values) {
        int[] result = new int[values.size()];
        for (int i = 0; i < values.size(); i++) {
            result[i] = values.get(i);
        }
        return result;
    }


    /**
     * Returns whether any face appears at least {@code needed} times.
     *
     * @param counts
     *     face counts for values 1 through 6
     * @param needed
     *     the minimum number of matching dice
     * @return true if some face meets the required count
     */
    private boolean hasOfAKind(int[] counts, int needed) {
        for (int face = MIN_FACE; face <= MAX_FACE; face++) {
            if (counts[face] >= needed) {
                return true;
            }
        }
        return false;
    }


    /**
     * Returns whether the counts are three of one face and two of another.
     *
     * @param counts
     *     face counts for values 1 through 6
     * @return true if the roll is a full house
     */
    private boolean isFullHouse(int[] counts) {
        boolean hasThree = false;
        boolean hasTwo = false;
        for (int face = MIN_FACE; face <= MAX_FACE; face++) {
            if (counts[face] == FULL_HOUSE_THREE) {
                hasThree = true;
            }
            if (counts[face] == FULL_HOUSE_TWO) {
                hasTwo = true;
            }
        }
        return hasThree && hasTwo;
    }


    /**
     * Returns whether the dice contain four consecutive faces.
     *
     * @param counts
     *     face counts for values 1 through 6
     * @return true if the roll contains a small straight
     */
    private boolean isSmallStraight(int[] counts) {
        return hasConsecutive(counts, SMALL_STRAIGHT_LENGTH);
    }


    /**
     * Returns whether the dice contain five consecutive faces.
     *
     * @param counts
     *     face counts for values 1 through 6
     * @return true if the roll is a large straight
     */
    private boolean isLargeStraight(int[] counts) {
        return hasConsecutive(counts, LARGE_STRAIGHT_LENGTH);
    }


    /**
     * Returns whether the dice contain the given number of consecutive faces.
     *
     * @param counts
     *     face counts for values 1 through 6
     * @param length
     *     how many consecutive faces are required
     * @return true if such a run is present
     */
    private boolean hasConsecutive(int[] counts, int length) {
        int maxStart = MAX_FACE - length + 1;
        for (int start = MIN_FACE; start <= maxStart; start++) {
            if (hasRun(counts, start, start + length - 1)) {
                return true;
            }
        }
        return false;
    }


    /**
     * Returns whether every face from {@code start} through {@code end} appears
     * at least once.
     *
     * @param counts
     *     face counts for values 1 through 6
     * @param start
     *     the first face in the run
     * @param end
     *     the last face in the run
     * @return true if each face in that range is present
     */
    private boolean hasRun(int[] counts, int start, int end) {
        for (int face = start; face <= end; face++) {
            if (counts[face] == 0) {
                return false;
            }
        }
        return true;
    }


    /**
     * Returns whether all five dice currently show a legal face value.
     *
     * @param counts
     *     face counts for values 1 through 6
     * @return true if exactly five legal faces were counted
     */
    private boolean allDiceRolled(int[] counts) {
        int total = 0;
        for (int face = MIN_FACE; face <= MAX_FACE; face++) {
            total += counts[face];
        }
        return total == NUM_DICE;
    }
}
