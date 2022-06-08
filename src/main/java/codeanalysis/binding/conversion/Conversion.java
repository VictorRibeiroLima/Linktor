package codeanalysis.binding.conversion;

import codeanalysis.symbol.TypeSymbol;

public class Conversion {
    private final boolean exists;
    private final boolean isIdentity;
    private final boolean isImplicit;

    public static final Conversion NONE = new Conversion(false, false, false);
    public static final Conversion IDENTITY = new Conversion(true, true, true);
    public static final Conversion EXPLICIT = new Conversion(true, false, false);
    public static final Conversion IMPLICIT = new Conversion(true, false, true);

    private Conversion(boolean exists, boolean isIdentity, boolean isImplicit) {
        this.exists = exists;
        this.isIdentity = isIdentity;
        this.isImplicit = isImplicit;
    }

    public boolean isExists() {
        return exists;
    }

    public boolean isIdentity() {
        return isIdentity;
    }

    public boolean isImplicit() {
        return isImplicit;
    }

    public boolean isExplicit() {
        return exists && !isImplicit;
    }

    public static Conversion classify(TypeSymbol from, TypeSymbol to) {
        if (from == to)
            return Conversion.IDENTITY;
        if (from == TypeSymbol.BOOLEAN || from == TypeSymbol.INTEGER) {
            if (to == TypeSymbol.STRING)
                return Conversion.EXPLICIT;
        }
        if (from == TypeSymbol.STRING) {
            if (to == TypeSymbol.BOOLEAN || to == TypeSymbol.INTEGER)
                return Conversion.EXPLICIT;
        }
        return Conversion.NONE;
    }
}
