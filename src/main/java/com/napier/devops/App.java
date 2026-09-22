package com.napier.devops;

public class App
{
    /**
     * Display an employee's details.
     * @param emp The Employee object to print.
     */
    public void displayEmployee(Employee emp)
    {
        if (emp != null)
        {
            System.out.println(
                    emp.emp_no + " "
                            + emp.first_name + " "
                            + emp.last_name + "\n"
            );
        }
        else
        {
            System.out.println("No employee found.");
        }
    }

    public static void main(String[] args)
    {
        // Create new Application and DatabaseHandler instances
        App a = new App();
        DatabaseHandler db = new DatabaseHandler();

        // Connect to database
        db.connect();

        // Extract employee information
        Employee emp = db.getEmployee(255530);

        // Display results
        a.displayEmployee(emp);

        // Disconnect from database
        db.disconnect();
    }
}