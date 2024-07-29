package controller;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class HomeController {
    @FXML
    private AnchorPane root;

    @FXML
    void btnMemberOnAction(ActionEvent event) throws IOException {
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Members.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Library Members");
            stage.show();





           
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception, e.g., show an error dialog
        }
    }

    @FXML
    void btnBookCatogoryOnAction(ActionEvent event) {
       
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Catogories.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Library Book Catogories");
            stage.show();

            
            
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception, e.g., show an error dialog
        }
    }

    @FXML
    void btnBookOnAction(ActionEvent event) {
       
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Books.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Library Books");
            stage.show();

            
            
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception, e.g., show an error dialog
        }
    }

    @FXML
    void btnBorrowingsOnAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Borrowing.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Books Borrow and Return List");
            stage.show();

            
            
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception, e.g., show an error dialog
        }
    }

    



    @FXML
    void btnLogoutOnAction(ActionEvent event) throws IOException {
        // Get the current stage (home window)
    Stage homeStage = (Stage) root.getScene().getWindow();
    
    // Close the current home stage
    homeStage.close();
  

   
    }

   
}
