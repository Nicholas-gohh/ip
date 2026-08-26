import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

// Used Codex to do the descriptions of the class and methods
// Also use Codex to help with the code of accessing files
/**
 * Saves Alice tasks to, and loads them from, data/Alice.txt.
 */
public class Storage {
    private static final Path FILE_PATH = Path.of("data", "Alice.txt");

    /**
     * Creates the data folder and Alice.txt if they do not already exist.
     */
    private void createFileIfMissing() throws IOException {
        Files.createDirectories(FILE_PATH.getParent());
        if (Files.notExists(FILE_PATH)) {
            Files.createFile(FILE_PATH);
        }
    }

    /**
     * Converts a task into one line in readable storage.
     *
     * @throws AliceException if the task is not valid
     */
    private String formatTask(Task task) throws AliceException {
        String status = task.isDone() ? "1" : "0"; //If done is 1, else 0
        switch (task) {
            case ToDo toDo -> {
                return "T | " + status + " | " + task.getDescription();
            }
            case Deadline deadline -> {
                return "D | " + status + " | " + task.getDescription()
                        + " | " + deadline.getBy();
            }
            case Event event -> {
                return "E | " + status + " | " + task.getDescription()
                        + " | " + event.getFrom() + " | " + event.getTo();
            }
            default -> {
                throw new AliceException("Unknown task type.");
            }
        }
    }

    /**
     * Converts one storage line into a task.
     *
     * @throws AliceException if the line does not follow the expected format
     */
    private Task parseTask(String line) throws AliceException {
        // Split the line with '|' into parts with no limits. '\\' needed as '|' is or
        String[] parts = line.split("\\|", -1);

        for (int index = 0; index < parts.length; index++) {
            parts[index] = parts[index].trim();
        }

        // Check the 2nd and 3rd part for validity
        if (parts.length < 3 || parts[2].isEmpty()
                || (!parts[1].equals("0") && !parts[1].equals("1"))) {
            throw new AliceException("Invalid saved task.");
        }

        // Assign the task into their specific type according to first part
        Task task = switch (parts[0]) {
            case "T" -> {
                if (parts.length != 3) {
                    throw new AliceException("Invalid todo task.");
                }
                yield new ToDo(parts[2]);
            }
            case "D" -> {
                if (parts.length != 4 || parts[3].isEmpty()) {
                    throw new AliceException("Invalid deadline task.");
                }
                yield new Deadline(parts[2], parts[3]);
            }
            case "E" -> {
                if (parts.length != 5 || parts[3].isEmpty() || parts[4].isEmpty()) {
                    throw new AliceException("Invalid event task.");
                }
                yield new Event(parts[2], parts[3], parts[4]);
            }
            default -> throw new AliceException("Unknown task type.");
        };

        if (parts[1].equals("1")) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Loads all valid tasks from disk.
     * Creates an empty storage file if Alice is being run for the first time.
     *
     * @return the successfully loaded tasks
     */
    public ArrayList<Task> load() {
        ArrayList<Task> tasks = new ArrayList<>();
        try {
            createFileIfMissing();
            List<String> lines = Files.readAllLines(FILE_PATH);
            for (int lineNumber = 0; lineNumber < lines.size(); lineNumber++) {
                try {
                    tasks.add(parseTask(lines.get(lineNumber)));
                } catch (AliceException exception) {
                    System.out.println("Skipped corrupted saved task on line " + (lineNumber + 1) + ".");
                }
            }
        } catch (IOException exception) {
            System.out.println("Unable to load saved tasks. Starting with an empty list.");
        }
        return tasks;
    }

    /**
     * Replaces the storage file contents with the current task list.
     *
     * @param tasks tasks that should be saved
     * @throws AliceException if the file cannot be written
     */
    public void save(List<Task> tasks) throws AliceException {
        ArrayList<String> lines = new ArrayList<>();
        for (Task task : tasks) {
            lines.add(formatTask(task));
        }

        try {
            createFileIfMissing();
            Files.write(FILE_PATH, lines,
                    StandardOpenOption.TRUNCATE_EXISTING, // clear old contents
                    StandardOpenOption.WRITE); // write the tasks
        } catch (IOException exception) {
            throw new AliceException("Unable to save tasks to " + FILE_PATH + ".");
        }
    }
}
