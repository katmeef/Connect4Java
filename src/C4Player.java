public enum C4Player {
    X('X'),
    O('O');

    private final char symbol;

    C4Player(char symbol) {
        this.symbol = symbol;
    }

    public char getSymbol() {
        return symbol;
    }

    public C4Player next() {
        return this == X ? O : X;
    }
}