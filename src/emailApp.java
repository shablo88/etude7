package src;


import java.util.Scanner;

public class emailApp {

    /* main method
     */
    public static void main(String[] args) {
        // creates a scanner object to read inputs
        Scanner scan = new Scanner(System.in);

        // boolean object to know whether or not to continue
        boolean cont = true;

        // as long as cont remains true, repeat the loop
        while(cont) {
            System.out.println("Enter email address or \"quit\"");
            String input = scan.next();

            input = input.toLowerCase();

            if (!input.equals("quit")) {
                // replaces _at_ and _dot_ with the appropriate substitutions
                input = input.replaceAll("_at_","@");
                input = input.replaceAll("_dot_",".");

            }
            // if input is quit, then end the loop and program
            else {
                System.out.println("Finished");
                cont = false;
            }
        }
    }
}
