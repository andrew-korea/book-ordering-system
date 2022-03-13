package api;

import java.sql.*;
import java.io.*;

import util.*;
import model.*;

import java.io.File;
import java.util.Scanner;

public class SystemTimeAPI {
    private static String st = "";

    public static void initialize() throws Exception {
        try {
            SystemTimeAPI.st = OrderAPI.selectSysTime();
        } catch (SQLException e) {
            throw new Exception("Cannot initialize system time");
        }
    }

    public static String get() {
        return SystemTimeAPI.st;
    }

    public static void set(String time) throws Exception {
        String latestDate = "";
        try {
            latestDate = OrderAPI.selectLatestOrderTime();
            System.out.println("Latest order date: " + latestDate);
        } catch (SQLException e) {
            throw new Exception("Failed to retrieve latest order date");
        }

        if (FormattedDate.isBefore(time, latestDate)) {
            throw new Exception("New system date must be later than or equal to the latest order date");
        }

        SystemTimeAPI.st = time;
    }
}