/**
 * Represents a task occurring between a start and end time.
 */
public class Event extends Task {
    private final String from;
    private final String to;

    /**
     * Creates an event task.
     *
     * @param description the task description
     * @param from the event start time text
     * @param to the event end time text
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /** Returns the event start time. */
    public String getFrom() {
        return from;
    }

    /** Returns the event end time. */
    public String getTo() {
        return to;
    }

    /**
     * Returns this event task in Alice's display format.
     *
     * @return the formatted event task
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")";
    }
}
