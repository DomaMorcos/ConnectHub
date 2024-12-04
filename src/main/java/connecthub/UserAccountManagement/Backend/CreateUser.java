package connecthub.UserAccountManagement.Backend;


import java.util.ArrayList;

import static connecthub.UserAccountManagement.Backend.HashPassword.hashPassword;
import static connecthub.UserAccountManagement.Backend.Validation.isEmailValid;
import static connecthub.UserAccountManagement.Backend.Validation.isUsernameValid;

public class CreateUser {
    UserDatabase userDB = UserDatabase.getInstance();

    public  synchronized String generateID() {
        if (userDB.users == null) {
            userDB.users = new ArrayList<>();
        }
        return Integer.toString(userDB.users.size() + 1);
    }



    public boolean signup(String email, String username, String password, String dateOfBirth) {

        if (!isEmailValid(email) || !isUsernameValid(username) || userDB.contains(email)) {
            return false;
        }
        String hash = hashPassword(password);
        User newUser = new User(generateID(), email, username, hash, dateOfBirth, "offline");
        userDB.users.add(newUser);
        userDB.saveUsersToJsonFile();
        return true;
    }
}


