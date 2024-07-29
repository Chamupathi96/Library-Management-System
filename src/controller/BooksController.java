package controller;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import model.Book;
import db.DBConnection;

public class BooksController implements Initializable {

    @FXML
    private AnchorPane root;

    @FXML
    private TableView<Book> tblBooks;

    @FXML
    private TableColumn<Book, Integer> colId;

    @FXML
    private TableColumn<Book, String> colTitle;

    @FXML
    private TableColumn<Book, String> colAuther;

    @FXML
    private TableColumn<Book, String> colCategory;

    @FXML
    private TableColumn<Book, String> colIsbn;

    @FXML
    private TableColumn<Book, Integer> colNoofCopies;

    @FXML
    private TextField txtId;

    @FXML
    private TextField txtAuther;

    @FXML
    private TextField txtCategory;

    @FXML
    private TextField txtIsbn;

    @FXML
    private TextField txtNoofCopies;

    @FXML
    private TextField txtTitle;

    private ObservableList<Book> bookList;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        bookList = FXCollections.observableArrayList();

        // Set up the columns
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colAuther.setCellValueFactory(new PropertyValueFactory<>("author"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colIsbn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        colNoofCopies.setCellValueFactory(new PropertyValueFactory<>("copiesAvailable"));

        loadBooks();

        // Add listener for table row selection
        tblBooks.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> showBookDetails(newValue));
    }

    private void loadBooks() {
        String query = "SELECT b.id, b.title, b.author, c.name AS category, b.isbn, b.copies_available " +
                       "FROM Books b LEFT JOIN Categories c ON b.category_id = c.id";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            bookList.clear();

            while (rs.next()) {
                String category = rs.getString("category");
                System.out.println("Category: " + category); // Debugging output
                bookList.add(new Book(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        category,
                        rs.getString("isbn"),
                        rs.getInt("copies_available")
                ));
            }

            tblBooks.setItems(bookList);

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load books from the database.");
        }
    }

    @FXML
    private void btnAddBooksOnAction(ActionEvent event) {
        String title = txtTitle.getText();
        String author = txtAuther.getText();
        String category = txtCategory.getText();
        String isbn = txtIsbn.getText();
        int copiesAvailable = Integer.parseInt(txtNoofCopies.getText());

        String query = "INSERT INTO Books (title, author, category_id, isbn, copies_available) " +
                       "VALUES (?, ?, (SELECT id FROM Categories WHERE name = ?), ?, ?)";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, title);
            stmt.setString(2, author);
            stmt.setString(3, category);
            stmt.setString(4, isbn);
            stmt.setInt(5, copiesAvailable);

            stmt.executeUpdate();
            showAlert("Success", "Book added successfully.");
            loadBooks();
            clearTextFields();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to add book to the database.");
        }
    }

    @FXML
    private void btnEditBooksOnAction(ActionEvent event) {
        int id = Integer.parseInt(txtId.getText());
        String title = txtTitle.getText();
        String author = txtAuther.getText();
        String category = txtCategory.getText();
        String isbn = txtIsbn.getText();
        int copiesAvailable = Integer.parseInt(txtNoofCopies.getText());

        String query = "UPDATE Books SET title = ?, author = ?, category_id = (SELECT id FROM Categories WHERE name = ?), isbn = ?, copies_available = ? WHERE id = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, title);
            stmt.setString(2, author);
            stmt.setString(3, category);
            stmt.setString(4, isbn);
            stmt.setInt(5, copiesAvailable);
            stmt.setInt(6, id);

            stmt.executeUpdate();
            showAlert("Success", "Book updated successfully.");
            loadBooks();
            clearTextFields();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to update book in the database.");
        }
    }

    @FXML
    private void btnDeleteBooksOnAction(ActionEvent event) {
        int id = Integer.parseInt(txtId.getText());

        String query = "DELETE FROM Books WHERE id = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);

            stmt.executeUpdate();
            showAlert("Success", "Book deleted successfully.");
            loadBooks();
            clearTextFields();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to delete book from the database.");
        }
    }

    @FXML
    private void btnGotoHomeOnAction(ActionEvent event) {
        
       try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Home.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) tblBooks.getScene().getWindow(); 
        stage.setScene(new Scene(root));
        stage.setTitle("Dashbord");
        stage.show();
    } catch (IOException e) {
        e.printStackTrace();
       
    }




    }

    private void showBookDetails(Book book) {
        if (book != null) {
            txtId.setText(String.valueOf(book.getId()));
            txtTitle.setText(book.getTitle());
            txtAuther.setText(book.getAuthor());
            txtCategory.setText(book.getCategory());
            txtIsbn.setText(book.getIsbn());
            txtNoofCopies.setText(String.valueOf(book.getCopiesAvailable()));
        } else {
            clearTextFields();
        }
    }

    private void clearTextFields() {
        txtId.clear();
        txtTitle.clear();
        txtAuther.clear();
        txtCategory.clear();
        txtIsbn.clear();
        txtNoofCopies.clear();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}