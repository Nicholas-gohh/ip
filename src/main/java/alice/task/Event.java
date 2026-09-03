package alice.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a task occurring between a start and end time.
 */
public class Event extends Task {
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy h:mm a");
    private final LocalDateTime fromDateTime;
    private final LocalDateTime toDateTime;

    /**
     * Creates an event task.
     *
     * @param description The task description.
     * @param from The event start date and time.
     * @param to The event end date and time.
     */
    public Event(String description, LocalDateTime from, LocalDateTime to) {
        super(description);
        this.fromDateTime = from;
        this.toDateTime = to;
    }

    /** Returns the event start date and time. */
    public LocalDateTime getFrom() {

        return fromDateTime;
    }

    /** Returns the event end date and time. */
    public LocalDateTime getTo() {

        return toDateTime;
    }

    /**
     * Returns this event task in Alice's display format.
     *
     * @return The formatted event task.
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + fromDateTime.format(DISPLAY_FORMAT)
                + " to: " + toDateTime.format(DISPLAY_FORMAT) + ")";
    }
}
