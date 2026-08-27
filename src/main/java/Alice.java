import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Runs the Alice bot.
 */
public class Alice {
    // Used Codex to find out how to set Format and parse the input accordingly
    private static final DateTimeFormatter DEADLINE_INPUT_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter EVENT_INPUT_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");

    /**
     * Starts Alice and processes commands until the user enters {@code bye}.
     *
     * @param args command-line arguments, which are not used
     */
    public static void main(String[] args) {
        Ui ui = new Ui();
        ui.showWelcome();
        // Load existing tasks
        Storage storage = new Storage();
        TaskList tasks = new TaskList(storage.load());
        while (true) {
            String userInput = ui.readCommand();
            ui.showSeparator();
            try {
                if (userInput.equals("bye")) { // check for exit
                    ui.showGoodbye();
                    break;

                } else if (userInput.equals("list")) { // print the list
                    ui.showTaskList(tasks);

                } else if (userInput.equals("mark") || userInput.startsWith("mark ")) { // Used Codex to figure out if statements for mark and unmark
                    int taskNo = getTaskNumber(userInput, "mark", tasks.size());
                    Task task = tasks.get(taskNo - 1); // 5th task means 4 in array

                    if (task.isDone()) {
                        throw new AliceException("This task is already marked as done.");
                    }
                    task.markAsDone();
                    storage.save(tasks.asList());
                    ui.showTaskMarked(task);

                } else if (userInput.equals("unmark") || userInput.startsWith("unmark ")) {
                    int taskNo = getTaskNumber(userInput, "unmark", tasks.size());
                    Task task = tasks.get(taskNo - 1); // 5th task means 4 in array

                    if (!task.isDone()) {
                        throw new AliceException("This task is already marked as not done.");
                    }
                    task.unmarkAsDone();
                    storage.save(tasks.asList());
                    ui.showTaskUnmarked(task);

                } else if (userInput.equals("todo") || userInput.startsWith("todo ")) {
                    // Remove first 4 chars
                    String description = userInput.substring(4).trim();
                    if (description.isEmpty()) {
                        throw new AliceException("The description of a todo cannot be empty.");
                    }
                    Task task = new ToDo(description);
                    tasks.add(task);
                    storage.save(tasks.asList());
                    ui.showTaskAdded(task, tasks.size());

                } else if (userInput.equals("deadline") || userInput.startsWith("deadline ")) { //found how to split using Codex
                    // Remove first 8 chars, then split the remaining string with "/by" into 2
                    String[] sections = userInput.substring(8).trim().split(" /by ", 2);
                    if (sections.length !=2 || sections[0].isBlank() || sections[1].isBlank()) {
                        throw new AliceException("A deadline needs a description and a /by date.");
                    }
                    LocalDate by;
                    try {
                        by = LocalDate.parse(sections[1], DEADLINE_INPUT_FORMAT);
                    } catch (DateTimeParseException exception) {
                        throw new AliceException("Please use the date format yyyy-MM-dd.");
                    }
                    Task task = new Deadline(sections[0], by);
                    tasks.add(task);
                    storage.save(tasks.asList());
                    ui.showTaskAdded(task, tasks.size());

                } else if (userInput.equals("event") || userInput.startsWith("event ")) { //found how to split using Codex
                    // Remove first 5 chars, then split the remaining string with "/from" into 2
                    String[] fromSections = userInput.substring(5).trim().split(" /from ", 2);
                    if (fromSections.length != 2 || fromSections[0].isBlank()) {
                        throw new AliceException("An event needs a description, a /from date time, and a /to date time.");
                    }
                    // Split again using "/to"
                    String[] toSections = fromSections[1].split(" /to ", 2);
                    if (toSections.length != 2 || toSections[0].isBlank() || toSections[1].isBlank()) {
                        throw new AliceException("An event needs a description, a /from date time, and a /to date time.");
                    }

                    LocalDateTime from;
                    LocalDateTime to;
                    try {
                        from = LocalDateTime.parse(toSections[0], EVENT_INPUT_FORMAT);
                        to = LocalDateTime.parse(toSections[1], EVENT_INPUT_FORMAT);
                    } catch (DateTimeParseException exception) {
                        throw new AliceException("Please use the event date time format yyyy-MM-dd HHmm.");
                    }
                    if (to.isBefore(from)) {
                        throw new AliceException("An event cannot end before it starts.");
                    }
                    Task task = new Event(fromSections[0], from, to);
                    tasks.add(task);
                    storage.save(tasks.asList());
                    ui.showTaskAdded(task, tasks.size());

                } else if (userInput.equals("date") || userInput.startsWith("date ")) {
                    String dateText = userInput.substring(4).trim();
                    LocalDate date;
                    try {
                        date = LocalDate.parse(dateText, DEADLINE_INPUT_FORMAT);
                    } catch (DateTimeParseException exception) {
                        throw new AliceException("Please use the date format yyyy-MM-dd.");
                    }
                    ui.showTasksOnDate(tasks, date);

                } else if (userInput.equals("delete") || userInput.startsWith("delete ")) {
                    int taskNo = getTaskNumber(userInput, "delete", tasks.size());
                    Task deletedTask = tasks.remove(taskNo - 1);
                    storage.save(tasks.asList());

                    ui.showTaskDeleted(deletedTask, tasks.size());

                } else { // Throw error
                    throw new AliceException("I don't understand that command.");
                }
            } catch (AliceException e) {
                ui.showError(e.getMessage());
            }
        }
    }

    /**
     * Validates and returns the task number supplied with a command.
     *
     * @param userInput the complete command entered by the user
     * @param command the command keyword, such as {@code mark}
     * @param inputCount the number of tasks currently in the list
     * @return the valid one-based task number
     * @throws AliceException if no valid task number was supplied
     */
    private static int getTaskNumber(String userInput, String command, int inputCount) throws AliceException {

        String number = userInput.substring(command.length()).trim();
        if (number.isEmpty()) {
            throw new AliceException("Please provide a task number to " + command + ".");
        }

        try {
            int num = Integer.parseInt(number);
            if (num < 1 || num > inputCount) {
                throw new AliceException("There is no task numbered " + num + ".");
            }
            return num;

        } catch (NumberFormatException e) {
            throw new AliceException("The task number must be a positive whole number.");
        }
    }
}
