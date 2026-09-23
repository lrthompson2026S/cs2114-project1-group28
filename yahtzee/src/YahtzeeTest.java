import student.TestCase;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Tests the Yahtzee class.
 *
 * @author Lucas Thompson (lrthompson@vt.edu)
 * @version 09.23.2026
 * @since 09.23.2026
 */
public class YahtzeeTest extends TestCase {

    private Yahtzee game;
    private InputStream originalIn;
    private PrintStream originalOut;


    /**
     * Creates a fresh game before each test.
     */
    @Override
    public void setUp() {
        game = new Yahtzee();
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
     * Tests the initial game state.
     */
    public void testInitialState() throws Exception {
        assertEquals(0, getPlayers().size());
        assertNotNull(getDice());
    }


    /**
     * Tests immediately quitting from the main menu.
     */
    public void testStartQuit() {
        setInput("2");

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        game.start();

        assertTrue(output.toString().contains("Welcome"));
    }


    /**
     * Tests setup for multiple players and resetting the shared dice.
     */
    public void testGameSetup() throws Exception {
        TrackingDiceSet dice = new TrackingDiceSet();
        setDice(dice);

        setInput("2", "Alice", "Bob");
        invoke("gameSetup");

        List<Player> players = getPlayers();
        assertEquals(2, players.size());
        assertTrue(players.get(0).toString().startsWith("Alice\n"));
        assertTrue(players.get(1).toString().startsWith("Bob\n"));
        assertEquals(1, dice.resetCount);
    }


    /**
     * Tests game completion for empty, incomplete, and complete player lists.
     */
    public void testGameComplete() throws Exception {
        assertFalse(invokeBoolean("gameComplete"));

        setPlayers(Arrays.asList(new StubPlayer("A", false)));
        assertFalse(invokeBoolean("gameComplete"));

        setPlayers(Arrays.asList(new StubPlayer("A", true),
            new StubPlayer("B", true)));
        assertTrue(invokeBoolean("gameComplete"));
    }


    /**
     * Tests that the game loop safely handles no players.
     */
    public void testGameLoopEmpty() throws Exception {
        invoke("gameLoop");
        assertEquals(0, getPlayers().size());
    }


    /**
     * Tests that only incomplete players receive turns.
     */
    public void testGameLoop() throws Exception {
        StubPlayer first = new StubPlayer("A", false);
        StubPlayer second = new StubPlayer("B", true);

        setPlayers(Arrays.asList(first, second));
        invoke("gameLoop");

        assertEquals(1, first.turns);
        assertEquals(0, second.turns);
        assertTrue(first.isComplete());
    }


    /**
     * Tests final result display.
     */
    public void testDisplayResults() throws Exception {
        setPlayers(Arrays.asList(new StubPlayer("Alice", true),
            new StubPlayer("Bob", true)));

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        invoke("displayResults");

        String result = output.toString();
        assertTrue(result.contains("Game over!"));
        assertTrue(result.contains("Alice"));
        assertTrue(result.contains("Bob"));
    }


    /**
     * Tests the complete Play path by scratching all thirteen categories.
     */
    public void testStartPlayCompleteGame() throws Exception {
        setDice(new NoScoreDiceSet());

        List<String> lines = new ArrayList<>();
        lines.add("1");
        lines.add("1");
        lines.add("Alice");

        for (int i = 0; i < 13; i++) {
            lines.add("4");
            lines.add("1");
            lines.add("1");
        }

        lines.add("2");
        setInput(lines.toArray(new String[0]));

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        game.start();

        assertEquals(1, getPlayers().size());
        assertTrue(getPlayers().get(0).isComplete());
        assertTrue(output.toString().contains("Game over!"));
        assertTrue(output.toString().contains("Alice"));
    }


    /**
     * Returns the private player list.
     */
    @SuppressWarnings("unchecked")
    private List<Player> getPlayers() throws Exception {
        Field field = Yahtzee.class.getDeclaredField("players");
        field.setAccessible(true);
        return (List<Player>)field.get(game);
    }


    /**
     * Replaces the private player list.
     */
    private void setPlayers(List<Player> players) throws Exception {
        Field field = Yahtzee.class.getDeclaredField("players");
        field.setAccessible(true);
        field.set(game, new ArrayList<>(players));
    }


    /**
     * Returns the shared private dice set.
     */
    private DiceSet getDice() throws Exception {
        Field field = Yahtzee.class.getDeclaredField("dice");
        field.setAccessible(true);
        return (DiceSet)field.get(game);
    }


    /**
     * Replaces the shared private dice set.
     */
    private void setDice(DiceSet dice) throws Exception {
        Field field = Yahtzee.class.getDeclaredField("dice");
        field.setAccessible(true);
        field.set(game, dice);
    }


    /**
     * Invokes a private no-argument Yahtzee method.
     */
    private Object invoke(String name) throws Exception {
        Method method = Yahtzee.class.getDeclaredMethod(name);
        method.setAccessible(true);
        return method.invoke(game);
    }


    /**
     * Invokes a private boolean Yahtzee method.
     */
    private boolean invokeBoolean(String name) throws Exception {
        return (Boolean)invoke(name);
    }


    /**
     * Supplies one console line to each Input object created by Yahtzee.
     */
    private void setInput(String... lines) {
        ChainedInputStream.install(lines);
    }


    /**
     * Player test double with controlled completion state.
     */
    private static class StubPlayer extends Player {
        private final String testName;
        private boolean complete;
        private int turns;


        /**
         * Creates a controlled player.
         */
        StubPlayer(String name, boolean complete) {
            super(name);
            this.testName = name;
            this.complete = complete;
        }


        @Override
        public boolean isComplete() {
            return complete;
        }


        @Override
        public void takeTurn(DiceSet dice) {
            turns++;
            complete = true;
        }


        @Override
        public String toString() {
            return testName + " result";
        }
    }




    /**
     * DiceSet used to verify setup reset behavior.
     */
    private static class TrackingDiceSet extends DiceSet {
        private int resetCount;


        public TrackingDiceSet() {
            super(null);
        }


        @Override
        public void reset() {
            resetCount++;
        }
    }




    /**
     * DiceSet that never exposes a scoreable category.
     */
    private static class NoScoreDiceSet extends DiceSet {
        private int rolls;


        /**
         * Creates a set of five unrolled dice with a roll count of zero.
         */
        public NoScoreDiceSet() {
            super(null);
        }


        @Override
        public void reset() {
            rolls = 0;
        }


        @Override
        public void roll() {
            if (rolls < 3) {
                rolls++;
            }
        }


        @Override
        public int getRolls() {
            return rolls;
        }


        @Override
        public List<Board.Section> getValidSections() {
            return new ArrayList<>();
        }


        @Override
        public String toString() {
            return "1 2 3 4 5";
        }
    }




    /**
     * Input stream that returns no more than one line per bulk read.
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
