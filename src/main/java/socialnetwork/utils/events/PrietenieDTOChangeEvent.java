package socialnetwork.utils.events;


import socialnetwork.domain.PrietenieDTO;
import socialnetwork.utils.events.ChangeEventType;
import socialnetwork.utils.events.Event;

public class PrietenieDTOChangeEvent implements Event {
    private ChangeEventType type;
    private PrietenieDTO data, oldData;

    public PrietenieDTOChangeEvent(ChangeEventType type, PrietenieDTO data) {
        this.type = type;
        this.data = data;
    }
    public PrietenieDTOChangeEvent(ChangeEventType type, PrietenieDTO data, PrietenieDTO oldData) {
        this.type = type;
        this.data = data;
        this.oldData=oldData;
    }

    public ChangeEventType getType() {
        return type;
    }

    public PrietenieDTO getData() {
        return data;
    }

    public PrietenieDTO getOldData() {
        return oldData;
    }
}
