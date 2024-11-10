/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatefulEjbClass.java to edit this template
 */
package session.stateful;

import entity.Employee;
import entity.Reservation;
import entity.Room;
import entity.RoomRate;
import entity.RoomType;
import enumeration.RateType;
import enumeration.RoomStatus;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.AvailableRoomNotFoundException;
import util.exception.EmployeeAddReservationException;
import util.exception.RoomRateAddReservationException;
import util.exception.RoomRateNotFoundException;
import util.exception.RoomTypeAddReservationException;

/**
 *
 * @author Toh Seaw Teng
 */
@Stateful
public class WalkInRoomReservation implements WalkInRoomReservationRemote, WalkInRoomReservationLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    // 23. Walk-in Search Room
    /*
        get available room across all room types offered by the hotel according to the check-in date and check-out date
        reservation amount should be calculated based on the prevailing published rate
        need to check that there is enough inventory for new reservation for that room type
    */ 
    
    @Override
    public List<Room> searchAvailableRoom(String roomType, Date checkInDate, Date checkOutDate) throws AvailableRoomNotFoundException {
        List<Room> roomList = em.createQuery(
                "SELECT r FROM Room r WHERE (r.roomType.name = :roomType AND r.disabled = false) OR r.roomStatus != :roomStatus")
            .setParameter("roomType", roomType)
            .setParameter("roomStatus", RoomStatus.UNAVAILABLE)
            .getResultList();
        
        if (roomList.size() < 1) {
            throw new AvailableRoomNotFoundException("No available room found, please try again.");
        }
        
        List<Room> availableRoom = new ArrayList<Room>();
        for (Room r : roomList) {
            List<Reservation> resList = r.getReservationList();
            Reservation res = resList.get(resList.size() - 1);
            if (
                !res.getCheckOutDate().after(checkInDate) &&
                !res.getCheckInDate().before(checkOutDate)
            ) {
                if (!availableRoom.contains(r)) {
                    availableRoom.add(r);
                }
            }
        }
        
        // for inventory can just check if the number of room <= availableRoom.size()
        return availableRoom;
    }
    
    public List<Room> searchAvailableRoomWithLimit(String roomType, Date checkInDate, Date checkOutDate, Integer limit) throws AvailableRoomNotFoundException {
        Query query = em.createQuery(
                "SELECT r FROM Room r WHERE (r.roomType.name = :roomType AND r.disabled = false) OR r.roomStatus != 'UNAVAILABLE'")
            .setParameter("roomType", roomType);
                
        if (limit != 0) {
            query.setMaxResults(limit);
        }
        
        List<Room> roomList = query.getResultList();
        if (roomList.size() < 1) {
            throw new AvailableRoomNotFoundException("No available room found, please try again.");
        }
        
        List<Room> availableRoom = new ArrayList<Room>();
        for (Room r : roomList) {
            List<Reservation> resList = r.getReservationList();
            Reservation res = resList.get(resList.size() - 1);
            if (
                !res.getCheckOutDate().after(checkInDate) &&
                !res.getCheckInDate().before(checkOutDate)
            ) {
                if (!availableRoom.contains(r)) {
                    availableRoom.add(r);
                }
            }
        }
        
        // for inventory can just check if the number of room <= availableRoom.size()
        return availableRoom;
    }
    
    @Override
    public BigDecimal getTotalPrice(String roomType, Date checkInDate, Date checkOutDate, Integer numOfRoom) 
            throws RoomRateNotFoundException, AvailableRoomNotFoundException {
        BigDecimal total = new BigDecimal(0);
        
        RoomRate rate = (RoomRate) em.createQuery(
                "SELECT rr FROM RoomRate rr WHERE rr.roomType.name = :roomType AND rr.rateType = :rateType")
            .setParameter("roomType", roomType)
            .setParameter("rateType", RateType.PUBLISHED)
            .getSingleResult();
        
        if (rate == null) {
            throw new RoomRateNotFoundException("Published room rate for current room not found");
        }
        
        try {
            List<Room> availableRoom = this.searchAvailableRoom(roomType, checkInDate, checkOutDate);
            if (availableRoom.size() >= numOfRoom) {
                total = rate.getRatePerNight().multiply(new BigDecimal(numOfRoom));
            }
        } catch (AvailableRoomNotFoundException ex) {
            throw new AvailableRoomNotFoundException("No available room found, please try again.");
        }
        
        return total;
    }
    
    /*
        24. Walk-in Reserve Room
        walk-in guest can reserve more than one room.
        A walk-in guest does not need to be a registered guest of the hotel.
        For same day check-in, allocate the required room(s) immediately if reservation is made after 2 am.
    */
    @Override
    public void walkInReserve (List<Room> selectedRoom, Date checkInDate, Date checkOutDate, BigDecimal total, Long employeeId) 
            throws RoomRateNotFoundException, RoomTypeAddReservationException, RoomRateAddReservationException, EmployeeAddReservationException {
        Reservation reservation = new Reservation(checkInDate, checkOutDate, total, selectedRoom.size());
        em.persist(reservation);
        
        // Reservation: add roomList; roomType; roomRate;
        reservation.setRoomList(selectedRoom);
        reservation.setRoomType(selectedRoom.get(0).getRoomType());
        RoomRate rate = (RoomRate) em.createQuery(
                "SELECT rr FROM RoomRate rr WHERE rr.roomType.name = :roomType AND rr.rateType = :rateType")
            .setParameter("roomType", selectedRoom.get(0).getRoomType())
            .setParameter("rateType", RateType.PUBLISHED)
            .getSingleResult();
        
        if (rate == null) {
            throw new RoomRateNotFoundException("Published room rate for current room not found");
        }
        
        reservation.setRoomRate(rate);
        
        // Room: add reservationList;
        // Room Rate: add reservationList;
        // Room Type: reservationList
        // Employee: add reservation
        try {
            RoomType roomType = selectedRoom.get(0).getRoomType();
            roomType.addReservation(reservation);
            rate.addReservation(reservation);
            Employee employee = em.find(Employee.class, employeeId);
            employee.addReservation(reservation);
        } catch (RoomTypeAddReservationException ex) {
            throw new RoomTypeAddReservationException("Reservation already added to room type");
        } catch (RoomRateAddReservationException ex) {
            throw new RoomRateAddReservationException("Reservation already added to room rate");
        } catch (EmployeeAddReservationException ex) {
            throw new EmployeeAddReservationException("Reservation already added to Employee");
        }
        
        for (Room r : selectedRoom) {
            r.getReservationList().add(reservation);
        }
    }
}
