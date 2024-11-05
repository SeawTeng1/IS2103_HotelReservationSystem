/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package session.stateless;

import entity.Employee;
import entity.Guest;
import entity.Reservation;
import entity.Room;
import entity.RoomRate;
import entity.RoomType;
import enumeration.RateType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.exception.AvailableRoomNotFoundException;
import util.exception.GuestAddReservationException;
import util.exception.GuestNotFoundException;
import util.exception.RoomAddReservationException;
import util.exception.RoomRateAddReservationException;
import util.exception.RoomRateNotFoundException;
import util.exception.RoomTypeAddReservationException;

/**
 *
 * @author Toh Seaw Teng
 */
@Stateless
public class GuestRoomReservationSessionBean implements GuestRoomReservationSessionBeanRemote, GuestRoomReservationSessionBeanLocal {

    @EJB
    private WalkInRoomReservationLocal walkInRoomReservation;

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;
    
    /*
        3. Search Hotel Room
        Search an available room across all room types offered by the hotel according to the check-in date and check-out date.
        The reservation amount should be calculated based on the available prevailing rate of that room type.
        The system needs to ensure that the hotel has sufficient room inventory to fulfil the new reservation
    */
    // searchAvailableRoom can utilise the walkinroonreservation method
    @Override
    public BigDecimal getTotalPrice(String roomType, Date checkInDate, Date checkOutDate, Integer numOfRoom) 
            throws RoomRateNotFoundException, AvailableRoomNotFoundException {
        BigDecimal total = new BigDecimal(0);
        
        try {
            List<Room> availableRoom = walkInRoomReservation.searchAvailableRoom(roomType, checkInDate, checkOutDate);
            if (availableRoom.size() >= numOfRoom) {
                try {
                    RoomRate rate = this.getCorrectRoomRate(roomType, checkInDate, checkOutDate);
                    total = rate.getRatePerNight().multiply(new BigDecimal(numOfRoom));
                } catch (RoomRateNotFoundException e) {
                    throw new RoomRateNotFoundException("Room rate for current room not found");
                }
                
            }
        } catch (AvailableRoomNotFoundException ex) {
            throw new AvailableRoomNotFoundException("No available room found, please try again.");
        }
        
        return total;
    }
    
    /*
        get rate
    */
    @Override
    public RoomRate getCorrectRoomRate(String roomType, Date checkInDate, Date checkOutDate) throws RoomRateNotFoundException {
        // get rate type normal for this room type
        // then search for any room rate that have validity start and end within the check in and check out
        RoomRate normalRate = (RoomRate) em.createQuery(
                "SELECT rr FROM RoomRate rr WHERE rr.roomType.name = :roomType AND rr.rateType = :rateType")
            .setParameter("roomType", roomType)
            .setParameter("rateType", RateType.NORMAL)
            .getSingleResult();
        
        List<RoomRate> specialRateList = em.createQuery(
                "SELECT rr FROM RoomRate rr WHERE "
                        + "rr.roomType.name = :roomType AND (rr.rateType = :peakType OR rr.rateType = :promoType)"
                        + "AND rr.validityStart >= :checkInDate AND rr.validityEnd <= :checkOutDate"
            )
            .setParameter("roomType", roomType)
            .setParameter("peakType", RateType.PEAK)
            .setParameter("promoType", RateType.PROMOTION)
            .setParameter("checkInDate", checkInDate)
            .setParameter("checkOutDate", checkOutDate)
            .getResultList();
        
        if (normalRate == null) {
            throw new RoomRateNotFoundException("Room rate for current room not found");
        }
        
        RoomRate specialRate = null;
        if (specialRateList != null) {
            for (RoomRate rate : specialRateList) {
                if (specialRate == null || rate.getRateType().equals(RateType.PROMOTION)) {
                    specialRate = rate;
                }
            }
        }
        
        return specialRate != null ? specialRate : normalRate;
    }
    
    /*
        4. Reserve Hotel Room 
        Reserve a room offered in the search results (see use case 
        It is possible for a guest to reserve more than one room.
        For same day check-in, allocate the required room(s) immediately if reservation is made after 2 am.
    */
    
    @Override
    public void onlineReserve (List<Room> selectedRoom, Date checkInDate, Date checkOutDate, BigDecimal total, Long guestId) 
            throws RoomRateNotFoundException, RoomTypeAddReservationException, RoomRateAddReservationException, GuestAddReservationException, RoomAddReservationException, GuestNotFoundException {
        
        Reservation reservation = new Reservation(checkInDate, checkOutDate, total, selectedRoom.size());
        em.persist(reservation);
        
        // Reservation: add roomList; roomType; roomRate;
        // add roomList only if the reservation is same day check in and is after 2am
        LocalDateTime now = LocalDateTime.now();
        Date today = new Date();
        
        // get room rate
        RoomRate rate = this.getCorrectRoomRate(selectedRoom.get(0).getRoomType().getName(), checkInDate, checkOutDate);
        
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
            RoomType roomType = selectedRoom.get(0).getRoomType();
            roomType.addReservation(reservation);
            rate.addReservation(reservation);
            guest.addReservation(reservation);
        } catch (RoomTypeAddReservationException ex) {
            throw new RoomTypeAddReservationException("Reservation already added to room type");
        } catch (RoomRateAddReservationException ex) {
            throw new RoomRateAddReservationException("Reservation already added to room rate");
        } catch (GuestAddReservationException ex) {
            throw new GuestAddReservationException("Reservation already added to Employee");
        }
    }
}
