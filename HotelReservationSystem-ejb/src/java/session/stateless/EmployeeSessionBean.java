/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package session.stateless;

import entity.Employee;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import util.exception.EmployeeExistException;
import util.exception.EmployeeInvalidPasswordException;
import util.exception.EmployeeNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author priskilarebecca.p
 */
@Stateless
public class EmployeeSessionBean implements EmployeeSessionBeanRemote, EmployeeSessionBeanLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    public void persist(Object object) {
        em.persist(object);
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    
    //1. Employee Login
    @Override
    public Employee employeeLogin(String username, String password) throws EmployeeNotFoundException, EmployeeInvalidPasswordException {
        Employee employee = retrieveEmployeebyUsername(username);
        if(employee.getPassword().equals(password)){
            return employee;
        } else {
            throw new EmployeeInvalidPasswordException("Invalid Password please try again!");
        }    
    }
    
    //2. Employee Logout - handle in main
    
    //3. Create New Employee
    @Override
    public Employee createNewEmployee(Employee newEmployee) throws EmployeeExistException, UnknownPersistenceException {
        try { 
            em.persist(newEmployee);
            em.flush();
            
        } catch(PersistenceException ex) {
            if(ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                if(ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                    throw new EmployeeExistException("Employee Already Exists!");
                } else {
                    throw new UnknownPersistenceException(ex.getMessage());
                }
            } else {
                throw new UnknownPersistenceException(ex.getMessage());
            }
        }
        return newEmployee;
    }
    
    //4. View All Employees
    @Override
    public List<Employee> viewAllEmployees() {
        Query query = em.createQuery("SELECT e FROM Employee e");
        return query.getResultList();
    }
    
    //Other methods
    @Override
    public Employee retrieveEmployeebyUsername(String username) throws EmployeeNotFoundException {
        try{
            Query query = em.createQuery("SELECT e FROM Employee e WHERE e.username = :inUsername ");
            query.setParameter("inUsername", username);
            return (Employee)query.getSingleResult();
        } catch (NoResultException ex) {
            throw new EmployeeNotFoundException("Employee does not exist: " + username);
        }
    }
  
}
