import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * <p>
 * Board <br> Represents the Yahtzee board.
 * </p>
 *
 * @author Lucas Thompson (lrthompson@vt.edu)
 * @version 09.21.2026
 * @since 09.16.2026
 */
public class Board {

    /**
     * The upper section of the board.
     *
     * @since 09.21.2026
     */
    private Upper upper;
    /**
     * The lower section of the board.
     *
     * @since 09.21.2026
     */
    private Lower lower;


    /**
     * Creates a new Yahtzee board.
     *
     * @since 09.21.2026
     */
    public Board() {
        this.upper = new Upper();
        this.lower = new Lower();
    }


    private static String scoreString(Integer score) {
        if (score == null) {
            return "__";
        }

        if (score == 0) {
            return "X";
        }

        return score.toString();
    }


    /**
     * <p>
     * Checks if a section of the board has been scored.
     * </p>
     *
     * @param section
     *     the section to check
     * @return true if the section has been scored, false otherwise
     * @throws IllegalArgumentException
     *     if the section is invalid
     * @since 09.21.2026
     */
    public boolean isScored(Section section) throws IllegalArgumentException {

        switch (section) {
            case Upper.Section u -> {
                return upper.isScored(u);
            }

            case Lower.Section l -> {
                return lower.isScored(l);
            }

            default -> throw new IllegalArgumentException(
                "The section does not exist!");
        }
    }


    /**
     * <p>
     * Attempts to score a section using the current dice.
     * </p>
     *
     * @param section
     *     the section to score
     * @param dice
     *     the current dice state
     * @return true if the section was scored, false otherwise
     * @throws IllegalArgumentException
     *     if the section is invalid
     * @since 09.21.2026
     */
    public boolean scoreSection(Section section, DiceSet dice)
        throws IllegalArgumentException {

        switch (section) {
            case Upper.Section u -> {
                return upper.scoreSection(u, dice);
            }

            case Lower.Section l -> {
                return lower.scoreSection(l, dice);
            }

            default -> throw new IllegalArgumentException(
                "The section does not exist!");
        }
    }


    /**
     * <p>
     * Gets a list of available sections that have not been scored yet.
     * </p>
     *
     * @return a list of available sections
     * @since 09.21.2026
     */
    public List<Section> getAvailableSections() {
        List<Section> result = new ArrayList<>();
        result.addAll(upper.getAvailableSections());
        result.addAll(lower.getAvailableSections());
        return result;
    }


    /**
     * <p>
     * Attempts to scratch a section of the board.
     * </p>
     *
     * @param section
     *     the section to scratch
     * @return true if the section was scratched, false otherwise
     * @throws IllegalArgumentException
     *     if the section is invalid
     * @since 09.21.2026
     */
    public boolean scratch(Section section) throws IllegalArgumentException {

        switch (section) {
            case Upper.Section u -> {
                return upper.scratch(u);
            }

            case Lower.Section l -> {
                return lower.scratch(l);
            }

            default -> throw new IllegalArgumentException(
                "The section does not exist!");
        }
    }


    /**
     * <p>
     * Returns a string representation of the board.
     * </p>
     *
     * @return a string representation of the board
     * @since 09.21.2026
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder(upper + "\n" + lower + "\n\n");

        result.repeat("-", 25).append('\n');
        result.append(String.format("%-20s %d\n", "Grand Total", totalScore()));
        result.repeat("-", 25).append('\n');

        return result.toString();
    }


    /**
     * <p>
     * Calculates the total score of the board, including the upper section
     * bonus.
     * </p>
     *
     * @return the total score of the board
     * @since 09.21.2026
     */
    public int totalScore() {
        return upper.totalScore() + upper.bonus() + lower.totalScore();
    }


    /**
     * <p>
     * Checks if every section of the board has been scored or scratched.
     * </p>
     *
     * @return true if the board is complete, false otherwise
     * @since 09.21.2026
     */
    public boolean isComplete() {
        return getAvailableSections().isEmpty();
    }


    /**
     * <p>
     * Section <br> Empty interface to represent a section of the board.
     * </p>
     *
     * @author Lucas Thompson (lrthompson@vt.edu)
     * @since 09.16.2026
     */
    public interface Section {
    }




    /**
     * <p>
     * Upper <br> Represents the upper section of the Yahtzee board.
     * </p>
     *
     * @author Lucas Thompson (lrthompson@vt.edu)
     * @version 09.21.2026
     * @since 09.16.2026
     */
    public class Upper {

        /**
         * The score threshold required to receive the upper section bonus.
         *
         * @since 09.21.2026
         */
        private static final int BONUS_THRESHHOLD = 3 * (1 + 2 + 3 + 4 + 5 + 6);
        /**
         * The value of the upper section bonus.
         *
         * @since 09.21.2026
         */
        private static final int BONUS_VALUE = 35;
        /**
         * <p>
         * Represents the board eg. <br> null -> not scored -> __ <br> number ->
         * scored with that number -> number <br> 0 -> scratched -> X <br>
         * </p>
         *
         * @since 09.16.2026
         */
        private Map<Upper.Section, Integer> sections;


        /**
         * Creates a new Upper section.
         *
         * @since 09.16.2026
         */
        public Upper() {
            sections = new LinkedHashMap<>();

            for (Upper.Section section : Upper.Section.values()) {
                sections.put(section, null);
            }
        }


        /**
         * <p>
         * Checks if a section has been scored.
         * </p>
         *
         * @param section
         *     the section to check
         * @return true if the section has been scored, false otherwise
         * @throws IllegalArgumentException
         *     if the section is invalid
         * @since 09.16.2026
         */
        public boolean isScored(Upper.Section section)
            throws IllegalArgumentException {

            if (!sections.containsKey(section)) {
                throw new IllegalArgumentException(
                    "Invalid section: " + section);
            }

            return sections.get(section) != null;
        }


        /**
         * <p>
         * Scores a section based on the dice roll.
         * </p>
         *
         * @param section
         *     the section to score
         * @param dice
         *     the current dice state
         * @return true if the section was scored, false otherwise
         * @throws IllegalArgumentException
         *     if the section is invalid
         * @since 09.21.2026
         */
        public boolean scoreSection(Upper.Section section, DiceSet dice)
            throws IllegalArgumentException {

            if (!sections.containsKey(section)) {
                throw new IllegalArgumentException(
                    "Invalid section: " + section);
            }

            if (sections.get(section) != null) {
                return false;
            }

            List<Upper.Section> scoreable = dice.getValidSections().stream()
                .filter(sect -> sect instanceof Upper.Section)
                .map(sect -> (Upper.Section)sect).toList();

            if (!scoreable.contains(section)) {
                return false;
            }

            switch (section) {
                case Aces -> sections.put(section,
                    IntStream.of(dice.getScoringValues(Upper.Section.Aces))
                        .sum());

                case Twos -> sections.put(section,
                    IntStream.of(dice.getScoringValues(Upper.Section.Twos))
                        .sum());

                case Threes -> sections.put(section,
                    IntStream.of(dice.getScoringValues(Upper.Section.Threes))
                        .sum());

                case Fours -> sections.put(section,
                    IntStream.of(dice.getScoringValues(Upper.Section.Fours))
                        .sum());

                case Fives -> sections.put(section,
                    IntStream.of(dice.getScoringValues(Upper.Section.Fives))
                        .sum());

                case Sixes -> sections.put(section,
                    IntStream.of(dice.getScoringValues(Upper.Section.Sixes))
                        .sum());
            }

            return true;
        }


        /**
         * <p>
         * Calculates the total score for the upper section.
         * </p>
         *
         * @return the total score
         * @since 09.16.2026
         */
        public int totalScore() {
            int sum = 0;

            for (Integer value : sections.values()) {
                if (value != null) {
                    sum += value;
                }
            }

            return sum;
        }


        /**
         * <p>
         * Calculates the bonus for the upper section.
         * </p>
         *
         * @return the bonus value if the bonus threshold is reached, otherwise
         *     0
         * @since 09.21.2026
         */
        public int bonus() {
            return this.totalScore() > BONUS_THRESHHOLD ? BONUS_VALUE : 0;
        }


        /**
         * <p>
         * Gets a list of available sections that have not been scored yet.
         * </p>
         *
         * @return a list of available sections
         * @since 09.16.2026
         */
        public List<Upper.Section> getAvailableSections() {
            List<Upper.Section> availableSections = new ArrayList<>();

            for (Map.Entry<Upper.Section, Integer> entry : sections.entrySet()) {

                if (entry.getValue() == null) {
                    // a section is available if unscored (null)
                    availableSections.add(entry.getKey());
                }
            }

            return availableSections;
        }


        /**
         * <p>
         * Attempts to scratch a section.
         * </p>
         *
         * @param section
         *     the section to scratch
         * @return true if the section was scratched, false otherwise
         * @throws IllegalArgumentException
         *     if the section is invalid
         * @since 09.16.2026
         */
        public boolean scratch(Upper.Section section) {
            if (!sections.containsKey(section)) {
                throw new IllegalArgumentException(
                    "Invalid section: " + section);
            }

            if (sections.get(section) != null) {
                return false;
            }

            sections.put(section, 0);
            return true;
        }


        /**
         * <p>
         * Returns a string representation of the upper section.
         * </p>
         *
         * @return a string representation of the upper section
         * @since 09.21.2026
         */
        @Override
        public String toString() {
            StringBuilder result = new StringBuilder();

            result.repeat("-", 25).append('\n');
            result.append("Upper Section\n");

            for (Upper.Section section : Upper.Section.values()) {
                Integer score = sections.get(section);

                result.repeat("-", 25).append('\n');
                result.append(
                    String.format("%-20s %s\n", section, scoreString(score)));
            }

            result.repeat("-", 25).append('\n');
            result.append(String.format("%-20s %d\n", "Total", totalScore()));

            result.repeat("-", 25).append('\n');
            result.append(String.format("%-20s %d\n", "Bonus", bonus()));

            result.repeat("-", 25).append('\n');
            result.append(String.format("%-20s %d\n", "Upper Total",
                totalScore() + bonus()));

            return result.toString();
        }


        /**
         * <p>
         * Section <br> Represents a section of the upper board.
         * </p>
         *
         * @author Lucas Thompson (lrthompson@vt.edu)
         * @since 09.16.2026
         */
        public enum Section implements Board.Section {
            Aces, Twos, Threes, Fours, Fives, Sixes;


            @Override
            public String toString() {
                switch (this) {
                    case Aces -> {
                        return "Aces";
                    }
                    case Twos -> {
                        return "Twos";
                    }
                    case Threes -> {
                        return "Threes";
                    }
                    case Fours -> {
                        return "Fours";
                    }
                    case Fives -> {
                        return "Fives";
                    }
                    case Sixes -> {
                        return "Sixes";
                    }
                    default -> throw new UnknownError();
                }
            }
        }
    }




    /**
     * <p>
     * Lower <br> Represents the lower section of the Yahtzee board.
     * </p>
     *
     * @author Lucas Thompson (lrthompson@vt.edu)
     * @version 09.21.2026
     * @since 09.16.2026
     */
    public class Lower {

        /**
         * <p>
         * Represents the board eg. <br> null -> not scored -> __ <br> number ->
         * scored with that number -> number <br> 0 -> scratched -> X <br>
         * </p>
         *
         * @since 09.16.2026
         */
        private Map<Lower.Section, Integer> sections;


        /**
         * Creates a new Lower section.
         *
         * @since 09.16.2026
         */
        public Lower() {
            sections = new LinkedHashMap<>();

            for (Lower.Section section : Lower.Section.values()) {
                sections.put(section, null);
            }
        }


        /**
         * <p>
         * Checks if a section has been scored.
         * </p>
         *
         * @param section
         *     the section to check
         * @return true if the section has been scored, false otherwise
         * @throws IllegalArgumentException
         *     if the section is invalid
         * @since 09.16.2026
         */
        public boolean isScored(Lower.Section section)
            throws IllegalArgumentException {

            if (!sections.containsKey(section)) {
                throw new IllegalArgumentException(
                    "Invalid section: " + section);
            }

            return sections.get(section) != null;
        }


        /**
         * <p>
         * Scores a section based on the dice roll.
         * </p>
         *
         * @param section
         *     the section to score
         * @param dice
         *     the current dice state
         * @return true if the section was scored, false otherwise
         * @throws IllegalArgumentException
         *     if the section is invalid
         * @since 09.21.2026
         */
        public boolean scoreSection(Lower.Section section, DiceSet dice)
            throws IllegalArgumentException {

            if (!sections.containsKey(section)) {
                throw new IllegalArgumentException(
                    "Invalid section: " + section);
            }

            if (sections.get(section) != null) {
                return false;
            }

            List<Lower.Section> scoreable = dice.getValidSections().stream()
                .filter(sect -> sect instanceof Lower.Section)
                .map(sect -> (Lower.Section)sect).toList();

            if (!scoreable.contains(section)) {
                return false;
            }

            switch (section) {
                case ThreeOfKind ->
                    sections.put(section, threeOfKindScore(dice));

                case FourOfKind -> sections.put(section, fourOfKindScore(dice));

                case FullHouse -> sections.put(section, fullHouseScore(dice));

                case SmallStraight ->
                    sections.put(section, smallStraightScore(dice));

                case LargeStraight ->
                    sections.put(section, largeStraightScore(dice));

                case Yahtzee -> sections.put(section, yahtzeeScore(dice));

                case Chance -> sections.put(section, chanceScore(dice));
            }

            return true;
        }


        /**
         * <p>
         * Calculates the total score for the lower section.
         * </p>
         *
         * @return the total score
         * @since 09.16.2026
         */
        public int totalScore() {
            int sum = 0;

            for (Integer value : sections.values()) {
                if (value != null) {
                    sum += value;
                }
            }

            return sum;
        }


        /**
         * <p>
         * Gets a list of available sections that have not been scored yet.
         * </p>
         *
         * @return a list of available sections
         * @since 09.16.2026
         */
        public List<Lower.Section> getAvailableSections() {
            List<Lower.Section> availableSections = new ArrayList<>();

            for (Map.Entry<Lower.Section, Integer> entry : sections.entrySet()) {

                if (entry.getValue() == null) {
                    // a section is available if unscored (null)
                    availableSections.add(entry.getKey());
                }
            }

            return availableSections;
        }


        /**
         * <p>
         * Attempts to scratch a section.
         * </p>
         *
         * @param section
         *     the section to scratch
         * @return true if the section was scratched, false otherwise
         * @throws IllegalArgumentException
         *     if the section is invalid
         * @since 09.16.2026
         */
        public boolean scratch(Lower.Section section) {
            if (!sections.containsKey(section)) {
                throw new IllegalArgumentException(
                    "Invalid section: " + section);
            }

            if (sections.get(section) != null) {
                return false;
            }

            sections.put(section, 0);
            return true;
        }


        /**
         * <p>
         * Returns a string representation of the lower section.
         * </p>
         *
         * @return a string representation of the lower section
         * @since 09.21.2026
         */
        @Override
        public String toString() {
            StringBuilder result = new StringBuilder();

            result.repeat("-", 25).append('\n');
            result.append("Lower Section\n");

            for (Lower.Section section : Lower.Section.values()) {
                Integer score = sections.get(section);

                result.repeat("-", 25).append('\n');
                result.append(
                    String.format("%-20s %s\n", section, scoreString(score)));
            }

            result.repeat("-", 25).append('\n');
            result.append(
                String.format("%-20s %d", "Lower Total", totalScore()));

            return result.toString();
        }


        /**
         * <p>
         * Calculates the score for a three of a kind.
         * </p>
         *
         * @param dice
         *     the current dice state
         * @return the score for a three of a kind
         * @since 09.21.2026
         */
        private Integer threeOfKindScore(DiceSet dice) {
            return IntStream.of(
                dice.getScoringValues(Lower.Section.ThreeOfKind)).sum();
        }


        /**
         * <p>
         * Calculates the score for a four of a kind.
         * </p>
         *
         * @param dice
         *     the current dice state
         * @return the score for a four of a kind
         * @since 09.21.2026
         */
        private Integer fourOfKindScore(DiceSet dice) {
            return IntStream.of(dice.getScoringValues(Lower.Section.FourOfKind))
                .sum();
        }


        /**
         * <p>
         * Calculates the score for a full house.
         * </p>
         *
         * @param dice
         *     the current dice state
         * @return the score for a full house
         * @since 09.21.2026
         */
        private Integer fullHouseScore(DiceSet dice) {
            return 25;
        }


        /**
         * <p>
         * Calculates the score for a small straight.
         * </p>
         *
         * @param dice
         *     the current dice state
         * @return the score for a small straight
         * @since 09.21.2026
         */
        private Integer smallStraightScore(DiceSet dice) {
            return 30;
        }


        /**
         * <p>
         * Calculates the score for a large straight.
         * </p>
         *
         * @param dice
         *     the current dice state
         * @return the score for a large straight
         * @since 09.21.2026
         */
        private Integer largeStraightScore(DiceSet dice) {
            return 40;
        }


        /**
         * <p>
         * Calculates the score for a Yahtzee.
         * </p>
         *
         * @param dice
         *     the current dice state
         * @return the score for a Yahtzee
         * @since 09.21.2026
         */
        private Integer yahtzeeScore(DiceSet dice) {
            return 50;
        }


        /**
         * <p>
         * Calculates the score for chance.
         * </p>
         *
         * @param dice
         *     the current dice state
         * @return the score for chance
         * @since 09.21.2026
         */
        private Integer chanceScore(DiceSet dice) {
            return IntStream.of(dice.getScoringValues(Lower.Section.Chance))
                .sum();
        }


        /**
         * <p>
         * Section <br> Represents a section of the lower board.
         * </p>
         *
         * @author Lucas Thompson (lrthompson@vt.edu)
         * @since 09.16.2026
         */
        public enum Section implements Board.Section {
            ThreeOfKind,
            FourOfKind,
            FullHouse,
            SmallStraight,
            LargeStraight,
            Yahtzee,
            Chance;


            @Override
            public String toString() {
                switch (this) {
                    case ThreeOfKind -> {
                        return "Three of a Kind";
                    }
                    case FourOfKind -> {
                        return "Four of a Kind";
                    }
                    case FullHouse -> {
                        return "Full House";
                    }
                    case SmallStraight -> {
                        return "Small Straight";
                    }
                    case LargeStraight -> {
                        return "Large Straight";
                    }
                    case Yahtzee -> {
                        return "Yahtzee";
                    }
                    case Chance -> {
                        return "Chance";
                    }
                    default -> throw new UnknownError();
                }
            }
        }
    }
}
