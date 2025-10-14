package com.pluralsight;

import java.io.*;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class FinancialTracker {
    private static final ArrayList<Transaction> transactions = new ArrayList<>();
    private static final String FILE_NAME = "transactions.csv";
    private static final String DATE_PATTERN = "yyyy-MM-dd";
    private static final String TIME_PATTERN = "HH:mm:ss";
    private static final String DATETIME_PATTERN = DATE_PATTERN + " " + TIME_PATTERN;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern(DATE_PATTERN);
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern(TIME_PATTERN);
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern(DATETIME_PATTERN);

    public static void main(String[] args) throws IOException {
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
     * - Exit method right away!
     * • Each line looks like: date|time|description|vendor|amount
     */
    public static void loadTransactions(String fileName) {
        File file = new File(fileName);
        try {
            if (!file.exists()) {
                file.createNewFile();
                //System.out.println("Created new file: " + fileName);
                return;
            }
        } catch (Exception e) {
            System.out.println("Error occurred creating a new file. Please try later:)");
            return;
        }
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }
                String[] token = line.split("\\|");
                if (token.length != 5) {
                    continue;
                }
                LocalDate date = LocalDate.parse(token[0]);
                LocalTime time = LocalTime.parse(token[1]);
                String description = token[2];
                String vendor = token[3];
                double amount = Double.parseDouble(token[4]);
                transactions.add(new Transaction(date, time, description, vendor, amount));
            }
            reader.close();
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
        LocalDate parsedDate = null;
        do {
            try {
                System.out.println("Please enter the date for the transaction: (" + DATE_PATTERN + ")");
                parsedDate = LocalDate.parse(scan.nextLine(), DATE_FMT);
            } catch (DateTimeException e) {
                System.out.println("Incorrect Input For Date! \n");
            }
        } while (parsedDate == null);

        LocalTime parsedTime = null;
        do {
            try {
                System.out.println("Please enter the time for the transaction: (" + TIME_PATTERN + ")");
                parsedTime = LocalTime.parse(scan.nextLine(), TIME_FMT);
            } catch (DateTimeException e) {
                System.out.println("Please input time based on format\n");
            }
        } while (parsedTime == null);

        String description;
        do {
            System.out.println("Enter Description: (Must be filled in)");
            description = scan.nextLine().trim();
        } while (description.isEmpty());

        String vendor;
        do {
            System.out.println("Enter Vendor: (Must be filled in) ");
            vendor = scan.nextLine().trim();
        } while (vendor.isEmpty());
        double amount = 0;
        do {
            try {
                System.out.println("Enter amount: (Must be greater than 0) ");
                amount = scan.nextDouble();
                scan.nextLine();
            } catch (InputMismatchException e) {
                System.out.println("Invalid entry. Please enter a numerical input");
                scan.nextLine();

            }
        } while (amount <= 0);

        transactions.add(new Transaction(parsedDate, parsedTime, description, vendor, amount));
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true));
            writer.write(parsedDate + "|");
            writer.write(parsedTime + "|");
            writer.write(description + "|");
            writer.write(vendor + "|");
            writer.write(Double.toString(amount));
            writer.newLine();
            System.out.println("Successfully Saved Deposit");
            writer.close();
        } catch (Exception e) {
            System.err.println("Error writing to file");
        }

    }

    /**
     * Same prompts as addDeposit.
     * Amount must be entered as a positive number,
     * then converted to a negative amount before storing.
     */
    private static void addPayment(Scanner scan) {
        LocalDate parsedDate = null;
        do {
            try {
                System.out.println("Please enter the date for the transaction: (" + DATE_PATTERN + ")");
                parsedDate = LocalDate.parse(scan.nextLine(), DATE_FMT);
            } catch (DateTimeException e) {
                System.out.println("Incorrect Input For Date! \n");
            }
        } while (parsedDate == null);

        LocalTime parsedTime = null;
        do {
            try {
                System.out.println("Please enter the time for the transaction: (" + TIME_PATTERN + ")");
                parsedTime = LocalTime.parse(scan.nextLine(), TIME_FMT);
            } catch (DateTimeException e) {
                System.out.println("Please input time based on format\n");
            }
        } while (parsedTime == null);

        String description;
        do {
            System.out.println("Enter Description: (Must be filled in)");
            description = scan.nextLine().trim();
        } while (description.isEmpty());

        String vendor;
        do {
            System.out.println("Enter Vendor: (Must be filled in) ");
            vendor = scan.nextLine().trim();
        } while (vendor.isEmpty());
        double amount = 0;
        do {
            try {
                System.out.println("Enter amount: (Must be greater than 0) ");
                amount = scan.nextDouble();
                scan.nextLine();
            } catch (InputMismatchException e) {
                System.out.println("Invalid entry. Please enter a numerical input");
                scan.nextLine();
            }
        } while (amount <= 0);
        amount = -amount;
        transactions.add(new Transaction(parsedDate, parsedTime, description, vendor, amount));
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true));
            writer.write(parsedDate + "|");
            writer.write(parsedTime + "|");
            writer.write(description + "|");
            writer.write(vendor + "|");
            writer.write(Double.toString(amount));
            writer.newLine();
            System.out.println("Successfully Saved Payment");
            writer.close();
        } catch (Exception e) {
            System.err.println("Error writing to file");
        }
    }

    private static void ledgerMenu(Scanner scanner) {
        boolean running = true;
        transactions.sort(Comparator.comparing(Transaction::getDate, Comparator.reverseOrder()).thenComparing(Transaction::getTime, Comparator.reverseOrder()));
        if (transactions.isEmpty()) {
            System.out.println("No entries to display. You must add a transaction first.");
            return;
        }
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

    /**
     * - USes consistent format across all ledger reports
     */
    private static void displayHeader() {
        System.out.println("\t------------------------------------------------------------------------------------------------------");
        System.out.format(
                "\t%-12s | %-10s | %-30s | %-25s | %s\n",
                "Date", "Time", "Description", "Vendor", "Amount"
        );
        System.out.println("\t------------------------------------------------------------------------------------------------------");
    }

    private static void displayLedger() {
        System.out.println("Displaying All Entries");
        displayHeader();
        for (Transaction transaction : transactions) {
            System.out.println(transaction);
        }
    }

    private static void displayDeposits() {
        boolean found = false;
        for (Transaction t : transactions) {
            if (t.getAmount() > 0) {
                found = true;
                break;
            }
        }
        if (!found) {
            System.out.println("No Deposits Found");
            return;
        }
        System.out.println("Displaying Deposits: ");
        displayHeader();
        for (Transaction t : transactions) {
            if (t.getAmount() > 0) {
                System.out.println(t);
            }
        }
    }


    private static void displayPayments() {
        boolean found = false;
        for (Transaction t : transactions) {
            if (t.getAmount() < 0) {
                found = true;
                break;
            }
        }
        if (!found) {
            System.out.println("No Payments Found");
            return;
        }
        System.out.println("Displaying Payments: ");
        displayHeader();
        for (Transaction t : transactions) {
            if (t.getAmount() < 0) {
                System.out.println(t);
            }
        }
    }


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
                case "1" -> {
                    String title = "Month To Date";
                    LocalDate start = LocalDate.now().withDayOfMonth(1);
                    LocalDate end = LocalDate.now();
                    filterTransactionsByDate(start, end, title);
                }
                case "2" -> {
                    String title = "Previous Month";
                    LocalDate end = LocalDate.now().withDayOfMonth(1).minusDays(1);
                    LocalDate start = LocalDate.now().minusMonths(1).withDayOfMonth(1);
                    filterTransactionsByDate(start, end, title);
                }
                case "3" -> {
                    String title = "Year To Date";
                    LocalDate start = LocalDate.now().withDayOfYear(1);
                    LocalDate end = LocalDate.now();
                    filterTransactionsByDate(start, end, title);
                }
                case "4" -> {
                    String title = "Previous Year";
                    LocalDate start = LocalDate.now().minusYears(1).withDayOfYear(1);
                    LocalDate end = LocalDate.now().withDayOfYear(1).minusDays(1);
                    filterTransactionsByDate(start, end, title);
                }
                case "5" -> {
                    System.out.println("Please enter the name of the vendor you would like to filter the entries by: ");
                    String userInput = scanner.nextLine().trim();
                    filterTransactionsByVendor(userInput);

                }
                case "6" -> customSearch(scanner);
                case "0" -> running = false;
                default -> System.out.println("Invalid option");
            }
        }
    }

    private static void filterTransactionsByDate(LocalDate start, LocalDate end, String title) {
        boolean found = false;
        int printStartingLine = 0;
        for (Transaction t : transactions) {
            if (!t.getDate().isBefore(start) && !t.getDate().isAfter(end)) {
                if (printStartingLine == 0) {
                    System.out.println("Displaying " + title + " Entries:");
                    displayHeader();
                    printStartingLine++;
                }
                System.out.println(t);
                found = true;
            }
        }
        if (!found) {
            System.out.println("No entries to display for " + title);
        }
    }

    private static void filterTransactionsByVendor(String vendor) {
        boolean found = false;
        int printStartingLine = 0;
        for (Transaction t : transactions) {
            if (t.getVendor().equalsIgnoreCase(vendor)) {
                if (printStartingLine == 0) {
                    System.out.printf("Displaying Entries From Vendor '%s':\n", vendor);
                    displayHeader();
                    printStartingLine++;
                }
                System.out.println(t);
                found = true;
            }
        }
        if (!found) {
            System.out.printf("No entries to display for vendor: '%s'\n", vendor);
        }

    }

    private static void customSearch(Scanner scan) {
        System.out.println("Enter values for the filters you'd like to apply.\n" +
                "Press Enter to skip any filter.\n");
        System.out.println("Starting Date: ");
        LocalDate startingDate = parseDate(scan.nextLine().trim());
        System.out.println("Ending Date: ");
        LocalDate endingDate = parseDate(scan.nextLine().trim());
        System.out.println("Description: ");
        String description = scan.nextLine().trim();
        System.out.println("Vendor: ");
        String vendor = scan.nextLine().trim();
        System.out.println("Exact Amount");
        Double amount = parseDouble(scan.nextLine().trim());
    }


    private static LocalDate parseDate(String s) {
        if (s.isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(s, DATE_FMT);
        } catch (DateTimeException e) {
            System.out.println("Invalid date \"" + s + "\". Skipping that filter.");
            return null;
        }
    }

    private static Double parseDouble(String s) {
        if (s.isEmpty()) {
            return null;
        }
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount \"" + s + "\". Skipping filter.");
            return null;
        }
    }
}
