import student.TestCase;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Random;

/**
 * Tests the DiceSet class.
 *
 * @author Lucas Thompson (lrthompson@vt.edu)
 * @version 09.23.2026
 * @since 09.23.2026
 */
public class DiceSetTest extends TestCase {

    private DiceSet dice;


    /**
     * Creates a fresh dice set before each test.
     */
    @Override
    public void setUp() {
        dice = new DiceSet(new Random());
    }


    /**
     * Tests the initial dice state.
     */
    public void testInitialState() {
        assertEquals(0, dice.getRolls());
        assertEquals("0 0 0 0 0", dice.toString());
        assertTrue(dice.getValidSections().isEmpty());
        assertEquals(0,
            dice.getScoringValues(Board.Lower.Section.Chance).length);
        assertEquals(0, dice.getScoringValues(null).length);
    }


    /**
     * Tests rolling and the three-roll limit.
     */
    public void testRollAndMaximumRolls() throws Exception {
        setRandom(
            new SequenceRandom(0, 1, 2, 3, 4, 5, 4, 3, 2, 1, 1, 1, 1, 1, 1, 5,
                5, 5, 5, 5));

        dice.roll();
        assertEquals(1, dice.getRolls());
        assertEquals("1 2 3 4 5", dice.toString());

        dice.roll();
        assertEquals(2, dice.getRolls());
        assertEquals("6 5 4 3 2", dice.toString());

        dice.roll();
        assertEquals(3, dice.getRolls());
        assertEquals("2 2 2 2 2", dice.toString());

        dice.roll();
        assertEquals(3, dice.getRolls());
        assertEquals("2 2 2 2 2", dice.toString());
    }


    /**
     * Tests holding and releasing one die.
     */
    public void testHoldAndRelease() throws Exception {
        setRandom(new SequenceRandom(0, 1, 2, 3, 4, 5, 5, 5, 5, 2, 2, 2, 2, 2));

        dice.roll();
        assertTrue(dice.hold(0));
        assertEquals("1* 2 3 4 5", dice.toString());

        dice.roll();
        assertEquals("1* 6 6 6 6", dice.toString());

        assertTrue(dice.release(0));
        dice.roll();
        assertEquals("3 3 3 3 3", dice.toString());
    }


    /**
     * Tests invalid die indexes.
     */
    public void testInvalidIndexes() {
        assertFalse(dice.hold(-1));
        assertFalse(dice.hold(5));
        assertFalse(dice.release(-1));
        assertFalse(dice.release(5));
        assertEquals("0 0 0 0 0", dice.toString());
    }


    /**
     * Tests releasing every die at once.
     */
    public void testReleaseAll() throws Exception {
        setRandom(new SequenceRandom(0, 1, 2, 3, 4, 5, 5, 5, 5, 5));

        dice.roll();
        dice.hold(0);
        dice.hold(2);
        dice.hold(4);

        assertEquals("1* 2 3* 4 5*", dice.toString());

        dice.releaseAll();
        assertEquals("1 2 3 4 5", dice.toString());

        dice.roll();
        assertEquals("6 6 6 6 6", dice.toString());
    }


    /**
     * Tests reset behavior.
     */
    public void testReset() throws Exception {
        rollValues(1, 2, 3, 4, 5);
        dice.hold(1);
        dice.hold(3);

        assertEquals(1, dice.getRolls());
        assertEquals("1 2* 3 4* 5", dice.toString());

        dice.reset();

        assertEquals(0, dice.getRolls());
        assertEquals("1 2 3 4 5", dice.toString());
    }


    /**
     * Tests full-house section detection.
     */
    public void testFullHouseSections() throws Exception {
        rollValues(2, 2, 3, 3, 3);

        List<Board.Section> valid = dice.getValidSections();

        assertTrue(valid.contains(Board.Upper.Section.Twos));
        assertTrue(valid.contains(Board.Upper.Section.Threes));
        assertTrue(valid.contains(Board.Lower.Section.ThreeOfKind));
        assertTrue(valid.contains(Board.Lower.Section.FullHouse));
        assertTrue(valid.contains(Board.Lower.Section.Chance));

        assertFalse(valid.contains(Board.Lower.Section.FourOfKind));
        assertFalse(valid.contains(Board.Lower.Section.SmallStraight));
        assertFalse(valid.contains(Board.Lower.Section.LargeStraight));
        assertFalse(valid.contains(Board.Lower.Section.Yahtzee));
    }


    /**
     * Tests small- and large-straight detection.
     */
    public void testStraightSections() throws Exception {
        rollValues(1, 2, 3, 4, 6);

        List<Board.Section> small = dice.getValidSections();
        assertTrue(small.contains(Board.Lower.Section.SmallStraight));
        assertFalse(small.contains(Board.Lower.Section.LargeStraight));

        dice = new DiceSet(null);
        rollValues(2, 3, 4, 5, 6);

        List<Board.Section> large = dice.getValidSections();
        assertTrue(large.contains(Board.Lower.Section.SmallStraight));
        assertTrue(large.contains(Board.Lower.Section.LargeStraight));
        assertTrue(large.contains(Board.Lower.Section.Chance));
    }


    /**
     * Tests Yahtzee and of-a-kind detection.
     */
    public void testYahtzeeSections() throws Exception {
        rollValues(6, 6, 6, 6, 6);

        List<Board.Section> valid = dice.getValidSections();

        assertTrue(valid.contains(Board.Upper.Section.Sixes));
        assertTrue(valid.contains(Board.Lower.Section.ThreeOfKind));
        assertTrue(valid.contains(Board.Lower.Section.FourOfKind));
        assertTrue(valid.contains(Board.Lower.Section.Yahtzee));
        assertTrue(valid.contains(Board.Lower.Section.Chance));
        assertFalse(valid.contains(Board.Lower.Section.FullHouse));
    }


    /**
     * Tests values returned for upper and lower scoring sections.
     */
    public void testScoringValues() throws Exception {
        rollValues(1, 1, 3, 4, 5);

        int[] aces = dice.getScoringValues(Board.Upper.Section.Aces);
        assertEquals(2, aces.length);
        assertEquals(1, aces[0]);
        assertEquals(1, aces[1]);

        int[] chance = dice.getScoringValues(Board.Lower.Section.Chance);
        assertEquals(5, chance.length);
        assertEquals(1, chance[0]);
        assertEquals(1, chance[1]);
        assertEquals(3, chance[2]);
        assertEquals(4, chance[3]);
        assertEquals(5, chance[4]);

        assertEquals(0,
            dice.getScoringValues(Board.Upper.Section.Sixes).length);
    }


    /**
     * Tests the dice display including held state.
     */
    public void testToString() throws Exception {
        rollValues(1, 2, 3, 4, 5);
        dice.hold(1);
        dice.hold(4);

        assertEquals("1 2* 3 4 5*", dice.toString());
    }


    /**
     * Rolls the requested face values into the current dice set.
     *
     * @param values
     *     five die face values from 1 through 6
     */
    private void rollValues(int... values) throws Exception {
        int[] randomValues = new int[values.length];

        for (int i = 0; i < values.length; i++) {
            randomValues[i] = values[i] - 1;
        }

        setRandom(new SequenceRandom(randomValues));
        dice.roll();
    }


    /**
     * Replaces the DiceSet random generator for repeatable tests.
     *
     * @param random
     *     controlled random generator
     */
    private void setRandom(Random random) throws Exception {
        Field field = DiceSet.class.getDeclaredField("random");
        field.setAccessible(true);
        field.set(dice, random);
    }


    /**
     * Random implementation that returns a fixed sequence.
     */
    private static class SequenceRandom extends Random {
        private final int[] values;
        private int index;


        /**
         * Creates a random generator using the supplied sequence.
         *
         * @param values
         *     values returned by successive nextInt calls
         */
        SequenceRandom(int... values) {
            this.values = values;
            this.index = 0;
        }


        /**
         * Returns the next controlled value.
         *
         * @param bound
         *     upper bound supplied by Die
         * @return the next value in the sequence
         */
        @Override
        public int nextInt(int bound) {
            return values[index++];
        }
    }
}
