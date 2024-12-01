package connecthub.UserAccountManagement.Backend;

import static connecthub.UserAccountManagement.Backend.UserDatabase.users;
import static connecthub.UserAccountManagement.Backend.HashPassword.hashPassword;
import static connecthub.UserAccountManagement.Backend.Validation.isEmailValid;
import static connecthub.UserAccountManagement.Backend.Validation.isUsernameValid;

public class CreateUser {
    private static long counter = 1;

    public static synchronized String generateID() {
        return String.valueOf(counter++);
    }

    public boolean signup(String email, String username, String password, String dateOfBirth) {
        if (!isEmailValid(email)) {
            return  false;
        } else if (!isUsernameValid(username)) {
            return false;
        } else if (users.contains(email)) {
            return false;
        }
        String hash = hashPassword(password);
        User newUser = new User(generateID(), email, username, hash, dateOfBirth, "offline");
        users.add(newUser);
        UserDatabase.saveUsersToJsonFile();
        return true;
    }
}
