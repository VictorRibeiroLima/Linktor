package codeanalysis.symbol;

import java.lang.reflect.Type;

public record VariableSymbol(String name, Type type, boolean readOnly) {

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        return this == o;
    }

}
