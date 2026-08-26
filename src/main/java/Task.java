/**
 * Represents a task with a description and completion status.
 */
public class Task {
    private final String description;
    private boolean isDone;

    /**
     * Creates a task with the given description.
     *
     * @param description the task description
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the icon representing the task's completion status.
     *
     * @return {@code [X]} when complete, otherwise {@code [ ]}
     */
    public String getStatusIcon() {
        return (isDone ? "[X]" : "[ ]"); // mark done task with X
    }

    /** Returns whether this task has been completed. */
    public boolean isDone() {
        return isDone;
    }

    /** Returns the task description. */
    public String getDescription() {
        return description;
    }

    /** Marks this task as complete. */
    public void markAsDone() {
        isDone = true;
    }

    /** Marks this task as incomplete. */
    public void unmarkAsDone() {
        isDone = false;
    }

    /**
     * Returns the task status and description for display.
     *
     * @return a display representation of this task
     */
    @Override
    public String toString() {
        return getStatusIcon() + " " + description;
    }
}
