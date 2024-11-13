/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package horsmanagementclient;

import entity.Employee;
import entity.RoomType;
import entity.RoomRate;
import entity.Room;
import entity.RoomAllocationExceptionReport;
import enumeration.RoleType;
import enumeration.RoomStatus;
import enumeration.RateType;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.management.Query;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import session.stateless.EmployeeSessionBeanRemote;
import session.stateless.PartnerSessionBeanRemote;
import session.stateless.RoomAllocationExceptionReportSessionBeanRemote;
import session.stateless.RoomRateSessionBeanRemote;
import session.stateless.RoomSessionBeanRemote;
import session.stateless.RoomTypeSessionBeanRemote;
import util.exception.ReportExistException;
import util.exception.ReservationNotFoundException;
import util.exception.RoomExistException;
import util.exception.PersistentContextException;
import util.exception.RoomDeleteException;
import util.exception.RoomNotFoundException;
import util.exception.RoomRateExistException;
import util.exception.RoomTypeNotFoundException;
import util.exception.RoomRateNotFoundException;
import util.exception.RoomTypeDisabledException;
import util.exception.RoomTypeDeleteException;
import util.exception.RoomTypeNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.InvalidAccessRightException;
import util.exception.RoomTypeExistException;
import util.exception.RoomTypeRemoveRoomRateException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author priskilarebecca.p
 */
public class HotelOperationModule {
    private final ValidatorFactory validatorFactory;
    private final Validator validator;
    
    private EmployeeSessionBeanRemote employeeSessionBeanRemote;
    private RoomSessionBeanRemote roomSessionBeanRemote;
    private RoomTypeSessionBeanRemote roomTypeSessionBeanRemote;
    private RoomAllocationExceptionReportSessionBeanRemote roomAllocationExceptionReportSessionBeanRemote;
    private RoomRateSessionBeanRemote roomRateSessionBeanRemote;
    
    private Employee currentEmployee;
    
    public HotelOperationModule()
    {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    public HotelOperationModule(EmployeeSessionBeanRemote employeeSessionBeanRemote, RoomSessionBeanRemote roomSessionBeanRemote, RoomTypeSessionBeanRemote roomTypeSessionBeanRemote, RoomAllocationExceptionReportSessionBeanRemote roomAllocationExceptionReportSessionBeanRemote, RoomRateSessionBeanRemote roomRateSessionBeanRemote, Employee currentEmployee) {
        this();
        this.employeeSessionBeanRemote = employeeSessionBeanRemote;
        this.roomSessionBeanRemote = roomSessionBeanRemote;
        this.roomTypeSessionBeanRemote = roomTypeSessionBeanRemote;
        this.roomAllocationExceptionReportSessionBeanRemote = roomAllocationExceptionReportSessionBeanRemote;
        this.roomRateSessionBeanRemote = roomRateSessionBeanRemote;
        this.currentEmployee = currentEmployee;
    }


    public void menuHotelOperation() throws InvalidAccessRightException {
        if (currentEmployee != null) {
            if(currentEmployee.getRoleType() == RoleType.OPERATION_MANAGER) {
                Scanner scanner = new Scanner(System.in);
                Integer response = 0;

                while(true)
                {
                    System.out.println("*** HoR System :: Hotel Operation ***\n");
                    System.out.println("1: Create New Room Type");
                    System.out.println("2: View Room Type Details");
                    System.out.println("3: View All Room Types");
                    System.out.println("-----------------------");
                    System.out.println("4: Create New Room");
                    System.out.println("5: Update Room");
                    System.out.println("6: Delete Room");
                    System.out.println("7: View All Rooms");
                    System.out.println("-----------------------");
                    System.out.println("8: View Room Allocation Exception Report");
                    System.out.println("-----------------------");
                    System.out.println("9: Allocate Room to Current Day Reservations");
                    System.out.println("10: Back\n");
                    response = 0;

                    while(response < 1 || response > 9)
                    {
                        System.out.print("> ");

                        response = scanner.nextInt();

                        if(response == 1)
                        {
                            doCreateNewRoomType();
                        }
                        else if(response == 2)
                        {
                            doViewRoomTypeDetails();
                        }
                        else if(response == 3)
                        {
                            doViewAllRoomTypes();
                        }
                        else if(response == 4)
                        {
                            doCreateNewRoom();
                        }
                        else if(response == 5)
                        {
                            doUpdateRoom();
                        }
                        else if(response == 6)
                        {
                            doDeleteRoom();
                        }
                        else if(response == 7)
                        {
                            doViewAllRooms();
                        }
                        else if(response == 8)
                        {
                            doViewRoomAllocationExceptionReport();
                        }
                        else if(response == 9)
                        {
                            doAllocateRoomToCurrentDayReservations();
                        }
                        else if(response == 10)
                        {
                            break;
                        }
                        else
                        {
                            System.out.println("Invalid option, please try again!\n");                
                        }
                    }

                    if(response == 9)
                    {
                        break;
                    }
                }
            } else if (currentEmployee.getRoleType() == RoleType.SALES_MANAGER) {
                Scanner scanner = new Scanner(System.in);
                Integer response = 0;

                while(true)
                {
                    System.out.println("*** HoR System :: Hotel Operation ***\n");
                    System.out.println("1: Create New Room Rate");
                    System.out.println("2: View Room Rate Details");
                    System.out.println("3: View All Room Rates");
                    System.out.println("4: Back\n");
                    response = 0;

                    while(response < 1 || response > 4)
                    {
                        System.out.print("> ");

                        response = scanner.nextInt();

                        if(response == 1)
                        {
                            doCreateNewRoomRate();
                        }
                        else if(response == 2)
                        {
                            doViewRoomRateDetails();
                        }
                        else if(response == 3)
                        {
                            doViewAllRoomRates();
                        }
                        else if(response == 4)
                        {  
                            break;
                        }
                        else
                        {
                            System.out.println("Invalid option, please try again!\n");                
                        }
                    }

                    if(response == 6)
                    {
                        break;
                    }
                }
            } else {
                throw new InvalidAccessRightException("You don't have OPERATION_MANAGER or SALES_MANAGER rights to access the hotel operation module.");
            }
        }
    }
    
    private void doCreateNewRoomType() {
        Scanner scanner = new Scanner(System.in);
        RoomType newRoomType = new RoomType();
        
        System.out.println("*** HoR System :: Hotel Operation :: Create New Room Type ***\n");
        System.out.print("Enter Room Type Name> ");
        newRoomType.setName(scanner.nextLine().trim());
        System.out.print("Enter a Description of the Room Type> ");
        newRoomType.setDescription(scanner.nextLine().trim());
        System.out.print("Enter the Size of the Room Type> ");
        newRoomType.setSize(scanner.nextBigDecimal());
        System.out.print("Enter the Number of Beds in the Room Type> ");
        newRoomType.setBeds(scanner.nextInt());
        System.out.print("Enter the Capacity of the Room Type> ");
        newRoomType.setCapacity(scanner.nextInt());
        System.out.print("Enter the Amenities in the Room Type> ");
        newRoomType.setAmenities(scanner.nextLine().trim());
        System.out.print("Enter Higher Room Type Name. Enter 'None' if there is no Higher Room Type> ");
        String higherRoomType = scanner.nextLine().trim();
        newRoomType.setDisabled(Boolean.FALSE);
        
        Set<ConstraintViolation<RoomType>>constraintViolations = validator.validate(newRoomType);
        
        if(constraintViolations.isEmpty())
        {
            try
            {
                newRoomType = roomTypeSessionBeanRemote.createNewRoomType(newRoomType, higherRoomType);
                Long newRoomTypeId = newRoomType.getRoomTypeId();
                System.out.println("New Room Type created successfully!: " + newRoomTypeId + "\n");
            }
            catch(RoomTypeExistException ex)
            {
                System.out.println("An error has occurred while creating the new Room Type!: The name already exist\n");
            }
            catch(RoomTypeNotFoundException ex)
            {
                System.out.println("An error has occurred while creating the new Room Type!: The name already exist\n");
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
            showInputDataValidationErrorsForRoomType(constraintViolations);
        }  
    }

    private void doViewRoomTypeDetails() {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        System.out.println("*** HoR System :: Hotel Operation :: View Room Type Details ***\n");
        System.out.print("Enter Room Type ID> ");
        Long roomTypeId = scanner.nextLong();
        
        try
        {
            RoomType roomType = roomTypeSessionBeanRemote.retrieveRoomTypebyId(roomTypeId);
            System.out.println("---------------------");
            System.out.println("Room Type ID: " + roomType.getRoomTypeId());
            System.out.println("Room Type Name: " + roomType.getName());
            System.out.println("Room Type Description: " + roomType.getDescription());
            System.out.println("Room Type Size: " + roomType.getSize()); 
            System.out.println("Number of Beds: " + roomType.getBeds());
            System.out.println("Room Type Capacity: " + roomType.getCapacity());
            System.out.println("Room Type Amenities: " + roomType.getAmenities());
            System.out.println("Is Room Type Disabled? " + roomType.getDisabled());
            if (roomType.getHigherRoomType() != null) {
                System.out.println("Higher Room Type: " + roomType.getHigherRoomType().getName());
            } else {
                System.out.println("Higher Room Type: This room is the Highest Room Type in the Hotel Reservation System, no Higher Room Type has been allocated to it!");
            }
            // display room rates for each room type
            System.out.println("Room Type Rates: ");
            roomTypeSessionBeanRemote.retrieveRoomRatesForRoomType(roomType);
            System.out.println("---------------------");
            System.out.println("1: Update Room Type");
            System.out.println("2: Delete Room Type");
            System.out.println("3: Back\n");
            System.out.print("> ");
            response = scanner.nextInt();

            if(response == 1)
            {
                doUpdateRoomType(roomType);
            }
            else if(response == 2)
            {
                doDeleteRoomType(roomType);
            }
        }
        catch(RoomTypeNotFoundException ex)
        {
            System.out.println("An error has occurred while retrieving Room Type: " + ex.getMessage() + "\n");
        }
    }
    
    private void doUpdateRoomType(RoomType roomType) {
        Scanner scanner = new Scanner(System.in);
        //String roomTypeName = roomType.getName();
        String input;
        BigDecimal inputDec;
        Integer inputInt;
        
        System.out.println("*** HoR System :: Hotel Operation :: View Room Type Details :: Update Room Type ***\n");
        System.out.print("Enter Room Type Name (blank if no change)> ");
        input = scanner.nextLine().trim();
        if(input.length() > 0)
        {
            roomType.setName(input);
        }
                
        System.out.print("Enter Room Type Description (blank if no change)> ");
        input = scanner.nextLine().trim();
        if(input.length() > 0)
        {
            roomType.setDescription(input);
        }
        
        System.out.print("Enter Room Type Size (blank if no change)> ");
        inputDec = scanner.nextBigDecimal();
        if(inputDec.compareTo(BigDecimal.ZERO) > 0)
        {
            roomType.setSize(inputDec);
        }
        
        System.out.print("Enter Room Type Number of Beds (blank if no change)> ");
        inputInt = scanner.nextInt();
        if(inputInt != null)
        {
            roomType.setBeds(inputInt);
        }
        
        System.out.print("Enter Room Type Capacity (blank if no change)> ");
        inputInt = scanner.nextInt();
        if(inputInt != null)
        {
            roomType.setCapacity(inputInt);
        }
        
        System.out.print("Enter Room Type Amenities (blank if no change)> ");
        input = scanner.nextLine().trim();
        if(input.length() > 0)
        {
            roomType.setAmenities(input);
        }
        
        
        Set<ConstraintViolation<RoomType>>constraintViolations = validator.validate(roomType);
        
        if(constraintViolations.isEmpty())
        {
            try
            {
                roomTypeSessionBeanRemote.updateRoomType(roomType);
                System.out.println("Room Type updated successfully!\n");
            }
            catch (RoomTypeNotFoundException ex) 
            {
                System.out.println("An error has occurred while updating room type: " + ex.getMessage() + "\n");
            } catch (InputDataValidationException ex) {
                System.out.println(ex.getMessage() + "\n");
            }
        } else {
             showInputDataValidationErrorsForRoomType(constraintViolations);
        }
    }
    
    private void doDeleteRoomType(RoomType roomType) {
        Scanner scanner = new Scanner(System.in);        
        String input;
        
        System.out.println("*** HoR System :: Hotel Operation :: View Room Type Details :: Delete Room Type ***\n");
        System.out.printf("Confirm Delete Room Type %s (Room Type ID: %d) (Enter 'Y' to Delete)> ", roomType.getName(), roomType.getRoomTypeId());
        input = scanner.nextLine().trim();
        
        
        
        if(input.equals("Y"))
        {
            try
            {
                roomTypeSessionBeanRemote.deleteRoomType(roomType.getRoomTypeId());
                System.out.println("RoomType deleted successfully!\n");
            }
            catch (RoomTypeNotFoundException | RoomTypeDeleteException ex) 
            {
                System.out.println("An error has occurred while deleting the Room Type: " + ex.getMessage() + "\n");
            }
        }
        else
        {
            System.out.println("Room Type NOT deleted!\n");
        }
    }

    private void doViewAllRoomTypes() { //cant use printf because desc is too long
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("*** HoR System :: Hotel Operation :: View All Room Types ***\n");
        
        List<RoomType> roomTypeList = roomTypeSessionBeanRemote.viewAllRoomTypes();
        for(RoomType roomType : roomTypeList) {
            System.out.println("---------------------");
            System.out.println("Room Type ID: " + roomType.getRoomTypeId());
            System.out.println("Room Type Name: " + roomType.getName());
            System.out.println("Room Type Description: " + roomType.getDescription());
            System.out.println("Room Type Size: " + roomType.getSize()); 
            System.out.println("Room Type Capacity: " + roomType.getCapacity());
            System.out.println("Number of Beds: " + roomType.getBeds());
            System.out.println("Amenities: " + roomType.getAmenities());
            System.out.println("Is Room Type Disabled? " + roomType.getDisabled());
            if (roomType.getHigherRoomType() != null) {
                System.out.println("Higher Room Type: " + roomType.getHigherRoomType().getName());
            } else {
                System.out.println("Higher Room Type: This room is the Highest Room Type in the Hotel Reservation System, no Higher Room Type has been allocated to it!");
            }
            // display room rates for each room type
            System.out.println("Room Type Rates: ");
            List<RoomRate> roomRateList = roomType.getRoomRateList();
            for (RoomRate roomRate : roomRateList) {
                System.out.println(" - " + roomRate.getName() + ": " + roomRate.getRatePerNight() + " dollars per night");
            }
            System.out.println("---------------------");
        }
        System.out.print("Press any key to continue...> ");
        scanner.nextLine();
    }

    private void doCreateNewRoom() {
        Scanner scanner = new Scanner(System.in);
        Room newRoom = new Room();
        
        System.out.println("*** HoR System :: Hotel Operation :: Create New Room ***\n");
        System.out.print("Enter Room Number> ");
        newRoom.setRoomNumber(scanner.nextLine());
        newRoom.setRoomStatus(RoomStatus.AVAILABLE);
        newRoom.setDisabled(Boolean.FALSE);
        newRoom.setIsOccupied(Boolean.FALSE);
        
        System.out.print("Enter Room Type for Room> ");
        String roomTypeName = scanner.nextLine().trim();
        
        Set<ConstraintViolation<Room>>constraintViolations = validator.validate(newRoom);
        
        if(constraintViolations.isEmpty())
        {
            try
            {
                newRoom = roomSessionBeanRemote.createNewRoom(newRoom, roomTypeName);
                Long newRoomId = newRoom.getRoomId();
                System.out.println("New Room Type created successfully!: " + newRoomId + "\n");
            }
            catch(RoomExistException ex)
            {
                System.out.println("An error has occurred while creating the new Room! : The room number already exist\n");
            }
            catch(RoomTypeNotFoundException ex)
            {
                System.out.println("An error has occurred while creating the new Room! : The room type cannot be found\n");
            }
            catch(RoomTypeDisabledException ex)
            {
                System.out.println("An error has occurred while creating the new Room! : The room type is disabled\n");
            }
            catch(UnknownPersistenceException ex)
            {
                System.out.println("An unknown error has occurred while creating the new room!: " + ex.getMessage() + "\n");
            }
            catch(InputDataValidationException ex)
            {
                System.out.println(ex.getMessage() + "\n");
            }
        } else {
            showInputDataValidationErrorsForRoom(constraintViolations);
        } 
   
    }

    
    //different from update room type because no input...
    private void doUpdateRoom() {
        Scanner scanner = new Scanner(System.in);
        //Room updateRoom = new Room();
        String input;
        BigDecimal inputDec;
        String inputStr;
        
        System.out.println("*** HoR System :: Hotel Operation :: Update Room ***\n");
        System.out.print("Enter Room Number> ");
        Integer roomNumber = scanner.nextInt();
        
        //should we view room details before we update??
        try
        {
            Room updateRoom = roomSessionBeanRemote.retrieveRoombyRoomNumber(roomNumber);
            
            System.out.println("--------------------");
            System.out.println("Room ID: " + updateRoom.getRoomId());
            System.out.println("Room Number: " + updateRoom.getRoomNumber());
            System.out.println("Rate Status: " + updateRoom.getRoomStatus());
            System.out.println("--------------------");

            System.out.print("Enter Room Number (blank if no change)> ");
            inputStr = scanner.nextLine();
            if(inputStr != null)
            {
                updateRoom.setRoomNumber(inputStr);
            }

            while(true)
            {
                System.out.print("Select Room Status (0: No Change, 1: AVAILABLE, 2: UNAVAILABLE)> ");
                Integer roomStatusInt = scanner.nextInt();

                if(roomStatusInt >= 1 && roomStatusInt <= 2)
                {
                    updateRoom.setRoomStatus(RoomStatus.values()[roomStatusInt-1]);
                    break;
                }
                else if (roomStatusInt == 0)
                {
                    break;
                }
                else
                {
                    System.out.println("Invalid option, please try again!\n");
                }
            }

            Set<ConstraintViolation<Room>>constraintViolations = validator.validate(updateRoom);

            if(constraintViolations.isEmpty()) {
                try
                {
                    roomSessionBeanRemote.updateRoom(updateRoom);
                    System.out.println("Room Type updated successfully!\n");
                } catch (InputDataValidationException ex) {
                    System.out.println(ex.getMessage() + "\n");
                }
            } else {
             showInputDataValidationErrorsForRoom(constraintViolations);
            }
        } catch (RoomNotFoundException ex) {
             System.out.println("An error occurred: " + ex.getMessage());
        }
    }

    //delete only when the room is not occupied!!!
    private void doDeleteRoom() {
        Scanner scanner = new Scanner(System.in);        
        String input;
        System.out.println("*** HoR System :: Hotel Operation :: Delete Room ***\n");
        System.out.print("Enter Room Number to be Deleted> ");
        Integer roomNumber = scanner.nextInt();
       
        Room room;
        try {
            room = roomSessionBeanRemote.retrieveRoombyRoomNumber(roomNumber);
            System.out.printf("Confirm Delete Room %s (Room ID: %d) (Enter 'Y' to Delete)> ", room.getRoomNumber(), room.getRoomId());
            input = scanner.nextLine().trim();

            if(input.equals("Y"))
            {
                try
                {
                    roomSessionBeanRemote.deleteRoom(room.getRoomId());
                    System.out.println("Room deleted successfully!\n");
                }
                catch (RoomNotFoundException | RoomDeleteException ex) 
                {
                    System.out.println("An error has occurred while deleting the Room : " + ex.getMessage() + "\n");
                }
            }
            else
            {
                System.out.println("Room  NOT deleted!\n");
            }
        } catch (RoomNotFoundException ex) {
            System.out.println("An error occurred: " + ex.getMessage());
        }
    }

    private void doViewAllRooms() {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("*** HoR System :: Hotel Operation :: View All Rooms ***\n");
        
        List<Room> roomList = roomSessionBeanRemote.viewAllRooms();
        System.out.printf("%8s%4s%30s%24s%24s%24s\n", "Room ID", "Room Number", "Room Status", "Room Type", "Is Room Disabled?", "Is Room Occupied?");

        for(Room room:roomList)
        {
            System.out.printf("%8s%4s%30s%24s%24s%24s\n", room.getRoomId().toString(), room.getRoomNumber().toString(), room.getRoomStatus(), room.getRoomType().getName(), room.getDisabled(), room.getIsOccupied());
        }
        
        System.out.print("Press any key to continue...> ");
        scanner.nextLine();
    }

    private void doViewRoomAllocationExceptionReport() {
        System.out.println("*** HoR System :: Hotel Operation :: View Room Allocation Exception Report ***\n");
        System.out.printf("%8s%30s%24s\n","Room Allocation Exception Report ID", "Exception Type", "Reservation ID");
        for(RoomAllocationExceptionReport report : roomAllocationExceptionReportSessionBeanRemote.viewAllReports()){
              
            System.out.printf("%8s%30s%24s\n", report.getRoomAllocationExceptionReportId(), report.getDetails(), report.getReservation().getReservationId());
        }
    }

    private void doAllocateRoomToCurrentDayReservations() {
        try {
            Scanner scanner = new Scanner (System.in);
            String inputDate = "";

            System.out.println("*** HoR System :: Hotel Operation :: Allocate Room To Current Day Reservations ***\n");

            Date checkinDate;
            SimpleDateFormat date = new SimpleDateFormat("dd/MM/yyyy");

            System.out.print("Enter the Check-in Date (dd/mm/yyyy)> ");
            checkinDate = date.parse(scanner.nextLine().trim());

            roomSessionBeanRemote.allocateRoomToReservation(checkinDate);

        } 
        catch (ParseException ex) 
        {
            System.out.println("Invalid date input, please try again!");
        } 
        catch (ReservationNotFoundException | ReportExistException ex) 
        {
            System.out.println("An error occurred: " + ex.getMessage());
        }
        catch(UnknownPersistenceException ex)
        {
            System.out.println("An unknown error has occurred while creating the new report!: " + ex.getMessage() + "\n");
        }
        catch(InputDataValidationException ex)
        {
            System.out.println(ex.getMessage() + "\n");
        }
    }

    private void doCreateNewRoomRate() {
        try {  
            Scanner scanner = new Scanner(System.in);
            RoomRate newRoomRate = new RoomRate();

            System.out.println("*** HoR System :: Hotel Operation :: Create New Room Rate ***\n");
            System.out.print("Enter Room Rate Name> ");
            newRoomRate.setName(scanner.nextLine().trim());

            while(true)
            {
                System.out.print("Select Room Rate Type (1: PUBLISHED, 2: NORMAL, 3: PEAK, 4: PROMOTION)> ");
                Integer rateTypeInt = scanner.nextInt();

                if(rateTypeInt >= 1 && rateTypeInt <= 2)
                {
                    newRoomRate.setRateType(RateType.values()[rateTypeInt-1]);
                    break;
                }
                else
                {
                    System.out.println("Invalid option, please try again!\n");
                }
            }

            System.out.print("Enter the Rate per Night> ");
            newRoomRate.setRatePerNight(scanner.nextBigDecimal());
            scanner.nextLine();
            SimpleDateFormat date = new SimpleDateFormat("dd/MM/yyyy");

            //only peak and promotion have valid end and valid start!!! else dont need!!!
            if(newRoomRate.getRateType().equals(RateType.PEAK) || newRoomRate.getRateType().equals(RateType.PROMOTION)) {
                    System.out.print("Enter validity start date (dd/mm/yyyy)> ");
                    newRoomRate.setValidityStart(date.parse(scanner.nextLine().trim()));

                    System.out.print("Enter validity end date (dd/mm/yyyy)> ");
                    newRoomRate.setValidityEnd(date.parse(scanner.nextLine().trim()));
            }

            System.out.println("");

            //set room type??
            List<RoomType> roomTypes = roomTypeSessionBeanRemote.viewAllRoomTypes();
            System.out.println("Available room type:");
            for(RoomType roomType:roomTypes){
                System.out.println("- " + roomType.getName());
            }
            System.out.print("Enter room type to associate with room rate with> ");
            String roomTypeName = scanner.nextLine().trim();

            newRoomRate.setDisabled(Boolean.FALSE);


            Set<ConstraintViolation<RoomRate>>constraintViolations = validator.validate(newRoomRate);

            if(constraintViolations.isEmpty())
            {
                try
                {
                    newRoomRate = roomRateSessionBeanRemote.createRoomRate(newRoomRate, roomTypeName);
                    Long newRoomRateId = newRoomRate.getRoomRateId();
                    System.out.println("New Room Rate created successfully!: " + newRoomRateId + "\n");
                }
                catch(RoomRateExistException ex)
                {
                    System.out.println("An error has occurred while creating the new Room Rate!: The name already exist\n");
                }
                catch(UnknownPersistenceException ex)
                {
                    System.out.println("An unknown error has occurred while creating the new staff!: " + ex.getMessage() + "\n");
                }
                catch(InputDataValidationException ex)
                {
                    System.out.println(ex.getMessage() + "\n");
                }
            } else {
                showInputDataValidationErrorsForRoomRate(constraintViolations);
            } 
        } catch (ParseException ex) {
            System.out.println("Invalid date input, please try again!!\n");
        }
    }

    private void doViewRoomRateDetails() {
       Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        System.out.println("*** HoR System :: Hotel Operation :: View Room Rate Details ***\n");
        System.out.print("Enter Room Rate Id> ");
        Long roomRateId = scanner.nextLong();
        
        try
        {
            RoomRate roomRate = roomRateSessionBeanRemote.retrieveRoomRateById(roomRateId);
            System.out.println("---------------------");
            System.out.println("Room Rate ID: " + roomRate.getRoomRateId());
            System.out.println("Room Rate Name: " + roomRate.getName());
            System.out.println("Room Rate Type: " + roomRate.getRateType());
            System.out.println("Room Rate per Night: " + roomRate.getRatePerNight());
            //only peak and promotion have valid end and valid start!!! else dont have!!!
            if(roomRate.getRateType().equals(RateType.PEAK) || roomRate.getRateType().equals(RateType.PROMOTION)) {
                System.out.println("Room Rate Validity Start Date: " + roomRate.getValidityStart());
                System.out.println("Room Rate Validity End Date: " + roomRate.getValidityEnd());
            }
            
            System.out.println("Is Room Rate Disabled? " + roomRate.getDisabled());
            System.out.println("What Room Type is the Room Rate associated to?: " + roomRate.getRoomType());
            
            System.out.println("---------------------");
            System.out.println("1: Update Room Rate");
            System.out.println("2: Delete Room Rate");
            System.out.println("3: Back\n");
            System.out.print("> ");
            response = scanner.nextInt();

            if(response == 1)
            {
                doUpdateRoomRate(roomRate);
            }
            else if(response == 2)
            {
                doDeleteRoomRate(roomRate);
            }
        }
        catch(RoomRateNotFoundException ex)
        {
            System.out.println("An error has occurred while retrieving Room Rate: " + ex.getMessage() + "\n");
        } 
    }

    private void doUpdateRoomRate(RoomRate roomRate) {
        //update name, rate type, rate per night, validity start, end, disabled, room type??
        Scanner scanner = new Scanner(System.in);
        String input;
        BigDecimal inputDec;
        Integer inputInt;
        
        System.out.println("*** HoR System :: Hotel Operation :: Update Room Rate ***\n");
        System.out.print("Enter Room Rate Id> ");
        Long roomRateId = scanner.nextLong();
        
        try
        {
            RoomRate updateRoomRate = roomRateSessionBeanRemote.retrieveRoomRateById(roomRateId);

            System.out.print("Enter Room Rate Name (blank if no change)> ");
            input = scanner.nextLine();
            if(input.length() > 0)
            {
                updateRoomRate.setName(input);
            }

            while(true)
            {
                System.out.print("Select Room Rate Type (0: No Change, 1: PUBLISHED, 2: NORMAL, 3: PEAK, 4: PROMOTION)> ");
                Integer rateTypeInt = scanner.nextInt();

                if(rateTypeInt >= 1 && rateTypeInt <= 4)
                {
                    updateRoomRate.setRateType(RateType.values()[rateTypeInt-1]);
                    break;
                }
                else if (rateTypeInt == 0)
                {
                    break;
                }
                else
                {
                    System.out.println("Invalid option, please try again!\n");
                }
            }
            
            System.out.print("Enter Room Rate per Night (blank if no change)> ");
            inputDec = scanner.nextBigDecimal();
            if(inputDec.compareTo(BigDecimal.ZERO) > 0)
            {
                updateRoomRate.setRatePerNight(inputDec);
            }
            
            /*System.out.print("Enter Room Rate per Night (blank if no change)> ");
            inputDec = scanner.nextBigDecimal();
            if(input != null)
            {
                updateRoomRate.setRatePerNight(inputDec);
            }*/
            
            SimpleDateFormat date = new SimpleDateFormat("dd/MM/yyyy");
            if(roomRate.getRateType().equals(RateType.PEAK) || roomRate.getRateType().equals(RateType.PROMOTION)) {
                System.out.print("Enter start date (dd/mm/yyyy) (blank if no change)> ");
                input = scanner.nextLine().trim();
                if(input.length() > 0) {
                    roomRate.setValidityStart(date.parse(input));
                }
                

                System.out.print("Enter end date (dd/mm/yyyy) (blank if no change)> ");
                input = scanner.nextLine().trim();
                if(input.length() > 0) {
                    roomRate.setValidityEnd(date.parse(input));
                }
            }
            
            //many room rates to a room type? so show all room types?? so they can choose which to add..

            List<RoomType> roomTypes = roomTypeSessionBeanRemote.viewAllRoomTypes();
            System.out.println("All Room Types you can associate with Room Rate:");
            for(RoomType roomType: roomTypes){
                System.out.println("- " + roomType.getName());
            }
            System.out.print("Enter Room Type Name to associate with Room Rate (blank if no change)> ");
            input = scanner.nextLine().trim();
            if(input.length() > 0) {
                try {
                    RoomType updateRoomType = roomTypeSessionBeanRemote.retrieveRoomTypebyName(input);
                    roomRate.setRoomType(updateRoomType);
                } catch (RoomTypeNotFoundException ex) {
                    System.out.println("An error occurred: " + ex.getMessage());
                }
            }
            
            Set<ConstraintViolation<RoomRate>>constraintViolations = validator.validate(updateRoomRate);

            if(constraintViolations.isEmpty()) {
                try
                {
                    roomRateSessionBeanRemote.updateRoomRate(roomRateId, updateRoomRate);
                    System.out.println("Room Rate updated successfully!\n");
                } catch (InputDataValidationException | RoomRateNotFoundException | PersistentContextException ex) {
                    System.out.println("An error occurred: " + ex.getMessage());
                }
            } else {
             showInputDataValidationErrorsForRoomRate(constraintViolations);
            }
        } catch (ParseException | RoomRateNotFoundException ex) {
             System.out.println("An error occurred: " + ex.getMessage());
        }
        
    }
    
    private void doDeleteRoomRate(RoomRate roomRate) {
        Scanner scanner = new Scanner(System.in);        
        String input;
        System.out.println("*** HoR System :: Hotel Operation :: Delete Room Rate ***\n");
        //System.out.print("Enter Room Rate Name to be Deleted> ");
        //Integer roomNumber = scanner.nextInt();
       
        //Room room;
        //try {
            //room = roomSessionBeanRemote.retrieveRoombyRoomNumber(roomNumber);
            System.out.printf("Confirm Delete Room Rate %s (Room Rate ID: %d) for Room Type %s (Enter 'Y' to Delete)> ", roomRate.getName(), roomRate.getRoomRateId(), roomRate.getRoomType());
            input = scanner.nextLine().trim();

            if(input.equals("Y"))
            {
                try
                {
                    roomRateSessionBeanRemote.deleteRoomRate(roomRate.getRoomRateId());
                    System.out.println("Room Rate deleted successfully!\n");
                }
                catch (RoomRateNotFoundException | RoomTypeRemoveRoomRateException | PersistentContextException ex) 
                {
                    System.out.println("An error has occurred while deleting the Room Rate : " + ex.getMessage() + "\n");
                }
            }
            else
            {
                System.out.println("Room Rate NOT deleted!\n");
            }
        //} catch (RoomNotFoundException ex) {
            //System.out.println("An error occurred: " + ex.getMessage());
        //}
    }
    
    
    private void doViewAllRoomRates() {
        Scanner scanner = new Scanner(System.in);
        SimpleDateFormat date = new SimpleDateFormat("dd/MM/yyyy");
        System.out.println("*** HoR System :: Hotel Operation :: View All Room Rates ***\n");
        
        List<RoomRate> roomRateList = roomRateSessionBeanRemote.viewAllRoomRates();
        System.out.printf("%8s%40s%24s%24s%24s%24s\n","Rate ID", "Rate Name", "Rate Type", "Rate Per Night","Validity Start Date", "Validity End Date");

        for(RoomRate roomRate:roomRateList)
        {
            String validStartDate = "";
            String validEndDate = "";
            
            //!! check validity for PEAK and PROMOTION!!!
            if(roomRate.getRateType().equals(RateType.PEAK) || roomRate.getRateType().equals(RateType.PROMOTION)) {
                validStartDate = "" + date.format(roomRate.getValidityStart());
                validEndDate = "" + date.format(roomRate.getValidityEnd());
            }
            System.out.printf("%8s%40s%24s%24s%24s%24s\n",roomRate.getRoomRateId(),roomRate.getName(), roomRate.getRateType(), 
                    roomRate.getRatePerNight(), validStartDate, validEndDate);
        }
        
        System.out.print("Press any key to continue...> ");
        scanner.nextLine();
    }
    
    private void showInputDataValidationErrorsForRoomType(Set<ConstraintViolation<RoomType>>constraintViolations)
    {
        System.out.println("\nInput data validation error!:");
            
        for(ConstraintViolation constraintViolation:constraintViolations)
        {
            System.out.println("\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage());
        }

        System.out.println("\nPlease try again......\n");
    }
    
    private void showInputDataValidationErrorsForRoom(Set<ConstraintViolation<Room>>constraintViolations)
    {
        System.out.println("\nInput data validation error!:");
            
        for(ConstraintViolation constraintViolation:constraintViolations)
        {
            System.out.println("\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage());
        }

        System.out.println("\nPlease try again......\n");
    }
    
    private void showInputDataValidationErrorsForRoomRate(Set<ConstraintViolation<RoomRate>>constraintViolations)
    {
        System.out.println("\nInput data validation error!:");
            
        for(ConstraintViolation constraintViolation:constraintViolations)
        {
            System.out.println("\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage());
        }

        System.out.println("\nPlease try again......\n");
    }
    
    

}
