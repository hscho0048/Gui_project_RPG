package main;

import javax.swing.*;
import controller.UserController;
import view.LoginView;

public class RPGGameApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            UserController userController = new UserController();
            new LoginView(userController);
        });
    }
}