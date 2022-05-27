package src.codeanalysis.binding.expression;

import src.codeanalysis.binding.BoundNode;

import java.lang.reflect.Type;

public abstract class BoundExpression extends BoundNode {
    public abstract Type getType();
}
