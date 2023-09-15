package com.mindex.challenge.data;

import java.util.Date;
import java.util.Objects;

public class Compensation {
    private Employee employee;
    private Integer salary;
    private Date effectiveDate;
    
    public void setEmployee(Employee e) { this.employee = e; }
    public void setSalary(Integer s) { this.salary = s; }
    public void setEffectiveDate(Date d) { this.effectiveDate = d; }

    public Employee getEmployee() { return employee; }
    public Integer getSalary() { return salary; }
    public Date getEffectiveDate() { return effectiveDate; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true; // Same instance, so they are equal
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false; // Different classes or null, so they are not equal
        }

        Compensation other = (Compensation) obj; 
    
        return salary.equals(other.salary) &&
            employee.equals(other.employee) &&
            Objects.equals(effectiveDate, other.effectiveDate);
    }
}
