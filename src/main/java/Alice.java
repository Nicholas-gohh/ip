import java.util.Scanner; //in order to get inputs from user

public class Alice {
    public static void main(String[] args) {
        String separator = "_______________________________________";
        //Used Codex to redesign banner
        String banner = separator + "\n"
                + "    _    _     ___ ____ _____ \n"
                + "   / \\  | |   |_ _/ ___| ____|\n"
                + "  / _ \\ | |    | | |   |  _|  \n"
                + " / ___ \\| |___ | | |___| |___ \n"
                + "/_/   \\_\\_____|___\\____|_____|\n"
                + "Hello! I'm Alice.\n"
                + "What can I do for you?\n"
                + separator;
 //               + "Bye. Hope to see you again soon!\n"
 //               + separator;
        System.out.println(banner);

        //Used Codex to find how to take in inputs from user
        Scanner scanner = new Scanner(System.in); //object to read
        Task[] tasks = new Task[100];
        int inputCount = 0;
        while (true) {
            String userInput = scanner.nextLine(); //take in input
            System.out.println(separator);
            try {
                if (userInput.equals("bye")) { //check if need to exit
                    System.out.println("Bye. Hope to see you again soon!");
                    System.out.println(separator);
                    break;

                } else if (userInput.equals("list")) { //print the list
                    System.out.println("Here are the tasks in your list:");
                    for (int i = 0; i < inputCount; i++) {
                        System.out.println((i + 1) + "." + tasks[i]);
                    }
                    System.out.println(separator);

                } else if (userInput.equals("mark") || userInput.startsWith("mark ")) { //used Codex to find out how to if statements for mark and unmark
                    int taskNo = getTaskNumber(userInput, "mark", inputCount);
                    Task task = tasks[taskNo - 1]; //5th task means 4 in array

                    task.markAsDone();
                    System.out.println("Nice! I've marked this task as done:");
                    System.out.println("  " + task);
                    System.out.println(separator);

                } else if (userInput.equals("unmark") || userInput.startsWith("unmark ")) {
                    int taskNo = getTaskNumber(userInput, "unmark", inputCount);
                    Task task = tasks[taskNo - 1]; //5th task means 4 in array

                    task.unmarkAsDone();
                    System.out.println("OK, I've marked this task as not done yet:");
                    System.out.println("  " + task);
                    System.out.println(separator);

                } else if (userInput.equals("todo") || userInput.startsWith("todo ")) {
                    //remove first 4 chars
                    String description = userInput.substring(4).trim();
                    if (description.isEmpty()) {
                        throw new AliceException("The description of a todo cannot be empty.");
                    }
                    tasks[inputCount] = new ToDo(description);
                    inputCount++;
                    taskAdded(tasks[inputCount - 1], inputCount, separator);

                } else if (userInput.equals("deadline") || userInput.startsWith("deadline ")) { //found how to split using Codex
                    //remove first 8 chars, then split the remaining string with "/by" into 2
                    String[] sections = userInput.substring(8).trim().split(" /by ", 2);
                    if (sections.length !=2 || sections[0].isBlank() || sections[1].isBlank()) {
                        throw new AliceException("A deadline needs a description and a /by value.");
                    }
                    tasks[inputCount] = new Deadline(sections[0], sections[1]);
                    inputCount++;
                    taskAdded(tasks[inputCount - 1], inputCount, separator);

                } else if (userInput.equals("event") || userInput.startsWith("event ")) { //found how to split using Codex
                    //remove first 5 chars, then split the remaining string with "/from" into 2
                    String[] fromSections = userInput.substring(5).trim().split(" /from ", 2);
                    if (fromSections.length != 2 || fromSections[0].isBlank()) {
                        throw new AliceException("An event needs a description, a /from value, and a /to value.");
                    }
                    //then split again
                    String[] toSections = fromSections[1].split(" /to ", 2);
                    if (toSections.length != 2 || toSections[0].isBlank() || toSections[1].isBlank()) {
                        throw new AliceException("An event needs a description, a /from value, and a /to value.");
                    }

                    tasks[inputCount] = new Event(fromSections[0], toSections[0], toSections[1]);
                    inputCount++;
                    taskAdded(tasks[inputCount - 1], inputCount, separator);

                } else { //error
                    throw new AliceException("I don't understand that command.");
                }
            } catch (AliceException e) {
                System.out.println(e.getMessage());
                System.out.println(separator);
            }
        }
    }

    //Helper class for printing task added
    public static void taskAdded(Task task, int taskNo, String separator) {
        System.out.println("Got it. I've added this task:");
        System.out.println("  " + task);
        System.out.println("Now you have " + taskNo + " tasks in the list.");
        System.out.println(separator);

    }

    public static int getTaskNumber(String userInput, String command, int inputCount) throws AliceException {

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
