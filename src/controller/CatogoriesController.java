package controller;

import db.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import model.Catagory;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CatogoriesController {

    @FXML
    private AnchorPane root;

    @FXML
    private TableView<Catagory> tblCatogory;

    @FXML
    private TableColumn<Catagory, Integer> colCID;

    @FXML
    private TableColumn<Catagory, String> colCName;

    @FXML
    private TableColumn<Catagory, String> colDescription;

    @FXML
    private TextField txtCatogoryId;

    @FXML
    private TextField txtCatogoryName;

    @FXML
    private TextField txtDescription;

    @FXML
    void initialize() {
        // Initialize table columns
        colCID.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        colCName.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        colDescription.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());

        // Load data from database
        loadCategories();

        // Set up row selection listener
        tblCatogory.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                populateFields(newValue);
            }
        });
    }

    @FXML
    void AddCatogoryOnAction(ActionEvent event) {
        String name = txtCatogoryName.getText();
        String description = txtDescription.getText();

        if (name.isEmpty() || description.isEmpty()) {
            showAlert(AlertType.ERROR, "Input Error", "Please fill in all fields.");
            return;
        }

        try {
            Connection connection = DBConnection.getInstance().getConnection();
            String sql = "INSERT INTO Categories (name, description) VALUES (?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, description);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                showAlert(AlertType.INFORMATION, "Success", "Category added successfully.");
                loadCategories(); // Refresh the table
                clearFields(); // Clear fields after adding
            } else {
                showAlert(AlertType.ERROR, "Failure", "Failed to add category.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Database Error", "An error occurred while adding the category.");
        }
    }

    @FXML
    void DeleteCatogoryOnAction(ActionEvent event) {
        Catagory selectedCategory = tblCatogory.getSelectionModel().getSelectedItem();

        if (selectedCategory == null) {
            showAlert(AlertType.WARNING, "No Selection", "Please select a category to delete.");
            return;
        }

        Alert confirmationAlert = new Alert(AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirm Delete");
        confirmationAlert.setHeaderText(null);
        confirmationAlert.setContentText("Are you sure you want to delete this category?");

        if (confirmationAlert.showAndWait().get() == ButtonType.OK) {
            try {
                Connection connection = DBConnection.getInstance().getConnection();
                String sql = "DELETE FROM Categories WHERE id = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setInt(1, selectedCategory.getId());

                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    showAlert(AlertType.INFORMATION, "Success", "Category deleted successfully.");
                    loadCategories(); // Refresh the table
                    clearFields(); // Clear fields after deleting
                } else {
                    showAlert(AlertType.ERROR, "Failure", "Failed to delete category.");
                }

            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(AlertType.ERROR, "Database Error", "An error occurred while deleting the category.");
            }
        }
    }

    @FXML
    void EditCatogoryOnAction(ActionEvent event) {
        Catagory selectedCategory = tblCatogory.getSelectionModel().getSelectedItem();

        if (selectedCategory == null) {
            showAlert(AlertType.WARNING, "No Selection", "Please select a category to edit.");
            return;
        }

        String name = txtCatogoryName.getText();
        String description = txtDescription.getText();

        if (name.isEmpty() || description.isEmpty()) {
            showAlert(AlertType.ERROR, "Input Error", "Please fill in all fields.");
            return;
        }

        try {
            Connection connection = DBConnection.getInstance().getConnection();
            String sql = "UPDATE Categories SET name = ?, description = ? WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, description);
            preparedStatement.setInt(3, selectedCategory.getId());

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                showAlert(AlertType.INFORMATION, "Success", "Category updated successfully.");
                loadCategories(); // Refresh the table
                clearFields(); // Clear fields after updating
            } else {
                showAlert(AlertType.ERROR, "Failure", "Failed to update category.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Database Error", "An error occurred while updating the category.");
        }
    }

    @FXML
    void GotoHomeOnAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Home.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) tblCatogory.getScene().getWindow(); // Assuming tblCatogory is an element in your current scene
            stage.setScene(new Scene(root));
            stage.setTitle("Dashboard");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Failed to load the home screen.");
        }
    }

    private void loadCategories() {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            String sql = "SELECT * FROM Categories";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            ObservableList<Catagory> categories = FXCollections.observableArrayList();
            while (resultSet.next()) {
                Integer id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String description = resultSet.getString("description");
                categories.add(new Catagory(id, name, description));
            }

            tblCatogory.setItems(categories);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Database Error", "An error occurred while loading categories.");
        }
    }

    private void clearFields() {
        txtCatogoryId.clear();
        txtCatogoryName.clear();
        txtDescription.clear();
    }

    private void populateFields(Catagory category) {
        txtCatogoryId.setText(String.valueOf(category.getId()));
        txtCatogoryName.setText(category.getName());
        txtDescription.setText(category.getDescription());
    }

    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}