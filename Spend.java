import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Spend{
   
   /**
      * reads from file and puts transactions in arraylist
      *
      * @param String filename - file path to read
      * @return arraylist of transaction objects
      */
    private static List<Transaction> readTransactionsFromFile(String filename) {
        List<Transaction> transactions = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
        
            // Skip the first line (header)
            br.readLine();
             
            //loads contents of csv into arraylist 
            String line;
            while ((line = br.readLine()) != null) {
                String[] content = line.split(",");
                String payer = content[0];
                
                int points = Integer.parseInt(content[1]);
                LocalDateTime timestamp = LocalDateTime.parse(content[2], DateTimeFormatter.ISO_DATE_TIME);
                
                //add new object of transaction into list
                transactions.add(new Transaction(payer, points, timestamp));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return transactions;
    }
   
   /**
      * tracks spent points and updates available balances
      *
      * @param int pointsToSpend - how many points intending to spend
      * @param List transactions - list of transactions
      * @return arraylist of transaction objects
      */
    private static Map<String, Integer> spendPoints(int pointsToSpend, List<Transaction> transactions) {
 
        // oldest to newest timestamp
        Collections.sort(transactions);

        //will keep track of payer balances
        Map<String, Integer> payerBalances = new HashMap<>();

        // Iterate over transactions and spend points from oldest to newest
        for (Transaction transaction : transactions) {
        
            if (pointsToSpend <= 0) 
                break;
            
            String payer = transaction.getPayer();
            int points = transaction.getPoints();
            LocalDateTime timestamp = transaction.getTimestamp();

            // Calculate the number of points to spend from this transaction
            int pointsAvailFromTrans = Math.min(points, pointsAvail);

            // Subtract the spent points from the transaction
            transaction.setPoints(points - pointsAvailFromTrans);

            // Subtract the spent points from the payer's balance
            payerBalances.put(payer, payerBalances.getOrDefault(payer, 0) - pointsAvailFromTrans);

            // Update the total number of points spent
            pointsToSpend -= pointsAvailFromTrans;
        }

        return payerBalances;
    }
   
  /**
    * transaction class that allows for detailed transaction objects
    *
    */
    private static class Transaction implements Comparable<Transaction> {
        private final String payer;
        private int points;
        private final LocalDateTime timestamp;

        public Transaction(String payer, int points, LocalDateTime timestamp) {
            this.payer = payer;
            this.points = points;
            this.timestamp = timestamp;
        }

        public String getPayer() {
            return payer;
        }

        public int getPoints() {
            return points;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }

        public void setPoints(int points) {
            this.points = points;
        }

        @Override
        public int compareTo(Transaction comp) {
            return timestamp.compareTo(comp.timestamp);
        }
    }
    
    /**
      * reads from file, calls methods and prints balances afterwards.
      *
      */
    public static void main(String[] args) {
    
        // Check that the correct number of arguments have been provided
        if (args.length != 2) {
            System.err.println("Err: arg length does not match required number of arguments");
            System.exit(1);
        }

        // Parse arguments
        int pointsToSpend = Integer.parseInt(args[0]);
        String filename = args[1];

        // Read the transactions from the file
        List<Transaction> transactions = readTransactionsFromFile(filename);

        // Spend points based on the argument using the rules above
        Map<String, Integer> payerBalances = spendPoints(pointsToSpend, transactions);

        // Returns current balance of all payers
        System.out.println(payerBalances);
    }

}
