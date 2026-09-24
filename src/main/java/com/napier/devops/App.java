package com.napier.devops;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class App {

    private Connection con = null;

    /**
     * Connect to the MySQL database with retry mechanism.
     */
    public void connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Could not load SQL driver");
            System.exit(-1);
        }

        String dbLocation = System.getenv("DB_LOCATION");
        if (dbLocation == null || dbLocation.isEmpty()) {
            dbLocation = "db:3306";
        }

        String url = "jdbc:mysql://" + dbLocation + "/employees?useSSL=false&allowPublicKeyRetrieval=true";
        String user = "root";
        String password = "example";

        int retries = 30;
        for (int i = 0; i < retries; ++i) {
            System.out.println("Connecting to database...");
            try {
                Thread.sleep(5000);
                con = DriverManager.getConnection(url, user, password);
                System.out.println("Successfully connected");
                break;
            } catch (SQLException sqle) {
                System.out.println("Failed to connect to database attempt " + i);
                System.out.println(sqle.getMessage());
            } catch (InterruptedException ie) {
                System.out.println("Thread interrupted? Should not happen.");
            }
        }
    }

    /**
     * Disconnect from the MySQL database.
     */
    public void disconnect() {
        if (con != null) {
            try {
                con.close();
                System.out.println("Disconnected from database");
            } catch (Exception e) {
                System.out.println("Error closing connection to database");
            }
        }
    }

    /**
     * Retrieve complete details for a given employee ID.
     */
    public Employee getEmployee(int ID) {
        try {
            Statement stmt = con.createStatement();

            // SQL query joining all employee-related tables for current records (to_date = '9999-01-01')
            String strSelect =
                    "SELECT emp.emp_no, emp.first_name, emp.last_name, "
                            + "titles.title, salaries.salary, departments.dept_name, "
                            + "CONCAT(mgr.first_name, ' ', mgr.last_name) AS manager "
                            + "FROM employees emp "
                            + "LEFT JOIN titles ON emp.emp_no = titles.emp_no AND titles.to_date = '9999-01-01' "
                            + "LEFT JOIN salaries ON emp.emp_no = salaries.emp_no AND salaries.to_date = '9999-01-01' "
                            + "LEFT JOIN dept_emp ON emp.emp_no = dept_emp.emp_no AND dept_emp.to_date = '9999-01-01' "
                            + "LEFT JOIN departments ON dept_emp.dept_no = departments.dept_no "
                            + "LEFT JOIN dept_manager ON departments.dept_no = dept_manager.dept_no AND dept_manager.to_date = '9999-01-01' "
                            + "LEFT JOIN employees mgr ON dept_manager.emp_no = mgr.emp_no "
                            + "WHERE emp.emp_no = " + ID;

            ResultSet rset = stmt.executeQuery(strSelect);

            if (rset.next()) {
                Employee emp = new Employee();
                emp.setEmp_no(rset.getInt("emp_no"));
                emp.setFirst_name(rset.getString("first_name"));
                emp.setLast_name(rset.getString("last_name"));
                emp.setTitle(rset.getString("title"));
                emp.setSalary(rset.getInt("salary"));
                emp.setDept_name(rset.getString("dept_name"));
                emp.setManager(rset.getString("manager"));
                return emp;
            } else {
                return null;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to get employee details");
            return null;
        }
    }

    /**
     * Display Employee Information
     */
    public void displayEmployee(Employee emp) {
        if (emp != null) {
            System.out.println(
                    emp.getEmp_no() + " "
                            + emp.getFirst_name() + " "
                            + emp.getLast_name() + "\n"
                            + emp.getTitle() + "\n"
                            + "Salary:" + emp.getSalary() + "\n"
                            + emp.getDept_name() + "\n"
                            + "Manager: " + emp.getManager() + "\n");
        }
    }

    public static void main(String[] args) {
        // Create new Application
        App a = new App();

        // Connect to database
        a.connect();

        // Get Employee
        Employee emp = a.getEmployee(255530);

        // Display results
        a.displayEmployee(emp);

        // Disconnect from database
        a.disconnect();
    }
}