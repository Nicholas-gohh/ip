/**
 * Represents a task without a date or time.
 */
public class ToDo extends Task {
    /**
     * Creates a todo task.
     *
     * @param description the task description
     */
    public ToDo(String description) {
        super(description);
    }

    /**
     * Returns this todo task in Alice's display format.
     *
     * @return the formatted todo task
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
