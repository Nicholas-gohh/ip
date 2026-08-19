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
                int taskNo = Integer.parseInt(userInput.substring(5)); //5th char in string
                Task task = tasks[taskNo - 1]; //5th task means 4 in array
                task.markAsDone();
                System.out.println("Nice! I've marked this task as done:");
                System.out.println("  " + task);
                System.out.println(separator);

            } else if (userInput.startsWith("unmark ")) {
                int taskNo = Integer.parseInt(userInput.substring(7)); //7th char in string
                Task task = tasks[taskNo - 1]; //5th task means 4 in array
                task.unmarkAsDone();
                System.out.println("OK, I've marked this task as not done yet:");
                System.out.println("  " + task);
                System.out.println(separator);

            } else { //add the string to list and print it
                //store the string
                tasks[inputCount] = new Task(userInput);
                inputCount++;

                System.out.println("added: " + userInput);
                System.out.println(separator);
            }
        }
    }
}
