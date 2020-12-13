package socialnetwork.utils.events;


import socialnetwork.domain.Entity;
import socialnetwork.utils.events.ChangeEventType;
import socialnetwork.utils.events.Event;

public class ChangeEvent implements Event {
    private ChangeEventType type;
    private Entity e1, e2;

    public ChangeEvent(ChangeEventType type, Entity e1,Entity e2) {
        this.type = type;
        this.e1 = e1;
        this.e2 = e2;
    }
    public ChangeEvent(ChangeEventType type, Entity e1) {
        this.type = type;
        this.e1 = e1;
    }

    public ChangeEventType getType() {
        return type;
    }

    public Entity getData() {
        return e1;
    }

    public Entity getOldData() {
        return e2;
    }
}
