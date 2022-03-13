package model;

public class Customer {
    public String cid;          // non-empty string of maximum 10 characters
    public String name;         // non-empty string of maximum 50 characters
    public String addr;         // non-empty string of maximum 200 characters. The components of the address are delimited by ','.
    public String cardNo;       // format of "XXXX-XXXX-XXXX-XXXX"
}