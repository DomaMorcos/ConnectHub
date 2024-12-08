package connecthub;

import java.time.Duration;
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
    public static String formatTimeAgo(String timestamp) {
        try {
            // Parse the timestamp
            LocalDateTime dateTime = LocalDateTime.parse(timestamp);

            // Get the current time
            LocalDateTime now = LocalDateTime.now();

            // Calculate the duration between now and the provided timestamp
            Duration duration = Duration.between(dateTime, now);

            // Determine the time ago format
            long seconds = duration.getSeconds();
            long minutes = seconds / 60;
            long hours = minutes / 60;


            if (seconds < 60) {
                return seconds + "s ago";
            } else if (minutes < 60) {
                return minutes + "m ago";
            } else{
                return hours + "h ago";
            }
        } catch (Exception e) {
            // Handle parsing errors gracefully
            System.err.println("Invalid timestamp format: " + e.getMessage());
            return "Invalid timestamp";
        }
    }
}
