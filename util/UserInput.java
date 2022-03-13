package util;

import java.util.Scanner;

public class UserInput {
    private static Scanner scanner = new Scanner(System.in);

    public static int getChoice(int choices) {
        while(true) {
            try {
                System.out.print("Choice: ");
                int choice = Integer.parseInt(scanner.nextLine());
                if(choice >= 1 && choice <= choices) return choice;
                System.out.println("[ERROR]: Invalid choice\n");
            } catch(NumberFormatException e) {
                System.out.println("[ERROR]: Input must be a number\n");
            }
        }
    }

    public static String getString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    public static int getInt(String prompt) {
        while(true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine());
            } catch(NumberFormatException e) {
                System.out.println("[ERROR]: Input must be a number\n");
            }
        }
    }
}