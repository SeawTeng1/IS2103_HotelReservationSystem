/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionRemote.java to edit this template
 */
package session.stateless;

import entity.Guest;
import entity.Reservation;
import entity.Room;
import entity.RoomAllocationExceptionReport;
import java.util.List;
import javax.ejb.Remote;
import util.exception.GuestExistException;
import util.exception.GuestNotFoundException;
import util.exception.InvalidCredentialException;
import util.exception.PersistentContextException;
import util.exception.ReservationListForGuestNotFoundException;

/**
 *
 * @author Toh Seaw Teng
 */
@Remote
public interface GuestSessionBeanRemote {
    
    public Guest guestLogin(String passportNumber, String password) throws GuestNotFoundException, InvalidCredentialException;

    public Guest retrieveGuestById(Long guestId) throws GuestNotFoundException;

    public Guest retrieveGuestByPassportNumber(String passportNumber) throws GuestNotFoundException;

    public Guest createGuest(Guest guest) throws PersistentContextException, GuestExistException;

    public List<Reservation> getReservationListByGuest(Long guestId) throws ReservationListForGuestNotFoundException;

    public List<Room> guestCheckIn(Long guestId) throws ReservationListForGuestNotFoundException;

    public void guestCheckOut(Long guestId) throws ReservationListForGuestNotFoundException, PersistentContextException;

    //public List<ExceptionItem> getRoomException(Long guestId) throws ReservationListForGuestNotFoundException;
    public List<RoomAllocationExceptionReport> getRoomException(Long guestId) throws ReservationListForGuestNotFoundException;
    
}
