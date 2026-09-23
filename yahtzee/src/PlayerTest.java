import student.TestCase;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Tests the Player class.
 *
 * @author Lucas Thompson (lrthompson@vt.edu)
 * @version 09.23.2026
 * @since 09.23.2026
 */
public class PlayerTest extends TestCase {

    private Player player;
    private InputStream originalIn;
    private PrintStream originalOut;


    /**
     * Creates a fresh player before each test.
     */
    @Override
    public void setUp() {
        player = new Player("Lucas");
        originalIn = System.in;
        originalOut = System.out;
    }


    /**
     * Restores console streams after each test.
     */
    @Override
    public void tearDown() {
        System.setIn(originalIn);
        System.setOut(originalOut);
    }


    /**
     * Tests the initial player state and string representation.
     */
    public void testInitialState() {
        assertFalse(player.isComplete());

        String result = player.toString();
        assertTrue(result.startsWith("Lucas\n"));
        assertTrue(result.contains("Upper Section"));
        assertTrue(result.contains("Lower Section"));
    }


    /**
     * Tests action selection while rolls are still available.
     */
    public void testChooseActionBeforeMaximumRolls() throws Exception {
        setInput("3");

        assertEquals("Roll",
            invokeString("chooseAction", new Class<?>[] { int.class }, 1));
    }


    /**
     * Tests that scoring is the only action after three rolls.
     */
    public void testChooseActionAtMaximumRolls() throws Exception {
        setInput("1");

        assertEquals("Score",
            invokeString("chooseAction", new Class<?>[] { int.class }, 3));
    }


    /**
     * Tests holding and releasing selected dice.
     */
    public void testHoldAndReleaseDie() throws Exception {
        TrackingDiceSet dice = new TrackingDiceSet();

        setInput("2");
        invoke("holdDie", new Class<?>[] { DiceSet.class }, dice);
        assertEquals(1, dice.heldIndex);

        setInput("4");
        invoke("releaseDie", new Class<?>[] { DiceSet.class }, dice);
        assertEquals(3, dice.releasedIndex);
    }


    /**
     * Tests scoring a valid section through the score menu.
     */
    public void testTryScore() throws Exception {
        TrackingDiceSet dice =
            new TrackingDiceSet(Arrays.asList(Board.Upper.Section.Aces), 1, 1,
                1);

        setInput("1");

        assertTrue(
            invokeBoolean("tryScore", new Class<?>[] { DiceSet.class }, dice));

        Board board = getBoard();
        assertTrue(board.isScored(Board.Upper.Section.Aces));
        assertEquals(3, board.totalScore());
    }


    /**
     * Tests choosing Scratch from inside the score menu.
     */
    public void testTryScoreScratch() throws Exception {
        TrackingDiceSet dice = new TrackingDiceSet();

        setInput("1", "1");

        assertTrue(
            invokeBoolean("tryScore", new Class<?>[] { DiceSet.class }, dice));

        Board board = getBoard();
        assertTrue(board.isScored(Board.Upper.Section.Aces));
        assertEquals(0, board.totalScore());
    }


    /**
     * Tests the failed-score path if the dice validity changes before scoring.
     */
    public void testTryScoreFailure() throws Exception {
        FlakyDiceSet dice = new FlakyDiceSet();

        setInput("1");

        assertFalse(
            invokeBoolean("tryScore", new Class<?>[] { DiceSet.class }, dice));
        assertFalse(getBoard().isScored(Board.Upper.Section.Aces));
    }


    /**
     * Tests a complete turn containing hold, release, roll, and scratch
     * actions.
     */
    public void testTakeTurnActions() throws Exception {
        TrackingDiceSet dice = new TrackingDiceSet();

        setInput("1", "2", "2", "2", "3", "4", "1", "1");

        player.takeTurn(dice);

        assertEquals(1, dice.heldIndex);
        assertEquals(1, dice.releasedIndex);
        assertEquals(2, dice.rolls);
        assertEquals(1, dice.resetCount);
        assertTrue(getBoard().isScored(Board.Upper.Section.Aces));
    }


    /**
     * Tests that a completed player does not begin another turn.
     */
    public void testCompletePlayerDoesNotTakeTurn() throws Exception {
        completeBoard();
        TrackingDiceSet dice = new TrackingDiceSet();

        assertTrue(player.isComplete());

        player.takeTurn(dice);

        assertEquals(0, dice.resetCount);
        assertEquals(0, dice.rolls);
    }


    /**
     * Tests score helpers when no sections remain. Note that scratch can never
     * be called if sections are available
     */
    public void testNoSectionsRemain() throws Exception {
        completeBoard();

        assertFalse(invokeBoolean("tryScore", new Class<?>[] { DiceSet.class },
            new TrackingDiceSet()));

        assertNull(
            invoke("chooseSection", new Class<?>[] { String.class }, "Choose"));
    }


    /**
     * Scratches every section on this player's board.
     */
    private void completeBoard() throws Exception {
        Board board = getBoard();

        for (Board.Upper.Section section : Board.Upper.Section.values()) {
            board.scratch(section);
        }

        for (Board.Lower.Section section : Board.Lower.Section.values()) {
            board.scratch(section);
        }
    }


    /**
     * Returns the player's private board for state verification.
     *
     * @return this player's board
     */
    private Board getBoard() throws Exception {
        Field field = Player.class.getDeclaredField("board");
        field.setAccessible(true);
        return (Board)field.get(player);
    }


    /**
     * Invokes a private Player method.
     */
    private Object invoke(String name, Class<?>[] types, Object... arguments)
        throws Exception {

        Method method = Player.class.getDeclaredMethod(name, types);
        method.setAccessible(true);
        return method.invoke(player, arguments);
    }


    /**
     * Invokes a private Player method returning boolean.
     */
    private boolean invokeBoolean(
        String name,
        Class<?>[] types,
        Object... arguments) throws Exception {

        return (Boolean)invoke(name, types, arguments);
    }


    /**
     * Invokes a private Player method returning String.
     */
    private String invokeString(
        String name,
        Class<?>[] types,
        Object... arguments) throws Exception {

        return (String)invoke(name, types, arguments);
    }


    /**
     * Supplies one console line to each Input object created by Player.
     */
    private void setInput(String... lines) {
        ChainedInputStream.install(lines);
    }


    /**
     * DiceSet test double used to control Player turn behavior.
     */
    private static class TrackingDiceSet extends DiceSet {
        private int rolls;
        private int resetCount;
        private int heldIndex = -1;
        private int releasedIndex = -1;
        private List<Board.Section> valid;
        private int[] scoringValues;


        /**
         * Creates dice with no scoreable sections.
         */
        TrackingDiceSet() {
            this(new ArrayList<>());
        }


        /**
         * Creates dice with controlled valid sections.
         */
        TrackingDiceSet(List<Board.Section> valid, int... scoringValues) {
            super(null);

            this.valid = valid;
            this.scoringValues = scoringValues;
        }


        @Override
        public void roll() {
            if (rolls < 3) {
                rolls++;
            }
        }


        @Override
        public void reset() {
            rolls = 0;
            resetCount++;
        }


        @Override
        public int getRolls() {
            return rolls;
        }


        @Override
        public boolean hold(int index) {
            heldIndex = index;
            return true;
        }


        @Override
        public boolean release(int index) {
            releasedIndex = index;
            return true;
        }


        @Override
        public List<Board.Section> getValidSections() {
            return new ArrayList<>(valid);
        }


        @Override
        public int[] getScoringValues(Board.Section section) {
            if (valid.contains(section)) {
                return scoringValues.clone();
            }
            return new int[0];
        }


        @Override
        public String toString() {
            return "1 1 1 1 1";
        }
    }




    /**
     * DiceSet that is valid on the menu check but invalid during board
     * scoring.
     */
    private static class FlakyDiceSet extends TrackingDiceSet {
        private int calls;


        /**
         * Creates dice initially valid for Aces.
         */
        FlakyDiceSet() {
            super(Arrays.asList(Board.Upper.Section.Aces), 1, 1, 1);
        }


        @Override
        public List<Board.Section> getValidSections() {
            calls++;
            if (calls == 1) {
                return Arrays.asList(Board.Upper.Section.Aces);
            }
            return new ArrayList<>();
        }
    }




    /**
     * Input stream that returns no more than one line per bulk read. This keeps
     * separate Scanner instances from getting later scripted prompts.
     *
     * GenAI - ChatGPT
     */
    private static class ChainedInputStream extends ByteArrayInputStream {
        private final String[] remaining;
        private boolean advanced;


        /**
         * Creates a stream containing one scripted input line.
         */
        ChainedInputStream(String line, String[] remaining) {
            super((line + "\n").getBytes());
            this.remaining = remaining;
            this.advanced = false;
        }


        /**
         * Installs a chain of one-line streams on System.in.
         */
        static void install(String... lines) {
            System.setIn(create(lines, 0));
        }


        /**
         * Creates the stream for one position in the input script.
         */
        private static InputStream create(String[] lines, int index) {
            if (index >= lines.length) {
                return new ByteArrayInputStream(new byte[0]);
            }

            String[] rest = Arrays.copyOfRange(lines, index + 1, lines.length);
            return new ChainedInputStream(lines[index], rest);
        }


        /**
         * Advances System.in to the next scripted line.
         */
        private void advance() {
            if (!advanced) {
                advanced = true;
                System.setIn(create(remaining, 0));
            }
        }


        @Override
        public synchronized int read() {
            int value = super.read();

            if (value == '\n' || value == -1) {
                advance();
            }

            return value;
        }


        @Override
        public synchronized int read(byte[] buffer, int offset, int length) {
            int countRead = super.read(buffer, offset, length);

            if (countRead == -1) {
                advance();
                return -1;
            }

            for (int i = offset; i < offset + countRead; i++) {
                if (buffer[i] == '\n') {
                    advance();
                    break;
                }
            }

            return countRead;
        }
    }
}
