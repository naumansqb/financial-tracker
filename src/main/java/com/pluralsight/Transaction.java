package com.pluralsight;

import java.time.LocalDate;
import java.time.LocalTime;

public class Transaction{
    private LocalDate date;
    private LocalTime time;
    private String description;
    private String vendor;
    private double amount;

    public Transaction(LocalDate date, LocalTime time, String description, String vendor, double amount) {
        this.date = date;
        this.time = time;
        this.description = description;
        this.vendor = vendor;
        this.amount = amount;
    }
    public LocalTime getTime() {
        return time;
    }
    public LocalDate getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public String getVendor() {
        return vendor;
    }

    public double getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return String.format(
                "\t%-12s | %-10s | %-30s | %-25s | $%,.2f",
                date, time, description, vendor, amount
        );
    }


}
