package api;

import java.sql.*;
import java.io.*;

import util.*;
import model.*;

import java.io.File;
import java.util.Scanner;

public class SchemaAPI {
    public static void createTables() throws SQLException {
        Database db = new Database();
        Statement stmt = db.connection().createStatement();
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS Book(isbn VARCHAR(13),title varchar(100),unit_price int,no_copies int,primary key(isbn));");
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS Customer(cid VARCHAR(10),name varchar(50),ship_addr varchar(200),card_no varchar(19),primary key(cid));");
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS Orders(oid VARCHAR(8),cid varchar(10),date date,charge int,status varchar(1), primary key(oid), foreign key(cid) references Customer (cid) on delete cascade);");
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS Ordering(oid VARCHAR(8),isbn varchar(13),quantity int,primary key(oid,isbn), foreign key(oid) references Orders (oid) on delete cascade,foreign key(isbn) references Book (isbn) on delete cascade);");
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS Author(isbn VARCHAR(13),name varchar(50),primary key(isbn,name),foreign key(isbn) references Book (isbn) on delete cascade);");
        db.close();
    }

    public static void deleteTables() throws SQLException {
        Database db = new Database();
        Statement stmt = db.connection().createStatement();
        stmt.executeUpdate("SET FOREIGN_KEY_CHECKS=0");
        stmt.executeUpdate("DROP TABLE IF EXISTS Book");
        stmt.executeUpdate("DROP TABLE IF EXISTS Customer");
        stmt.executeUpdate("DROP TABLE IF EXISTS Orders");
        stmt.executeUpdate("DROP TABLE IF EXISTS Ordering");
        stmt.executeUpdate("DROP TABLE IF EXISTS Author");
        stmt.executeUpdate("SET FOREIGN_KEY_CHECKS=1");
        db.close();

    }

    public static void loadData(String path) throws Exception, SQLException, FileNotFoundException {
        File dir = new File(path);
        if(!dir.isDirectory()) throw new Exception("Path is not directory");

        File[] files = dir.listFiles();
        if (files == null || files.length == 0) throw new Exception("Directory is empty");

        for (File file : files) {
            String filename = file.getName();
            Scanner scanner = new Scanner(file);

            if (filename.equals("book.txt")) {
                System.out.println("Loading book data...");

                Book b = new Book();
                while (scanner.hasNextLine()) {
                    String[] data_tokens = scanner.nextLine().split("\\|");
                    
                    b.isbn = data_tokens[0]; //string
                    b.title = data_tokens[1]; //string
                    b.price = Integer.parseInt(data_tokens[2]); //int
                    b.availableCopies = Integer.parseInt(data_tokens[3]);//int

                    Database db = new Database();
                    String psql = "INSERT INTO Book Values(?,?,?,?)";
                    PreparedStatement pstmt = db.connection().prepareStatement(psql);
                    pstmt.setString(1, b.isbn);
                    pstmt.setString(2, b.title);
                    pstmt.setInt(3, b.price);
                    pstmt.setInt(4, b.availableCopies);
                    pstmt.executeUpdate();
                }
            } else if (filename.equals("customer.txt")) {
                System.out.println("Loading customer data...");

                Customer c = new Customer();
                while (scanner.hasNextLine()) {
                    String[] data_tokens = scanner.nextLine().split("\\|");
                    
                    c.cid = data_tokens[0]; //string
                    c.name = data_tokens[1]; //string
                    c.addr = data_tokens[2]; //string
                    c.cardNo = data_tokens[3];//string

                    Database db = new Database();
                    String psql = "INSERT INTO Customer Values(?,?,?,?)";
                    PreparedStatement pstmt = db.connection().prepareStatement(psql);
                    pstmt.setString(1, c.cid);
                    pstmt.setString(2, c.name);
                    pstmt.setString(3, c.addr);
                    pstmt.setString(4, c.cardNo);
                    pstmt.executeUpdate();
                }

            }
            else if (filename.equals("orders.txt")) {
                System.out.println("Loading order data...");

                Order o = new Order();
                while (scanner.hasNextLine()) {
                    String[] data_tokens = scanner.nextLine().split("\\|");
                    
                    o.oid = data_tokens[0]; //string
                    //o.date = data_tokens[1]; //date
                    Date sqldate = Date.valueOf(data_tokens[1]); //Date
                    o.status = data_tokens[2].charAt(0); //char
                    o.charge = Integer.parseInt(data_tokens[3]); //int
                    o.cid = data_tokens[4]; //string

                    Database db = new Database();
                    String psql = "INSERT INTO Orders Values(?,?,?,?,?)";
                    PreparedStatement pstmt = db.connection().prepareStatement(psql);
                    pstmt.setString(1, o.oid);
                    pstmt.setString(2, o.cid);
                    pstmt.setDate(3, sqldate);
                    pstmt.setInt(4, o.charge);
                    pstmt.setString(5, data_tokens[2]);
                    pstmt.execute();
                }

            }
            scanner.close();
        }

        for (File file : files) {
            String filename = file.getName();
            Scanner scanner = new Scanner(file);
            
            if (filename.equals("book_author.txt")) {
                System.out.println("Loading author data...");

                Book au = new Book();
                while (scanner.hasNextLine()) {
                    String[] data_tokens = scanner.nextLine().split("\\|");
                    
                    au.isbn = data_tokens[0]; //string
                    //au.authors = Arrays.asList(data_tokens[2].split("\\s*,\\s*"));
                    Database db = new Database();
                    String psql = "INSERT INTO Author Values(?,?)";
                    PreparedStatement pstmt = db.connection().prepareStatement(psql);
                    pstmt.setString(1, au.isbn);
                    pstmt.setString(2, data_tokens[1]);
                    pstmt.executeUpdate();
                }
            }

            else if (filename.equals("ordering.txt")) {
                System.out.println("Loading order detail data...");

                Order ordering = new Order();
                while (scanner.hasNextLine()) {
                    String[] data_tokens = scanner.nextLine().split("\\|");
                    
                    ordering.oid = data_tokens[0]; //string, oid
                    //ordering.orders.put(data_tokens[1], Integer.parseInt(data_tokens[2])); //isbn, quantity

                    Database db = new Database();
                    String psql = "INSERT INTO Ordering Values(?,?,?)";
                    PreparedStatement pstmt = db.connection().prepareStatement(psql);

                    pstmt.setString(1, ordering.oid);
                    pstmt.setString(2, data_tokens[1]);
                    pstmt.setInt(3, Integer.parseInt(data_tokens[2]));
                    pstmt.executeUpdate();
                }

            }
            scanner.close();
        }
    }
}