package alice.parser;

// Used Codex to create these tests.
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import alice.exception.AliceException;
import alice.task.Deadline;
import alice.task.Event;
import alice.task.Task;
import alice.task.ToDo;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/**
 * Tests conversion and validation of Alice command input.
 */
class ParserTest {
    private final Parser parser = new Parser();

    /** Verifies that valid task commands create task objects with the supplied details. */
    @Test
    void parseTask_validTaskCommands_createsCorrectTasks() throws AliceException {
        Task todo = parser.parseTask("todo read book");
        Task deadline = parser.parseTask("deadline return book /by 2019-12-02");
        Task event = parser.parseTask("event project meeting /from 2019-12-02 1400 /to 2019-12-02 1600");

        assertEquals("read book", assertInstanceOf(ToDo.class, todo).getDescription());
        assertEquals(LocalDate.of(2019, 12, 2), assertInstanceOf(Deadline.class, deadline).getBy());
        Event parsedEvent = assertInstanceOf(Event.class, event);
        assertEquals(LocalDateTime.of(2019, 12, 2, 14, 0), parsedEvent.getFrom());
        assertEquals(LocalDateTime.of(2019, 12, 2, 16, 0), parsedEvent.getTo());
    }

    /** Verifies that malformed task commands report their specific validation errors. */
    @Test
    void parseTask_unknownOrIncompleteCommand_throwsHelpfulException() {
        AliceException unknownCommand = assertThrows(AliceException.class,
                () -> parser.parseTask("remind me"));
        AliceException incompleteDeadline = assertThrows(AliceException.class,
                () -> parser.parseTask("deadline return book"));
        AliceException backwardsEvent = assertThrows(AliceException.class,
                () -> parser.parseTask("event meeting /from 2019-12-02 1600 /to 2019-12-02 1400"));

        assertEquals("I don't understand that command.", unknownCommand.getMessage());
        assertEquals("A deadline needs a description and a /by date.", incompleteDeadline.getMessage());
        assertEquals("An event cannot end before it starts.", backwardsEvent.getMessage());
    }

    /** Verifies that a valid one-based task number is returned unchanged. */
    @Test
    void parseTaskNumber_validNumber_returnsOneBasedTaskNumber() throws AliceException {
        assertEquals(2, parser.parseTaskNumber("mark 2", "mark", 3));
    }

    /** Verifies that missing, non-numeric, and out-of-range task numbers are rejected. */
    @Test
    void parseTaskNumber_invalidNumbers_throwHelpfulException() {
        AliceException missingNumber = assertThrows(AliceException.class,
                () -> parser.parseTaskNumber("delete", "delete", 2));
        AliceException nonNumericNumber = assertThrows(AliceException.class,
                () -> parser.parseTaskNumber("mark two", "mark", 2));
        AliceException outOfRangeNumber = assertThrows(AliceException.class,
                () -> parser.parseTaskNumber("unmark 3", "unmark", 2));

        assertEquals("Please provide a task number to delete.", missingNumber.getMessage());
        assertEquals("The task number must be a positive whole number.", nonNumericNumber.getMessage());
        assertEquals("There is no task numbered 3.", outOfRangeNumber.getMessage());
    }

    /** Verifies that date input is parsed or rejected with the expected message. */
    @Test
    void parseDate_validAndInvalidDates_returnsDateOrThrowsHelpfulException() throws AliceException {
        assertEquals(LocalDate.of(2019, 12, 2), parser.parseDate("date 2019-12-02"));

        AliceException exception = assertThrows(AliceException.class,
                () -> parser.parseDate("date 2019-13-02"));
        assertEquals("Please use the date format yyyy-MM-dd.", exception.getMessage());
    }
}
