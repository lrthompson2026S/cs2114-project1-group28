import student.TestCase;

import java.util.List;

/**
 * Tests the Board class.
 *
 * @author Lucas Thompson (lrthompson@vt.edu)
 * @version 09.23.2026
 * @since 09.23.2026
 */
public class BoardTest extends TestCase {

    private Board board;


    /**
     * Creates a fresh board before each test.
     */
    @Override
    public void setUp() {
        board = new Board();
    }


    /**
     * Creates controlled dice for a section.
     *
     * @param section
     *     valid section
     * @param values
     *     scoring values
     * @return controlled dice set
     */
    private TestDiceSet dice(Board.Section section, int... values) {

        return new TestDiceSet(section, values);
    }


    /**
     * Verifies the initial board state.
     */
    public void testInitialState() {
        assertEquals(13, board.getAvailableSections().size());
        assertEquals(0, board.totalScore());
        assertFalse(board.isComplete());

        for (Board.Upper.Section section : Board.Upper.Section.values()) {

            assertFalse(board.isScored(section));
            assertTrue(board.getAvailableSections().contains(section));
        }

        for (Board.Lower.Section section : Board.Lower.Section.values()) {

            assertFalse(board.isScored(section));
            assertTrue(board.getAvailableSections().contains(section));
        }
    }


    /**
     * Tests scoring every upper section.
     */
    public void testUpperScoring() {
        Board current;

        current = new Board();
        assertTrue(current.scoreSection(Board.Upper.Section.Aces,
            dice(Board.Upper.Section.Aces, 1, 1, 1)));
        assertEquals(3, current.totalScore());

        current = new Board();
        assertTrue(current.scoreSection(Board.Upper.Section.Twos,
            dice(Board.Upper.Section.Twos, 2, 2, 2)));
        assertEquals(6, current.totalScore());

        current = new Board();
        assertTrue(current.scoreSection(Board.Upper.Section.Threes,
            dice(Board.Upper.Section.Threes, 3, 3, 3)));
        assertEquals(9, current.totalScore());

        current = new Board();
        assertTrue(current.scoreSection(Board.Upper.Section.Fours,
            dice(Board.Upper.Section.Fours, 4, 4, 4)));
        assertEquals(12, current.totalScore());

        current = new Board();
        assertTrue(current.scoreSection(Board.Upper.Section.Fives,
            dice(Board.Upper.Section.Fives, 5, 5, 5)));
        assertEquals(15, current.totalScore());

        current = new Board();
        assertTrue(current.scoreSection(Board.Upper.Section.Sixes,
            dice(Board.Upper.Section.Sixes, 6, 6, 6)));
        assertEquals(18, current.totalScore());
    }


    /**
     * Tests scoring every lower section.
     */
    public void testLowerScoring() {
        Board current;

        current = new Board();
        assertTrue(current.scoreSection(Board.Lower.Section.ThreeOfKind,
            dice(Board.Lower.Section.ThreeOfKind, 2, 2, 2, 4, 5)));
        assertEquals(15, current.totalScore());

        current = new Board();
        assertTrue(current.scoreSection(Board.Lower.Section.FourOfKind,
            dice(Board.Lower.Section.FourOfKind, 3, 3, 3, 3, 5)));
        assertEquals(17, current.totalScore());

        current = new Board();
        assertTrue(current.scoreSection(Board.Lower.Section.FullHouse,
            dice(Board.Lower.Section.FullHouse, 2, 2, 3, 3, 3)));
        assertEquals(25, current.totalScore());

        current = new Board();
        assertTrue(current.scoreSection(Board.Lower.Section.SmallStraight,
            dice(Board.Lower.Section.SmallStraight, 1, 2, 3, 4, 6)));
        assertEquals(30, current.totalScore());

        current = new Board();
        assertTrue(current.scoreSection(Board.Lower.Section.LargeStraight,
            dice(Board.Lower.Section.LargeStraight, 2, 3, 4, 5, 6)));
        assertEquals(40, current.totalScore());

        current = new Board();
        assertTrue(current.scoreSection(Board.Lower.Section.Yahtzee,
            dice(Board.Lower.Section.Yahtzee, 6, 6, 6, 6, 6)));
        assertEquals(50, current.totalScore());

        current = new Board();
        assertTrue(current.scoreSection(Board.Lower.Section.Chance,
            dice(Board.Lower.Section.Chance, 1, 2, 3, 4, 6)));
        assertEquals(16, current.totalScore());
    }


    /**
     * Tests that an Upper section cannot be scored twice.
     */
    public void testCannotScoreUpperTwice() {
        TestDiceSet currentDice = dice(Board.Upper.Section.Aces, 1, 1, 1);

        assertTrue(board.scoreSection(Board.Upper.Section.Aces, currentDice));

        assertFalse(board.scoreSection(Board.Upper.Section.Aces, currentDice));

        assertEquals(3, board.totalScore());
    }


    /**
     * Tests that a Lower section cannot be scored twice.
     */
    public void testCannotScoreLowerTwice() {
        TestDiceSet currentDice =
            dice(Board.Lower.Section.Chance, 1, 2, 3, 4, 5);

        assertTrue(board.scoreSection(Board.Lower.Section.Chance, currentDice));

        assertFalse(
            board.scoreSection(Board.Lower.Section.Chance, currentDice));

        assertEquals(15, board.totalScore());
    }


    /**
     * Tests scratching upper and lower sections.
     */
    public void testScratch() {
        assertTrue(board.scratch(Board.Upper.Section.Aces));

        assertTrue(board.isScored(Board.Upper.Section.Aces));

        assertFalse(
            board.getAvailableSections().contains(Board.Upper.Section.Aces));

        assertEquals(0, board.totalScore());

        assertFalse(board.scratch(Board.Upper.Section.Aces));

        assertTrue(board.scratch(Board.Lower.Section.Yahtzee));

        assertTrue(board.isScored(Board.Lower.Section.Yahtzee));

        assertFalse(
            board.getAvailableSections().contains(Board.Lower.Section.Yahtzee));

        assertEquals(0, board.totalScore());

        assertFalse(board.scratch(Board.Lower.Section.Yahtzee));
    }


    /**
     * Tests that an Upper section cannot be scratched twice.
     */
    public void testCannotScratchUpperTwice() {
        TestDiceSet currentDice = dice(Board.Upper.Section.Aces, 1, 1, 1);

        assertTrue(board.scoreSection(Board.Upper.Section.Aces, currentDice));

        assertFalse(board.scratch(Board.Upper.Section.Aces));

        assertEquals(3, board.totalScore());
    }


    /**
     * Tests that a Lower section cannot be scratched twice.
     */
    public void testCannotScratchLowerTwice() {
        TestDiceSet currentDice =
            dice(Board.Lower.Section.Chance, 1, 2, 3, 4, 5);

        assertTrue(board.scoreSection(Board.Lower.Section.Chance, currentDice));

        assertFalse(board.scratch(Board.Lower.Section.Chance));

        assertEquals(15, board.totalScore());
    }


    /**
     * tests whether a section can be scored if it is not scoreable
     */
    public void testUpperSectionNotScoreable() {
        TestDiceSet currentDice = dice(Board.Upper.Section.Twos, 2, 2);

        assertFalse(board.scoreSection(Board.Upper.Section.Aces, currentDice));

        assertFalse(board.isScored(Board.Upper.Section.Aces));
    }


    /**
     * tests whether a section can be scored if it is not scoreable
     */
    public void testLowerSectionNotScoreable() {
        TestDiceSet currentDice =
            dice(Board.Lower.Section.Chance, 1, 2, 3, 4, 5);

        assertFalse(
            board.scoreSection(Board.Lower.Section.Yahtzee, currentDice));

        assertFalse(board.isScored(Board.Lower.Section.Yahtzee));
    }


    /**
     * Tests completion after every category is used.
     */
    public void testComplete() {
        assertFalse(board.isComplete());

        for (Board.Upper.Section section : Board.Upper.Section.values()) {

            assertTrue(board.scratch(section));
        }

        assertFalse(board.isComplete());

        for (Board.Lower.Section section : Board.Lower.Section.values()) {

            assertTrue(board.scratch(section));
        }

        assertTrue(board.isComplete());
        assertEquals(0, board.getAvailableSections().size());
    }


    /**
     * Tests the upper bonus boundary.
     */
    public void testUpperBonusAt63() {
        assertTrue(board.scoreSection(Board.Upper.Section.Aces,
            dice(Board.Upper.Section.Aces, 1, 1, 1)));

        assertTrue(board.scoreSection(Board.Upper.Section.Twos,
            dice(Board.Upper.Section.Twos, 2, 2, 2)));

        assertTrue(board.scoreSection(Board.Upper.Section.Threes,
            dice(Board.Upper.Section.Threes, 3, 3, 3)));

        assertTrue(board.scoreSection(Board.Upper.Section.Fours,
            dice(Board.Upper.Section.Fours, 4, 4, 4)));

        assertTrue(board.scoreSection(Board.Upper.Section.Fives,
            dice(Board.Upper.Section.Fives, 5, 5, 5)));

        assertTrue(board.scoreSection(Board.Upper.Section.Sixes,
            dice(Board.Upper.Section.Sixes, 6, 6, 6)));

        // 63 upper points + 35 bonus
        assertEquals(98, board.totalScore());
    }


    /**
     * Tests that the bonus is not awarded below 63.
     */
    public void testNoUpperBonusBelow63() {
        assertTrue(board.scoreSection(Board.Upper.Section.Aces,
            dice(Board.Upper.Section.Aces, 1, 1)));

        assertTrue(board.scoreSection(Board.Upper.Section.Twos,
            dice(Board.Upper.Section.Twos, 2, 2, 2)));

        assertTrue(board.scoreSection(Board.Upper.Section.Threes,
            dice(Board.Upper.Section.Threes, 3, 3, 3)));

        assertTrue(board.scoreSection(Board.Upper.Section.Fours,
            dice(Board.Upper.Section.Fours, 4, 4, 4)));

        assertTrue(board.scoreSection(Board.Upper.Section.Fives,
            dice(Board.Upper.Section.Fives, 5, 5, 5)));

        assertTrue(board.scoreSection(Board.Upper.Section.Sixes,
            dice(Board.Upper.Section.Sixes, 6, 6, 6)));

        // 2 + 6 + 9 + 12 + 15 + 18 = 62
        assertEquals(62, board.totalScore());
    }


    /**
     * Tests string output for unscored, scratched, and scored sections.
     */
    public void testToString() {
        assertTrue(board.scoreSection(Board.Upper.Section.Aces,
            dice(Board.Upper.Section.Aces, 1, 1, 1)));

        assertTrue(board.scratch(Board.Upper.Section.Twos));

        String result = board.toString();

        assertTrue(result.contains("Upper Section"));
        assertTrue(result.contains("Lower Section"));

        assertTrue(result.contains(
            String.format("%-20s %s", Board.Upper.Section.Aces, "3")));

        assertTrue(result.contains(
            String.format("%-20s %s", Board.Upper.Section.Twos, "X")));

        assertTrue(result.contains(
            String.format("%-20s %s", Board.Upper.Section.Threes, "__")));

        assertTrue(
            result.contains(String.format("%-20s %d", "Upper Total", 3)));

        assertTrue(
            result.contains(String.format("%-20s %d", "Lower Total", 0)));
    }


    /**
     * Tests an implementation of Section that does not belong to either board
     * section.
     */
    public void testInvalidBoardSection() {
        Board.Section invalid = new InvalidSection();

        try {
            board.isScored(invalid);
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e) {
            // expected
        }

        try {
            board.scratch(invalid);
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e) {
            // expected
        }

        try {
            board.scoreSection(invalid,
                dice(Board.Upper.Section.Aces, 1, 1, 1));

            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e) {
            // expected
        }
    }


    /**
     * Tests invalid sections supplied directly to Upper.
     */
    public void testInvalidUpperSection() {
        Board.Upper upper = new Board.Upper();

        try {
            upper.isScored(null);
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e) {
            // expected
        }

        try {
            upper.scratch(null);
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e) {
            // expected
        }

        try {
            upper.scoreSection(null, null);
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e) {
            // expected
        }
    }


    /**
     * Tests invalid sections supplied directly to Lower.
     */
    public void testInvalidLowerSection() {
        Board.Lower lower = new Board.Lower();

        try {
            lower.isScored(null);
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e) {
            // expected
        }

        try {
            lower.scratch(null);
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e) {
            // expected
        }

        try {
            lower.scoreSection(null, null);
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e) {
            // expected
        }
    }


    /**
     * A controlled DiceSet used to test Board without random rolls.
     */
    private static class TestDiceSet extends DiceSet {

        private Board.Section validSection;
        private int[] scoringValues;


        /**
         * Creates a dice set that reports one valid section and the specified
         * scoring values.
         *
         * @param section
         *     the section considered valid
         * @param values
         *     values contributing to its score
         */
        public TestDiceSet(Board.Section section, int... values) {

            validSection = section;
            scoringValues = values.clone();
        }


        /**
         * Returns the single section this test dice set satisfies.
         *
         * @return valid sections
         */
        @Override
        public List<Board.Section> getValidSections() {
            return List.of(validSection);
        }


        /**
         * Returns the controlled scoring values.
         *
         * @param section
         *     requested scoring section
         * @return scoring values
         */
        @Override
        public int[] getScoringValues(Board.Section section) {
            return scoringValues.clone();
        }
    }




    /**
     * A section implementation that does not belong to the upper or lower
     * board.
     */
    private static class InvalidSection implements Board.Section {
        // intentionally empty
    }
}
