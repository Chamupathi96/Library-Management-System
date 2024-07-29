package dao;

import model.Borrowing;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BorrowingDAO {

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/librarymanagementsystem", "root", "Janith@123");
    }

    public void save(Borrowing borrowing) throws SQLException {
        String query = "INSERT INTO Borrowings (book_id, member_id, borrow_date, due_date, return_date, fine) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, borrowing.getBookId());
            statement.setInt(2, borrowing.getMemberId());
            statement.setDate(3, borrowing.getBorrowDate());
            statement.setDate(4, borrowing.getDueDate());
            statement.setDate(5, borrowing.getReturnDate());
            statement.setDouble(6, borrowing.getFine());
            statement.executeUpdate();
        }
    }

    public void updateReturnDate(int memberId, int bookId, Date returnDate, double fine) throws SQLException {
        String query = "UPDATE Borrowings SET return_date = ?, fine = ? WHERE member_id = ? AND book_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setDate(1, returnDate);
            statement.setDouble(2, fine);
            statement.setInt(3, memberId);
            statement.setInt(4, bookId);
            statement.executeUpdate();
        }
    }

    public List<Borrowing> getAllBorrowings() throws SQLException {
        List<Borrowing> borrowings = new ArrayList<>();
        String query = "SELECT * FROM Borrowings";
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                Borrowing borrowing = new Borrowing(
                        resultSet.getInt("id"),
                        resultSet.getInt("book_id"),
                        resultSet.getInt("member_id"),
                        resultSet.getDate("borrow_date"),
                        resultSet.getDate("due_date"),
                        resultSet.getDate("return_date"),
                        resultSet.getDouble("fine")
                );
                borrowings.add(borrowing);
            }
        }
        return borrowings;
    }

    public List<Borrowing> getBorrowingsByMemberId(int memberId) throws SQLException {
        List<Borrowing> borrowings = new ArrayList<>();
        String query = "SELECT * FROM Borrowings WHERE member_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, memberId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Borrowing borrowing = new Borrowing(
                            resultSet.getInt("id"),
                            resultSet.getInt("book_id"),
                            resultSet.getInt("member_id"),
                            resultSet.getDate("borrow_date"),
                            resultSet.getDate("due_date"),
                            resultSet.getDate("return_date"),
                            resultSet.getDouble("fine")
                    );
                    borrowings.add(borrowing);
                }
            }
        }
        return borrowings;
    }
}