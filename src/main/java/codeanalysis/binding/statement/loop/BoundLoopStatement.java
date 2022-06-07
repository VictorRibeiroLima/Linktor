package codeanalysis.binding.statement.loop;

import codeanalysis.binding.statement.BoundStatement;
import codeanalysis.binding.statement.jumpto.BoundLabel;

public abstract class BoundLoopStatement extends BoundStatement {
    private final BoundLabel breakLabel;
    private final BoundLabel continueLabel;

    public BoundLoopStatement(BoundLabel breakLabel, BoundLabel continueLabel) {
        this.breakLabel = breakLabel;
        this.continueLabel = continueLabel;
    }

    public BoundLabel getBreakLabel() {
        return breakLabel;
    }

    public BoundLabel getContinueLabel() {
        return continueLabel;
    }
}
