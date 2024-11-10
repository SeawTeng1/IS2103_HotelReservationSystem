/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatefulEjbClass.java to edit this template
 */
package session.stateful;

import entity.Guest;
import entity.Partner;
import entity.Reservation;
import entity.Room;
import entity.RoomRate;
import entity.RoomType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.exception.AvailableRoomNotFoundException;
import util.exception.GuestAddReservationException;
import util.exception.GuestNotFoundException;
import util.exception.PartnerAddReservationException;
import util.exception.PartnerNotFoundException;
import util.exception.RoomAddReservationException;
import util.exception.RoomRateAddReservationException;
import util.exception.RoomRateNotFoundException;
import util.exception.RoomTypeAddReservationException;

/**
 *
 * @author Toh Seaw Teng
 */
@Stateful
public class PartnerRoomReservation implements PartnerRoomReservationRemote, PartnerRoomReservationLocal {
    
    @PersistenceContext()
    private EntityManager em;

    @EJB
    private GuestRoomReservationSessionBeanLocal guestRoomReservationSessionBeanLocal;
    
    /*
        3. Partner Reserve Room 
        Reserve a room offered in the search results
        It is possible to reserve more than one room.
        The reservation is done by a partner employee on behalf of a customer of the partner organisation.
        Such a reservation would not be visible in the HoRS Reservation Client when the partner customer login with 
        his/her HoRS guest account.
        For same day check-in, allocate the required room
    */
    @Override
    public void onlineReserve(String roomType, Integer noOfRoom, Date checkInDate, Date checkOutDate, Long partnerId, Long guestId) 
            throws RoomRateNotFoundException, RoomTypeAddReservationException, RoomRateAddReservationException, 
            PartnerAddReservationException, RoomAddReservationException, PartnerNotFoundException, 
            GuestNotFoundException, GuestAddReservationException, AvailableRoomNotFoundException {
        
        List<Room> selectedRoom = new ArrayList<Room>();
        try {
            selectedRoom = guestRoomReservationSessionBeanLocal.searchAvailableRoomWithLimit(roomType, checkInDate, checkOutDate, noOfRoom);
        } catch (AvailableRoomNotFoundException ex) {
            throw ex;
        }
        
        BigDecimal total = new BigDecimal(0);
        try {
            total = guestRoomReservationSessionBeanLocal.getTotalPrice(roomType, checkInDate, checkOutDate, noOfRoom);
        } catch (AvailableRoomNotFoundException ex) {
            throw ex;
        }
        
        Reservation reservation = new Reservation(checkInDate, checkOutDate, total, noOfRoom);
        em.persist(reservation);
        
        // Reservation: add roomList; roomType; roomRate;
        // add roomList only if the reservation is same day check in and is after 2am
        LocalDateTime now = LocalDateTime.now();
        Date today = new Date();
        
        // get room rate
        RoomRate rate = guestRoomReservationSessionBeanLocal.getCorrectRoomRate(selectedRoom.get(0).getRoomType().getName(), checkInDate, checkOutDate);
        
        Partner partner = em.find(Partner.class, partnerId);
        if (partner == null) {
            throw new PartnerNotFoundException("Partner ID " + partnerId + " not found.");
        }
        
        Guest guest = em.find(Guest.class, guestId);
        if (guest == null) {
            throw new GuestNotFoundException("Guest ID " + guestId + " not found.");
        }
        
        if (rate == null) {
            throw new RoomRateNotFoundException("Published room rate for current room not found");
        }
        
        if (today.equals(checkInDate) && 
                (now.toLocalTime().equals(LocalTime.of(2, 0)) || now.toLocalTime().isAfter(LocalTime.of(2, 0)))) {
            reservation.setRoomList(selectedRoom);
            for (Room r : selectedRoom) {
                r.addReservation(reservation);
            }
        }
        
        reservation.setRoomType(selectedRoom.get(0).getRoomType());
        reservation.setRoomRate(rate);
        
        // Room: add reservationList;
        // Room Rate: add reservationList;
        // Room Type: reservationList
        // Guest: add reservation
        try {
            RoomType type = selectedRoom.get(0).getRoomType();
            type.addReservation(reservation);
            rate.addReservation(reservation);
            partner.addReservation(reservation);
            guest.addReservation(reservation);
        } catch (RoomTypeAddReservationException ex) {
            throw new RoomTypeAddReservationException("Reservation already added to room type");
        } catch (RoomRateAddReservationException ex) {
            throw new RoomRateAddReservationException("Reservation already added to room rate");
        } catch (PartnerAddReservationException ex) {
            throw new PartnerAddReservationException("Reservation already added to Employee");
        }  catch (GuestAddReservationException ex) {
            throw new GuestAddReservationException("Reservation already added to Employee");
        }
    }
}
