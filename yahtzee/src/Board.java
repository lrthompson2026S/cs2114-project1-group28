public class Board {
    public interface Section {}

    public class Upper {
        public enum Section implements Board.Section {
            Aces, 
            Twos, 
            Threes,
            Fours,
            Fives,
            Sixes
        }
    }

    public class Lower {
        public enum Section implements Board.Section {
            ThreeOfKind,
            FourOfKind,
            FullHouse,
            SmStraight,
            LgStraight,
            Yahtzee,
            Chance
        }
    }

    private Upper upper;
    private Lower lower;
}
