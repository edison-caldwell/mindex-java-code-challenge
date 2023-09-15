package com.mindex.challenge.service.impl;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;

import net.bytebuddy.utility.RandomString;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import javax.validation.ValidationException;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EmployeeServiceImplTest {

    private String employeeUrl;
    private String employeeIdUrl;
    private String reportUrl;
    private String compensationUrl;
    private String compensationIdUrl;
    private HashMap<String, Integer> employeeReportersHash = new HashMap<String, Integer>();
    private RandomString string = new RandomString();


    @Autowired
    private EmployeeService employeeService;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setup() {
        employeeUrl = "http://localhost:" + port + "/employee";
        employeeIdUrl = "http://localhost:" + port + "/employee/{id}";
        reportUrl = "http://localhost:" + port + "/employee/reporting/{id}";
        compensationUrl = "http://localhost:" + port + "/employee/compensation";
        compensationIdUrl = "http://localhost:" + port + "/employee/compensation/{id}";

    }

    @Test
    public void testCreateReadUpdate() {
        Employee testEmployee = new Employee();
        testEmployee.setFirstName("John");
        testEmployee.setLastName("Doe");
        testEmployee.setDepartment("Engineering");
        testEmployee.setPosition("Developer");

        // Create checks
        Employee createdEmployee = restTemplate.postForEntity(employeeUrl, testEmployee, Employee.class).getBody();

        assertNotNull(createdEmployee.getEmployeeId());
        assertEmployeeEquivalence(testEmployee, createdEmployee);


        // Read checks
        Employee readEmployee = restTemplate.getForEntity(employeeIdUrl, Employee.class, createdEmployee.getEmployeeId()).getBody();
        assertEquals(createdEmployee.getEmployeeId(), readEmployee.getEmployeeId());
        assertEmployeeEquivalence(createdEmployee, readEmployee);


        // Update checks
        readEmployee.setPosition("Development Manager");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Employee updatedEmployee =
                restTemplate.exchange(employeeIdUrl,
                        HttpMethod.PUT,
                        new HttpEntity<Employee>(readEmployee, headers),
                        Employee.class,
                        readEmployee.getEmployeeId()).getBody();

        assertEmployeeEquivalence(readEmployee, updatedEmployee);
    }

    @Test
    public void testGetReportingEmployees() {
        List<Employee> employeeList = new ArrayList<>();

        for(int i = 0; i < 5; i++) {
            Employee employee = new Employee();
            employeeList.add(restTemplate.postForEntity(employeeUrl, employee, Employee.class).getBody());
        }

        Employee manager = new Employee();
        manager.setDirectReports(employeeList);
        Employee createdManager = restTemplate.postForEntity(employeeUrl, manager, Employee.class).getBody();

        ReportingStructure reportResponse = restTemplate.getForEntity(reportUrl, ReportingStructure.class, createdManager.getEmployeeId()).getBody();
        assertEquals(5, reportResponse.getNumberOfReports());
    }

    @Test()
    public void testGetReportingEmployees_Invalid_EmployeeId() { 
        try {
            restTemplate.getForEntity(reportUrl, ReportingStructure.class, UUID.randomUUID()); 
        } catch(ValidationException ve){}; 
    }

    @Test()
    public void testGetReportingEmployees_Null_EmployeeId() { 
        try {
            restTemplate.getForEntity(reportUrl, ReportingStructure.class, (UUID) null); 
        } catch(ValidationException ve){}; 
    }

    @Test()
    public void testCreateCompensation() {

        Employee testEmployee = new Employee();
        testEmployee.setFirstName("Rohn");
        testEmployee.setLastName("John");
        testEmployee.setDepartment("Do-ers");
        testEmployee.setPosition("Stuff Accomplisher");
        Employee createdEmployee = restTemplate.postForEntity(employeeUrl, testEmployee, Employee.class).getBody();

        Compensation compensation = new Compensation();
        compensation.setEffectiveDate(new Date());
        compensation.setEmployee(createdEmployee);
        compensation.setSalary(1000000);
        
        Compensation createdCompensation = restTemplate.postForEntity(compensationUrl, compensation, Compensation.class).getBody();

        assertTrue(compensation.equals(createdCompensation));
    }

    @Test()
    public void testCreateCompensation_EmployeeDoesNotExist_ThrowsValidationException() {
        Compensation compensation = new Compensation();
        Employee emp = new Employee();
        emp.setEmployeeId(UUID.randomUUID().toString());
        compensation.setEmployee(emp);
        try {
            restTemplate.postForEntity(compensationUrl, compensation, Compensation.class);
        } catch (ValidationException ve){};
    }

    @Test()
    public void testCreateCompensation_EmployeeMissing_ThrowsValidationException() {
        Compensation compensation = new Compensation();
        compensation.setEmployee(null);
        try {
            restTemplate.postForEntity(compensationUrl, compensation, Compensation.class);
        } catch (ValidationException ve){};
    }

    @Test()
    public void testCreateCompensation_NullRequest_ThrowsValidationException() {
        try {
            restTemplate.postForEntity(compensationUrl, null, Compensation.class);
        } catch (ValidationException ve){};
    }

    @Test
    public void testGetCompensationByEmployeeId() {
        Employee testEmployee = new Employee();
        testEmployee.setFirstName("John");
        testEmployee.setLastName("Doe");
        testEmployee.setDepartment("Engineering");
        testEmployee.setPosition("Developer");
        Employee createdEmployee = restTemplate.postForEntity(employeeUrl, testEmployee, Employee.class).getBody();

        Compensation compensation = new Compensation();
        compensation.setEffectiveDate(new Date());
        compensation.setEmployee(createdEmployee);
        compensation.setSalary(1000000);
        
        Compensation createdCompensation = restTemplate.postForEntity(compensationUrl, compensation, Compensation.class).getBody();

        assertTrue(compensation.equals(createdCompensation));

        Compensation readCompensation = restTemplate.getForEntity(compensationIdUrl, Compensation.class, createdEmployee.getEmployeeId()).getBody();
        assertTrue(createdCompensation.equals(readCompensation));
    }


    /* Private/Helper Methods */

    private static void assertEmployeeEquivalence(Employee expected, Employee actual) {
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getLastName(), actual.getLastName());
        assertEquals(expected.getDepartment(), actual.getDepartment());
        assertEquals(expected.getPosition(), actual.getPosition());
    }

}
