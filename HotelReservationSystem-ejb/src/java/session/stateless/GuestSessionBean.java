/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package session.stateless;


import entity.Guest;
import entity.Reservation;
import entity.Room;
import entity.RoomAllocationExceptionReport;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import util.exception.GuestExistException;
import util.exception.GuestNotFoundException;
import util.exception.InvalidCredentialException;
import util.exception.PersistentContextException;
import util.exception.ReservationListForGuestNotFoundException;

/**
 *
 * @author Toh Seaw Teng
 */
@Stateless
public class GuestSessionBean implements GuestSessionBeanRemote, GuestSessionBeanLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;
    
    /*
        1. Guest Login
    */
    @Override
    public Guest guestLogin(String passportNumber, String password) throws GuestNotFoundException, InvalidCredentialException {
        try {
            Guest guest = this.retrieveGuestByPassportNumber(passportNumber);
            
            if (guest.getPassword().equals(password)) {
                return guest;
            } else {
                throw new InvalidCredentialException("Invalid passport Number or password, please try again");
            }
        } catch (GuestNotFoundException ex) {
            throw new GuestNotFoundException("Passport Number " + passportNumber + " not found.");
        }
    }
    
    @Override
    public Guest retrieveGuestById(Long guestId) throws GuestNotFoundException {
        Guest guest = em.find(Guest.class, guestId);
        if (guest == null) {
            throw new GuestNotFoundException("Guest ID " + guestId + " not found.");
        }
        return guest;
    }
    
    @Override
    public Guest retrieveGuestByPassportNumber(String passportNumber) throws GuestNotFoundException {
        Guest guest = (Guest) em.createQuery("SELECT g FROM Guest g WHERE g.passportNumber = :passportNumber")
            .setParameter("passportNumber", passportNumber)
            .getSingleResult();
        if (guest == null) {
            throw new GuestNotFoundException("Passport Number " + passportNumber + " not found.");
        }
        return guest;
    }
    
    /*
        2. Register as Guest
    */
    
    @Override
    public Guest createGuest(Guest guest) throws PersistentContextException, GuestExistException{
        try {
            em.persist(guest);
            em.flush();
            return guest;
        } catch (PersistenceException e) {
            Long count = em.createQuery("SELECT COUNT(g) FROM Guest g WHERE g.passportNumber = :passportNumber", Long.class)
                .setParameter("passportNumber", guest.getPassportNumber())
                .getSingleResult();
            if (count > 0) {
                throw new GuestExistException("Guest with same passport number exist!");
            } else {
                 throw new PersistentContextException("Persistent Context issue " + e.getMessage());
            }
        }
    }
    
    @Override
    public List<Reservation> getReservationListByGuest(Long guestId) throws ReservationListForGuestNotFoundException {
        List<Reservation> reservationList = em.createQuery(
                "SELECT r FROM Reservation r WHERE r.guest.guestId = :guestId")
            .setParameter("guestId", guestId)
            .getResultList();
        
        if (reservationList.size() < 1) {
            throw new ReservationListForGuestNotFoundException("Reservation not found for this guest.");
        }
        
        return reservationList;
    }
 
    /*
        25. Check-in Guest
        Check-in a guest by informing him/her of the allocated room
        Room allocation exception needs to be handled manually
    */
    @Override
    public List<Room> guestCheckIn(Long guestId) throws ReservationListForGuestNotFoundException {
        Date today = new Date();
        List<Room> checkInRoom = new ArrayList<Room>();
        
        // get all the reservation for guest which will be check in today
        List<Reservation> reservationList = em.createQuery(
                "SELECT r FROM Reservation r WHERE r.guest.guestId = :guestId AND r.checkInDate = :today")
            .setParameter("guestId", guestId)
            .setParameter("today", today)
            .getResultList();
        
        if (reservationList.size() < 1) {
            throw new ReservationListForGuestNotFoundException("Reservation for guest ID " + guestId + " not found.");
        }
        
        for (Reservation res : reservationList) {
            List<Room> roomList = res.getRoomList();
            
            if (roomList.size() > 0) {
                for (Room room : roomList) {
                    room.setIsOccupied(Boolean.TRUE);
                    res.setIsCheckIn(Boolean.TRUE);
                    
                    checkInRoom.add(room);
                }
            }
        }
        
        return checkInRoom;
    }
    
    // check if there is any room exception 
    @Override
    public List<RoomAllocationExceptionReport> getRoomException(Long guestId) throws ReservationListForGuestNotFoundException {
        Date today = new Date();
        List<RoomAllocationExceptionReport> roomExceptionList = new ArrayList<RoomAllocationExceptionReport>();
        
        List<Reservation> reservationList = em.createQuery(
                "SELECT r FROM Reservation r WHERE r.guest.guestId = :guestId AND r.checkInDate = :today")
            .setParameter("guestId", guestId)
            .setParameter("today", today)
            .getResultList();
        
        if (reservationList.size() < 1) {
            throw new ReservationListForGuestNotFoundException("Reservation for guest ID " + guestId + " not found.");
        }
        
        for (Reservation res : reservationList) {
            if (res.getReport() != null) {
                roomExceptionList.add(res.getReport());
            }
        }
        
        return roomExceptionList;
    } 
    
    /*
        26. Check-out Guest
        Check-out a guest to indicate the end of his/her visit to the hotel.
    */
    @Override
    public void guestCheckOut(Long guestId) throws ReservationListForGuestNotFoundException, PersistentContextException {
        Date today = new Date();
        
        // get all the reservation for guest which will be check in today
        List<Reservation> reservationList = em.createQuery(
                "SELECT r FROM Reservation r WHERE r.guest.guestId = :guestId AND r.isCheckIn = TRUE AND r.isCheckOut = FALSE")
            .setParameter("guestId", guestId)
            .getResultList();
        
        if (reservationList.size() < 1) {
            throw new ReservationListForGuestNotFoundException("Reservation for guest ID " + guestId + " not found.");
        }
        
        try {
            for (Reservation res : reservationList) {
                List<Room> roomList = res.getRoomList();

                if (roomList.size() > 0) {
                    for (Room room : roomList) {
                        room.setIsOccupied(Boolean.FALSE);
                        res.setIsCheckOut(Boolean.TRUE);
                    }
                }
            }
        } catch (PersistenceException e) {
            throw new PersistentContextException("Persistent Context issue " + e.getMessage());
        }
        
    }
}
