package com.example.geektrust;

public class Main {
    private static final String STOCK_DATA_JSON_PATH = "stock_data.json";
    
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java -jar geektrust.jar <input_file_path>");
            System.exit(1);
        }
        
        try {
            Application application = new Application(STOCK_DATA_JSON_PATH);
            application.run(args[0]);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }
}
