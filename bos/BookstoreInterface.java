package bos;

import java.sql.SQLException;
import java.util.List;
import java.util.Map.Entry;

import api.BookAPI;
import api.OrderAPI;
import model.Book;
import model.FormattedDate;
import model.Order;
import util.UserInput;

public class BookstoreInterface {
    public static void run () {
        boolean running = true;
        while(running) {
            switch(printMenu()) {
                case 1:
                    BookstoreInterface.performOrderUpdate();
                    break;

                case 2:
                    BookstoreInterface.performOrderQuery();
                    break;

                case 3:
                    BookstoreInterface.performPopularBookQuery();
                    break;

                case 4:
                    running = false;
                    break;
            }
        };
    }

    private static int printMenu() {
        System.out.println("\n<Bookstore Interface>");
        System.out.println("---------------------");
        System.out.println("1. Order Update");
        System.out.println("2. Order Query");
        System.out.println("3. Popular Book Query");
        System.out.println("4. Back");
        return UserInput.getChoice(4);
    }

    private static void performOrderUpdate() {
        String oid;
        Order order;

        // get order from DB
        oid = UserInput.getString("Order ID: "); 
        try {
            order = OrderAPI.selectOrderById(oid);
            if (order==null) {
                System.out.println("[INFO]: Order is not found");
                return;
            }
        } catch(SQLException e) {
            System.out.println("[ERROR]: Failed load order detail");
            return;
        }
        
        // count total quantity in order
        int totalQty = 0;
        for (Entry<String, Integer> entry : order.orders.entrySet())
            totalQty += entry.getValue();

        // print order summary
        System.out.println("Order Summary");
        System.out.println("Order ID:\t" + order.oid);
        System.out.println("Customer ID:\t" + order.cid);
        System.out.println("Date:\t" + order.date);
        System.out.println("Charge:\t" + order.charge);
        System.out.println("Total Books:\t" + totalQty);
        System.out.println("Shipping Status:\t" + order.status);
        System.out.println("");

        if (order.status=='Y') {
            System.out.println("[INFO]: The order is already shipped");
            return;
        }

        String confirm = UserInput.getString("Mark order shipped (Y/N): ");
        if(!confirm.equals("Y")) return;

        try {
            OrderAPI.updateOrderStatus(oid, 'Y');
            System.out.println("[SUCCESS]: Successfully updated the order");
        } catch(SQLException e) {
            System.out.println("[ERROR]: Failed to update the order");
        }
    }

    private static void performOrderQuery() {
        List<Order> orders;
        String month = UserInput.getString("Month(YYYY-MM): ");

        if(!FormattedDate.validateMonth(month)) {
            System.out.println("[ERROR]: Month format is invalid");
            return;
        }

        try {
            orders = OrderAPI.selectOrdersByMonth(month);

            if(orders.size() == 0) {
                System.out.println("[INFO]: No order records found");
                return;
            }

            // count total sales of orders
            int totalSales = 0;
            for (Order order : orders)
                totalSales += order.charge;

            for(int i = 0; i < orders.size(); i++) {
                System.out.println("Record " + (i+1));
                System.out.println("Order ID:\t" + orders.get(i).oid);
                System.out.println("Customer ID:\t" + orders.get(i).cid);
                System.out.println("Date:\t" + orders.get(i).date);
                System.out.println("Charge:\t" + orders.get(i).charge);
                System.out.println("");
            }

            System.out.println("Total Sales: " + totalSales);
        } catch(SQLException e) {
            System.out.println("[ERROR]: Failed to load orders");
        }
    }

    private static void performPopularBookQuery() {
        List<Book> books;
        int limit = UserInput.getInt("Limit: ");

        try {
            books = BookAPI.selectPopularBooks(limit);

            System.out.println("ISBN\t\t\t\tTitle\t\t\t\tNo.Copies");
            for(int i = 0; i < books.size(); i++)
                System.out.println(books.get(i).isbn + books.get(i).title + books.get(i).availableCopies);
        } catch (SQLException e) {
            System.out.println("[ERROR]: Failed to load books");
        }
    }
}