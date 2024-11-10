/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package horsreservationclient;

import entity.Guest;
import entity.Reservation;
import entity.Room;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import session.stateful.GuestRoomReservationSessionBeanRemote;
import session.stateless.GuestSessionBeanRemote;
import util.exception.AvailableRoomNotFoundException;
import util.exception.GuestAddReservationException;
import util.exception.GuestExistException;
import util.exception.GuestNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.InvalidCredentialException;
import util.exception.PersistentContextException;
import util.exception.ReservationForGuestNotFoundException;
import util.exception.ReservationListForGuestNotFoundException;
import util.exception.RoomAddReservationException;
import util.exception.RoomRateAddReservationException;
import util.exception.RoomRateNotFoundException;
import util.exception.RoomTypeAddReservationException;

/**
 *
 * @author Toh Seaw Teng
 */
public class MainApp {
    private GuestSessionBeanRemote guestSessionBeanRemote;
    private GuestRoomReservationSessionBeanRemote guestRoomReservationSessionBeanRemote;
    private Guest guest;
    private static List<String> roomTypeList = Arrays.asList("Deluxe Room", "Premier Room", "Family Room", "Junior Suite", "Grand Suite");
    
    public MainApp() {
    }

    public MainApp(GuestSessionBeanRemote guestSessionBeanRemote, GuestRoomReservationSessionBeanRemote guestRoomReservationSessionBeanRemote) {
        this.guestSessionBeanRemote = guestSessionBeanRemote;
        this.guestRoomReservationSessionBeanRemote = guestRoomReservationSessionBeanRemote;
        this.guest = null;
    }
    
    public void runApp() {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while(true)
        {
            System.out.println("*** Welcome to HoRS Reservation System ***\n");
            System.out.println("1: Guest Login");
            System.out.println("2: Register as Guest");
            System.out.println("3: Exit\n");
            response = 0;
            
            while(response < 1 || response > 3)
            {
                System.out.print("> ");

                response = scanner.nextInt();

                if(response == 1)
                {
                    guestLogin();
                    
                    if (this.guest != null) {
                        reservationMenu();
                    }
                }
                else if(response == 2)
                {
                    registerAsGuest();
                }
                else if (response == 3)
                {
                    break;
                }
                else
                {
                    System.out.println("Invalid option, please try again!\n");                
                }
            }
            
            if(response == 3)
            {
                break;
            }
        }
    }

    public void guestLogin() {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("\n*** HoRS Reservation System : Guest Login ***\n");
        System.out.print("Enter Passport Number> ");
        String passportNumber = scanner.nextLine().trim();
        System.out.print("Enter Password> ");
        String password = scanner.nextLine().trim();
        
        try {
            this.guest = this.guestSessionBeanRemote.guestLogin(passportNumber, password);
            System.out.println("Guest successfully login!");
        } catch (GuestNotFoundException | InvalidCredentialException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void registerAsGuest() {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("\n*** HoRS Reservation System : Register as Guest ***\n");
        System.out.print("Enter Guest First Name> ");
        String firstName = scanner.nextLine().trim();
        System.out.print("Enter Guest Last Name> ");
        String lastName = scanner.nextLine().trim();
        System.out.print("Enter Passport Number> ");
        String passportNumber = scanner.nextLine().trim();
        System.out.print("Enter Password> ");
        String password = scanner.nextLine().trim();
        
        try {
            Guest guest = this.guestSessionBeanRemote.createGuest(new Guest(firstName, lastName, passportNumber, password));
            System.out.println("Guest Successfully Created with ID " + guest.getGuestId());
        } catch (PersistentContextException | GuestExistException | InputDataValidationException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void reservationMenu() {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while(true)
        {
            System.out.println("*** Welcome to HoRS Reservation System: Reservation Service ***\n");
            System.out.println("1: Reserve Hotel Room");
            System.out.println("2: View My Reservation Details");
            System.out.println("3: View All My Reservations");
            System.out.println("4: Exit\n");
            response = 0;
            
            while(response < 1 || response > 4)
            {
                System.out.print("> ");

                response = scanner.nextInt();

                if(response == 1)
                {
                    reserveHotel();
                }
                else if(response == 2)
                {
                    viewReservationDetail();
                }
                else if(response == 3)
                {
                    viewAllReservation();
                }
                else if (response == 4)
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

    public void reserveHotel() {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while(true)
        {
            System.out.println("*** Welcome to HoRS Reservation System: Reserve Hotel Room ***\n");
            System.out.println("1: Search and Reserve Hotel Room");
            System.out.println("2: Exit\n");
            response = 0;
            
            while(response < 1 || response > 2) {
                System.out.print("> ");
                response = scanner.nextInt();
                
                if(response == 1)
                {
                    searchReserveRoom();
                }
                else if(response == 2)
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
    
    public void searchReserveRoom() {
        // search room
        Scanner scanner = new Scanner(System.in);
        System.out.println("*** Welcome to HoRS Reservation System: Search and Reserve Hotel Room ***\n");
        // validate Date is future date
        
        Date checkInDate;
        Date checkOutDate;
        
        while (true) {
            System.out.print("Enter Check In Date (MM/DD/YYYY) >");
            checkInDate = new Date(scanner.nextLine().trim());
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
                List<Room> roomList = this.guestRoomReservationSessionBeanRemote.searchAvailableRoom(type, checkInDate, checkOutDate);
                if (!roomList.isEmpty()) {
                    BigDecimal roomRate = this.guestRoomReservationSessionBeanRemote.getTotalPrice(type, checkInDate, checkOutDate, 1);
                    System.out.println("Room Type: " + type);
                    System.out.print(" No of Room Available: " + roomList.size());
                    System.out.print(" Room Rate: $" + roomRate);
                }
                
                count++;
            } catch (AvailableRoomNotFoundException | RoomRateNotFoundException e) {
                continue;
            } 
        }
        
        if (count < 1) {
            System.out.println("No Available Room between " + checkInDate.toString() + " to " + checkOutDate.toString());
        } else {        
            Integer response = 0;
            while(true)
            {
                System.out.println("1: Reserve Hotel Room");
                System.out.println("2: Exit\n");
                response = 0;

                while(response < 1 || response > 2) {
                    System.out.print("> ");
                    response = scanner.nextInt();
                    if(response == 1)
                    {
                        System.out.println("*** Reserve Hotel Room between" + checkInDate.toString() + " to " + checkOutDate.toString() + " ***");
                        System.out.print("Enter Room Type> ");
                        String roomType = scanner.nextLine().trim();

                        // will just assume that user will not input 0
                        System.out.print("Enter No of Room> ");
                        Integer noOfRoom = scanner.nextInt();

                        try {
                            this.guestRoomReservationSessionBeanRemote.onlineReserve(
                                roomType, noOfRoom, checkInDate, checkOutDate, this.guest.getGuestId());

                            System.out.println("Room Successfully Reserved");
                        } catch (RoomRateNotFoundException | 
                                RoomTypeAddReservationException | 
                                RoomRateAddReservationException | 
                                GuestAddReservationException | 
                                RoomAddReservationException | 
                                GuestNotFoundException ex) {
                            System.out.println(ex.getMessage());
                        }
                    }
                    else if(response == 2)
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
    }

    public void viewAllReservation() {
        // get all
        System.out.println("*** Welcome to HoRS Reservation System: View All My Reservations ***\n");
        try {
            List<Reservation> reservationList = this.guestSessionBeanRemote.getReservationListByGuest(this.guest.getGuestId());
            
            if (!reservationList.isEmpty()) {
                System.out.println("*** Reservations Records ***\n");
                Integer count = 1;
                for (Reservation res : reservationList) {
                    System.out.print(count + ".");
                    System.out.print(" Reservation Id: " + res.getReservationId());
                    System.out.print(" Check In Date: " + res.getCheckInDate());
                    System.out.print(" Check Out Date: " + res.getCheckOutDate());
                    System.out.print(" Room Type: " + res.getRoomType().getName());
                    System.out.print(" No of Room: " + res.getCheckOutDate());
                    System.out.print(" Total Price: " + res.getTotalPrice() + "\n");
                }
            }
        } catch (ReservationListForGuestNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void viewReservationDetail() {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("\n*** HoRS Reservation System : View My Reservation Details ***\n");
        System.out.print("Enter Reservation Id> ");
        Long reservationId = scanner.nextLong();
        
        try {
            Reservation reservation = this.guestSessionBeanRemote.getReservationDetailByGuest(this.guest.getGuestId(), reservationId);
            
            if (reservation != null) {
                System.out.println("Reservation Id: " + reservation.getReservationId());
                System.out.println("Check In Date: " + reservation.getCheckInDate());
                System.out.println("Check Out Date: " + reservation.getCheckOutDate());
                System.out.println("Room Type: " + reservation.getRoomType().getName());
                System.out.println("No of Room: " + reservation.getCheckOutDate());
                System.out.println("Total Price: " + reservation.getTotalPrice());
            }
        } catch (ReservationForGuestNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
