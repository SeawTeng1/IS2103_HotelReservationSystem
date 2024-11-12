/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package holidayreservationsystemclient;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Scanner;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import ws.partner.PartnerWebService_Service;
import ws.partner.Reservation;
import ws.partner.Room;

/**
 *
 * @author Toh Seaw Teng
 */
public class MainApp {
    private ws.partner.Partner partner;
    private PartnerWebService_Service service;
    private static List<String> roomTypeList = Arrays.asList("Deluxe Room", "Premier Room", "Family Room", "Junior Suite", "Grand Suite");

    public MainApp() {
        this.service = new PartnerWebService_Service();
    }
    
    public void runApp() {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while(true)
        {
            System.out.println("*** Welcome to Holiday Reservation System ***\n");
            System.out.println("1: Partner Login");
            System.out.println("2: Search Hotel Room");
            System.out.println("3: Exit\n");
            response = 0;
            
            while(response < 1 || response > 3)
            {
                System.out.print(" > ");

                response = scanner.nextInt();

                if(response == 1)
                {
                    this.partnerLogin();
                    
                    if (this.partner != null) {
                        reservationMenu();
                    }
                }
                else if (response == 2)
                {
                    searchReserveRoom();
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
    
    public void partnerLogin() {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("\n*** Holiday Reservation System : Partner Login ***\n");
        System.out.print("Enter Username > ");
        String username = scanner.nextLine().trim();
        System.out.print("Enter Password > ");
        String password = scanner.nextLine().trim();
        
        try {
            this.partner = this.service.getPartnerWebServicePort().partnerLogin(username, password);
            System.out.println("Guest successfully login!");
        } catch (ws.partner.PartnerInvalidPasswordException_Exception | ws.partner.PartnerNotFoundException_Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    public void reservationMenu() {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while(true)
        {
            System.out.println("*** Welcome to Holiday Reservation System: Reservation Service ***\n");
            System.out.println("1: Search and Reserve Hotel Room");
            System.out.println("2: View Partner Reservation Details");
            System.out.println("3: View All Partner Reservations");
            System.out.println("4: Exit\n");
            response = 0;
            
            while(response < 1 || response > 4)
            {
                System.out.print(" > ");

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
            System.out.println("*** Welcome to Holiday Reservation System: Reserve Hotel Room ***\n");
            System.out.println("1: Search and Reserve Hotel Room");
            System.out.println("2: Exit\n");
            response = 0;
            
            while(response < 1 || response > 2) {
                System.out.print(" > ");
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
        if (this.partner != null) {
            System.out.println("*** Welcome to Holiday Reservation System: Search and Reserve Hotel Room ***\n");
        } else {
            System.out.println("*** Welcome to Holiday Reservation System: Search Hotel Room ***\n");
        }
        // validate Date is future date
        
        Date checkInDate;
        Date checkOutDate;
        
        while (true) {
            System.out.print("Enter Check In Date (MM/DD/YYYY) >");
            checkInDate = new Date(scanner.nextLine().trim());
            checkInDate.setTime(checkInDate.getTime() + (23 * 60 * 60 * 1000) + (59 * 60 * 1000));
            System.out.print("Enter Check Out Date (MM/DD/YYYY) > ");
            checkOutDate = new Date(scanner.nextLine().trim());
            
            // invalid
            if (checkInDate.before(new Date()) && !checkInDate.equals(checkOutDate)) {
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
                List<Room> roomList = this.service.getPartnerWebServicePort().searchAvailableRoom(
                        type, convertDateToXMLGregorianCalendar(checkInDate), convertDateToXMLGregorianCalendar(checkOutDate));
                if (!roomList.isEmpty()) {
                    BigDecimal roomRate = this.service.getPartnerWebServicePort().getTotalPrice(
                            type, convertDateToXMLGregorianCalendar(checkInDate), convertDateToXMLGregorianCalendar(checkOutDate));
                    System.out.println("Room Type: " + type);
                    System.out.println("No of Room Available: " + roomList.size());
                    System.out.println("Room Rate: $" + roomRate);
                    System.out.println("-----------------------------------------------");
                }
                
                count++;
            } catch (ws.partner.AvailableRoomNotFoundException_Exception | ws.partner.RoomRateNotFoundException_Exception e) {
                continue;
            } 
        }
        
        if (count < 1) {
            System.out.println("No Available Room between " + checkInDate.toString() + " to " + checkOutDate.toString());
        } else {
            if (this.partner != null) {
                System.out.println("*** Reserve Hotel Room between" + checkInDate.toString() + " to " + checkOutDate.toString() + " ***");
                System.out.print("Enter Room Type> ");
                String roomType = scanner.nextLine().trim();

                // will just assume that user will not input 0
                System.out.print("Enter No of Room> ");
                Integer noOfRoom = scanner.nextInt();

                System.out.print("Enter Guest Id >");
                Long guestId = scanner.nextLong();

                try {
                    Reservation reservation = this.service.getPartnerWebServicePort().onlineReserve(
                        roomType, noOfRoom, 
                            convertDateToXMLGregorianCalendar(checkInDate), 
                            convertDateToXMLGregorianCalendar(checkOutDate), this.partner.getPartnerId(), guestId);

                    System.out.println("Reservation Id: " + reservation.getReservationId() + " Successfully Created");
                } catch (ws.partner.RoomRateNotFoundException_Exception | 
                        ws.partner.RoomTypeAddReservationException_Exception | 
                        ws.partner.RoomRateAddReservationException_Exception | 
                        ws.partner.PartnerAddReservationException_Exception | 
                        ws.partner.RoomAddReservationException_Exception | 
                        ws.partner.PartnerNotFoundException_Exception |
                        ws.partner.AvailableRoomNotFoundException_Exception |
                        ws.partner.GuestAddReservationException_Exception |
                        ws.partner.GuestNotFoundException_Exception ex) {
                    System.out.println(ex.getMessage());
                }
            } else {
                 System.out.println("Please login to reserve the available room.");
            }
        }
    }

    public XMLGregorianCalendar convertDateToXMLGregorianCalendar(Date date) {
        XMLGregorianCalendar xmlGregorianCalendar = null;
        try {
            GregorianCalendar gregorianCalendar = new GregorianCalendar();
            gregorianCalendar.setTime(date);
            xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        }
        return xmlGregorianCalendar;
    }
    
    public void viewReservationDetail() {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("\n*** Holiday Reservation System : View Partner Reservation Details ***\n");
        System.out.print("Enter Reservation Id > ");
        Long reservationId = scanner.nextLong();
        
        try {
            Reservation reservation = this.service.getPartnerWebServicePort().getReservationDetailByPartner(this.partner.getPartnerId(), reservationId);
            
            if (reservation != null) {
                System.out.println("Reservation Id: " + reservation.getReservationId());
                System.out.println("Guest Id: " + reservation.getGuest().getGuestId());
                System.out.println("Check In Date: " + reservation.getCheckInDate());
                System.out.println("Check Out Date: " + reservation.getCheckOutDate());
                System.out.println("Room Type: " + reservation.getRoomType().getName());
                System.out.println("No of Room: " + reservation.getNumOfRoom());
                System.out.println("Total Price: " + reservation.getTotalPrice());
            }
        } catch (ws.partner.ReservationForPartnerNotFoundException_Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void viewAllReservation() {
        System.out.println("*** Welcome to Holiday Reservation System: View All Partner Reservations ***\n");
        try {
            List<Reservation> reservationList = this.service.getPartnerWebServicePort().getReservationListByPartner(this.partner.getPartnerId());
            
            if (!reservationList.isEmpty()) {
                System.out.println("*** Reservations Records ***\n");
                for (Reservation res : reservationList) {
                    System.out.println(" Reservation Id: " + res.getReservationId());
                    System.out.println("Guest Id: " + res.getGuest().getGuestId());
                    System.out.println("Check In Date: " + res.getCheckInDate());
                    System.out.println(" Check Out Date: " + res.getCheckOutDate());
                    System.out.println("Room Type: " + res.getRoomType().getName());
                    System.out.println("No of Room: " + res.getNumOfRoom());
                    System.out.println("Total Price: " + res.getTotalPrice());
                    System.out.println("-----------------------------------------------");
                }
            }
        } catch (ws.partner.ReservationListForPartnerNotFoundException_Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}
