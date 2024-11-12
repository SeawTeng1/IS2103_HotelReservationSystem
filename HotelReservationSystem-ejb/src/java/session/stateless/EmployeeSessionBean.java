/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package session.stateless;

import entity.Employee;
import entity.Room;
import java.util.List;
import java.util.Set;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.exception.EmployeeExistException;
import util.exception.EmployeeInvalidPasswordException;
import util.exception.EmployeeNotFoundException;
import util.exception.InputDataValidationException;
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
    
    private final ValidatorFactory validatorFactory;
    private final Validator validator;
    
    
    
    public EmployeeSessionBean()
    {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
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
    public Employee createNewEmployee(Employee newEmployee) throws EmployeeExistException, UnknownPersistenceException, InputDataValidationException {
        Set<ConstraintViolation<Employee>>constraintViolations = validator.validate(newEmployee);
        
        if(constraintViolations.isEmpty())
        {
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
        } else {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
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
    
    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<Employee>>constraintViolations)
    {
        String msg = "Input data validation error!:";
            
        for(ConstraintViolation constraintViolation:constraintViolations)
        {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }
        
        return msg;
    }
  
}
