package main;

import javax.swing.*;
<<<<<<< HEAD
import view.*;
=======
import controller.UserController;
import view.LoginView;
>>>>>>> 0ca0e7c (commit message)

public class RPGGameApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
<<<<<<< HEAD
            new LoginView();
=======
            UserController userController = new UserController();
            new LoginView(userController);
>>>>>>> 0ca0e7c (commit message)
        });
    }
}
