package controller;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.control.DatePicker;
import model.Borrowing;
import dao.BorrowingDAO;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;

public class BorrowingController {

    @FXML
    private TextField txtMemberId;
    @FXML
    private TextField txtBookId;
    @FXML
    private DatePicker dpBorrowDate;
    @FXML
    private DatePicker dpDueDate;
    @FXML
    private DatePicker dpReturnDate;
    @FXML
    private TextField txtFine;
    @FXML
    private TableView<Borrowing> tblBorrowings;
    @FXML
    private TableColumn<Borrowing, Integer> colMemberId;
    @FXML
    private TableColumn<Borrowing, Integer> colBookId;
    @FXML
    private TableColumn<Borrowing, Date> colBorrowDate;
    @FXML
    private TableColumn<Borrowing, Date> colDueDate;
    @FXML
    private TableColumn<Borrowing, Date> colReturnDate;
    @FXML
    private TableColumn<Borrowing, Double> colFine;

    private ObservableList<Borrowing> borrowingList = FXCollections.observableArrayList();
    private BorrowingDAO borrowingDAO = new BorrowingDAO();

    @FXML
    private void btnHandleBorrowOnAction(ActionEvent event) throws SQLException {
        int memberId = Integer.parseInt(txtMemberId.getText());
        int bookId = Integer.parseInt(txtBookId.getText());
        Date borrowDate = Date.valueOf(dpBorrowDate.getValue());
        Date dueDate = Date.valueOf(dpDueDate.getValue());
        double fine = 0.0; // Default fine value, can be updated later

        Borrowing borrowing = new Borrowing(0, bookId, memberId, borrowDate, dueDate, null, fine);
        borrowingDAO.save(borrowing);
        showAlert(Alert.AlertType.INFORMATION, "Success", "Book borrowed successfully.");
        clearFields();
        loadBorrowingData(); // Refresh table data
    }

    @FXML
    private void btnHandleReturnOnAction(ActionEvent event) throws SQLException {
        int memberId = Integer.parseInt(txtMemberId.getText());
        int bookId = Integer.parseInt(txtBookId.getText());
        Date returnDate = Date.valueOf(dpReturnDate.getValue());
        double fine = Double.parseDouble(txtFine.getText());

        borrowingDAO.updateReturnDate(memberId, bookId, returnDate, fine);
        showAlert(Alert.AlertType.INFORMATION, "Success", "Book returned successfully.");
        clearFields();
        loadBorrowingData(); // Refresh table data
    }

    @FXML
    private void btnGotoHomeOnAction(ActionEvent event) {
        
     try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Home.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) tblBorrowings.getScene().getWindow(); 
            stage.setScene(new Scene(root));
            stage.setTitle("Dashboard");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Failed to load the home screen.");
        }


    }

    private void loadBorrowingData() {
        try {
            borrowingList.setAll(borrowingDAO.getAllBorrowings());
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load borrowing data.");
        }
        tblBorrowings.setItems(borrowingList);
    }

    private void clearFields() {
        txtMemberId.clear();
        txtBookId.clear();
        dpBorrowDate.setValue(null);
        dpDueDate.setValue(null);
        dpReturnDate.setValue(null);
        txtFine.clear();
        tblBorrowings.getItems().clear(); // Clear the TableView data
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void initialize() {
        // Initialize columns
        colMemberId.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getMemberId()).asObject());
        colBookId.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getBookId()).asObject());
        colBorrowDate.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getBorrowDate()));
        colDueDate.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getDueDate()));
        colReturnDate.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getReturnDate()));
        colFine.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getFine()).asObject());
        
        // Add listener to Member ID field
        txtMemberId.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                clearFields(); // Clear fields and table when Member ID is erased
            } else {
                loadBorrowingDataForMember(Integer.parseInt(newValue)); // Load data for the new Member ID
            }
        });
        
        loadBorrowingData(); // Load data when the UI is initialized
    }

    private void loadBorrowingDataForMember(int memberId) {
        try {
            borrowingList.setAll(borrowingDAO.getBorrowingsByMemberId(memberId));
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load borrowing data for member.");
        }
        tblBorrowings.setItems(borrowingList);
        if (!borrowingList.isEmpty()) {
            Borrowing borrowing = borrowingList.get(0);
            txtBookId.setText(String.valueOf(borrowing.getBookId()));
            dpBorrowDate.setValue(borrowing.getBorrowDate().toLocalDate());
            dpDueDate.setValue(borrowing.getDueDate().toLocalDate());
        }
    }
}