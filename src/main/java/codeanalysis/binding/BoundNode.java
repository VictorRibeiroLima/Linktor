package codeanalysis.binding;

import java.util.List;

public abstract class BoundNode {
    public abstract BoundNodeKind getKind();

    public abstract List<BoundNode> getChildren();

}
