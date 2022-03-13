package model;

import java.util.Map;

public class Order {
    public String oid;                      // format of "XXXXXXXX"
    public String cid;                      // non-empty string of maximum 10 chars
    public String date;                     // fomrat of "YYYY-MM-DD"
    public Map<String, Integer> orders;     // key in format of "X-XXXX-XXXX-X" and value the quantity
    public int charge;                      // non-negative integer
    public char status;                     // one of <"Y", "N">
}