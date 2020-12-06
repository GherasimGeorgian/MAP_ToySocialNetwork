package socialnetwork.utils.events;

import socialnetwork.domain.Invite;
import socialnetwork.domain.PrietenieDTO;

public class InviteChangeEvent implements Event{
    private ChangeEventType type;
    private Invite data, oldData;

    public InviteChangeEvent(ChangeEventType type, Invite data) {
        this.type = type;
        this.data = data;
    }
    public InviteChangeEvent(ChangeEventType type, Invite data, Invite oldData) {
        this.type = type;
        this.data = data;
        this.oldData=oldData;
    }

    public ChangeEventType getType() {
        return type;
    }

    public Invite getData() {
        return data;
    }

    public Invite getOldData() {
        return oldData;
    }
}
