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
        while (true) {
            String userInput = scanner.nextLine(); //take in input
            System.out.println(separator);

            if (userInput.equals("bye")) { //check if need to exit
                System.out.println("Bye. Hope to see you again soon!");
                System.out.println(separator);
                break;
            }
            else {
                System.out.println(userInput);
                System.out.println(separator);
            }
        }
    }
}
