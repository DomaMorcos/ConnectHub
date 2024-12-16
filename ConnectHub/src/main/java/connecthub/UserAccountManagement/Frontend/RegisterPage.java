package connecthub.UserAccountManagement.Frontend;

import connecthub.AlertUtils;
import connecthub.UserAccountManagement.Backend.CreateUser;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import static connecthub.UserAccountManagement.Backend.Validation.isEmailValid;
import static connecthub.UserAccountManagement.Backend.Validation.isUsernameValid;

public class RegisterPage {
    private GridPane grid;
    private Label emailLabel, usernameLabel, passwordLabel, dateOfBirthLabel, emailValidationLabel, usernameValidationLabel;
    private TextField emailTextField, usernameTextField;
    private PasswordField passwordField;
    private DatePicker dateOfBirthDatePicker;
    private Button registerButton;


    public void start() throws Exception {
        Stage stage = new Stage();

        grid = new GridPane();
        grid.setId("Components");


        emailLabel = new Label("Email");
        grid.add(emailLabel, 0, 0);

        emailTextField = new TextField();
        grid.add(emailTextField, 1, 0);

        emailValidationLabel = new Label();
        emailValidationLabel.setTextFill(Color.BLACK);
        emailValidationLabel.setId("EmailValidationLabel");
        grid.add(emailValidationLabel, 1, 1);

        // Add listener to email field for real-time validation
        emailTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (isEmailValid(newValue)) {
                emailValidationLabel.setText("");
            } else {
                emailValidationLabel.setText("Enter a valid email format");
            }
        });

        usernameLabel = new Label("Username");
        grid.add(usernameLabel, 0, 2);

        usernameTextField = new TextField();
        grid.add(usernameTextField, 1, 2);
        usernameValidationLabel = new Label();
        usernameValidationLabel.setTextFill(Color.RED);
        emailValidationLabel.setId("UsernameValidationLabel");
        grid.add(usernameValidationLabel, 1, 3);

        // Add listener to email field for real-time validation
        usernameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (isUsernameValid(newValue)) {
                usernameValidationLabel.setText("");
            } else {
                usernameValidationLabel.setText("Username should start with a character");
            }
        });

        passwordLabel = new Label("Password");
        grid.add(passwordLabel, 0, 5);

        passwordField = new PasswordField();
        grid.add(passwordField, 1, 5);


        dateOfBirthLabel = new Label("Date of Birth");
        grid.add(dateOfBirthLabel, 0, 7);

        dateOfBirthDatePicker = new DatePicker();
        grid.add(dateOfBirthDatePicker, 1, 7);

        registerButton = new Button("Register");
        registerButton.setOnAction(e -> {
            // Call the registration handler and check if registration was successful
            boolean registrationSuccessful = handleRegisterAction(stage);

            // Go to the login page if there is a successful registration
            if (registrationSuccessful) {
                LoginPage loginPage = new LoginPage();
                try {
                    loginPage.start(stage);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        grid.add(registerButton, 1, 9);


        Scene scene = new Scene(grid, 1280, 720); // Scene -> Grid -> Buttons
        scene.getStylesheets().add(getClass().getResource("RegisterPage.css").toExternalForm());
        stage.setScene(scene); // Stage -> Scene - > Grid -> Buttons
        stage.setTitle("Register Window");
        stage.show();

    }

    public boolean handleRegisterAction(Stage stage) {
        CreateUser  createUser  = new CreateUser ();
        String email = emailTextField.getText();
        String username = usernameTextField.getText();
        String password = passwordField.getText();
        String dateOfBirth = dateOfBirthDatePicker.getValue() != null ? dateOfBirthDatePicker.getValue().toString() : "";

        boolean registrationSuccessful = false; // Flag to indicate registration success

        // Check if all fields are empty
        if (email.isEmpty() && username.isEmpty() && password.isEmpty() && dateOfBirth.isEmpty()) {
            AlertUtils.showErrorMessage("Empty Fields", "Please fill in all the required fields!");
        } else if (email.isEmpty()) {
            AlertUtils.showErrorMessage("Empty Email", "Please enter your email!");
        } else if (username.isEmpty()) {
            AlertUtils.showErrorMessage("Empty Username", "Please enter your username!");
        } else if (password.isEmpty()) {
            AlertUtils.showErrorMessage("Empty Password", "Please enter your password!");
        } else if (dateOfBirth.isEmpty()) {
            AlertUtils.showErrorMessage("Empty Date of Birth", "Please select your date of birth!");
        } else if (!isEmailValid(email)) {
            AlertUtils.showErrorMessage("Invalid Email", "Please enter a valid email format!");
        } else if (!isUsernameValid(username)) {
            AlertUtils.showErrorMessage("Invalid Username", "Username should start with a character!");
        } else if (!createUser.signup(email, username, password, dateOfBirth)) {
            AlertUtils.showWarningMessage("Error", "Email already in use!");
        } else {
            // Successful registration
            AlertUtils.showInformationMessage("Registration Successful", "Registration successful!");
            registrationSuccessful = true; // Set the flag to true
            stage.close(); // Close the registration window only on successful registration
        }
        return registrationSuccessful;
    }
}
