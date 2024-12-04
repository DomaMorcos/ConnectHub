package connecthub.UserAccountManagement.Backend;

import java.util.ArrayList;

import static connecthub.UserAccountManagement.Backend.UserDatabase.contains;
import static connecthub.UserAccountManagement.Backend.UserDatabase.users;
import static connecthub.UserAccountManagement.Backend.HashPassword.hashPassword;
import static connecthub.UserAccountManagement.Backend.Validation.isEmailValid;
import static connecthub.UserAccountManagement.Backend.Validation.isUsernameValid;

public class CreateUser {
    public synchronized String generateID() {
        if (users == null) {
            users = new ArrayList<>();
        }
        return Integer.toString(users.size() + 1);
    }

    public boolean signup(String email, String username, String password, String dateOfBirth) {
        if (!isEmailValid(email) || !isUsernameValid(username) || contains(email)) {
            return false;
        }
        String hash = hashPassword(password);
        User newUser = new User(generateID(), email, username, hash, dateOfBirth, "offline");
        users.add(newUser);
        UserDatabase.saveUsersToJsonFile();
        return true;
    }
}