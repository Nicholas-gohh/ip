package alice.parser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import alice.exception.AliceException;
import alice.task.Deadline;
import alice.task.Event;
import alice.task.Task;
import alice.task.ToDo;

/**
 * Interprets Alice commands and converts their arguments into domain objects.
 */
public class Parser {
    private static final DateTimeFormatter DEADLINE_INPUT_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter EVENT_INPUT_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");
    private static final int MAX_COMMAND_SECTIONS = 2;
    private static final String DEADLINE_BY_PREFIX = " /by ";
    private static final String EVENT_FROM_PREFIX = " /from ";
    private static final String EVENT_TO_PREFIX = " /to ";
    private static final String EVENT_DETAILS_ERROR =
            "An event needs a description, a /from date time, and a /to date time.";

    /**
     * Returns the first word of a user command without changing the original input.
     *
     * @param userInput The complete command entered by the user.
     * @return The command word, or an empty string when the input is empty.
     */
    public String getCommandWord(String userInput) {
        int firstSpace = userInput.indexOf(' ');
        return firstSpace == -1 ? userInput : userInput.substring(0, firstSpace);
    }

    /**
     * Returns the portion of a command after its command word.
     *
     * @param userInput The complete command entered by the user.
     * @param command The command to remove from the input.
     * @return The trimmed command arguments.
     */
    private String getCommandArguments(String userInput, Command command) {
        return userInput.substring(command.getCommandWord().length()).trim();
    }

    /**
     * Creates a task from a todo, deadline, or event command.
     *
     * @param userInput The complete task-creation command.
     * @return The task described by the command.
     * @throws AliceException If the task command is incomplete or invalid.
     */
    public Task parseTask(String userInput) throws AliceException {
        return switch (Command.fromCommandWord(getCommandWord(userInput))) {
            case TODO -> parseToDo(userInput);
            case DEADLINE -> parseDeadline(userInput);
            case EVENT -> parseEvent(userInput);
            default -> throw new AliceException("I don't understand that command.");
        };
    }

    /**
     * Parses a task number and checks that it identifies an existing task.
     *
     * @param userInput The complete command entered by the user.
     * @param command The command being parsed.
     * @param taskCount The number of tasks currently in the list.
     * @return The valid one-based task number.
     * @throws AliceException If no valid task number was supplied.
     */
    public int parseTaskNumber(String userInput, Command command, int taskCount) throws AliceException {
        String commandWord = command.getCommandWord();
        String number = getCommandArguments(userInput, command);

        if (number.isEmpty()) {
            throw new AliceException("Please provide a task number to " + commandWord + ".");
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
     * @param userInput The complete date command.
     * @return The requested date.
     * @throws AliceException If the date is not in yyyy-MM-dd format.
     */
    public LocalDate parseDate(String userInput) throws AliceException {
        String dateText = getCommandArguments(userInput, Command.DATE);
        try {
            return LocalDate.parse(dateText, DEADLINE_INPUT_FORMAT);
        } catch (DateTimeParseException exception) {
            throw new AliceException("Please use the date format yyyy-MM-dd.");
        }
    }

    /**
     * Parses the keyword supplied to a find command.
     *
     * @param userInput The complete find command.
     * @return The keyword to search for.
     * @throws AliceException If the keyword is empty.
     */
    public String parseKeyword(String userInput) throws AliceException {
        String keyword = getCommandArguments(userInput, Command.FIND);
        if (keyword.isEmpty()) {
            throw new AliceException("Please provide a keyword to find.");
        }
        return keyword;
    }

    /**
     * Creates a todo task from its command.
     *
     * @param userInput The complete todo command.
     * @return The requested todo task.
     * @throws AliceException If the description is empty.
     */
    private Task parseToDo(String userInput) throws AliceException {
        String description = getCommandArguments(userInput, Command.TODO);
        if (description.isEmpty()) {
            throw new AliceException("The description of a todo cannot be empty.");
        }
        return new ToDo(description);
    }

    /**
     * Creates a deadline task from its command.
     *
     * @param userInput The complete deadline command.
     * @return The requested deadline task.
     * @throws AliceException If the description or date is invalid.
     */
    private Task parseDeadline(String userInput) throws AliceException {
        String[] sections = getCommandArguments(userInput, Command.DEADLINE)
                .split(DEADLINE_BY_PREFIX, MAX_COMMAND_SECTIONS);
        if (sections.length != MAX_COMMAND_SECTIONS
                || sections[0].isBlank() || sections[1].isBlank()) {
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
     * @param userInput The complete event command.
     * @return The requested event task.
     * @throws AliceException If the description or date and time values are invalid.
     */
    private Task parseEvent(String userInput) throws AliceException {
        String[] fromSections = getCommandArguments(userInput, Command.EVENT)
                .split(EVENT_FROM_PREFIX, MAX_COMMAND_SECTIONS);
        if (fromSections.length != MAX_COMMAND_SECTIONS || fromSections[0].isBlank()) {
            throw new AliceException(EVENT_DETAILS_ERROR);
        }

        String[] toSections = fromSections[1].split(EVENT_TO_PREFIX, MAX_COMMAND_SECTIONS);
        if (toSections.length != MAX_COMMAND_SECTIONS
                || toSections[0].isBlank() || toSections[1].isBlank()) {
            throw new AliceException(EVENT_DETAILS_ERROR);
        }

        try {
            LocalDateTime fromDateTime = LocalDateTime.parse(toSections[0], EVENT_INPUT_FORMAT);
            LocalDateTime toDateTime = LocalDateTime.parse(toSections[1], EVENT_INPUT_FORMAT);
            if (toDateTime.isBefore(fromDateTime)) {
                throw new AliceException("An event cannot end before it starts.");
            }
            return new Event(fromSections[0], fromDateTime, toDateTime);
        } catch (DateTimeParseException exception) {
            throw new AliceException("Please use the event date time format yyyy-MM-dd HHmm.");
        }
    }
}
