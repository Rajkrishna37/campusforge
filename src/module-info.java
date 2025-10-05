/**
 * 
 */
/**
 * 
 */
module LoginApp {
    requires java.sql;      // For JDBC (MySQL connection)
    requires java.desktop;  // For Swing GUI components

    opens com.loginapp to java.sql;  // Allow reflection if needed
}
