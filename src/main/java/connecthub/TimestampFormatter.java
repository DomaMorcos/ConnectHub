package connecthub;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimestampFormatter {

    // Private constructor to prevent instantiation
    private TimestampFormatter() {
    }
    public static String formatTimestamp(String timestamp) {
        try {
            // Parse the timestamp
            LocalDateTime dateTime = LocalDateTime.parse(timestamp);

            // Create the formatter with the desired format
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a");

            // Format and return the date-time string
            return dateTime.format(formatter);
        } catch (Exception e) {
            // Handle parsing errors gracefully
            System.err.println("Invalid timestamp format: " + e.getMessage());
            return null;
        }
    }
}
