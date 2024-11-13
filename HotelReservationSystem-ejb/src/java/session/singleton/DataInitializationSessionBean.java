/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB31/SingletonEjbClass.java to edit this template
 */
package session.singleton;

import entity.Employee;
import entity.Room;
import entity.RoomRate;
import entity.RoomType;
import enumeration.RateType;
import enumeration.RoleType;
import enumeration.RoomStatus;
import java.math.BigDecimal;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;
import session.stateless.EmployeeSessionBeanLocal;
import session.stateless.RoomRateSessionBeanLocal;
import session.stateless.RoomSessionBeanLocal;
import session.stateless.RoomTypeSessionBeanLocal;
import util.exception.EmployeeExistException;
import util.exception.EmployeeNotFoundException;
import util.exception.GuestExistException;
import util.exception.InputDataValidationException;
import util.exception.PersistentContextException;
import util.exception.RoomExistException;
import util.exception.RoomRateExistException;
import util.exception.RoomTypeAddRoomRateException;
import util.exception.RoomTypeDisabledException;
import util.exception.RoomTypeExistException;
import util.exception.RoomTypeNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author Toh Seaw Teng
 */
@Singleton
@LocalBean
@Startup

public class DataInitializationSessionBean {

    @EJB
    private RoomTypeSessionBeanLocal roomTypeSessionBeanLocal;

    @EJB
    private RoomSessionBeanLocal roomSessionBeanLocal;

    @EJB
    private RoomRateSessionBeanLocal roomRateSessionBeanLocal;

    @EJB
    private EmployeeSessionBeanLocal employeeSessionBeanLocal;



    public DataInitializationSessionBean() {
    }

    @PostConstruct
    public void postConstruct()
    {
        // if data not found then do the init
        try {
            employeeSessionBeanLocal.retrieveEmployeebyUsername("sysadmin");
        } catch (EmployeeNotFoundException ex) {
            initializeData();
        }
    }

    private void initializeData()
    {
        try
        {
            // Add Employee
            employeeSessionBeanLocal.createNewEmployee(new Employee("sysadmin", "password", RoleType.SYSTEM_ADMINISTRATOR));
            employeeSessionBeanLocal.createNewEmployee(new Employee("opmanager", "password", RoleType.OPERATION_MANAGER));
            employeeSessionBeanLocal.createNewEmployee(new Employee("salesmanager", "password", RoleType.SALES_MANAGER));
            employeeSessionBeanLocal.createNewEmployee(new Employee("guestrelo", "password", RoleType.GUEST_RELATION_OFFICER));

            // add room type
            RoomType grand = new RoomType("Grand Suite");
            grand = roomTypeSessionBeanLocal.createNewRoomType(grand);

            RoomType junior = new RoomType("Junior Suite");
            junior.setHigherRoomType(grand);
            junior = roomTypeSessionBeanLocal.createNewRoomType(junior);

            RoomType family = new RoomType("Family Room");
            family.setHigherRoomType(junior);
            family = roomTypeSessionBeanLocal.createNewRoomType(family);

            RoomType premier = new RoomType("Premier Room");
            premier.setHigherRoomType(family);
            premier = roomTypeSessionBeanLocal.createNewRoomType(premier);

            RoomType deluxe = new RoomType("Deluxe Room");
            deluxe.setHigherRoomType(premier);
            deluxe = roomTypeSessionBeanLocal.createNewRoomType(deluxe);

            // add room rate
            RoomRate deluxePub = new RoomRate("Deluxe Room Published", RateType.PUBLISHED, new BigDecimal(100));
            deluxePub.setRoomType(deluxe);
            deluxePub = roomRateSessionBeanLocal.createRoomRate(deluxePub);
            deluxe.addRoomRate(deluxePub);

            RoomRate deluxeNor = new RoomRate("Deluxe Room Normal", RateType.NORMAL, new BigDecimal(50));
            deluxeNor.setRoomType(deluxe);
            deluxeNor = roomRateSessionBeanLocal.createRoomRate(deluxeNor);
            deluxe.addRoomRate(deluxeNor);

            RoomRate premierPub = new RoomRate("Premier Room Published", RateType.PUBLISHED, new BigDecimal(200));
            premierPub.setRoomType(premier);
            premierPub = roomRateSessionBeanLocal.createRoomRate(premierPub);
            premier.addRoomRate(premierPub);

            RoomRate premierNor = new RoomRate("Premier Room Normal", RateType.NORMAL, new BigDecimal(100));
            premierNor.setRoomType(premier);
            premierNor = roomRateSessionBeanLocal.createRoomRate(premierNor);
            premier.addRoomRate(premierNor);

            RoomRate familyPub = new RoomRate("Family Room Published", RateType.PUBLISHED, new BigDecimal(300));
            familyPub.setRoomType(family);
            familyPub = roomRateSessionBeanLocal.createRoomRate(familyPub);
            family.addRoomRate(familyPub);

            RoomRate familyNor = new RoomRate("Family Room Normal", RateType.NORMAL, new BigDecimal(150));
            familyNor.setRoomType(family);
            familyNor = roomRateSessionBeanLocal.createRoomRate(familyNor);
            family.addRoomRate(familyNor);

            RoomRate juniorPub = new RoomRate("Junior Suite Published", RateType.PUBLISHED, new BigDecimal(400));
            juniorPub.setRoomType(junior);
            juniorPub = roomRateSessionBeanLocal.createRoomRate(juniorPub);
            junior.addRoomRate(juniorPub);

            RoomRate juniorNor = new RoomRate("Junior Suite Normal", RateType.NORMAL, new BigDecimal(200));
            juniorNor.setRoomType(junior);
            juniorNor = roomRateSessionBeanLocal.createRoomRate(juniorNor);
            junior.addRoomRate(juniorNor);

            RoomRate grandPub = new RoomRate("Grand Suite Published", RateType.PUBLISHED, new BigDecimal(500));
            grandPub.setRoomType(grand);
            grandPub = roomRateSessionBeanLocal.createRoomRate(grandPub);
            grand.addRoomRate(grandPub);

            RoomRate grandNor = new RoomRate("Grand Suite Normal", RateType.NORMAL, new BigDecimal(250));
            grandNor.setRoomType(grand);
            grandNor = roomRateSessionBeanLocal.createRoomRate(grandNor);
            grand.addRoomRate(grandNor);

            // room
            roomSessionBeanLocal.createNewRoom(new Room("0101", RoomStatus.AVAILABLE), "Deluxe Room");
            roomSessionBeanLocal.createNewRoom(new Room("0201", RoomStatus.AVAILABLE), "Deluxe Room");
            roomSessionBeanLocal.createNewRoom(new Room("0301", RoomStatus.AVAILABLE), "Deluxe Room");
            roomSessionBeanLocal.createNewRoom(new Room("0401", RoomStatus.AVAILABLE), "Deluxe Room");
            roomSessionBeanLocal.createNewRoom(new Room("0501", RoomStatus.AVAILABLE), "Deluxe Room");

            roomSessionBeanLocal.createNewRoom(new Room("0102", RoomStatus.AVAILABLE), "Premier Room");
            roomSessionBeanLocal.createNewRoom(new Room("0202", RoomStatus.AVAILABLE), "Premier Room");
            roomSessionBeanLocal.createNewRoom(new Room("0302", RoomStatus.AVAILABLE), "Premier Room");
            roomSessionBeanLocal.createNewRoom(new Room("0402", RoomStatus.AVAILABLE), "Premier Room");
            roomSessionBeanLocal.createNewRoom(new Room("0502", RoomStatus.AVAILABLE), "Premier Room");

            roomSessionBeanLocal.createNewRoom(new Room("0103", RoomStatus.AVAILABLE), "Family Room");
            roomSessionBeanLocal.createNewRoom(new Room("0203", RoomStatus.AVAILABLE), "Family Room");
            roomSessionBeanLocal.createNewRoom(new Room("0303", RoomStatus.AVAILABLE), "Family Room");
            roomSessionBeanLocal.createNewRoom(new Room("0403", RoomStatus.AVAILABLE), "Family Room");
            roomSessionBeanLocal.createNewRoom(new Room("0503", RoomStatus.AVAILABLE), "Family Room");

            roomSessionBeanLocal.createNewRoom(new Room("0104", RoomStatus.AVAILABLE), "Junior Suite");
            roomSessionBeanLocal.createNewRoom(new Room("0204", RoomStatus.AVAILABLE), "Junior Suite");
            roomSessionBeanLocal.createNewRoom(new Room("0304", RoomStatus.AVAILABLE), "Junior Suite");
            roomSessionBeanLocal.createNewRoom(new Room("0404", RoomStatus.AVAILABLE), "Junior Suite");
            roomSessionBeanLocal.createNewRoom(new Room("0504", RoomStatus.AVAILABLE), "Junior Suite");

            roomSessionBeanLocal.createNewRoom(new Room("0105", RoomStatus.AVAILABLE), "Grand Suite");
            roomSessionBeanLocal.createNewRoom(new Room("0205", RoomStatus.AVAILABLE), "Grand Suite");
            roomSessionBeanLocal.createNewRoom(new Room("0305", RoomStatus.AVAILABLE), "Grand Suite");
            roomSessionBeanLocal.createNewRoom(new Room("0405", RoomStatus.AVAILABLE), "Grand Suite");
            roomSessionBeanLocal.createNewRoom(new Room("0505", RoomStatus.AVAILABLE), "Grand Suite");

            // Testing
        }
        catch(RoomTypeDisabledException | RoomExistException | RoomTypeNotFoundException | RoomTypeAddRoomRateException | PersistentContextException | RoomRateExistException | RoomTypeExistException | EmployeeExistException | UnknownPersistenceException | InputDataValidationException ex)
        {
            ex.printStackTrace();
        }
    }
}
