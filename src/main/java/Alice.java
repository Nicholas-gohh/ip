import java.util.Scanner; //in order to get inputs from user

public class Alice {
    public static void main(String[] args) {
        String separator = "_______________________________________\n";
        //Used Codex to redesign banner
        String banner = separator
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

            } else if (userInput.startsWith("mark ")) { //used Codex to find out how to if statements for mark and unmark
                //remove first 5 chars
                int taskNo = Integer.parseInt(userInput.substring(5)); //5th char in string
                Task task = tasks[taskNo - 1]; //5th task means 4 in array
                task.markAsDone();
                System.out.println("Nice! I've marked this task as done:");
                System.out.println("  " + task);
                System.out.println(separator);

            } else if (userInput.startsWith("unmark ")) {
                //remove first 7 chars
                int taskNo = Integer.parseInt(userInput.substring(7)); //7th char in string
                Task task = tasks[taskNo - 1]; //5th task means 4 in array
                task.unmarkAsDone();
                System.out.println("OK, I've marked this task as not done yet:");
                System.out.println("  " + task);
                System.out.println(separator);

            } else if (userInput.startsWith("todo ")) {
                //remove first 5 chars
                String description = userInput.substring(5);
                tasks[inputCount] = new ToDo(description);
                inputCount++;

                taskAdded(tasks[inputCount - 1], inputCount, separator);
            } else if (userInput.startsWith("deadline ")) { //found how to split using Codex
                //remove first 9 chars, then split the remaining string with "/by" into 2
                String[] sections = userInput.substring(9).split(" /by ", 2);
                tasks[inputCount] = new Deadline(sections[0], sections[1]);
                inputCount++;

                taskAdded(tasks[inputCount - 1], inputCount, separator);
            } else if (userInput.startsWith("event ")) { //found how to split using Codex
                //remove first 6 chars, then split the remaining string with "/from" into 2
                String[] fromSection = userInput.substring(6).split(" /from ", 2);
                //then split again
                String[] toSection = fromSection[1].split(" /to ", 2);
                tasks[inputCount] = new Event(fromSection[0], toSection[0], toSection[1]);
                inputCount++;

                taskAdded(tasks[inputCount - 1], inputCount, separator);
            } else { //error
                System.out.println("error");
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
}
