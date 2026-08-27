import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Stores and manages the tasks currently known by Alice.
 */
public class TaskList {
    private final ArrayList<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing the supplied tasks.
     *
     * @param tasks the tasks to add to this list
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Adds a task to the end of this list.
     *
     * @param task the task to add
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Returns the task at the specified zero-based index.
     *
     * @param index the zero-based index of the task
     * @return the task at the specified index
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Removes and returns the task at the specified zero-based index.
     *
     * @param index the zero-based index of the task to remove
     * @return the removed task
     */
    public Task remove(int index) {
        return tasks.remove(index);
    }

    /**
     * Returns the number of tasks in this list.
     *
     * @return the task count
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns an immutable view of the current tasks for saving.
     *
     * @return the current tasks
     */
    public List<Task> asList() {
        return List.copyOf(tasks);
    }

    /**
     * Returns the deadlines and events that occur on the specified date.
     *
     * @param date the date to match
     * @return the tasks occurring on the specified date
     */
    public List<Task> getTasksOnDate(LocalDate date) {
        ArrayList<Task> matchingTasks = new ArrayList<>();
        for (Task task : tasks) {
            if (occursOn(task, date)) {
                matchingTasks.add(task);
            }
        }
        return matchingTasks;
    }

    /**
     * Returns the zero-based position of a task in this list.
     *
     * @param task the task to locate
     * @return the zero-based position of the task
     */
    public int indexOf(Task task) {
        return tasks.indexOf(task);
    }

    /**
     * Returns whether a task's date or date range includes the specified date.
     *
     * @param task the task to check
     * @param date the date to match
     * @return whether the task occurs on the specified date
     */
    private boolean occursOn(Task task, LocalDate date) {
        if (task instanceof Deadline deadline) {
            return deadline.getBy().equals(date);
        }
        if (task instanceof Event event) {
            return !date.isBefore(event.getFrom().toLocalDate())
                    && !date.isAfter(event.getTo().toLocalDate());
        }
        return false;
    }
}
