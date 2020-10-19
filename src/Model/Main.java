/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author dwsou
 */
public class Main extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
 Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("View/LoginScreen.fxml"));
stage.setScene(new Scene(root));
 stage.show();
}
}
