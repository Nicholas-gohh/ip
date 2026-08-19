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
        String[] userInputs = new String[100];
        int inputCount = 0;
        while (true) {
            String userInput = scanner.nextLine(); //take in input
            System.out.println(separator);

            if (userInput.equals("bye")) { //check if need to exit
                System.out.println("Bye. Hope to see you again soon!");
                System.out.println(separator);
                break;
            } else if (userInput.equals("list")) { //print the list
                for (int i = 0; i < inputCount; i++) {
                    System.out.println((i + 1) + ". " + userInputs[i]);
                }
                System.out.println(separator);
            } else { //add the string to list and print it
                //store the string
                userInputs[inputCount] = userInput;
                inputCount++;

                System.out.println("added: " + userInput);
                System.out.println(separator);
            }
        }
    }
}
