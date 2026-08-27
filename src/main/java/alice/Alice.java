package alice;

// Used Codex to do OOP of classes.
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
    /**
     * Starts Alice and processes commands until the user enters {@code bye}.
     *
     * @param args command-line arguments, which are not used
     */
    public static void main(String[] args) {
        Ui ui = new Ui();
        ui.showWelcome();
        Storage storage = new Storage();
        TaskList tasks = new TaskList(storage.load());
        Parser parser = new Parser();
        while (true) {
            String userInput = ui.readCommand();
            String command = parser.getCommandWord(userInput);
            ui.showSeparator();
            try {
                if (userInput.equals("bye")) { // check for exit
                    ui.showGoodbye();
                    break;

                } else if (userInput.equals("list")) { // print the list
                    ui.showTaskList(tasks);

                } else if (command.equals("mark")) {
                    int taskNo = parser.parseTaskNumber(userInput, "mark", tasks.size());
                    Task task = tasks.get(taskNo - 1); // 5th task means 4 in array

                    if (task.isDone()) {
                        throw new AliceException("This task is already marked as done.");
                    }
                    task.markAsDone();
                    storage.save(tasks.asList());
                    ui.showTaskMarked(task);

                } else if (command.equals("unmark")) {
                    int taskNo = parser.parseTaskNumber(userInput, "unmark", tasks.size());
                    Task task = tasks.get(taskNo - 1); // 5th task means 4 in array

                    if (!task.isDone()) {
                        throw new AliceException("This task is already marked as not done.");
                    }
                    task.unmarkAsDone();
                    storage.save(tasks.asList());
                    ui.showTaskUnmarked(task);

                } else if (command.equals("todo") || command.equals("deadline") || command.equals("event")) {
                    Task task = parser.parseTask(userInput);
                    tasks.add(task);
                    storage.save(tasks.asList());
                    ui.showTaskAdded(task, tasks.size());

                } else if (command.equals("date")) {
                    ui.showTasksOnDate(tasks, parser.parseDate(userInput));

                } else if (command.equals("find")) {
                    ui.showMatchingTasks(tasks, parser.parseKeyword(userInput));

                } else if (command.equals("delete")) {
                    int taskNo = parser.parseTaskNumber(userInput, "delete", tasks.size());
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

}
