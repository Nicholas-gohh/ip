package alice;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import alice.exception.AliceException;
import alice.parser.Command;
import alice.parser.Parser;
import alice.storage.Storage;
import alice.task.Task;
import alice.task.TaskList;
import alice.ui.Ui;

/**
 * Runs the Alice bot.
 */
public class Alice {
    private static final DateTimeFormatter DATE_DISPLAY_FORMAT = DateTimeFormatter.ofPattern("MMM dd yyyy");

    private final Storage storage;
    private final TaskList tasks;
    private final Parser parser;

    /**
     * Creates an Alice bot with its saved task list.
     */
    public Alice() {
        storage = new Storage();
        tasks = new TaskList(storage.load());
        parser = new Parser();
    }

    /**
     * Processes a command from either the graphical or console user interface.
     *
     * @param userInput The command entered by the user.
     * @return Alice's response to the command.
     */
    public String getResponse(String userInput) {
        Command command = Command.fromCommandWord(parser.getCommandWord(userInput));
        try {
            return switch (command) {
                case BYE -> "Bye. Hope to see you again soon!";
                case LIST -> getTaskListResponse();
                case MARK -> getMarkResponse(userInput);
                case UNMARK -> getUnmarkResponse(userInput);
                case TODO, DEADLINE, EVENT -> getAddTaskResponse(userInput);
                case DATE -> getDateResponse(parser.parseDate(userInput));
                case FIND -> getFindResponse(parser.parseKeyword(userInput));
                case DELETE -> getDeleteResponse(userInput);
                case UNKNOWN -> throw new AliceException("I don't understand that command.");
            };
        } catch (AliceException e) {
            return e.getMessage();
        }
    }

    /** Creates the response for the {@code mark} command. */
    private String getMarkResponse(String userInput) throws AliceException {
        int taskNumber = parser.parseTaskNumber(userInput, "mark", tasks.size());
        Task task = tasks.get(taskNumber - 1);
        if (task.isDone()) {
            throw new AliceException("This task is already marked as done.");
        }
        task.markAsDone();
        storage.save(tasks.asList());
        return "Nice! I've marked this task as done:\n  " + task;
    }

    /** Creates the response for the {@code unmark} command. */
    private String getUnmarkResponse(String userInput) throws AliceException {
        int taskNumber = parser.parseTaskNumber(userInput, "unmark", tasks.size());
        Task task = tasks.get(taskNumber - 1);
        if (!task.isDone()) {
            throw new AliceException("This task is already marked as not done.");
        }
        task.unmarkAsDone();
        storage.save(tasks.asList());
        return "OK, I've marked this task as not done yet:\n  " + task;
    }

    /** Creates the response for a task-creation command. */
    private String getAddTaskResponse(String userInput) throws AliceException {
        Task task = parser.parseTask(userInput);
        tasks.add(task);
        storage.save(tasks.asList());
        return "Got it. I've added this task:\n  " + task
                + "\nNow you have " + tasks.size() + " tasks in the list.";
    }

    /** Creates the response for the {@code delete} command. */
    private String getDeleteResponse(String userInput) throws AliceException {
        int taskNumber = parser.parseTaskNumber(userInput, "delete", tasks.size());
        Task deletedTask = tasks.remove(taskNumber - 1);
        storage.save(tasks.asList());
        return "Noted. I've removed this task:\n  " + deletedTask
                + "\nNow you have " + tasks.size() + " tasks in the list.";
    }

    /**
     * Creates the response for the {@code list} command.
     *
     * @return The formatted task list.
     */
    private String getTaskListResponse() {
        StringBuilder response = new StringBuilder("Here are the tasks in your list:");
        for (int index = 0; index < tasks.size(); index++) {
            response.append("\n  ").append(index + 1).append('.').append(tasks.get(index));
        }
        return response.toString();
    }

    /**
     * Creates the response for the {@code date} command.
     *
     * @param date The date to search for.
     * @return The formatted matching tasks.
     */
    private String getDateResponse(LocalDate date) {
        List<Task> matchingTasks = tasks.getTasksOnDate(date);
        if (matchingTasks.isEmpty()) {
            return "No tasks occur on " + date.format(DATE_DISPLAY_FORMAT) + ".";
        }
        String heading = "Here are the tasks occurring on " + date.format(DATE_DISPLAY_FORMAT) + ":";
        return formatTaskListResponse(heading, matchingTasks);
    }

    /**
     * Creates the response for the {@code find} command.
     *
     * @param keyword The keyword to search for.
     * @return The formatted matching tasks.
     */
    private String getFindResponse(String keyword) {
        List<Task> matchingTasks = tasks.getTasksWithKeyword(keyword);
        if (matchingTasks.isEmpty()) {
            return "No matching tasks found.";
        }
        return formatTaskListResponse("Here are the matching tasks in your list:", matchingTasks);
    }

    /**
     * Formats tasks with their positions in the complete task list.
     *
     * @param heading The heading to show before the tasks.
     * @param tasksToDisplay The tasks to format.
     * @return The formatted task list response.
     */
    private String formatTaskListResponse(String heading, List<Task> tasksToDisplay) {
        StringBuilder response = new StringBuilder(heading);
        for (Task task : tasksToDisplay) {
            response.append("\n  ").append(tasks.indexOf(task) + 1).append('.').append(task);
        }
        return response.toString();
    }

    /**
     * Starts Alice and processes commands until the user enters {@code bye}.
     *
     * @param args Command-line arguments, which are not used.
     */
    public static void main(String[] args) {
        Ui ui = new Ui();
        ui.showWelcome();
        Alice alice = new Alice();
        while (true) {
            String userInput = ui.readCommand();
            ui.showSeparator();
            if (userInput.equals("bye")) {
                ui.showGoodbye();
                break;
            }
            ui.showResponse(alice.getResponse(userInput));
        }
    }
}
