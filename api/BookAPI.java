package api;

import java.sql.*;
import java.util.*;

import util.*;
import model.*;

public class BookAPI {
    public static Book selectBookByISBN(String isbn) throws SQLException {
        // Reference: Documentation 5.2 - 1.a

        Book book = null;

        Database db = new Database();
        PreparedStatement pstatement = db.connection().prepareStatement("SELECT B.isbn, B.title, B.unit_price, B.no_copies, GROUP_CONCAT(A.name) " + 
        " FROM Book B " +
        " JOIN Author A ON B.isbn = A.isbn " +
        " WHERE B.isbn = ? " +
        " GROUP BY B.isbn;");
        pstatement.setString(1, isbn);    
        ResultSet rs = null;
        rs = pstatement.executeQuery();

        if (rs.next()) {
            book = new Book();
            book.authors = new ArrayList<String>();
            book.isbn = rs.getString("isbn");
            book.title = rs.getString("title");
            book.price = rs.getInt("unit_price");
            book.availableCopies = rs.getInt("no_copies");
            String authorsstring = rs.getString("GROUP_CONCAT(A.name)");
            String[] authors = authorsstring.split(",");
            for (String a: authors) {
                book.authors.add(a);
            }
        }
                        
        db.close();

        return book;
    }

    public static List<Book> selectBooksByTitle(String title) throws SQLException {
        // Reference: Documentation 5.2 - 1.b

        List<Book> books = new ArrayList<Book>();

        Database db = new Database();
        PreparedStatement pstatement = db.connection().prepareStatement("SELECT Temp.isbn, Temp.title, Temp.unit_price, Temp.no_copies, GROUP_CONCAT(A.name) " + 
        " FROM (SELECT B.isbn, B.title, B.unit_price, B.no_copies " + 
        " FROM Book B " + 
        " WHERE B.title LIKE ? " + 
        " ) AS Temp " + 
        " JOIN Author A ON Temp.isbn = A.isbn " + 
        " WHERE Temp.isbn = A.isbn " + 
        " GROUP BY Temp.isbn " +
        " ORDER BY Temp.title;");    
        pstatement.setString(1, title);    
        ResultSet rs = null;
        rs = pstatement.executeQuery();

        while (rs.next()) {
            Book book = new Book();
            book.authors = new ArrayList<String>();
            book.isbn = rs.getString("isbn");
            book.title = rs.getString("title");
            book.price = rs.getInt("unit_price");
            book.availableCopies = rs.getInt("no_copies");
            String authorsstring = rs.getString("GROUP_CONCAT(A.name)");
            String[] authors = authorsstring.split(",");
            for (String a: authors) {
                book.authors.add(a);
            }
            books.add(book);
        }
        db.close();

        return books;        
    }

    public static List<Book> selectBooksByAuthor(String author) throws SQLException {
        // Reference: Documentation 5.2 - 1.c

        List<Book> books = new ArrayList<Book>();

        Database db = new Database();
        PreparedStatement pstatement = db.connection().prepareStatement("SELECT Temp.isbn, Temp.title, Temp.unit_price, Temp.no_copies, Temp.name " +
        " FROM ( " +
        " SELECT * " +
        " FROM Book B natural join Author A " +
        " WHERE B.isbn = A.isbn AND A.name LIKE ?) AS Temp;");    
        pstatement.setString(1, author);
        ResultSet rs = null;
        rs = pstatement.executeQuery();

        while (rs.next()) {
            Book book = new Book();
            book.authors = new ArrayList<String>();
            book.isbn = rs.getString("isbn");
            book.title = rs.getString("title");
            book.price = rs.getInt("unit_price");
            book.availableCopies = rs.getInt("no_copies");

            book.authors.add(rs.getString("name"));
            books.add(book);
        }

        db.close();
        return books;
    }

    public static List<Book> selectPopularBooks(int limit) throws SQLException {
        // Reference: Documentation 5.3 - 3

        List<Book> books = new ArrayList<Book>();

        Database db = new Database();
        PreparedStatement pstatement = db.connection().prepareStatement("SELECT Temp.isbn, Temp.title, Temp.total " +
        " From ( " +
        " SELECT *, SUM(O.quantity) as total " +
        " FROM Ordering O natural join Book " +
        " GROUP BY O.isbn ) AS Temp " +
        " ORDER BY Temp.total DESC;");
        
        ResultSet rs = null;
        rs = pstatement.executeQuery();

        while (rs.next()) {
            Book book = new Book();
            book.isbn = rs.getString("isbn");
            book.title = rs.getString("title");
            //book.availableCopies here refers to the quantity ordered, not the # of available copies
            book.availableCopies = rs.getInt("total");

            books.add(book);
        }
       
        List<Book> ansbooks = new ArrayList<Book>();

        db.close();
        if (limit <= books.size()) {
            Book targetbook = new Book();
            targetbook = books.get(limit-1);
            Integer targetQ = targetbook.availableCopies;
            for (Book b: books) if (b.availableCopies >= targetQ) ansbooks.add(b);
            return ansbooks;
        }
        else return books;
    }
}