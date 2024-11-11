/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package horsmanagementclient;

import entity.Employee;
import entity.Partner;
import enumeration.RoleType;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import session.stateless.EmployeeSessionBeanRemote;
import session.stateless.PartnerSessionBeanRemote;
import util.exception.EmployeeExistException;
import util.exception.InputDataValidationException;
import util.exception.InvalidAccessRightException;
import util.exception.PartnerExistException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author priskilarebecca.p
 */
public class SystemAdministrationModule {
    private final ValidatorFactory validatorFactory;
    private final Validator validator;
    
    private EmployeeSessionBeanRemote employeeSessionBeanRemote;
    private PartnerSessionBeanRemote partnerSessionBeanRemote;
    
    private Employee currentEmployee;

    
    
    public SystemAdministrationModule()
    {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }
    
    
    
    public void menuSystemAdministration() throws InvalidAccessRightException
    {
        if(currentEmployee.getRoleType() != RoleType.SYSTEM_ADMINISTRATOR)
        {
            throw new InvalidAccessRightException("You don't have SYSTEM_ADMINISTRATOR rights to access the system administration module.");
        }
        
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while(true)
        {
            System.out.println("*** POS System :: System Administration ***\n");
            System.out.println("1: Create New Employee");
            System.out.println("2: View All Employees");
            System.out.println("-----------------------");
            System.out.println("3: Create New Partner");
            System.out.println("4: View All Partners");
            System.out.println("-----------------------");
            System.out.println("5: Back\n");
            response = 0;
            
            while(response < 1 || response > 5)
            {
                System.out.print("> ");

                response = scanner.nextInt();

                if(response == 1)
                {
                    doCreateNewEmployee();
                }
                else if(response == 2)
                {
                    doViewAllEmployees();
                }
                else if(response == 3)
                {
                    doCreateNewPartner();
                }
                else if(response == 4)
                {
                    doViewAllPartners();
                }
                else if(response == 5)
                {
                    break;
                }
                else
                {
                    System.out.println("Invalid option, please try again!\n");                
                }
            }
            
            if(response == 5)
            {
                break;
            }
        }
    }
    
    private void doCreateNewEmployee()
    {
        Scanner scanner = new Scanner(System.in);
        Employee newEmployee = new Employee();
        
        System.out.println("*** POS System :: System Administration :: Create New Employee ***\n");
        System.out.print("Enter Username> ");
        newEmployee.setUsername(scanner.nextLine().trim());
        
        while(true)
        {
            System.out.print("Select Access Right (1: System Administrator, 2: Operation Manager, 3: Sales Manager, 4: Guest Relation Officer)> ");
            Integer accessRightInt = scanner.nextInt();
            
            if(accessRightInt >= 1 && accessRightInt <= 4)
            {
                newEmployee.setRoleType(RoleType.values()[accessRightInt-1]);
                break;
            }
            else
            {
                System.out.println("Invalid option, please try again!\n");
            }
        }
        
        scanner.nextLine();
        System.out.print("Enter Password> ");
        newEmployee.setPassword(scanner.nextLine().trim());
        
        Set<ConstraintViolation<Employee>>constraintViolations = validator.validate(newEmployee);
        
        if(constraintViolations.isEmpty())
        {
            try
            {
                newEmployee = employeeSessionBeanRemote.createNewEmployee(newEmployee);
                Long newEmployeeId = newEmployee.getEmployeeId();
                System.out.println("New staff created successfully!: " + newEmployeeId + "\n");
            }
            catch(EmployeeExistException ex)
            {
                System.out.println("An error has occurred while creating the new staff!: The username already exist\n");
            }
            catch(UnknownPersistenceException ex)
            {
                System.out.println("An unknown error has occurred while creating the new staff!: " + ex.getMessage() + "\n");
            }
            catch(InputDataValidationException ex)
            {
                System.out.println(ex.getMessage() + "\n");
            }
        }
        else
        {
            showInputDataValidationErrorsForEmployee(constraintViolations);
        }
    }
    
    private void doViewAllEmployees()
    {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("*** POS System :: System Administration :: View All Employees ***\n");
        
        List<Employee> employeeList = employeeSessionBeanRemote.viewAllEmployees();
        System.out.printf("%8s%20s%20s%15s%20s%20s\n", "Employee ID", "Userame", "Access Right", "Password");

        for(Employee employee:employeeList)
        {
            System.out.printf("%8s%20s%20s%15s%20s%20s\n", employee.getEmployeeId().toString(), employee.getUsername(), employee.getRoleType().toString(), employee.getPassword());
        }
        
        System.out.print("Press any key to continue...> ");
        scanner.nextLine();
    }
    
    private void showInputDataValidationErrorsForEmployee(Set<ConstraintViolation<Employee>>constraintViolations)
    {
        System.out.println("\nInput data validation error!:");
            
        for(ConstraintViolation constraintViolation:constraintViolations)
        {
            System.out.println("\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage());
        }

        System.out.println("\nPlease try again......\n");
    }
    
    private void doCreateNewPartner() 
    {
        Scanner scanner = new Scanner(System.in);
        Partner newPartner = new Partner();
        
        System.out.println("*** POS System :: System Administration :: Create New Partner ***\n");
        System.out.print("Enter Username> ");
        newPartner.setUsername(scanner.nextLine().trim());
        System.out.print("Enter Password> ");
        newPartner.setPassword(scanner.nextLine().trim());
        
        Set<ConstraintViolation<Partner>>constraintViolations = validator.validate(newPartner);
        
        if(constraintViolations.isEmpty())
        {
            try
            {
                newPartner = partnerSessionBeanRemote.createNewPartner(newPartner);
                Long newPartnerId = newPartner.getPartnerId();
                System.out.println("New partner created successfully!: " + newPartnerId + "\n");
            }
            catch(PartnerExistException ex)
            {
                System.out.println("An error has occurred while creating the new partner!: The username already exist\n");
            }
            catch(UnknownPersistenceException ex)
            {
                System.out.println("An unknown error has occurred while creating the new staff!: " + ex.getMessage() + "\n");
            }
            catch(InputDataValidationException ex)
            {
                System.out.println(ex.getMessage() + "\n");
            }
        }
        else
        {
            showInputDataValidationErrorsForPartner(constraintViolations);
        }
    }
    
    private void showInputDataValidationErrorsForPartner(Set<ConstraintViolation<Partner>>constraintViolations)
    {
        System.out.println("\nInput data validation error!:");
            
        for(ConstraintViolation constraintViolation:constraintViolations)
        {
            System.out.println("\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage());
        }

        System.out.println("\nPlease try again......\n");
    }
    
    private void doViewAllPartners()
    {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("*** POS System :: System Administration :: View All Partners ***\n");
        
        List<Partner> partnerList = partnerSessionBeanRemote.viewAllPartners();
        System.out.printf("%8s%20s%20s%15s%20s%20s\n", "Partner ID", "Userame", "Password");

        for(Partner partner:partnerList)
        {
            System.out.printf("%8s%20s%20s%15s%20s%20s\n", partner.getPartnerId().toString(), partner.getUsername(), partner.getPassword());
        }
        
        System.out.print("Press any key to continue...> ");
        scanner.nextLine();
    }
    
    
}
