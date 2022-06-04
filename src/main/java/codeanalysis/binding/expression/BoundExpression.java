package codeanalysis.binding.expression;

import codeanalysis.binding.BoundNode;
import codeanalysis.symbol.TypeSymbol;

public abstract class BoundExpression extends BoundNode {
    public abstract TypeSymbol getType();
}
