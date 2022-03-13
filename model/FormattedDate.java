package model;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class FormattedDate {
    public static boolean validateDate(String date) {
        try {
            LocalDate.parse(date);
        } catch (DateTimeParseException e) {
            return false;
        }
        return true;
    }

    public static boolean validateMonth(String month) {
        try {
            LocalDate.parse(month + "-01");
        } catch (DateTimeParseException e) {
            return false;
        }
        return true;
    }

    public static boolean isBefore(String date1, String date2) {
        return LocalDate.parse(date1).isBefore(LocalDate.parse(date2));
    }
}