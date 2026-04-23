import java.sql.*;

public class Bank_Transactions {
    final static private String URL = "jdbc:mysql://localhost:3306/customer";
    final static private String user = "root";
    final static private String password = "9999";
    public static void main(String[] args) {
try(Connection conn = DriverManager.getConnection(URL, user, password)) {
    System.out.println("Database connected");
    createTable(conn);
//   int fromId = insertCustomer(conn, "Nisha", 38000.0);
//   int toId =  insertCustomer(conn, "Suga", 900000000.0);
    int fromId = insertCustomer(conn, "JK", 38000.00);
    int toId = insertCustomer(conn, "RM", 50000.0);
    transferMoney(conn,fromId, toId, 40000);
} catch (SQLException e) {
    e.printStackTrace();
}
    }
    private static void createTable(Connection conn) {
        String sql = "Create table if not exists accounts(" +
                "id int auto_increment primary key, " +
                "name varchar(50), " +
                "balance double)";
        try(PreparedStatement pstm = conn.prepareStatement(sql)) {
           int rows = pstm.executeUpdate();
            System.out.println("Created : "+rows);

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    private static int insertCustomer(Connection conn, String name, double balance) {
        String sql = "Insert into accounts(name, balance) values(?, ?)";
        try (PreparedStatement pstm = conn.prepareStatement(sql,PreparedStatement.RETURN_GENERATED_KEYS)) {
            pstm.setString(1, name);
            pstm.setDouble(2, balance);
            int rows = pstm.executeUpdate();
            System.out.println("Inserted : " + rows);
            try (ResultSet rs = pstm.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    System.out.println("Order Id : " + id);
                    return id;
                } else {
                    throw new SQLException("No customer id found");
                }
            } catch (SQLException e) {
                throw new RuntimeException();
            }
        }
          catch (SQLException e){
                throw new RuntimeException(e);
            }
    }
    private static void transferMoney(Connection conn, int fromId, int toId, double amount) {
/*
*  Logic (important)
Start transaction (setAutoCommit(false))
Check balance of sender
Deduct amount from sender
Add amount to receiver
Commit (or rollback if error)*/
        try {
            conn.setAutoCommit(false);
            // Check balance of sender
            String checksql = "Select balance from accounts where id = ?";
            PreparedStatement checkStm = conn.prepareStatement(checksql);
            checkStm.setInt(1, fromId);
            ResultSet rs = checkStm.executeQuery();
            if(rs.next()) {
                double balance = rs.getDouble("balance");
                if (balance < amount) {
                    throw new Exception("Insufficient balance");
                }
                // Deduct amount from sender
                String debt = "Update accounts set balance = balance - ? where id = ?";
                PreparedStatement debtStm = conn.prepareStatement(debt);
                debtStm.setDouble(1, amount);
                debtStm.setInt(2, fromId);
                debtStm.executeUpdate();

                // Add amount to receiver
                String credt = "Update accounts set balance = balance + ? where id = ?";
                PreparedStatement crdStm = conn.prepareStatement(credt);
                crdStm.setDouble(1, amount);
                crdStm.setInt(2, toId);
                crdStm.executeUpdate();

//            Commit (or rollback if error)
                conn.commit();
                System.out.println("Transaction successful");
            }
            else {
                throw new Exception("User not found");
            }

        }
        catch (Exception e) {
            try {
                conn.rollback();
                System.out.println("Transaction Failed : "+e.getMessage());
            } catch (SQLException ex) {
                e.printStackTrace();
            }
        }
        finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        }
    }
