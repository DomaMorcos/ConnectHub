package connecthub.UserAccountManagement.Frontend;

import connecthub.AlertUtils;
import connecthub.UserAccountManagement.Backend.LogUser;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.paint.Color;

import static connecthub.UserAccountManagement.Backend.Validation.isEmailValid;

public class LoginPage extends Application {
    private VBox root; // VBox containing TextFields and Buttons for Login/Signup
    private Label titleLabel , emailValidationLabel;
    private TextField email;
    private PasswordField password;
    private Button loginButton, registerButton;


    @Override
    public void start(Stage stage) throws Exception {
        root = new VBox();
        root.setId("Components");
        titleLabel = new Label("ConnectHub");
        titleLabel.setId("Title");

        email = new TextField();
        email.setPromptText("Email");


        emailValidationLabel = new Label();
        emailValidationLabel.setTextFill(Color.BLACK);
        emailValidationLabel.setId("EmailValidationLabel");

        // Add listener to email field for real-time validation
        email.textProperty().addListener((observable, oldValue, newValue) -> {
            if (isEmailValid(newValue)) {
                emailValidationLabel.setText("");
            } else {
                emailValidationLabel.setText("Invalid email format");
            }
        });

        password = new PasswordField();
        password.setPromptText("Password");

        loginButton = new Button("Login");
        loginButton.setOnAction(e -> handleLoginAction());

        registerButton = new Button("Register");
        registerButton.setOnAction(e ->{
            RegisterPage registerPage = new RegisterPage();
            try {
                stage.close();
                registerPage.start();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });

        root.getChildren().addAll(titleLabel, email, emailValidationLabel, password, loginButton, registerButton);
        Scene scene = new Scene(root, 1280, 720);
        scene.getStylesheets().add(getClass().getResource("LoginPage.css").toExternalForm());
        stage.setTitle("Login Page");
        stage.setScene(scene);
        stage.show();
    }

    private void handleLoginAction() {
        String emailText = email.getText();
        String passwordText = password.getText();
        LogUser logUser = new LogUser();
        if (emailText.isEmpty() || passwordText.isEmpty()) {
            AlertUtils.showErrorMessage("Empty Fields", "Please Fill All The Required Fields!");
        } else if (!isEmailValid(emailText)) {
            AlertUtils.showErrorMessage("Invalid Email", "Please enter a valid email address!");
        } else if (!logUser.login(emailText, passwordText)) {
            AlertUtils.showErrorMessage("Login Failed", "Invalid Email or Password!");
        } else {
            logUser.login(emailText, passwordText);
            AlertUtils.showConfirmationMessage("Login Successful", "Login Successful!");
        }
    }



    public static void main(String[] args) {
        launch(args);
    }
}
