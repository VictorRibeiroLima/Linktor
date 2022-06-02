package codeanalysis.lowering;

import codeanalysis.binding.rewriter.BoundTreeRewriter;
import codeanalysis.binding.statement.BoundStatement;

public final class Lowerer extends BoundTreeRewriter {

    private Lowerer() {
    }

    public static BoundStatement lower(BoundStatement statement) throws Exception {
        Lowerer lowerer = new Lowerer();
        return lowerer.rewriteStatement(statement);
    }
}
