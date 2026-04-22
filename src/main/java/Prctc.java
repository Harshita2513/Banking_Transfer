import java.sql.*;

public class Prctc {
    static private String URL = "jdbc:mysql://localhost:3306/customer";
    static private String user = "root";
    static private String password = "9999";
    public static void main(String[] args) {

//        try(Connection conn = DriverManager.getConnection(URL, user, password)) {
//            System.out.println("Database connected");
//            conn.setAutoCommit(false);
////            createTable(conn);
////            insertCustomer(conn, "Nisha", 38000.0);
////            insertCustomer(conn, "Suga", 900000000.0);
//
//            conn.commit();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
        try (Connection conn = DriverManager.getConnection(URL, user, password)) {
            conn.setAutoCommit(false);

            // 1. Check balance
            String checkSql = "SELECT balance FROM accounts WHERE id = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setInt(1, fromId);

            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                double balance = rs.getDouble("balance");

                if (balance < amount) {
                    throw new Exception("Insufficient Balance");
                }

                // 2. Deduct from sender
                String debitSql = "UPDATE accounts SET balance = balance - ? WHERE id = ?";
                PreparedStatement debitStmt = conn.prepareStatement(debitSql);
                debitStmt.setDouble(1, amount);
                debitStmt.setInt(2, fromId);
                debitStmt.executeUpdate();

                // 3. Add to receiver
                String creditSql = "UPDATE accounts SET balance = balance + ? WHERE id = ?";
                PreparedStatement creditStmt = conn.prepareStatement(creditSql);
                creditStmt.setDouble(1, amount);
                creditStmt.setInt(2, toId);
                creditStmt.executeUpdate();

                // Commit
                conn.commit();
                System.out.println("Transaction Successful");

            } else {
                throw new Exception("Sender not found");
            }

        } catch (Exception e) {
            try {
                conn.rollback();
                System.out.println("Transaction Failed: " + e.getMessage());
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    }
}
