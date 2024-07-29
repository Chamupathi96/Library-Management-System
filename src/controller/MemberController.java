package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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
import model.Member;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ResourceBundle;

import db.DBConnection;

public class MemberController implements Initializable {

    @FXML
    private AnchorPane root;

    @FXML
    private TableView<Member> memberTable;

    @FXML
    private TableColumn<Member, Integer> idColumn;

    @FXML
    private TableColumn<Member, String> nameColumn;

    @FXML
    private TableColumn<Member, String> emailColumn;

    @FXML
    private TableColumn<Member, String> phoneColumn;

    @FXML
    private TableColumn<Member, LocalDate> membershipDateColumn;

    @FXML
    private TextField txtMemberEmail;

    @FXML
    private TextField txtMemberName;

    @FXML
    private TextField txtMemberPhone;

    @FXML
    private TextField txtMembersID;

    @FXML
    private TextField txtMembershipDate;

    

    @FXML
    void btnMemberAddOnAction(ActionEvent event) {
        String idText = txtMembersID.getText();
        String name = txtMemberName.getText();
        String email = txtMemberEmail.getText();
        String phone = txtMemberPhone.getText();
        String membershipDateText = txtMembershipDate.getText();

        int id;
        try {
            id = Integer.parseInt(idText);
        } catch (NumberFormatException e) {
            showAlert(AlertType.ERROR, "Invalid ID", "ID must be a number.");
            return;
        }

        // Validate Membership Date
        LocalDate membershipDate;
        try {
            membershipDate = LocalDate.parse(membershipDateText);
        } catch (DateTimeParseException e) {
            showAlert(AlertType.ERROR, "Invalid Date", "Date must be in the format YYYY-MM-DD.");
            return;
        }

        // Create Member object
        Member member = new Member(id, name, email, phone, membershipDate);

        // Save member to the database
        try {
            saveMemberToDatabase(member);
            showAlert(AlertType.INFORMATION, "Success", "Member added successfully.");
            clearFields();
            loadMemberData();  // Reload data to update the table view
        } catch (SQLException | ClassNotFoundException e) {
            showAlert(AlertType.ERROR, "Database Error", "Failed to add member to the database.");
            e.printStackTrace();
        }
    }

    private void saveMemberToDatabase(Member member) throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO members (id, name, email, phone, membership_date) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, member.getId());
            statement.setString(2, member.getName());
            statement.setString(3, member.getEmail());
            statement.setString(4, member.getPhone());
            statement.setDate(5, java.sql.Date.valueOf(member.getMembershipDate()));

            statement.executeUpdate();
        }
    }

    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearFields() {
        txtMembersID.clear();
        txtMemberName.clear();
        txtMemberEmail.clear();
        txtMemberPhone.clear();
        txtMembershipDate.clear();
    }

    @FXML
    void btnMemberEditOnAction(ActionEvent event) {
        String idText = txtMembersID.getText();
        String name = txtMemberName.getText();
        String email = txtMemberEmail.getText();
        String phone = txtMemberPhone.getText();
        String membershipDateText = txtMembershipDate.getText();

        int id;
        try {
            id = Integer.parseInt(idText);
        } catch (NumberFormatException e) {
            showAlert(AlertType.ERROR, "Invalid ID", "ID must be a number.");
            return;
        }

        // Validate Membership Date
        LocalDate membershipDate;
        try {
            membershipDate = LocalDate.parse(membershipDateText);
        } catch (DateTimeParseException e) {
            showAlert(AlertType.ERROR, "Invalid Date", "Date must be in the format YYYY-MM-DD.");
            return;
        }

        // Update Member object
        Member member = new Member(id, name, email, phone, membershipDate);

        // Update member in the database
        try {
            updateMemberInDatabase(member);
            showAlert(AlertType.INFORMATION, "Success", "Member updated successfully.");
            clearFields();
            loadMemberData();  // Reload data to update the table view
        } catch (SQLException | ClassNotFoundException e) {
            showAlert(AlertType.ERROR, "Database Error", "Failed to update member in the database.");
            e.printStackTrace();
        }
    }

    private void updateMemberInDatabase(Member member) throws SQLException, ClassNotFoundException {
        String sql = "UPDATE members SET name = ?, email = ?, phone = ?, membership_date = ? WHERE id = ?";
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, member.getName());
            statement.setString(2, member.getEmail());
            statement.setString(3, member.getPhone());
            statement.setDate(4, java.sql.Date.valueOf(member.getMembershipDate()));
            statement.setInt(5, member.getId());

            statement.executeUpdate();
        }
    }

    @FXML
    void btnMemberDeleteOnAction(ActionEvent event) {
        String idText = txtMembersID.getText();

        int id;
        try {
            id = Integer.parseInt(idText);
        } catch (NumberFormatException e) {
            showAlert(AlertType.ERROR, "Invalid ID", "ID must be a number.");
            return;
        }

        // Delete member from the database
        try {
            deleteMemberFromDatabase(id);
            showAlert(AlertType.INFORMATION, "Success", "Member deleted successfully.");
            clearFields();
            loadMemberData();  // Reload data to update the table view
        } catch (SQLException | ClassNotFoundException e) {
            showAlert(AlertType.ERROR, "Database Error", "Failed to delete member from the database.");
            e.printStackTrace();
        }
    }

    private void deleteMemberFromDatabase(int id) throws SQLException, ClassNotFoundException {
        String sql = "DELETE FROM members WHERE id = ?";
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);
            statement.executeUpdate();
        }
    }

    @FXML
    void btnGotoHomeOnAction(ActionEvent event) {
        try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Home.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) memberTable.getScene().getWindow(); //  current scene
        stage.setScene(new Scene(root));
        stage.setTitle("Dashbord");
        stage.show();
    } catch (IOException e) {
        e.printStackTrace();
        showAlert(Alert.AlertType.ERROR, "Navigation Error", "Failed to load the home screen.");
    }
    }

    @FXML
    void btnMemberViewOnAction(ActionEvent event) {
        try {
            loadMemberData();
        } catch (ClassNotFoundException e) {
            showAlert(AlertType.ERROR, "Load Data Error", "Failed to load member data.");
            e.printStackTrace();
        }
    }

    private void loadMemberData() throws ClassNotFoundException {
        ObservableList<Member> members = FXCollections.observableArrayList();

        String sql = "SELECT id, name, email, phone, membership_date FROM members";
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String email = resultSet.getString("email");
                String phone = resultSet.getString("phone");
                LocalDate membershipDate = resultSet.getDate("membership_date").toLocalDate();

                Member member = new Member(id, name, email, phone, membershipDate);
                members.add(member);
            }

            if (memberTable != null) {
                memberTable.setItems(members);
            } else {
                showAlert(AlertType.ERROR, "Table Error", "Table view is not initialized.");
            }

        } catch (SQLException e) {
            showAlert(AlertType.ERROR, "Database Error", "Failed to retrieve members from the database.");
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize TableColumns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        membershipDateColumn.setCellValueFactory(new PropertyValueFactory<>("membershipDate"));

        // Add listener for row selection
        memberTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                populateFields(newSelection);
            }
        });

        // Ensure memberTable is initialized
        if (memberTable == null) {
            System.out.println("memberTable is null during initialization!");
        } else {
            // Load member data into the table when UI is initialized
            try {
                loadMemberData();
            } catch (ClassNotFoundException e) {
                showAlert(AlertType.ERROR, "Initialization Error", "Failed to load member data.");
                e.printStackTrace();
            }
        }
    }

    private void populateFields(Member member) {
        txtMembersID.setText(String.valueOf(member.getId()));
        txtMemberName.setText(member.getName());
        txtMemberEmail.setText(member.getEmail());
        txtMemberPhone.setText(member.getPhone());
        txtMembershipDate.setText(member.getMembershipDate().toString());
    }
}