package com.fuelflow.ui.panels;
public class EmployeePanel extends CrudPanel {
    public EmployeePanel() {
        super("Employee Management","Employee","employee_id",
            new String[]{"employee_name","phone","role","salary","hire_date","email"},
            new String[]{"Employee name","Phone","Role","Salary","Hire date (YYYY-MM-DD)","Email"});
    }
}
