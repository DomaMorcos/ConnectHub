package connecthub.UserAccountManagement.Backend;

import static connecthub.UserAccountManagement.Backend.HashPassword.hashPassword;

public class LogUser {


    public boolean login(String email, String password) {
        User user = users.get(email);
        if (user == null || !user.getPassword().equals(hashPassword(password))) {
            return false;
        }
        user.setStatus("online");
        saveUSers();
        return true;
    }

    public void logout(String email) {
        User user = users.get(email);
        if (user != null) {
            user.setStatus("offline");
            saveUsers();
        }
    }
}
