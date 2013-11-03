package workflow.event;

import java.util.Date;

public class DatedEvent {
    private Date date;

    public final Date getDate() {
        return date;
    }

    public final void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "DatedEvent [date=" + date + "]";
    }
}
