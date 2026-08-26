import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a task occurring between a start and end time.
 */
public class Event extends Task {
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy h:mm a");
    private final LocalDateTime from;
    private final LocalDateTime to;

    /**
     * Creates an event task.
     *
     * @param description the task description
     * @param from the event start date and time
     * @param to the event end date and time
     */
    public Event(String description, LocalDateTime from, LocalDateTime to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /** Returns the event start date and time. */
    public LocalDateTime getFrom() {
        return from;
    }

    /** Returns the event end date and time. */
    public LocalDateTime getTo() {
        return to;
    }

    /**
     * Returns this event task in Alice's display format.
     *
     * @return the formatted event task
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from.format(DISPLAY_FORMAT)
                + " to: " + to.format(DISPLAY_FORMAT) + ")";
    }
}
