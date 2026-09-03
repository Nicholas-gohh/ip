package alice;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import alice.exception.AliceException;
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
        String command = parser.getCommandWord(userInput);
        try {
            if (userInput.equals("bye")) {
                return "Bye. Hope to see you again soon!";
            } else if (userInput.equals("list")) {
                return getTaskListResponse();
            } else if (command.equals("mark")) {
                int taskNumber = parser.parseTaskNumber(userInput, "mark", tasks.size());
                Task task = tasks.get(taskNumber - 1);
                if (task.isDone()) {
                    throw new AliceException("This task is already marked as done.");
                }
                task.markAsDone();
                storage.save(tasks.asList());
                return "Nice! I've marked this task as done:\n  " + task;
            } else if (command.equals("unmark")) {
                int taskNumber = parser.parseTaskNumber(userInput, "unmark", tasks.size());
                Task task = tasks.get(taskNumber - 1);
                if (!task.isDone()) {
                    throw new AliceException("This task is already marked as not done.");
                }
                task.unmarkAsDone();
                storage.save(tasks.asList());
                return "OK, I've marked this task as not done yet:\n  " + task;
            } else if (command.equals("todo") || command.equals("deadline") || command.equals("event")) {
                Task task = parser.parseTask(userInput);
                tasks.add(task);
                storage.save(tasks.asList());
                return "Got it. I've added this task:\n  " + task
                        + "\nNow you have " + tasks.size() + " tasks in the list.";
            } else if (command.equals("date")) {
                return getDateResponse(parser.parseDate(userInput));
            } else if (command.equals("find")) {
                return getFindResponse(parser.parseKeyword(userInput));
            } else if (command.equals("delete")) {
                int taskNumber = parser.parseTaskNumber(userInput, "delete", tasks.size());
                Task deletedTask = tasks.remove(taskNumber - 1);
                storage.save(tasks.asList());
                return "Noted. I've removed this task:\n  " + deletedTask
                        + "\nNow you have " + tasks.size() + " tasks in the list.";
            } else {
                throw new AliceException("I don't understand that command.");
            }
        } catch (AliceException e) {
            return e.getMessage();
        }
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
        StringBuilder response = new StringBuilder("Here are the tasks occurring on ")
                .append(date.format(DATE_DISPLAY_FORMAT)).append(':');
        for (Task task : matchingTasks) {
            response.append("\n  ").append(tasks.indexOf(task) + 1).append('.').append(task);
        }
        return response.toString();
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
        StringBuilder response = new StringBuilder("Here are the matching tasks in your list:");
        for (Task task : matchingTasks) {
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
