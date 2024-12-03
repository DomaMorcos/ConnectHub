package connecthub.UserAccountManagement.Backend;

import static connecthub.UserAccountManagement.Backend.HashPassword.hashPassword;
import static connecthub.UserAccountManagement.Backend.UserDatabase.getUser;
import static connecthub.UserAccountManagement.Backend.UserDatabase.users;

public class LogUser {
    public boolean login(String email, String password) {
        User user = getUser(email);
        if (user == null || !user.getPassword().equals(hashPassword(password)) ) {
            return false;
        }
        user.setStatus("online");
        UserDatabase.saveUsersToJsonFile();
        return true;
    }

    public void logout(String email) {
        User user = getUser(email);
        if (user != null) {
            user.setStatus("offline");
            UserDatabase.saveUsersToJsonFile();
        }
    }
}
