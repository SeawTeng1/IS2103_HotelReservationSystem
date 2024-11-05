/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionRemote.java to edit this template
 */
package session.stateless;

import entity.Employee;
import java.util.List;
import javax.ejb.Remote;
import util.exception.EmployeeExistException;
import util.exception.EmployeeInvalidPasswordException;
import util.exception.EmployeeNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author priskilarebecca.p
 */
@Remote
public interface EmployeeSessionBeanRemote {
    public Employee employeeLogin(String username, String password) throws EmployeeNotFoundException, EmployeeInvalidPasswordException;

    public Employee createNewEmployee(Employee newEmployee) throws EmployeeExistException, UnknownPersistenceException;

    public List<Employee> viewAllEmployees();

    public Employee retrieveEmployeebyUsername(String username) throws EmployeeNotFoundException; 
}
