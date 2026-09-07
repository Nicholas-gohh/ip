package alice.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import alice.exception.AliceException;
import alice.task.Deadline;
import alice.task.Event;
import alice.task.Task;
import alice.task.ToDo;

// Used Codex to draft the class and method descriptions.
// Used Codex to help implement file access.
/**
 * Saves Alice tasks to, and loads them from, data/Alice.txt.
 */
public class Storage {
    private static final Path FILE_PATH = Path.of("data", "Alice.txt");
    private static final String TODO_TYPE = "T";
    private static final String DEADLINE_TYPE = "D";
    private static final String EVENT_TYPE = "E";
    private static final String INCOMPLETE_STATUS = "0";
    private static final String COMPLETED_STATUS = "1";

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
     * @param task The task to format.
     * @return The task's storage representation.
     * @throws AliceException If the task is not valid.
     */
    private String formatTask(Task task) throws AliceException {
        String status = task.isDone() ? COMPLETED_STATUS : INCOMPLETE_STATUS;
        switch (task) {
            case ToDo toDo -> {
                return TODO_TYPE + " | " + status + " | " + task.getDescription();
            }
            case Deadline deadline -> {
                return DEADLINE_TYPE + " | " + status + " | " + task.getDescription()
                        + " | " + deadline.getBy();
            }
            case Event event -> {
                return EVENT_TYPE + " | " + status + " | " + task.getDescription()
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
     * @param line The storage line to parse.
     * @return The task represented by the storage line.
     * @throws AliceException If the line does not follow the expected format.
     */
    private Task parseTask(String line) throws AliceException {
        String[] parts = line.split("\\|", -1);
        trimFields(parts);
        validateCommonFields(parts);
        Task task = createTask(parts);
        restoreCompletionStatus(task, parts[1]);
        return task;
    }

    /** Removes surrounding whitespace from each storage field. */
    private void trimFields(String[] parts) {
        for (int index = 0; index < parts.length; index++) {
            parts[index] = parts[index].trim();
        }
    }

    /** Validates the fields common to every saved task type. */
    private void validateCommonFields(String[] parts) throws AliceException {
        if (parts.length < 3 || parts[2].isEmpty()
                || (!parts[1].equals(INCOMPLETE_STATUS) && !parts[1].equals(COMPLETED_STATUS))) {
            throw new AliceException("Invalid saved task.");
        }
    }

    /** Creates the task subtype identified by the storage type field. */
    private Task createTask(String[] parts) throws AliceException {
        return switch (parts[0]) {
            case TODO_TYPE -> createToDo(parts);
            case DEADLINE_TYPE -> createDeadline(parts);
            case EVENT_TYPE -> createEvent(parts);
            default -> throw new AliceException("Unknown task type.");
        };
    }

    /** Creates a todo task from its validated storage fields. */
    private Task createToDo(String[] parts) throws AliceException {
        if (parts.length != 3) {
            throw new AliceException("Invalid todo task.");
        }
        return new ToDo(parts[2]);
    }

    /** Creates a deadline task from its validated storage fields. */
    private Task createDeadline(String[] parts) throws AliceException {
        if (parts.length != 4 || parts[3].isEmpty()) {
            throw new AliceException("Invalid deadline task.");
        }
        try {
            return new Deadline(parts[2], LocalDate.parse(parts[3]));
        } catch (DateTimeParseException exception) {
            throw new AliceException("Invalid deadline date.");
        }
    }

    /** Creates an event task from its validated storage fields. */
    private Task createEvent(String[] parts) throws AliceException {
        if (parts.length != 5 || parts[3].isEmpty() || parts[4].isEmpty()) {
            throw new AliceException("Invalid event task.");
        }
        try {
            return new Event(parts[2], LocalDateTime.parse(parts[3]),
                    LocalDateTime.parse(parts[4]));
        } catch (DateTimeParseException exception) {
            throw new AliceException("Invalid event date time.");
        }
    }

    /** Restores a task's saved completion status. */
    private void restoreCompletionStatus(Task task, String status) {
        if (status.equals(COMPLETED_STATUS)) {
            task.markAsDone();
        }
    }

    /**
     * Loads all valid tasks from disk.
     * Creates an empty storage file if Alice is being run for the first time.
     *
     * @return The successfully loaded tasks.
     */
    public List<Task> load() {
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
     * @param tasks The tasks that should be saved.
     * @throws AliceException If the file cannot be written.
     */
    public void save(List<Task> tasks) throws AliceException {
        ArrayList<String> lines = new ArrayList<>();
        for (Task task : tasks) {
            lines.add(formatTask(task));
        }

        try {
            createFileIfMissing();
            Files.write(FILE_PATH, lines,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE);
        } catch (IOException exception) {
            throw new AliceException("Unable to save tasks to " + FILE_PATH + ".");
        }
    }
}
