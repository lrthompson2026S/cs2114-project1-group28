import student.TestCase;

import java.util.Random;

/**
 * Tests the Die class.
 */
public class DieTest extends TestCase {
    public void testInitialState() {
        Die die = new Die();

        assertEquals(0, die.value());
        assertFalse(die.isHeld());
        assertEquals("0", die.toString());
    }


    public void testRollUsesOneThroughSix() {
        Die die = new Die();
        SequenceRandom random = new SequenceRandom(0, 5);

        die.roll(random);
        assertEquals(1, die.value());
        assertEquals("1", die.toString());

        die.roll(random);
        assertEquals(6, die.value());
        assertEquals("6", die.toString());
    }


    public void testHoldPreventsRoll() {
        Die die = new Die();
        SequenceRandom random = new SequenceRandom(2, 4);

        die.roll(random);
        assertEquals(3, die.value());

        die.hold();
        assertTrue(die.isHeld());
        assertEquals("3*", die.toString());

        die.roll(random);
        assertEquals(3, die.value());
        assertEquals("3*", die.toString());
    }


    public void testReleaseAllowsRollAgain() {
        Die die = new Die();
        SequenceRandom random = new SequenceRandom(1, 4);

        die.roll(random);
        die.hold();
        die.release();

        assertFalse(die.isHeld());
        die.roll(random);
        assertEquals(5, die.value());
        assertEquals("5", die.toString());
    }


    public void testResetReleasesButKeepsValue() {
        Die die = new Die();
        SequenceRandom random = new SequenceRandom(3);

        die.roll(random);
        die.hold();
        die.reset();

        assertFalse(die.isHeld());
        assertEquals(4, die.value());
        assertEquals("4", die.toString());
    }




    /**
     * Random source that returns a known sequence of nextInt values.
     */
    private static class SequenceRandom extends Random {
        private final int[] values;
        private int index;


        SequenceRandom(int... values) {
            this.values = values;
            this.index = 0;
        }


        @Override
        public int nextInt(int bound) {
            if (index >= values.length) {
                throw new AssertionError("Random sequence exhausted");
            }
            int value = values[index++];
            if (value < 0 || value >= bound) {
                throw new AssertionError("Random value outside bound");
            }
            return value;
        }
    }
}
