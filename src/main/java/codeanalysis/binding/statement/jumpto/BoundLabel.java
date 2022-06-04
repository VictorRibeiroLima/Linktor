package codeanalysis.binding.statement.jumpto;

public record BoundLabel(String name) {
    @Override
    public String toString() {
        return name;
    }
}
