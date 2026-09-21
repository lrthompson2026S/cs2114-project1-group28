import java.util.Random;

/**
 * Represents one six-sided die with a current face value and held
 * state.
 *
 * @author Kaustubh Pasumarthi
 * @version 2026.09.21.1
 */
public class Die {
    /**
     * The lowest valid face value of a die.
     */
    private static final int MIN_FACE = 1;

    /**
     * The number of faces on a die.
     */
    private static final int NUM_FACES = 6;

    /**
     * The current face value. Zero means the die has not been rolled
     * yet.
     */
    private int value;

    /**
     * True when this die should keep its value on the next roll.
     */
    private boolean held;

    /**
     * Creates a die that is not held and has not been rolled.
     */
    public Die() {
        this.value = 0;
        this.held = false;
    }

    /**
     * Assigns a random face value from 1 through 6 if this die is not
     * held. A held die is left unchanged.
     *
     * @param random
     *            the random number generator used to choose the face
     *            value
     */
    public void roll(Random random) {
        if (!held) {
            value = random.nextInt(NUM_FACES) + MIN_FACE;
        }
    }

    /**
     * Marks this die as held so later rolls skip it.
     */
    public void hold() {
        held = true;
    }

    /**
     * Removes the held state so later rolls can change this die.
     */
    public void release() {
        held = false;
    }

    /**
     * Restores this die to the unheld state for a new turn.
     */
    public void reset() {
        held = false;
    }

    /**
     * Returns the current face value as a primitive int. Scoring reads
     * these values through {@code DiceSet.getScoringValues()}.
     *
     * @return the face value, or 0 if this die has not been rolled
     */
    public int value() {
        return value;
    }

    /**
     * Returns whether this die is currently held.
     *
     * @return true if the die is held; false otherwise
     */
    public boolean isHeld() {
        return held;
    }

    /**
     * Returns a display representation of this die. A held die is
     * marked with an asterisk.
     *
     * @return the face value, with "*" appended when the die is held
     */
    @Override
    public String toString() {
        if (held) {
            return value + "*";
        }
        return Integer.toString(value);
    }
}
