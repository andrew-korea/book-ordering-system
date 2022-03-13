package bos;

import util.UserInput;
import api.SystemTimeAPI;

public class BookOrderingSystem {
    public static void main(String[] args) {
        try {
            SystemTimeAPI.initialize();
        } catch (Exception e) {
            System.out.println("[SYSTEM ERROR]: " + e.getMessage());
            return;
        }

        boolean running = true;
        while(running) {
            switch(printMenu()) {
                case 1:
                    SystemInterface.run();
                    break;

                case 2:
                    CustomerInterface.run();
                    break;

                case 3:
                    BookstoreInterface.run();
                    break;

                case 4:
                    SystemInterface.showSystemDate();
                    break;

                case 5:
                    running = false;
                    break;
            }
        }
    }

    private static int printMenu() {
        System.out.println("\n<Book Ordering System>");
        System.out.println("----------------------");
        System.out.println("1. System Interface");
        System.out.println("2. Customer Interface");
        System.out.println("3. Bookstore Interface");
        System.out.println("4. Show System Date");
        System.out.println("5. Quit");
        return UserInput.getChoice(5);
    }
}