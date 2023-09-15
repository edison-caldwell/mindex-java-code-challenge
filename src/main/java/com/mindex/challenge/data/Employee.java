package com.mindex.challenge.data;

import java.util.List;
import java.util.Objects;


public class Employee {
    private String employeeId;
    private String firstName;
    private String lastName;
    private String position;
    private String department;
    private List<Employee> directReports;

    public Employee() {
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public List<Employee> getDirectReports() {
        return directReports;
    }

    public void setDirectReports(List<Employee> directReports) {
        this.directReports = directReports;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true; // Same instance, so they are equal
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false; // Different classes or null, so they are not equal
        }

        Employee other = (Employee) obj; 
    
        return employeeId.equals(other.employeeId) &&
            firstName.equals(other.firstName) &&
            lastName.equals(other.lastName) &&
            position.equals(other.position) &&
            department.equals(other.department) &&
            Objects.equals(directReports, other.directReports);
    }
}
