package bos;

import java.io.FileNotFoundException;
import java.sql.SQLException;

import api.SchemaAPI;
import api.SystemTimeAPI;
import model.FormattedDate;
import util.UserInput;

public class SystemInterface {
    public static void run () {
        boolean running = true;
        while(running) {
            switch(printMenu()) {
                case 1:
                    SystemInterface.performCreateTable();
                    break;

                case 2:
                    SystemInterface.performDeleteTable();
                    break;

                case 3:
                    SystemInterface.performInsertData();
                    break;

                case 4:
                    SystemInterface.performSetSystemDate();
                    break;

                case 5:
                    running = false;
                    break;
            }
        };
    }

    public static void showSystemDate() {
        SystemInterface.performShowSystemDate();
    }

    private static int printMenu() {
        System.out.println("\n<System Interface>");
        System.out.println("------------------");
        System.out.println("1. Create Tables");
        System.out.println("2. Delete Tables");
        System.out.println("3. Insert Data");
        System.out.println("4. Set System Date");
        System.out.println("5. Back");
        return UserInput.getChoice(5);
    }

    private static void performCreateTable() {
        try {
            SchemaAPI.createTables();
            System.out.println("[SUCCESS]: Successfully created tables");
        } catch(SQLException e) {
            System.out.println("[ERROR]: Failed to create tables");
        }
    }

    private static void performDeleteTable() {
        try {
            SchemaAPI.deleteTables();
            System.out.println("[SUCCESS]: Successfully deleted tables");
        } catch(SQLException e) {
            System.out.println("[ERROR]: Failed to delete tables");
        }
    }

    private static void performInsertData() {
        String path = UserInput.getString("Folder Path: ");

        try {
            SchemaAPI.loadData(path);
            System.out.println("[SUCCESS]: Successfully loaded data");
        } catch(SQLException e) {
            System.out.println("[ERROR]: Failed to load data");
        } catch(FileNotFoundException e) {
            System.out.println("[ERROR]: File not found");
        } catch(Exception e) {
            System.out.println("[ERROR]: " + e.getMessage());
        }

    }

    private static void performSetSystemDate() {
        String latestDate = "";
        String newDate = UserInput.getString("Date(YYYY-MM-DD): ");

        if(!FormattedDate.validateDate(newDate)) {
            System.out.println("[ERROR]: Date format is invalid");
            return;
        }

        try {
            SystemTimeAPI.set(newDate);
            System.out.println("[SUCCESS]: Sucessfully set system time: " + newDate);
        } catch (Exception e) {
            System.out.println("[ERROR]: " + e.getMessage());
        }
    }

    private static void performShowSystemDate() {
        System.out.println("System Date: " + SystemTimeAPI.get());
    }
}