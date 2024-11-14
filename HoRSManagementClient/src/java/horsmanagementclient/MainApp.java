/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package horsmanagementclient;

import entity.Employee;
import enumeration.RoleType;
import java.util.Scanner;
import session.stateful.WalkInRoomReservationRemote;
import session.stateless.EmployeeSessionBeanRemote;
import session.stateless.GuestSessionBeanRemote;
import session.stateless.PartnerSessionBeanRemote;
import session.stateless.RoomAllocationExceptionReportSessionBeanRemote;
import session.stateless.RoomRateSessionBeanRemote;
import session.stateless.RoomSessionBeanRemote;
import session.stateless.RoomTypeSessionBeanRemote;
import util.exception.EmployeeInvalidPasswordException;
import util.exception.EmployeeNotFoundException;
import util.exception.InvalidAccessRightException;
import util.exception.InvalidLoginCredentialException;

/**
 *
 * @author priskilarebecca.p
 */
public class MainApp {
    private EmployeeSessionBeanRemote employeeSessionBeanRemote;
    private PartnerSessionBeanRemote partnerSessionBeanRemote;
    private RoomTypeSessionBeanRemote roomTypeSessionBeanRemote;
    private RoomRateSessionBeanRemote roomRateSessionBeanRemote;
    private RoomSessionBeanRemote roomSessionBeanRemote;
    private RoomAllocationExceptionReportSessionBeanRemote roomAllocationExceptionReportSessionBeanRemote;
    private WalkInRoomReservationRemote walkInReservationRemote;
    private GuestSessionBeanRemote guestSessionBeanRemote;
    
    
    private SystemAdministrationModule systemAdministrationModule;
    
    private HotelOperationModule hotelOperationModule;
   
    private FrontOfficeModule frontOfficeModule;
    
    private Employee currentEmployee;

    public MainApp() {
    }

    public MainApp(EmployeeSessionBeanRemote employeeSessionBeanRemote, PartnerSessionBeanRemote partnerSessionBeanRemote, RoomTypeSessionBeanRemote roomTypeSessionBeanRemote, RoomRateSessionBeanRemote roomRateSessionBeanRemote, RoomSessionBeanRemote roomSessionBeanRemote, RoomAllocationExceptionReportSessionBeanRemote roomAllocationExceptionReportSessionBeanRemote, WalkInRoomReservationRemote walkInReservationRemote, GuestSessionBeanRemote guestSessionBeanRemote) {
        this.employeeSessionBeanRemote = employeeSessionBeanRemote;
        this.partnerSessionBeanRemote = partnerSessionBeanRemote;
        this.roomTypeSessionBeanRemote = roomTypeSessionBeanRemote;
        this.roomRateSessionBeanRemote = roomRateSessionBeanRemote;
        this.roomSessionBeanRemote = roomSessionBeanRemote;
        this.roomAllocationExceptionReportSessionBeanRemote = roomAllocationExceptionReportSessionBeanRemote;
        this.walkInReservationRemote = walkInReservationRemote;
        this.guestSessionBeanRemote = guestSessionBeanRemote;
    }
    
    public void runApp()
    {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while(true)
        {
            System.out.println("*** Welcome to Holiday Reservation (HoR) System :: Management System ***\n");
            System.out.println("1: Login");
            System.out.println("2: Exit\n");
            response = 0;
            
            while(response < 1 || response > 2)
            {
                System.out.print("> ");

                response = scanner.nextInt();

                if(response == 1)
                {
                    try
                    {
                        doLogin();
                        System.out.println("Login successful!\n");
                        
                        if(currentEmployee != null && currentEmployee.getRoleType()==RoleType.SYSTEM_ADMINISTRATOR){
                            systemAdministrationModule = new SystemAdministrationModule(employeeSessionBeanRemote, partnerSessionBeanRemote, currentEmployee);
                            systemAdministrationModule.menuSystemAdministration();                            
                        } else if (currentEmployee != null && (currentEmployee.getRoleType()==RoleType.OPERATION_MANAGER || currentEmployee.getRoleType()==RoleType.SALES_MANAGER)){
                            hotelOperationModule = new HotelOperationModule(employeeSessionBeanRemote, roomSessionBeanRemote, roomTypeSessionBeanRemote ,roomAllocationExceptionReportSessionBeanRemote, roomRateSessionBeanRemote, currentEmployee);
                            hotelOperationModule.menuHotelOperation();          
                        } else { //for GUEST_RELATION_OFFICER
                            frontOfficeModule = new FrontOfficeModule(employeeSessionBeanRemote, guestSessionBeanRemote, walkInReservationRemote, currentEmployee);
                            frontOfficeModule.menuFrontOffice();
                            
                        }
                    }
                    catch(InvalidLoginCredentialException ex) 
                    {
                        System.out.println("Invalid login credential: " + ex.getMessage() + "\n");
                    }
                    catch(InvalidAccessRightException ex) 
                    {
                        System.out.println("Invalid access right: " + ex.getMessage() + "\n");
                    }
                }
                else if (response == 2)
                {
                    break;
                }
                else
                {
                    System.out.println("Invalid option, please try again!\n");                
                }
            }
            
            if(response == 2)
            {
                break;
            }
        }
    }
    
    private void doLogin() throws InvalidLoginCredentialException
    {
        Scanner scanner = new Scanner(System.in);
        String username = "";
        String password = "";
        
        System.out.println("*** HoR Management System :: Login ***\n");
        System.out.print("Enter username> ");
        username = scanner.nextLine().trim();
        System.out.print("Enter password> ");
        password = scanner.nextLine().trim();
        
        if(username.length() > 0 && password.length() > 0)
        {
            try {
                currentEmployee = employeeSessionBeanRemote.employeeLogin(username, password);
            } catch (EmployeeNotFoundException ex) {
                System.out.println("An error has occurred while doing login: " + ex.getMessage() + "\n");
            } catch (EmployeeInvalidPasswordException ex) {
                System.out.println("An error has occurred while doing login: " + ex.getMessage() + "\n");
            }
        }
        else
        {
            throw new InvalidLoginCredentialException("Missing login credential!");
        }
    }
    
    
}
