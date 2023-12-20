package Server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler {

    private static final String url = "jdbc:sqlserver://ADMIN-PC:1433;"
                    + "database=QLTK;"
                    + "user=sa;"
                    + "password=123456789;"
                    + "encrypt=false;"
                    + "trustServerCertificate=false;"
                    + "loginTimeout=30;";

     private static final String url1 = "jdbc:sqlserver://ADMIN-PC:1433;"
                    + "database=ROOM;"
                    + "user=sa;"
                    + "password=123456789;"
                    + "encrypt=false;"
                    + "trustServerCertificate=false;"
                    + "loginTimeout=30;";

    public boolean validateLogin(String username, String password) {
        Connection connection = null;
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            connection = DriverManager.getConnection(url);

            String query = "SELECT user_name, password FROM ACCOUNT WHERE user_name=? AND password=?";
            try (PreparedStatement st = connection.prepareStatement(query)) {
                st.setString(1, username);
                st.setString(2, password);
                try (ResultSet rs = st.executeQuery()) {
                    return rs.next();
                }
            }
        } catch (ClassNotFoundException | SQLException exception) {
            exception.printStackTrace();
            return false;
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public boolean isUserExists(String username) {
        Connection connection = null;
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            connection = DriverManager.getConnection(url);

            String query = "SELECT * FROM YOUR_USER_TABLE WHERE username = ?";
            try (PreparedStatement st = connection.prepareStatement(query)) {
                st.setString(1, username);
                try (ResultSet rs = st.executeQuery()) {
                    return rs.next();
                }
            }
        } catch (ClassNotFoundException | SQLException exception) {
            exception.printStackTrace();
            return false;
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public boolean saveRegistrationToDatabase(String fullName, String email, String phone, String username, String password) {
        try (Connection connection = DriverManager.getConnection(url)) {
            String query = "INSERT INTO ACCOUNT (full_name, email_id, mobile_number, user_name, password) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement st = connection.prepareStatement(query)) {
                st.setString(1, fullName);
                st.setString(2, email);
                st.setString(3, phone);
                st.setString(4, username);
                st.setString(5, password);

                int rowsAffected = st.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    public List<String> getAllUsernames() {
        Connection connection = null;
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            connection = DriverManager.getConnection(url);

            String query = "SELECT user_name FROM ACCOUNT";
            try (PreparedStatement st = connection.prepareStatement(query)) {
                try (ResultSet rs = st.executeQuery()) {
                    List<String> usernames = new ArrayList<>();
                    while (rs.next()) {
                        String username = rs.getString("user_name");
                        usernames.add(username);
                    }
                    return usernames;
                }
            }
        } catch (ClassNotFoundException | SQLException exception) {
            exception.printStackTrace();
            return new ArrayList<>();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public boolean saveRoomToDatabase(int id, String name, String type, String creator, String orderUser) {
        try (Connection connection = DriverManager.getConnection(url1)) {
            String query = "INSERT INTO Room (id, name, type, creator, otherUser) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement st = connection.prepareStatement(query)) {
                st.setInt(1, id);
                st.setString(2, name);
                st.setString(3, type);
                st.setString(4, creator);
                st.setString(5, orderUser);

                int rowsAffected = st.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
            return false;
        }
    }


}
