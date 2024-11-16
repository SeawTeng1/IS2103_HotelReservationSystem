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
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
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
import util.exception.NoReservationsFoundException;
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
                    System.out.println("9: Allocate Room to Reservations");
                    System.out.println("-----------------------");
                    System.out.println("10: Logout\n");
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

                    if(response == 10)
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

                    if(response == 4)
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
        String input = scanner.nextLine().trim(); // Read input and trim whitespace

        if (input.isEmpty()) {
            System.out.println("No size entered. Skipping size assignment.");
            // Optionally, set a default value or leave it as null
            newRoomType.setSize(null); // Assuming size can be null in your RoomType entity
        } else {
            try {
                BigDecimal size = new BigDecimal(input); // Parse the input as BigDecimal
                newRoomType.setSize(size);
            } catch (NumberFormatException e) {
                System.out.println("Invalid size entered. Please enter a valid number.");
                return; // Exit or prompt again based on your requirements
            }
        }

        //System.out.print("Enter the Size of the Room Type> ");
        //String input = scanner.nextLine().trim();
        //try {
            //BigDecimal size = new BigDecimal(input);
            //newRoomType.setSize(size);
        //} catch (NumberFormatException e) {
            //System.out.println("Invalid input. Please enter a valid number.");
        //}

        //System.out.print("Enter the Number of Beds in the Room Type> ");
        //newRoomType.setBeds(Integer.parseInt(scanner.nextLine().trim()));
        //System.out.print("Enter the Capacity of the Room Type> ");
        //newRoomType.setCapacity(Integer.parseInt(scanner.nextLine().trim()));
        // Number of Beds
        System.out.print("Enter the Number of Beds in the Room Type> ");
        String bedsInput = scanner.nextLine().trim();
        if (bedsInput.isEmpty()) {
            System.out.println("No number of beds entered. Skipping beds assignment.");
            newRoomType.setBeds(null); // Assuming beds can be null in your RoomType entity
        } else {
            try {
                int numberOfBeds = Integer.parseInt(bedsInput); // Parse input as integer
                newRoomType.setBeds(numberOfBeds);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input for number of beds. Please enter a valid number.");
                return; // Exit or handle as needed
            }
        }

        // Capacity
        System.out.print("Enter the Capacity of the Room Type> ");
        String capacityInput = scanner.nextLine().trim();
        if (capacityInput.isEmpty()) {
            System.out.println("No capacity entered. Skipping capacity assignment.");
            newRoomType.setCapacity(null); // Assuming capacity can be null in your RoomType entity
        } else {
            try {
                int capacity = Integer.parseInt(capacityInput); // Parse input as integer
                newRoomType.setCapacity(capacity);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input for capacity. Please enter a valid number.");
                return; // Exit or handle as needed
            }
        }

        System.out.print("Enter the Amenities in the Room Type> ");
        newRoomType.setAmenities(scanner.nextLine().trim());
        System.out.print("Enter Next Higher Room Type Name. Enter 'None' if there is no Higher Room Type> ");
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
                System.out.println("An unknown error has occurred while creating the new Room Type!: " + ex.getMessage() + "\n");
            }
            catch(InputDataValidationException ex)
            {
                System.out.println(ex.getMessage() + "\n");
            }
            catch (NumberFormatException ex) {
                System.out.println("Invalid input. Please enter a valid number.");
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
        input = scanner.nextLine().trim();

        if(input.length() > 0)
        {
           try {
                BigDecimal size = new BigDecimal(input);
                roomType.setSize(size);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }

        System.out.print("Enter Room Type Number of Beds (blank if no change)> ");
        input = scanner.nextLine().trim();
        if(input.length() > 0)
        {
            roomType.setBeds(Integer.parseInt(input));
        }

        System.out.print("Enter Room Type Capacity (blank if no change)> ");
        input = scanner.nextLine().trim();
        if(input.length() > 0)
        {
            roomType.setCapacity(Integer.parseInt(input));
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
            } catch (InputDataValidationException | RoomTypeExistException ex) {
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
                System.out.println("Room Type deleted successfully!\n");
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
            roomTypeSessionBeanRemote.retrieveRoomRatesForRoomType(roomType);
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

        roomTypeSessionBeanRemote.viewAllRoomTypeNames();
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
        String roomNumber = scanner.nextLine().trim();

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
            if(inputStr.length() > 0)
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
                    System.out.println("Room updated successfully!\n");
                } catch (InputDataValidationException | RoomExistException ex) {
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
        String roomNumber = scanner.nextLine().trim();

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
        System.out.printf("%8s%10s%20s%20s%24s%24s\n", "Room ID", "Room Number", "Room Status", "Room Type", "Is Room Disabled?", "Is Room Occupied?");

        for(Room room:roomList)
        {
            System.out.printf("%8s%10s%20s%20s%24s%24s\n", room.getRoomId().toString(), room.getRoomNumber().toString(), room.getRoomStatus(), room.getRoomType().getName(), room.getDisabled(), room.getIsOccupied());
        }

        System.out.print("Press any key to continue...> ");
        scanner.nextLine();
    }

    private void doViewRoomAllocationExceptionReport() {
        //try {
            System.out.println("*** HoR System :: Hotel Operation :: View Room Allocation Exception Report ***\n");
            Scanner scanner = new Scanner (System.in);
            Date reportDate;
            // SimpleDateFormat date = new SimpleDateFormat("dd/MM/yyyy");

            System.out.print("Enter the Report Date (MM/DD/YYYY)> ");
            reportDate = new Date(scanner.nextLine().trim());
            
            for( RoomAllocationExceptionReport report : roomAllocationExceptionReportSessionBeanRemote.viewReportsbyDate(reportDate)){
                System.out.println("\nRoom Allocation Exception Report ID: " + report.getRoomAllocationExceptionReportId());
                System.out.println("Room Allocation Exception Detail: " + report.getDetails());
                System.out.println("Reservation ID: " + report.getReservation().getReservationId());
                System.out.println("=================================================================");
            }
       // }
        /*
        catch (ParseException ex)
        {
            System.out.println("Invalid date input, please try again!");
        }*/
    }



    private void doAllocateRoomToCurrentDayReservations() {
        try {
            Scanner scanner = new Scanner (System.in);
            String inputDate = "";

            System.out.println("*** HoR System :: Hotel Operation :: Allocate Room ToReservations ***\n");

            Date checkinDate;
            // SimpleDateFormat date = new SimpleDateFormat("dd/MM/yyyy");

            System.out.print("Enter the Check-in Date (MM/DD/YYYY)> ");
            checkinDate = new Date(scanner.nextLine().trim());
            checkinDate.setTime(checkinDate.getTime() + (23 * 60 * 60 * 1000) + (59 * 60 * 1000));
            roomSessionBeanRemote.allocateRoomToReservation(checkinDate);
            System.out.println("Room allocated successfully.");
        }
        /*
        catch (ParseException ex)
        {
            System.out.println("Invalid date input, please try again!");
        }*/
        catch (ReservationNotFoundException | ReportExistException | NoReservationsFoundException ex)
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
        // try {
            Scanner scanner = new Scanner(System.in);
            RoomRate newRoomRate = new RoomRate();

            System.out.println("*** HoR System :: Hotel Operation :: Create New Room Rate ***\n");
            System.out.print("Enter Room Rate Name> ");
            newRoomRate.setName(scanner.nextLine().trim());

            while(true)
            {
                System.out.print("Select Room Rate Type (1: PUBLISHED, 2: NORMAL, 3: PEAK, 4: PROMOTION)> ");
                Integer rateTypeInt = scanner.nextInt();

                if(rateTypeInt >= 1 && rateTypeInt <= 4)
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
            // SimpleDateFormat date = new SimpleDateFormat("dd/MM/yyyy");

            //only peak and promotion have valid end and valid start!!! else dont need!!!
            if(newRoomRate.getRateType().equals(RateType.PEAK) || newRoomRate.getRateType().equals(RateType.PROMOTION)) {
                Date validityStart;
                Date validityEnd;
                while (true) {
                    System.out.print("Enter validity start date (MM/DD/YYYY)> ");
                    validityStart = new Date(scanner.nextLine().trim());
                    validityStart.setTime(validityStart.getTime() + (23 * 60 * 60 * 1000) + (59 * 60 * 1000));
                    
                    System.out.print("Enter validity end date (MM/DD/YYYY) ");
                    validityEnd = new Date(scanner.nextLine().trim());
                    validityEnd.setTime(validityEnd.getTime() + (23 * 60 * 60 * 1000) + (59 * 60 * 1000));

                    if (validityStart.before(new Date())) {
                        System.out.println("Validity Start Date cannot be a past date");
                    } else if (validityEnd.before(new Date())) {
                        System.out.println("Validity End Date cannot be a past date");
                    } else if (validityStart.after(validityEnd)) {
                        System.out.println("Validity Start Date cannot be after Validity End Date");
                    } else {
                        break;
                    }
                }
                
                newRoomRate.setValidityStart(validityStart);
                newRoomRate.setValidityEnd(validityEnd);
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
                catch(UnknownPersistenceException | RoomTypeNotFoundException ex)
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
        // }
        /*
        catch (ParseException ex) {
            System.out.println("Invalid date input, please try again!!\n");
        } */
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
                DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss z yyyy");
                DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy");

                System.out.println("Room Rate Validity Start Date: " +
                        ZonedDateTime.parse(roomRate.getValidityStart().toString(), inputFormatter).format(outputFormatter));
                System.out.println("Room Rate Validity End Date: " +
                        ZonedDateTime.parse(roomRate.getValidityEnd().toString(), inputFormatter).format(outputFormatter));
            }

            System.out.println("Is Room Rate Disabled? " + roomRate.getDisabled());
            System.out.println("What Room Type is the Room Rate associated to?: " + roomRate.getRoomType().getName());

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
        scanner.nextLine();

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

            System.out.print("Enter Room Rate per Night (input -1 if no change)> ");
            inputDec = scanner.nextBigDecimal();
            if(inputDec.compareTo(BigDecimal.ZERO) > 0)
            {
                updateRoomRate.setRatePerNight(inputDec);
            }
            scanner.nextLine();
            /*System.out.print("Enter Room Rate per Night (blank if no change)> ");
            inputDec = scanner.nextBigDecimal();
            if(input != null)
            {
                updateRoomRate.setRatePerNight(inputDec);
            }*/

            // SimpleDateFormat date = new SimpleDateFormat("dd/MM/yyyy");
            if(updateRoomRate.getRateType().equals(RateType.PEAK) || updateRoomRate.getRateType().equals(RateType.PROMOTION)) {
                while (true) {
                    System.out.print("Enter start date (MM/DD/YYYY) (blank if no change)> ");
                    String validityStart = scanner.nextLine().trim();
                    Date startDate = null;
                    if (validityStart.length() > 0) {
                        startDate = new Date(validityStart);
                        startDate.setTime(startDate.getTime() + (23 * 60 * 60 * 1000) + (59 * 60 * 1000));
                    }

                    System.out.print("Enter end date (MM/DD/YYYY) (blank if no change)> ");
                    String validityEnd = scanner.nextLine().trim();
                    Date endDate = null;
                    if (validityEnd.length() > 0) {
                        endDate = new Date(validityEnd);
                        endDate.setTime(endDate.getTime() + (23 * 60 * 60 * 1000) + (59 * 60 * 1000));
                    }

                    if (startDate != null && startDate.before(new Date())) {
                        System.out.println("Validity Start Date cannot be a past date");
                    } else if (endDate != null && endDate.before(new Date())) {
                        System.out.println("Validity End Date cannot be a past date");
                    } else if (startDate != null && endDate != null && startDate.after(endDate)) {
                        System.out.println("Validity Start Date cannot be after Validity End Date");
                    } else if (startDate != null && endDate == null && startDate.after(roomRate.getValidityEnd())) {
                        System.out.println("Validity Start Date cannot be after Validity End Date");
                    } else if (startDate == null && endDate != null && roomRate.getValidityStart().after(endDate)) {
                        System.out.println("Validity Start Date cannot be after Validity End Date");
                    } else {
                        if (validityStart.length() > 0) {
                            updateRoomRate.setValidityStart(startDate);
                        }
                        if (validityEnd.length() > 0) {
                            updateRoomRate.setValidityEnd(endDate);
                        }
                        break;
                    }
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
                    updateRoomRate.setRoomType(updateRoomType);
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
                } catch (InputDataValidationException | RoomRateNotFoundException | RoomRateExistException | PersistentContextException ex) {
                    System.out.println("An error occurred: " + ex.getMessage());
                }
            } else {
             showInputDataValidationErrorsForRoomRate(constraintViolations);
            }
        } catch (RoomRateNotFoundException ex) {
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
            System.out.printf("Confirm Delete Room Rate %s (Room Rate ID: %d) for Room Type %s (Enter 'Y' to Delete)> ", roomRate.getName(), roomRate.getRoomRateId(), roomRate.getRoomType().getName());
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
        // SimpleDateFormat date = new SimpleDateFormat("dd/MM/yyyy");
        System.out.println("*** HoR System :: Hotel Operation :: View All Room Rates ***\n");

        List<RoomRate> roomRateList = roomRateSessionBeanRemote.viewAllRoomRates();
        System.out.printf("%8s%40s%24s%24s%24s%24s%24s\n","Rate ID", "Rate Name", "Rate Type", "Rate Per Night","Validity Start Date", "Validity End Date", "Disabled");

        for(RoomRate roomRate:roomRateList)
        {
            String validStartDate = "";
            String validEndDate = "";

            //!! check validity for PEAK and PROMOTION!!!
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss z yyyy");
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy");

            if(roomRate.getRateType().equals(RateType.PEAK) || roomRate.getRateType().equals(RateType.PROMOTION)) {
                validStartDate = "" + ZonedDateTime.parse(roomRate.getValidityStart().toString(), inputFormatter).format(outputFormatter);
                validEndDate = "" + ZonedDateTime.parse(roomRate.getValidityEnd().toString(), inputFormatter).format(outputFormatter);
            }
            System.out.printf("%8s%40s%24s%24s%24s%24s%24s\n",roomRate.getRoomRateId(),roomRate.getName(), roomRate.getRateType(),
                    roomRate.getRatePerNight(), validStartDate, validEndDate, roomRate.getDisabled());
        }

        // System.out.print("Press any key to continue...> ");
        // scanner.nextLine();
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
