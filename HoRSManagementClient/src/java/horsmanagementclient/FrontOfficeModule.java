/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package horsmanagementclient;

import entity.Employee;
import entity.Reservation;
import entity.Room;
import enumeration.RoleType;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import session.stateful.WalkInRoomReservationRemote;
import session.stateless.EmployeeSessionBeanRemote;
import session.stateless.GuestSessionBeanRemote;
import util.exception.AvailableRoomNotFoundException;
import util.exception.EmployeeAddReservationException;
import util.exception.EmployeeNotFoundException;
import util.exception.GuestAddReservationException;
import util.exception.GuestNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.InvalidAccessRightException;
import util.exception.ReservationAddRoomException;
import util.exception.ReservationNotFoundException;
import util.exception.RoomAddReservationException;
import util.exception.RoomCheckInException;
import util.exception.RoomCheckOutException;
import util.exception.RoomRateAddReservationException;
import util.exception.RoomRateNotFoundException;
import util.exception.RoomTypeAddReservationException;

/**
 *
 * @author priskilarebecca.p
 */
public class FrontOfficeModule {
    private EmployeeSessionBeanRemote employeeSessionBeanRemote;
    private GuestSessionBeanRemote GuestSessionBeanRemote;
    private WalkInRoomReservationRemote walkInRoomReservationRemote;
    private Employee currentEmployee;
    
    private static List<String> roomTypeList = Arrays.asList("Deluxe Room", "Premier Room", "Family Room", "Junior Suite", "Grand Suite");
    
    public FrontOfficeModule() {
    }

    public FrontOfficeModule(EmployeeSessionBeanRemote employeeSessionBeanRemote, GuestSessionBeanRemote GuestSessionBeanRemote, WalkInRoomReservationRemote walkInRoomReservationRemote, Employee currentEmployee) {
        this.employeeSessionBeanRemote = employeeSessionBeanRemote;
        this.GuestSessionBeanRemote = GuestSessionBeanRemote;
        this.walkInRoomReservationRemote = walkInRoomReservationRemote;
        this.currentEmployee = currentEmployee;
    }
    
    
    public void menuSystemAdministration() throws InvalidAccessRightException
    {
        if(currentEmployee.getRoleType() != RoleType.GUEST_RELATION_OFFICER)
        {
            throw new InvalidAccessRightException("You don't have GUEST_RELATION_OFFICER rights to access the system administration module.");
        }
        
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while(true)
        {
            System.out.println("*** HoRS Management System :: Front Office ***\n");
            System.out.println("1: Search and Reserve Hotel Room");
            System.out.println("2: Check-in Guest");
            System.out.println("3: Check-out Guest");
            System.out.println("3: Back\n");
            response = 0;
            
            while(response < 1 || response > 4)
            {
                System.out.print("> ");

                response = scanner.nextInt();

                if(response == 1)
                {
                    searchReserveRoom();
                }
                else if(response == 2)
                {
                    guestCheckIn();
                }
                else if(response == 3)
                {
                    guestCheckout();
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
    }
    
    public void searchReserveRoom() {
        // search room
        Scanner scanner = new Scanner(System.in);
        System.out.println("*** Welcome to HoRS Management System: Search Hotel Room ***\n");
        // validate Date is future 
        Date checkInDate;
        Date checkOutDate;
        
        while (true) {
            System.out.print("Enter Check In Date (MM/DD/YYYY) >");
            checkInDate = new Date(scanner.nextLine().trim());
            checkInDate.setTime(checkInDate.getTime() + (23 * 60 * 60 * 1000) + (59 * 60 * 1000));
            System.out.print("Enter Check Out Date (MM/DD/YYYY) > ");
            checkOutDate = new Date(scanner.nextLine().trim());
            
            // invalid
            if (checkInDate.before(new Date())) {
                System.out.println("Check In Date cannot be a past date");
            } else if (checkOutDate.before(new Date())) {
                System.out.println("Check Out Date cannot be a past date");
            } else if (checkInDate.after(checkOutDate)) {
                System.out.println("Check In Date cannot be after Check Out Date");
            } else {
                break;
            }
        }
        
        System.out.println("*** Available Room Types ***\n");
        Integer count = 0;
        for (String type : roomTypeList) {
            try {
                List<Room> roomList = this.walkInRoomReservationRemote.searchAvailableRoom(type, checkInDate, checkOutDate);
                if (!roomList.isEmpty()) {
                    BigDecimal roomRate = this.walkInRoomReservationRemote.getTotalPrice(type, checkInDate, checkOutDate, 1);
                    System.out.println("Room Type: " + type);
                    System.out.println("No of Room Available: " + roomList.size());
                    System.out.println("Room Rate: $" + roomRate);
                    System.out.println("-----------------------------------------------");
                }
                
                count++;
            } catch (AvailableRoomNotFoundException | RoomRateNotFoundException e) {
                continue;
            } 
        }
        
        if (count < 1) {
            System.out.println("No Available Room between " + checkInDate.toString() + " to " + checkOutDate.toString());
        } else {
            System.out.println("\"*** Welcome to HoRS Reservation System: Reserve Hotel Room ***\n");

            System.out.println("*** Reserve Hotel Room between" + checkInDate.toString() + " to " + checkOutDate.toString() + " ***");
            System.out.print("Enter Room Type> ");
            String roomType = scanner.nextLine().trim();

            // will just assume that user will not input 0
            System.out.print("Enter No of Room> ");
            Integer noOfRoom = scanner.nextInt();
            scanner.nextLine();
            
            System.out.print("Is the walk-in guest have registered In our system? (Y/N) > ");
            String registed = scanner.nextLine();
            Long guestId = null;
            if (registed.equals("Y")) {
                guestId = scanner.nextLong();
            }

            try {
                Reservation reservation = this.walkInRoomReservationRemote.walkInReserve(
                    roomType, noOfRoom, checkInDate, checkOutDate, currentEmployee.getEmployeeId(), guestId);

                System.out.println("Reservation Id: " + reservation.getReservationId() + " Successfully Created");
            } catch (RoomRateNotFoundException | 
                    RoomTypeAddReservationException | 
                    RoomRateAddReservationException | 
                    RoomAddReservationException | 
                    AvailableRoomNotFoundException |
                    InputDataValidationException | 
                    EmployeeNotFoundException |
                    EmployeeAddReservationException |
                    GuestNotFoundException |
                    GuestAddReservationException |
                    ReservationAddRoomException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    public void guestCheckIn() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("*** Welcome to HoRS Management System: Check In Guest ***\n");
        
        System.out.print("Enter Reservation Id> ");
        Long reservationId = scanner.nextLong();
        
        Reservation reservation = null;
        try {
            reservation = this.walkInRoomReservationRemote.checkIn(reservationId);
        } catch (ReservationNotFoundException | RoomCheckInException ex) {
            System.out.println(ex.getMessage());
        }
        
        if (reservation != null) {
            System.out.println("*** Reservation ***");
            System.out.println("Reservation Id: " + reservation.getReservationId());
            System.out.println("Check In Date: " + reservation.getCheckInDate());
            System.out.println("Check Out Date: " + reservation.getCheckOutDate());
            System.out.println("Room Type: " + reservation.getRoomType().getName());
            System.out.println("No of Room: " + reservation.getNumOfRoom());
            System.out.println("Total Price: " + reservation.getTotalPrice());
            
            if (!reservation.getRoomList().isEmpty()) {
                System.out.println("*** Check In Room(s) ***");
                for (Room room : reservation.getRoomList()) {
                    System.out.println("Room Number: " + room.getRoomNumber());
                    System.out.println("Room Type: " + room.getRoomType());
                    System.out.println("-----------------------------------------------");
                }
            }
            
            if (reservation.getReport() != null) {
                System.out.println("Room Allocation Exception");
                System.out.println(reservation.getReport());
            }
        }
    }
    
    public void guestCheckout() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("*** Welcome to HoRS Management System:  Check Out Guest ***\n");
        
        System.out.print("Enter Reservation Id> ");
        Long reservationId = scanner.nextLong();
        
        try {
            this.walkInRoomReservationRemote.checkOut(reservationId);
            System.out.println("Check Out Successfully");
        } catch (ReservationNotFoundException | RoomCheckOutException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
