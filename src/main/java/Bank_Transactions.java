import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Bank_Transactions {
    static private String URL = "jdbc:mysql://localhost:3306/customer";
    static private String user = "root";
    static private String password = "9999";
    public static void main(String[] args) {
try(Connection conn = DriverManager.getConnection(URL, user, password)) {
    System.out.println("Database connected");
    conn.setAutoCommit(false);
    createTable(conn);
    insertCustomer(conn, "Nisha", 38000.0);
    insertCustomer(conn, "Suga", 900000000.0);

    conn.commit();
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
        } ;

    }
    private static void insertCustomer(Connection conn, String name, double balance) {
        String sql = "Insert into accounts(name, balance) values(?, ?)";
        try (PreparedStatement pstm = conn.prepareStatement(sql)){
            pstm.setString(1, name);
            pstm.setDouble(2, balance);
            int rows = pstm.executeUpdate();
            System.out.println("Inserted : "+rows);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
          /*
        *
Tasks:
✅ Create accounts table (id, name, balance)
✅ Implement transferMoney(fromId, toId, amount) method
✅ Use PreparedStatement for all queries
✅ Transaction management:
   - Deduct from sender
   - Add to receiver
   - commit() if both succeed
   - rollback() if any fails
✅ Test with insufficient balance scenario
        *  */
    }
