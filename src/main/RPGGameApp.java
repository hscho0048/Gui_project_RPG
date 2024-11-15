package main;

import javax.swing.*;
<<<<<<< HEAD
import controller.UserController;
import view.LoginView;
=======
<<<<<<< HEAD
import view.*;
=======
import controller.UserController;
import view.LoginView;
>>>>>>> 0ca0e7c (commit message)
>>>>>>> 35f4c543b586ee3eacd249c2c8f43679c629fab6

public class RPGGameApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
<<<<<<< HEAD
            UserController userController = new UserController();
            new LoginView(userController);
=======
<<<<<<< HEAD
            new LoginView();
=======
            UserController userController = new UserController();
            new LoginView(userController);
>>>>>>> 0ca0e7c (commit message)
>>>>>>> 35f4c543b586ee3eacd249c2c8f43679c629fab6
        });
    }
}
