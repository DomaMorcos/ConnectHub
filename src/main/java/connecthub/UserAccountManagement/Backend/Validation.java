package connecthub.UserAccountManagement.Backend;

public class Validation {
    public static boolean isEmailValid(String email) {
        return email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"); // Validating the Email format
    }
}
