// === THEME PARK + RESORT SYSTEM - CORRECT VERSION ===
package db;
import java.sql.*;
public class DatabaseConnection {
    private static final String DB_URL = "jdbc:sqlite:themepark.db";
    private static DatabaseConnection instance;
    private Connection connection;
    private DatabaseConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
            this.connection = DriverManager.getConnection(DB_URL);
            this.connection.setAutoCommit(true);
            System.out.println("[DB] ✅ SQLite connected → themepark.db");
            createAllTables();
        } catch (ClassNotFoundException e) { throw new RuntimeException("[DB] sqlite-jdbc driver not found.", e);
        } catch (SQLException e) { throw new RuntimeException("[DB] Connection failed: " + e.getMessage(), e); }
    }
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) instance = new DatabaseConnection(); return instance;
    }
    public Connection getConnection() { return connection; }
    private void createAllTables() throws SQLException {
        Statement s = connection.createStatement();
        s.execute("CREATE TABLE IF NOT EXISTS users (userID TEXT PRIMARY KEY, email TEXT UNIQUE NOT NULL, sessionToken TEXT, userType TEXT NOT NULL)");
        s.execute("CREATE TABLE IF NOT EXISTS tickets (ticketID TEXT PRIMARY KEY, type TEXT NOT NULL, price REAL NOT NULL, availability INTEGER NOT NULL, qrCode TEXT)");
        s.execute("CREATE TABLE IF NOT EXISTS accommodations (accommodationID TEXT PRIMARY KEY, roomType TEXT NOT NULL, checkInDate TEXT, checkOutDate TEXT, available INTEGER NOT NULL DEFAULT 1)");
        s.execute("CREATE TABLE IF NOT EXISTS bookings (bookingID TEXT PRIMARY KEY, bookingReference TEXT UNIQUE NOT NULL, userID TEXT NOT NULL, checkInDate TEXT NOT NULL, checkOutDate TEXT NOT NULL, status TEXT NOT NULL DEFAULT 'PENDING', accommodationID TEXT)");
        s.execute("CREATE TABLE IF NOT EXISTS booking_tickets (bookingID TEXT NOT NULL, ticketID TEXT NOT NULL, PRIMARY KEY (bookingID, ticketID))");
        s.execute("CREATE TABLE IF NOT EXISTS memberships (membershipID TEXT PRIMARY KEY, userID TEXT NOT NULL, type TEXT NOT NULL, active INTEGER NOT NULL DEFAULT 0, perks TEXT)");
        s.execute("CREATE TABLE IF NOT EXISTS feedbacks (feedbackID TEXT PRIMARY KEY, userID TEXT NOT NULL, rating INTEGER NOT NULL, comment TEXT NOT NULL)");
        s.execute("CREATE TABLE IF NOT EXISTS reports (reportID TEXT PRIMARY KEY, type TEXT NOT NULL, content TEXT, generatedAt TEXT)");
        s.execute("CREATE TABLE IF NOT EXISTS checkinout (reference TEXT PRIMARY KEY, status TEXT NOT NULL DEFAULT 'PENDING', digitalKey TEXT)");
        s.close();
        System.out.println("[DB] ✅ All 9 tables verified.");
    }
    public void close() {
        try { if (connection != null && !connection.isClosed()) { connection.close(); System.out.println("[DB] Connection closed."); }
        } catch (SQLException e) { System.err.println("[DB] Close error: " + e.getMessage()); }
    }
}
