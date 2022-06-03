package codeanalysis.symbol;

public record LabelSymbol(String name) {
    @Override
    public String toString() {
        return name;
    }
}
