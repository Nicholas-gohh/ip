package alice.parser;

import alice.exception.AliceException;
import alice.task.Deadline;
import alice.task.Event;
import alice.task.Task;
import alice.task.ToDo;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Interprets Alice commands and converts their arguments into domain objects.
 */
public class Parser {
    private static final DateTimeFormatter DEADLINE_INPUT_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter EVENT_INPUT_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");

    /**
     * Returns the first word of a user command without changing the original input.
     *
     * @param userInput the complete command entered by the user
     * @return the command word, or an empty string when the input is empty
     */
    public String getCommandWord(String userInput) {
        int firstSpace = userInput.indexOf(' ');
        return firstSpace == -1 ? userInput : userInput.substring(0, firstSpace);
    }

    /**
     * Creates a task from a todo, deadline, or event command.
     *
     * @param userInput the complete task-creation command
     * @return the task described by the command
     * @throws AliceException if the task command is incomplete or invalid
     */
    public Task parseTask(String userInput) throws AliceException {
        return switch (getCommandWord(userInput)) {
            case "todo" -> parseToDo(userInput);
            case "deadline" -> parseDeadline(userInput);
            case "event" -> parseEvent(userInput);
            default -> throw new AliceException("I don't understand that command.");
        };
    }

    /**
     * Parses a task number and checks that it identifies an existing task.
     *
     * @param userInput the complete command entered by the user
     * @param command the command keyword, such as {@code mark}
     * @param taskCount the number of tasks currently in the list
     * @return the valid one-based task number
     * @throws AliceException if no valid task number was supplied
     */
    public int parseTaskNumber(String userInput, String command, int taskCount) throws AliceException {
        String number = userInput.substring(command.length()).trim();
        if (number.isEmpty()) {
            throw new AliceException("Please provide a task number to " + command + ".");
        }

        try {
            int taskNumber = Integer.parseInt(number);
            if (taskNumber < 1 || taskNumber > taskCount) {
                throw new AliceException("There is no task numbered " + taskNumber + ".");
            }
            return taskNumber;
        } catch (NumberFormatException exception) {
            throw new AliceException("The task number must be a positive whole number.");
        }
    }

    /**
     * Parses the date supplied to a date command.
     *
     * @param userInput the complete date command
     * @return the requested date
     * @throws AliceException if the date is not in yyyy-MM-dd format
     */
    public LocalDate parseDate(String userInput) throws AliceException {
        String dateText = userInput.substring("date".length()).trim();
        try {
            return LocalDate.parse(dateText, DEADLINE_INPUT_FORMAT);
        } catch (DateTimeParseException exception) {
            throw new AliceException("Please use the date format yyyy-MM-dd.");
        }
    }

    /**
     * Creates a todo task from its command.
     *
     * @param userInput the complete todo command
     * @return the requested todo task
     * @throws AliceException if the description is empty
     */
    private Task parseToDo(String userInput) throws AliceException {
        String description = userInput.substring("todo".length()).trim();
        if (description.isEmpty()) {
            throw new AliceException("The description of a todo cannot be empty.");
        }
        return new ToDo(description);
    }

    /**
     * Creates a deadline task from its command.
     *
     * @param userInput the complete deadline command
     * @return the requested deadline task
     * @throws AliceException if the description or date is invalid
     */
    private Task parseDeadline(String userInput) throws AliceException {
        String[] sections = userInput.substring("deadline".length()).trim().split(" /by ", 2);
        if (sections.length != 2 || sections[0].isBlank() || sections[1].isBlank()) {
            throw new AliceException("A deadline needs a description and a /by date.");
        }

        try {
            LocalDate by = LocalDate.parse(sections[1], DEADLINE_INPUT_FORMAT);
            return new Deadline(sections[0], by);
        } catch (DateTimeParseException exception) {
            throw new AliceException("Please use the date format yyyy-MM-dd.");
        }
    }

    /**
     * Creates an event task from its command.
     *
     * @param userInput the complete event command
     * @return the requested event task
     * @throws AliceException if the description or date and time values are invalid
     */
    private Task parseEvent(String userInput) throws AliceException {
        String[] fromSections = userInput.substring("event".length()).trim().split(" /from ", 2);
        if (fromSections.length != 2 || fromSections[0].isBlank()) {
            throw new AliceException("An event needs a description, a /from date time, and a /to date time.");
        }

        String[] toSections = fromSections[1].split(" /to ", 2);
        if (toSections.length != 2 || toSections[0].isBlank() || toSections[1].isBlank()) {
            throw new AliceException("An event needs a description, a /from date time, and a /to date time.");
        }

        try {
            LocalDateTime from = LocalDateTime.parse(toSections[0], EVENT_INPUT_FORMAT);
            LocalDateTime to = LocalDateTime.parse(toSections[1], EVENT_INPUT_FORMAT);
            if (to.isBefore(from)) {
                throw new AliceException("An event cannot end before it starts.");
            }
            return new Event(fromSections[0], from, to);
        } catch (DateTimeParseException exception) {
            throw new AliceException("Please use the event date time format yyyy-MM-dd HHmm.");
        }
    }
}
