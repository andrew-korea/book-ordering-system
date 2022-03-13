package model;

import java.util.regex.Pattern;
import java.util.List;

public class Book {
    public String isbn;             // format of "X-XXXX-XXXX-X"
    public String title;            // non-empty string of max 100 characters
    public int price;               // non-negative integer
    public int availableCopies;     // non-negative integer
    public List<String> authors;    // list of authors

    public static boolean validateIsbn(String isbn) {
        return Pattern.matches("\\d-\\d\\d\\d\\d-\\d\\d\\d\\d-\\d", isbn);
    }
}