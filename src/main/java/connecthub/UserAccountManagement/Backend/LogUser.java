package connecthub.UserAccountManagement.Backend;

import static connecthub.UserAccountManagement.Backend.HashPassword.hashPassword;



public class LogUser {

    UserDatabase userDB = UserDatabase.getInstance();
    public boolean login(String email, String password) {
        User user = userDB.getUser(email);
        if (user == null || !(user.getPassword().equals(hashPassword(password))) ) {
            return false;
        }
        user.setStatus("online");
        userDB.saveUsersToJsonFile();
        return true;
    }

    public void logout(String email) {
        User user = userDB.getUser(email);
        if (user != null) {
            user.setStatus("offline");
            userDB.saveUsersToJsonFile();
        }
    }
}


