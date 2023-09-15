package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.CompensationRepository;
import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import javax.validation.ValidationException;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger LOG = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private CompensationRepository compensationRepository;

    @Override
    public Employee create(Employee employee) {
        LOG.debug("Creating employee [{}]", employee);

        employee.setEmployeeId(UUID.randomUUID().toString());
        return employeeRepository.insert(employee);

    }

    @Override
    public Employee read(String id) {
        LOG.debug("Reading employee with id [{}]", id);

        Employee employee = employeeRepository.findByEmployeeId(id);

        if (employee == null) {
            throw new RuntimeException("Invalid employeeId: " + id);
        }

        return employee;
    }

    @Override
    public Employee update(Employee employee) {
        LOG.debug("Updating employee [{}]", employee);

        return employeeRepository.save(employee);
    }

    /**
     * @param employeeId
     */
    @Override
    public ReportingStructure getReportingEmployees(String employeeId) {
        LOG.debug("Creating list of employees that reports to [{}]", employeeId);
        ReportingStructure reportingStructure = new ReportingStructure();

        Employee employee = employeeRepository.findByEmployeeId(employeeId);

        if(employee == null) {
            throw new ValidationException("Employee not found for id: " + employeeId);
        }

        reportingStructure.setEmployee(employee);
        reportingStructure.setNumberOfReports(countReportingEmployees(employee));

        return reportingStructure;
    }

    @Override
    public Compensation create(Compensation compensation) {
        LOG.debug("Creating compensation [{}]", compensation);
        
        Employee employee = employeeRepository.findByEmployeeId(compensation.getEmployee().getEmployeeId());

        if(employee == null) {
            throw new ValidationException("Employee does not exist for employeeId: " + compensation.getEmployee().getEmployeeId());
        }

        return compensationRepository.insert(compensation);
    }

    @Override
    public Compensation getCompensationByEmployeeId(String employeeId) {
        LOG.debug("Reading compensation for employeeId [{}]", employeeId);

        Compensation compensation = compensationRepository.findByEmployeeEmployeeId(employeeId);

        if (compensation == null) {
        throw new ValidationException("Invalid employeeId: " + employeeId);
        }

        return compensation;
    }

    /**
     * @param employee
     */
    private int countReportingEmployees(Employee employee) {
        List<Employee> directReports = employee.getDirectReports();
        int numberOfReports = 0;
        
        if (directReports != null) {
            for (Employee directReport : directReports) {
                numberOfReports += 1 + countReportingEmployees(directReport);
            }
        }
        
        return numberOfReports;
    }
    
}
