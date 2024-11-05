package com.example;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

// @WebServlet("/hello")
public class HelloServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        // Get the hostname and IP address
        String hostname;
        String ipAddress;
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            hostname = inetAddress.getHostName();
            ipAddress = inetAddress.getHostAddress();
        } catch (UnknownHostException e) {
            hostname = "Unknown Host";
            ipAddress = "Unknown IP Address";
        }

        out.println("<html><body>");
        out.println("<h1>Hello from " + hostname + " with IP Address: " + ipAddress + "</h1>");

        // Read database connection details from environment variables
        String dbHost = System.getenv("PGHOST");
        String dbName = System.getenv("PGDATABASE");
        String dbPort = System.getenv("PGPORT");
        String dbUser = System.getenv("PGUSER");
        String dbPassword = System.getenv("PGPASSWORD");

        if (dbHost == null || dbName == null || dbPort == null || dbUser == null || dbPassword == null) {
            out.println("<p>Database connection details are not set in environment variables.</p>");
            return; // Exit if any variable is missing
        }

        String dbUrl = "jdbc:postgresql://" + dbHost + ":" + dbPort + "/" + dbName;

        try {
            // Load PostgreSQL JDBC Driver
            Class.forName("org.postgresql.Driver");

            // Establish connection
            try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
                 Statement statement = connection.createStatement()) {

                // Check if the employee table exists and create it if not
                String createTableQuery = "CREATE TABLE IF NOT EXISTS employee (" +
                                          "employee_id SERIAL PRIMARY KEY, " +
                                          "name VARCHAR(255) NOT NULL, " +
                                          "date_of_joining DATE DEFAULT CURRENT_DATE, " +
                                          "designation VARCHAR(100) NOT NULL)";
                statement.executeUpdate(createTableQuery);

                // Query the employee table
                String query = "SELECT employee_id, name, date_of_joining, designation FROM employee";
                try (ResultSet resultSet = statement.executeQuery(query)) {
                    out.println("<h2>Employee Details:</h2>");
                    out.println("<table border='1'>");
                    out.println("<tr><th>ID</th><th>Name</th><th>Date of Joining</th><th>Designation</th></tr>");

                    // Print each employee record
                    while (resultSet.next()) {
                        int employeeId = resultSet.getInt("employee_id");
                        String empName = resultSet.getString("name");
                        String dateOfJoining = resultSet.getDate("date_of_joining").toString();
                        String empDesignation = resultSet.getString("designation");

                        out.println("<tr><td>" + employeeId + "</td><td>" + empName + "</td><td>" +
                                dateOfJoining + "</td><td>" + empDesignation + "</td></tr>");
                    }
                    out.println("</table>");
                }

            } catch (SQLException e) {
                out.println("<p>Error connecting to the database: " + e.getMessage() + "</p>");
            }

        } catch (ClassNotFoundException e) {
            out.println("<p>PostgreSQL Driver not found: " + e.getMessage() + "</p>");
        }

        // HTML form to insert a new employee
        out.println("<h2>Add New Employee:</h2>");
        out.println("<form method='post' action='hello'>");
        out.println("Name: <input type='text' name='name' required><br>");
        out.println("Designation: <input type='text' name='designation' required><br>");
        out.println("<input type='submit' value='Add Employee'>");
        out.println("</form>");

        out.println("</body></html>");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        // Handle inserting new employee record
        String name = request.getParameter("name");
        String designation = request.getParameter("designation");

        // Read database connection details from environment variables
        String dbHost = System.getenv("PGHOST");
        String dbName = System.getenv("PGDATABASE");
        String dbPort = System.getenv("PGPORT");
        String dbUser = System.getenv("PGUSER");
        String dbPassword = System.getenv("PGPASSWORD");

        String dbUrl = "jdbc:postgresql://" + dbHost + ":" + dbPort + "/" + dbName;

        try {
            // Load PostgreSQL JDBC Driver
            Class.forName("org.postgresql.Driver");

            // Establish connection
            try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
                 Statement statement = connection.createStatement()) {

                if (name != null && designation != null) {
                    String insertQuery = "INSERT INTO employee (name, designation) VALUES ('" + name + "', '" + designation + "')";
                    statement.executeUpdate(insertQuery);
                    out.println("<p>Inserted new employee: " + name + " (" + designation + ")</p>");
                }

            } catch (SQLException e) {
                out.println("<p>Error connecting to the database: " + e.getMessage() + "</p>");
            }

        } catch (ClassNotFoundException e) {
            out.println("<p>PostgreSQL Driver not found: " + e.getMessage() + "</p>");
        }

        // Redirect back to doGet to show the updated employee list
        response.sendRedirect("hello");
    }
}
