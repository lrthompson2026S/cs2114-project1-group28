import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * <p>
 * Board <br>
 * Represents the Yahtzee board.
 * </p>
 * 
 * @author Lucas Thompson (lrthompson@vt.edu)
 * @version 09.16.2026
 */
public class Board {
    /**
     * <p>
     * Section <br>
     * Empty interface to represent a section of the board.
     * </p>
     * 
     * @author Lucas Thompson (lrthompson@vt.edu)
     * @since 09.16.2026
     */
    public interface Section {
    }

    /**
     * <p>
     * Upper <br>
     * Represents the upper section of the Yahtzee board.
     * </p>
     * 
     * @author Lucas Thompson (lrthompson@vt.edu)
     * @since 09.16.2026
     */
    public class Upper {

        /**
         * <p>
         * Section <br>
         * Represents a section of the upper board.
         * </p>
         * 
         * @author Lucas Thompson (lrthompson@vt.edu)
         * @since 09.16.2026
         */
        public enum Section implements Board.Section {
            Aces,
            Twos,
            Threes,
            Fours,
            Fives,
            Sixes
        }

        /**
         * <p>
         * Represents the board eg. <br>
         * null -> not scored -> __ <br>
         * number -> scored with that number -> number <br>
         * 0 -> scratched -> X <br>
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
            sections = new HashMap<>();

            for (Upper.Section section : Upper.Section.values()) {
                sections.put(section, null);
            }
        }

        /**
         * <p>
         * Checks if a section has been scored.
         * </p>
         * 
         * @param section the section to check
         * @return true if the section has been scored, false otherwise
         * @throws IllegalArgumentException if the section is invalid
         * @since 09.16.2026
         */
        public boolean isScored(Upper.Section section) throws IllegalArgumentException {
            if (!sections.containsKey(section)) {
                throw new IllegalArgumentException("Invalid section: " + section);
            }

            return sections.get(section) != null;
        }

        /**
         * <p>
         * Scores a section based on the dice roll.
         * </p>
         * 
         * @param section the section to score
         * @param dice    the current dice state
         * @return true if the section was scored, false otherwise
         * @throws IllegalArgumentException if the section is invalid
         * @since 09.16.2026
         */
        public boolean scoreSection(Upper.Section section, DiceSet dice) throws IllegalArgumentException {
            if (!sections.containsKey(section)) {
                throw new IllegalArgumentException("Invalid section: " + section);
            }

            if (sections.get(section) != null) {
                return false;
            }

            List<Upper.Section> scoreable = dice.getValidSections().stream()
                .filter(sect -> sect instanceof Upper.Section)
                .map(sect -> (Upper.Section) sect);

            switch (section) {
                case Aces ->{
                    if(!scoreable.contains(Upper.Section.Aces)) {return false;}
                    sections.put(section, IntStream.of(dice.getScoringValues(Upper.Section.Aces)).sum());
                }
                case Twos ->{
                    if(!scoreable.contains(Upper.Section.Twos)) {return false;}
                    sections.put(section, IntStream.of(dice.getScoringValues(Upper.Section.Twos)).sum());
                }
                case Threes ->{
                    if(!scoreable.contains(Upper.Section.Threes)) {return false;}
                    sections.put(section, IntStream.of(dice.getScoringValues(Upper.Section.Threes)).sum());
                }
                case Fours ->{
                    if(!scoreable.contains(Upper.Section.Fours)) {return false;}
                    sections.put(section, IntStream.of(dice.getScoringValues(Upper.Section.Fours)).sum());
                }
                case Fives ->{
                    if(!scoreable.contains(Upper.Section.Fives)) {return false;}
                    sections.put(section, IntStream.of(dice.getScoringValues(Upper.Section.Fives)).sum());
                }
                case Sixes ->{
                    if(!scoreable.contains(Upper.Section.Sixes)) {return false;}
                    sections.put(section, IntStream.of(dice.getScoringValues(Upper.Section.Sixes)).sum());
                }
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
         * Gets a list of available sections that have not been scored yet.
         * </p>
         * 
         * @return a list of available sections
         * @since 09.16.2026
         */
        public List<Upper.Section> getAvailableSections() {
            List<Upper.Section> availableSections = new ArrayList<>();

            for (Map.Entry<Upper.Section, Integer> entry : sections.entrySet()) {
                if (entry.getValue() == null) { // a section is available if unscored (null)
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
         * @param section the section to scratch
         * @return true if the section was scratched, false otherwise
         * @throws IllegalArgumentException if the section is invalid
         * @since 09.16.2026
         */
        public boolean scratch(Upper.Section section) {
            if (!sections.containsKey(section)) {
                throw new IllegalArgumentException("Invalid section: " + section);
            }

            if (sections.get(section) != null) {
                return false;
            }

            sections.put(section, 0);
            return true;
        }

        @Override
        public String toString() {
            return "";
        }
    }

    /**
     * <p>
     * Upper <br>
     * Represents the upper section of the Yahtzee board.
     * </p>
     * 
     * @author Lucas Thompson (lrthompson@vt.edu)
     * @since 09.16.2026
     */
    public class Lower {
        /**
         * <p>
         * Section <br>
         * Represents a section of the lower board.
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
            Chance
        }

        /**
         * <p>
         * Represents the board eg. <br>
         * null -> not scored -> __ <br>
         * number -> scored with that number -> number <br>
         * 0 -> scratched -> X <br>
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
            sections = new HashMap<>();

            for (Lower.Section section : Lower.Section.values()) {
                sections.put(section, null);
            }
        }

        /**
         * <p>
         * Checks if a section has been scored.
         * </p>
         * 
         * @param section the section to check
         * @return true if the section has been scored, false otherwise
         * @throws IllegalArgumentException if the section is invalid
         * @since 09.16.2026
         */
        public boolean isScored(Lower.Section section) throws IllegalArgumentException {
            if (!sections.containsKey(section)) {
                throw new IllegalArgumentException("Invalid section: " + section);
            }

            return sections.get(section) != null;
        }

        /**
         * <p>
         * Scores a section based on the dice roll.
         * </p>
         * 
         * @param section the section to score
         * @param dice    the current dice state
         * @return true if the section was scored, false otherwise
         * @throws IllegalArgumentException if the section is invalid
         * @since 09.16.2026
         */
        public boolean scoreSection(Lower.Section section, DiceSet dice) throws IllegalArgumentException {
            if (!sections.containsKey(section)) {
                throw new IllegalArgumentException("Invalid section: " + section);
            }

            if (sections.get(section) != null) {
                return false;
            }

            List<Lower.Section> scoreable = dice.getValidSections().stream()
                .filter(sect -> sect instanceof Lower.Section)
                .map(sect -> (Lower.Section) sect);

            switch (section) {
                case ThreeOfKind ->{
                    if(!scoreable.contains(Lower.Section.ThreeOfKind)) {return false;}
                    sections.put(section, threeOfKindScore(dice));
                }
                case FourOfKind ->{
                    if(!scoreable.contains(Lower.Section.FourOfKind)) {return false;}
                    sections.put(section, fourOfKindScore(dice));
                }
                case FullHouse ->{
                    if(!scoreable.contains(Lower.Section.FullHouse)) {return false;}
                    sections.put(section, fullHouseScore(dice));
                }
                case SmallStraight ->{
                    if(!scoreable.contains(Lower.Section.SmallStraight)) {return false;}
                    sections.put(section, smallStraightScore(dice));
                }
                case LargeStraight ->{
                    if(!scoreable.contains(Lower.Section.LargeStraight)) {return false;}
                    sections.put(section, largeStraightScore(dice));
                }
                case Yahtzee ->{
                    if(!scoreable.contains(Lower.Section.Yahtzee)) {return false;}
                    sections.put(section, yahtzeeScore(dice));
                }
                case Chance ->{
                    if(!scoreable.contains(Lower.Section.Chance)) {return false;}
                    sections.put(section, chanceScore(dice));
                }
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
                if (entry.getValue() == null) { // a section is available if unscored (null)
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
         * @param section the section to scratch
         * @return true if the section was scratched, false otherwise
         * @throws IllegalArgumentException if the section is invalid
         * @since 09.16.2026
         */
        public boolean scratch(Lower.Section section) {
            if (!sections.containsKey(section)) {
                throw new IllegalArgumentException("Invalid section: " + section);
            }

            if (sections.get(section) != null) {
                return false;
            }

            sections.put(section, 0);
            return true;
        }

        @Override
        public String toString() {
            return "";
        }

        private Integer threeOfKindScore(DiceSet dice) {
            return IntStream.of(dice.getScoringValues(Lower.Section.ThreeOfKind)).sum();
        }

        private Integer fourOfKindScore(DiceSet dice) {
            return IntStream.of(dice.getScoringValues(Lower.Section.FourOfKind)).sum();
        }

        private Integer fullHouseScore(DiceSet dice) {
            if (dice.getScoringValues(Lower.Section.FullHouse).length == 0) {
                return 0;
            }
            return IntStream.of(dice.getScoringValues(Lower.Section.FullHouse)).sum();
        }

        private Integer smallStraightScore(DiceSet dice) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        private Integer largeStraightScore(DiceSet dice) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        private Integer yahtzeeScore(DiceSet dice) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        private Integer chanceScore(DiceSet dice) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }

    private Upper upper;
    private Lower lower;
}
