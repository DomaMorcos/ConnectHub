package connecthub.UserAccountManagement.Backend;


import static connecthub.UserAccountManagement.Backend.HashPassword.hashPassword;
import static connecthub.UserAccountManagement.Backend.Validation.isEmailValid;
import static connecthub.UserAccountManagement.Backend.Validation.isUsernameValid;

public class CreateUser {
    private static long counter = 1;

    public static synchronized String generateID() {
        return String.valueOf(counter++);
    }
    UserDatabase userDB = UserDatabase.getInstance();

    public boolean signup(String email, String username, String password, String dateOfBirth) {
        if (!isEmailValid(email)) {
            return  false;
        } else if (!isUsernameValid(username)) {
            return false;
        } else if (userDB.contains(email)) {
            return false; // Email already exists
        }
        String hash = hashPassword(password);
        User newUser = new User(generateID(), email, username, hash, dateOfBirth, "offline");
        userDB.users.add(newUser);
        userDB.printUsers();
        userDB.saveUsersToJsonFile();
        return true;
    }
}
