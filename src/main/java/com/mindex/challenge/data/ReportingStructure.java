package com.mindex.challenge.data;

//import java.util.List;

public class ReportingStructure {
    private int numberOfReports = 0;
    private Employee employee;
    //private List<String> subordinates;


    public void setNumberOfReports(int num) { this.numberOfReports = num; }
    
    public void setEmployee(Employee emp) { this.employee = emp; }
    
    /*public void addSubordinate(String sub) { 
        if(!subordinates.contains(sub))
            subordinates.add(sub);
    }*/

    public int getNumberOfReports() { return numberOfReports; }

    public Employee getEmployee() { return employee; }

    //public List<String> getSubordinates() { return subordinates; }
}
