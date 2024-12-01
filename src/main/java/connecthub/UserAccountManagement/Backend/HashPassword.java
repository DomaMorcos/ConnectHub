package connecthub.UserAccountManagement.Backend;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashPassword {


    public static String hashPassword(String password) {
        try {

            MessageDigest m = MessageDigest.getInstance("SHA-256"); // Creating a MessageDigest instance for the SHA-256 hashing algorithm
            byte[] hash = m.digest(password.getBytes()); // Converting the password string into a byte array and hashing the byte array
            StringBuilder s = new StringBuilder();
            for (byte b : hash) {
                s.append(String.format("%02x", b)); // Converting each byte to its corresponding two-digit hexadecimal representation ( Compact Representation )
            }
            return s.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

}
