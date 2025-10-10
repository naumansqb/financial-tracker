package com.pluralsight;

import java.io.*;
import java.sql.SQLDataException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;

/*
 * Capstone skeleton – personal finance tracker.
 * ------------------------------------------------
 * File format  (pipe-delimited)
 *     yyyy-MM-dd|HH:mm:ss|description|vendor|amount
 * A deposit has a positive amount; a payment is stored
 * as a negative amount.
 */
public class FinancialTracker {
    private static final ArrayList<Transaction> transactions = new ArrayList<>();
    private static final String FILE_NAME = "transactions.csv";
    private static final String DATE_PATTERN = "yyyy-MM-dd";
    private static final String TIME_PATTERN = "HH:mm:ss";
    private static final String DATETIME_PATTERN = DATE_PATTERN + " " + TIME_PATTERN;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern(DATE_PATTERN);
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern(TIME_PATTERN);
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern(DATETIME_PATTERN);

    public static void main(String[] args) {
        loadTransactions(FILE_NAME);
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("Welcome to TransactionApp");
            System.out.println("Choose an option:");
            System.out.println("D) Add Deposit");
            System.out.println("P) Make Payment (Debit)");
            System.out.println("L) Ledger");
            System.out.println("X) Exit");

            String input = scanner.nextLine().trim();

            switch (input.toUpperCase()) {
                case "D" -> addDeposit(scanner);
                case "P" -> addPayment(scanner);
                case "L" -> ledgerMenu(scanner);
                case "X" -> running = false;
                default -> System.out.println("Invalid option");
            }
        }
        scanner.close();
    }


    /**
     * Load transactions from FILE_NAME.
     * • If the file doesn’t exist, create an empty one so that future writes succeed.
     * • Each line looks like: date|time|description|vendor|amount
     * • If line is empty or line doesn't have all values skip and print error
     * • Checks if date and time are in correct format
     */
    public static void loadTransactions(String fileName) {
        File file = new File(fileName);
        try {
            if (!file.exists()) {
                file.createNewFile();
                System.out.println("Created new file: " + fileName);
            }
            try (BufferedReader reader = new BufferedReader(new FileReader(fileName));) {
                String line = "";
                int lineNumber = 0;
                while ((line = reader.readLine()) != null) {
                    lineNumber++;
                    line = line.trim();
                    if (line.isEmpty()) {
                        continue;
                    }
                    String[] token = line.split("\\|");
                    try {
                        if (token.length != 5) {
                            System.out.println("Error getting data from line: " + lineNumber);
                            continue;
                        }
                        LocalDate date = LocalDate.parse(token[0], DATE_FMT);
                        LocalTime time = LocalTime.parse(token[1], TIME_FMT);
                        String description = token[2];
                        String vendor = token[3];
                        double amount = Double.parseDouble(token[4]);
                        transactions.add(new Transaction(date.toString(), time.toString(), description, vendor, amount));
                    } catch (DateTimeException e) {
                        System.err.println("Invalid Date or time at line " + lineNumber);
                    } catch (NumberFormatException e) {
                        System.err.printf("At line %d, invalid input for amount\n", lineNumber);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error Opening File: " + fileName);
        }
    }

    /**
     * Prompt for ONE date+time string in the format
     * "yyyy-MM-dd HH:mm:ss", plus description, vendor, amount.
     * Validate that the amount entered is positive.
     * Store the amount as-is (positive) and append to the file.
     */
    private static void addDeposit(Scanner scan) {
        boolean correctInput=false;
        while (!correctInput) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true))) {
                System.out.println("Please enter the date for the transaction: (" + DATE_PATTERN + ")");
                LocalDate parsedDate = LocalDate.parse(scan.nextLine().trim(), DATE_FMT);
                System.out.println("Please enter the time for the transaction: (" + TIME_PATTERN + ")");
                LocalTime parsedTime = LocalTime.parse(scan.nextLine().trim(), TIME_FMT);
                System.out.println("Enter Description: ");
                String description = scan.nextLine().trim();
                System.out.println("Enter Vendor");
                String vendor = scan.nextLine().trim();
                double amount;
                while (true) {
                    System.out.println("Enter amount: ");
                    amount = scan.nextDouble();
                    scan.nextLine();
                    if (amount > 0) {
                        break;
                    } else {
                        System.out.println("Amount is not positive. Please enter a positive amount");
                    }
                }
                transactions.add(new Transaction(parsedDate.toString(), parsedTime.toString(), description, vendor, amount));
                writer.write(parsedDate.toString() + "|");
                writer.write(parsedTime.toString() + "|");
                writer.write(description + "|");
                writer.write(vendor + "|");
                writer.write(Double.toString(amount));
                writer.newLine();
                System.out.println("Successfully Saved Deposit");
                correctInput=true;
            } catch (DateTimeException e) {
                System.err.println("Invalid Input For date or time. Please follow correct format");
            } catch (NumberFormatException e) {
                System.err.println("Please enter a number for amount ");
            } catch (Exception e) {
                System.err.println("Unable to open file");
                break;
            }
        }
    }

    /**
     * Same prompts as addDeposit.
     * Amount must be entered as a positive number,
     * then converted to a negative amount before storing.
     */
    private static void addPayment(Scanner scanner) {
        // TODO
    }

    /* ------------------------------------------------------------------
       Ledger menu
       ------------------------------------------------------------------ */
    private static void ledgerMenu(Scanner scanner) {
        boolean running = true;
        while (running) {
            System.out.println("Ledger");
            System.out.println("Choose an option:");
            System.out.println("A) All");
            System.out.println("D) Deposits");
            System.out.println("P) Payments");
            System.out.println("R) Reports");
            System.out.println("H) Home");

            String input = scanner.nextLine().trim();

            switch (input.toUpperCase()) {
                case "A" -> displayLedger();
                case "D" -> displayDeposits();
                case "P" -> displayPayments();
                case "R" -> reportsMenu(scanner);
                case "H" -> running = false;
                default -> System.out.println("Invalid option");
            }
        }
    }

    /* ------------------------------------------------------------------
       Display helpers: show data in neat columns
       ------------------------------------------------------------------ */
    private static void displayLedger() { /* TODO – print all transactions in column format */ }

    private static void displayDeposits() { /* TODO – only amount > 0               */ }

    private static void displayPayments() { /* TODO – only amount < 0               */ }

    /* ------------------------------------------------------------------
       Reports menu
       ------------------------------------------------------------------ */
    private static void reportsMenu(Scanner scanner) {
        boolean running = true;
        while (running) {
            System.out.println("Reports");
            System.out.println("Choose an option:");
            System.out.println("1) Month To Date");
            System.out.println("2) Previous Month");
            System.out.println("3) Year To Date");
            System.out.println("4) Previous Year");
            System.out.println("5) Search by Vendor");
            System.out.println("6) Custom Search");
            System.out.println("0) Back");

            String input = scanner.nextLine().trim();

            switch (input) {
                case "1" -> {/* TODO – month-to-date report */ }
                case "2" -> {/* TODO – previous month report */ }
                case "3" -> {/* TODO – year-to-date report   */ }
                case "4" -> {/* TODO – previous year report  */ }
                case "5" -> {/* TODO – prompt for vendor then report */ }
                case "6" -> customSearch(scanner);
                case "0" -> running = false;
                default -> System.out.println("Invalid option");
            }
        }
    }

    /* ------------------------------------------------------------------
       Reporting helpers
       ------------------------------------------------------------------ */
    private static void filterTransactionsByDate(LocalDate start, LocalDate end) {
        // TODO – iterate transactions, print those within the range
    }

    private static void filterTransactionsByVendor(String vendor) {
        // TODO – iterate transactions, print those with matching vendor
    }

    private static void customSearch(Scanner scanner) {
        // TODO – prompt for any combination of date range, description,
        //        vendor, and exact amount, then display matches
    }


}
