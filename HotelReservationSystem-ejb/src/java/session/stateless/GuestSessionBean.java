/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package session.stateless;

import entity.Guest;
import entity.Reservation;
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
    
     /*
        26. Check-out Guest
        Check-out a guest to indicate the end of his/her visit to the hotel.
    */
}
