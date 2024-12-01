package connecthub.UserAccountManagement.Backend;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashPassword {
    public static String hashPassword(String password) {
        try {
            MessageDigest m = MessageDigest.getInstance("SHA-256");
            byte[] hash = m.digest(password.getBytes());
            StringBuilder s = new StringBuilder();
            for (byte b : hash) {
                s.append(String.format("%02x", b));
            }
            return s.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}
