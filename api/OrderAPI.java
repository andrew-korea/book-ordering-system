package api;

import java.sql.*;
import java.util.*;

import util.*;
import model.*;

public class OrderAPI {
    public static Order selectOrderById(String oid) throws SQLException {
        Order order = null;

        // TODO
        Database db = new Database();
        PreparedStatement pstatement = db.connection().prepareStatement("SELECT Orders.oid, Orders.date, Orders.charge,Orders.status " +
                " FROM Orders " +
                " WHERE Orders.oid = ? "
        );
        pstatement.setString(1, oid);
        ResultSet rs = null;
        rs = pstatement.executeQuery();
        if (rs.next()) {
            order = new Order();

            order.oid = rs.getString("oid");
            order.date = rs.getString("date");
            order.charge = rs.getInt("charge");
            order.status = rs.getString("status").charAt(0);


        }
        db.close();
        return order;
    }

    public static List<Order> selectOrdersByMonth(String month) throws SQLException {
        // Reference: Documentation 5.3 - 2


        List<Order> orders = new ArrayList<Order>();
        // TODO: select orders of the month with shipping status 'Y'
        // TODO: orders should be sorted in ascending order by `Order ID`
        // NOTE: no need to sum charges here
        Database db = new Database();
        PreparedStatement pstatement = db.connection().prepareStatement("SELECT Orders.oid, Orders.date, Orders.charge,Orders.status " +
                " FROM Orders " +
                " WHERE month(Orders.date) = ? "
        );
        pstatement.setString(1, month);
        ResultSet rs = null;
        rs = pstatement.executeQuery();

        while (rs.next()) {
            Order order = new Order();

            order.oid = rs.getString("oid");
            order.date = rs.getString("date");
            order.charge = rs.getInt("charge");
            order.status = rs.getString("status").charAt(0);

            orders.add(order);
        }

        db.close();
        return orders;
    }

    public static List<Order> selectOrdersByCidAndYear(String cid, int year) throws SQLException {
        // Reference: Documentation 5.2 - 4

        List<Order> orders = new ArrayList<Order>();


        // TODO: select orders of the month with shipping status 'Y'
        // TODO: orders should be sorted in ascending order by `Order ID`
        // NOTE: no need to sum charges here
        Database db = new Database();
        String sql="SELECT Orders.oid, Orders.date, Orders.charge,Orders.status " +
                " FROM Orders " +
                " WHERE year(Orders.date) = ? AND cid=? ";
     
        PreparedStatement pstatement = db.connection().prepareStatement(sql
        );
        pstatement.setInt(1, year);
        pstatement.setString(2, cid);
        ResultSet rs = null;
        rs = pstatement.executeQuery();

        while (rs.next()) {
            Order order = new Order();

            order.oid = rs.getString("oid");
            order.date = rs.getString("date");
            order.charge = rs.getInt("charge");
            order.status = rs.getString("status").charAt(0);

            orders.add(order);
        }

        db.close();
        return orders;


    }

    public static void insertOrder(String cid, Map<String, Integer> orders) throws SQLException {
        // Reference: Documentation 5.2 - 2
        

        // TODO: Insert Order
        Database db = new Database();

        Statement stmt = db.connection().createStatement();
        ResultSet rs = stmt.executeQuery("SELECT LPAD(MAX(oid)+1, 8, '0') as oid From Orders;");
        String oid = "";
        if(rs.next()) oid = rs.getString("oid");
        System.out.println("oid:" + oid);
        
        String isbn = "";
        Integer qty = 0;
        Integer unitShippingCharge = 10, handlingCharge = 10;
        Integer totalQ = 0, totalCharge = 0,totalBookPrice = 0;
        boolean atleastOneBookwithpostiveQ = false;

        for(String key:orders.keySet()){
            isbn = key;
            qty = orders.get(key);
            if (qty>0) atleastOneBookwithpostiveQ = true;

            totalQ += qty;

            //calc book price
            Integer unitBookPrice = 0;
            Book b = new Book();
            b = BookAPI.selectBookByISBN(isbn);
            unitBookPrice = b.price;
            totalBookPrice += unitBookPrice * qty;
        }
        Integer shippingPrice = (totalQ * unitShippingCharge) + handlingCharge;
        if (atleastOneBookwithpostiveQ)
            totalCharge = totalBookPrice + shippingPrice;
        else
            totalCharge = 0;

        //Update Orders Table
        Statement stmt2 = db.connection().createStatement();
        ResultSet rs2 = stmt2.executeQuery("SELECT CURDATE() as date;");

        long dummy = 644598000000L;
        java.sql.Date o_date = new java.sql.Date(dummy);
        if(rs2.next()) o_date = rs2.getDate("date");

        PreparedStatement pstatement = db.connection().prepareStatement("INSERT INTO Orders(oid,cid,date,charge,status) "+
        "SELECT ?, C.Cid,?,?,?  From Customer C "+
        " Where C.cid = ?;");

        pstatement.setString(1, oid); //oid = latest oid + 1
        pstatement.setDate(2, o_date); //extracted
        pstatement.setInt(3, totalCharge); //ok
        pstatement.setString(4, "N"); //default N
        pstatement.setString(5, cid); //default N

        pstatement.executeUpdate();

        for(String key:orders.keySet()){
            isbn = key;
            qty = orders.get(key);

            //Insert into Ordering Table (oid, isbn, Q)
            PreparedStatement pstatementOrdering = db.connection().prepareStatement("INSERT INTO Ordering(oid,isbn,quantity) "+
            " SELECT O.oid, ?, ? FROM Orders O " +
            " WHERE O.oid = ?;");
            pstatementOrdering.setString(1, isbn);
            pstatementOrdering.setInt(2, qty);
            pstatementOrdering.setString(3, oid);

            pstatementOrdering.executeUpdate();
        }
        db.close();

    }

    public static void updateOrderStatus(String oid, char status) throws SQLException {
        // Reference: Documentation 5.3 - 1

        // TODO

        Database db = new Database();
        PreparedStatement pstatement = db.connection().prepareStatement("update Order set status=? where oid=?"
        );
        pstatement.setString(1, Character.toString(status));
        pstatement.setString(2, oid);
        pstatement.executeUpdate();
        db.close();
    }

    public static void updateOrderQty(String oid, String isbn, int qty) throws SQLException {
        // Reference: Documentation 5.2 - 3

        // TODO
        Database db = new Database();
        PreparedStatement pstatement = db.connection().prepareStatement("update Ordering set isbn=?,quantity=? where oid=?"
        );
        pstatement.setString(1, isbn);
        pstatement.setInt(2, qty);
        pstatement.setString(3, oid);

        pstatement.executeUpdate();
        db.close();

    }

    public static String selectLatestOrderTime() throws SQLException {
        String date = "";

        Database db = new Database();
        Statement stmt = db.connection().createStatement();
        ResultSet rs = stmt.executeQuery("SELECT DATE(date) AS latest FROM Orders ORDER BY date DESC LIMIT 1");

        if (rs.next()) {
            date = rs.getString("latest");
        }
        db.close();

        return date;
    }


     public static String selectSysTime() throws SQLException {
        long dummy = 644598000000L;
        java.sql.Date date = new java.sql.Date(dummy);

        Database db = new Database();
        Statement stmt = db.connection().createStatement();
        ResultSet rs = stmt.executeQuery("SELECT CURDATE() as systime;");

        if (rs.next()) {
            date = rs.getDate("systime");
        }
        db.close();
        
        return date.toString();
    }
}