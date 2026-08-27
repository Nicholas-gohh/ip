import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

/**
 * Handles Alice's console input and output.
 */
public class Ui {
    private static final String SEPARATOR = "_______________________________________";
    private static final DateTimeFormatter DATE_DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy");
    private final Scanner scanner;

    /**
     * Creates the user interface that reads commands from standard input.
     */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /**
     * Displays Alice's welcome banner.
     */
    public void showWelcome() {
        String banner = SEPARATOR + "\n"
                + "    _    _     ___ ____ _____ \n"
                + "   / \\  | |   |_ _/ ___| ____|\n"
                + "  / _ \\ | |    | | |   |  _|  \n"
                + " / ___ \\| |___ | | |___| |___ \n"
                + "/_/   \\_\\_____|___\\____|_____|\n"
                + "Hello! I'm Alice.\n"
                + "What can I do for you?\n"
                + SEPARATOR;
        System.out.println(banner);
    }

    /**
     * Reads the next command entered by the user.
     *
     * @return the command entered by the user
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Displays a line separating console messages.
     */
    public void showSeparator() {
        System.out.println(SEPARATOR);
    }

    /**
     * Displays Alice's goodbye message.
     */
    public void showGoodbye() {
        System.out.println("Bye. Hope to see you again soon!");
        showSeparator();
    }

    /**
     * Displays every task in the task list.
     *
     * @param tasks the tasks to display
     */
    public void showTaskList(TaskList tasks) {
        System.out.println("Here are the tasks in your list:");
        for (int index = 0; index < tasks.size(); index++) {
            System.out.println("  " + (index + 1) + "." + tasks.get(index));
        }
        showSeparator();
    }

    /**
     * Displays the confirmation for a newly added task.
     *
     * @param task the task that was added
     * @param taskCount the new number of tasks in the list
     */
    public void showTaskAdded(Task task, int taskCount) {
        System.out.println("Got it. I've added this task:");
        System.out.println("  " + task);
        System.out.println("Now you have " + taskCount + " tasks in the list.");
        showSeparator();
    }

    /**
     * Displays the confirmation for a completed task.
     *
     * @param task the task that was marked as done
     */
    public void showTaskMarked(Task task) {
        System.out.println("Nice! I've marked this task as done:");
        System.out.println("  " + task);
        showSeparator();
    }

    /**
     * Displays the confirmation for an incomplete task.
     *
     * @param task the task that was marked as not done
     */
    public void showTaskUnmarked(Task task) {
        System.out.println("OK, I've marked this task as not done yet:");
        System.out.println("  " + task);
        showSeparator();
    }

    /**
     * Displays the confirmation for a deleted task.
     *
     * @param task the task that was removed
     * @param taskCount the number of tasks remaining in the list
     */
    public void showTaskDeleted(Task task, int taskCount) {
        System.out.println("Noted. I've removed this task:");
        System.out.println("  " + task);
        System.out.println("Now you have " + taskCount + " tasks in the list.");
        showSeparator();
    }

    /**
     * Displays an error message.
     *
     * @param message the error message to display
     */
    public void showError(String message) {
        System.out.println(message);
        showSeparator();
    }

    /**
     * Displays the deadlines and events that occur on the given date.
     *
     * @param tasks the tasks to search and display
     * @param date the date to match
     */
    public void showTasksOnDate(TaskList tasks, LocalDate date) {
        boolean hasMatch = false;
        for (int index = 0; index < tasks.size(); index++) {
            Task task = tasks.get(index);
            boolean isMatchingDeadline = task instanceof Deadline deadline
                    && deadline.getBy().equals(date);
            boolean isMatchingEvent = task instanceof Event event
                    && !date.isBefore(event.getFrom().toLocalDate())
                    && !date.isAfter(event.getTo().toLocalDate());
            if (isMatchingDeadline || isMatchingEvent) {
                if (!hasMatch) {
                    System.out.println("Here are the tasks occurring on "
                            + date.format(DATE_DISPLAY_FORMAT) + ":");
                }
                System.out.println("  " + (index + 1) + "." + task);
                hasMatch = true;
            }
        }

        if (!hasMatch) {
            System.out.println("No tasks occur on " + date.format(DATE_DISPLAY_FORMAT) + ".");
        }
        showSeparator();
    }
}
