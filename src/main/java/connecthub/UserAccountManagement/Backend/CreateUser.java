package connecthub.UserAccountManagement.Backend;


import java.util.UUID;

import static connecthub.UserAccountManagement.Backend.UserDatabase.users;
import static connecthub.UserAccountManagement.Backend.Validation.isEmailValid;
import static connecthub.UserAccountManagement.Backend.HashPassword.hashPassword;

public class CreateUser {
    private static long counter = 1;

    public static synchronized String generateID() {
        return String.valueOf(counter++);
    }

    public boolean signup(String email, String username, String password, String dateOfBirth) {
//        if (!isEmailValid(email) || users.containsKey(email)){
//            return false;
//        }

        String hash = hashPassword(password);
        User newUser = new User(generateID(), email, username, hash, dateOfBirth, "offline");
        users.add(newUser);
        UserDatabase.saveUsersToJsonFile();
        return true;
    }

}
