package codeanalysis.symbol;

import java.lang.reflect.Type;

public record VariableSymbol(String name, Type type, boolean readOnly) {

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof VariableSymbol))
            return false;
        VariableSymbol v = (VariableSymbol) o;
        return v == this || v.name().equals(this.name);
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }
}
