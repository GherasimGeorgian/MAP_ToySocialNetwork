package socialnetwork.Algorithm;

import java.io.Serializable;

public class EntityGraph<ID> implements Serializable {
    private static final long serialVersionUID = 0L;
    private ID id;
    public ID getIdGraph() {
        return id;
    }
    public void setIdGraph(ID id) {
        this.id = id;
    }
}
