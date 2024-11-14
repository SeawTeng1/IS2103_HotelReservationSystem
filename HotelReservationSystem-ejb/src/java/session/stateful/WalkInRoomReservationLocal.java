/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionLocal.java to edit this template
 */
package session.stateful;

import entity.Reservation;
import entity.Room;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;
import util.exception.AvailableRoomNotFoundException;
import util.exception.EmployeeAddReservationException;
import util.exception.EmployeeNotFoundException;
import util.exception.GuestAddReservationException;
import util.exception.GuestNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.ReservationAddRoomException;
import util.exception.ReservationExceedAvailableRoomNumberException;
import util.exception.ReservationNotFoundException;
import util.exception.RoomAddReservationException;
import util.exception.RoomCheckInException;
import util.exception.RoomCheckOutException;
import util.exception.RoomRateAddReservationException;
import util.exception.RoomRateNotFoundException;
import util.exception.RoomTypeAddReservationException;

/**
 *
 * @author Toh Seaw Teng
 */
@Local
public interface WalkInRoomReservationLocal {
    
    public List<Room> searchAvailableRoom(String roomType, Date checkInDate, Date checkOutDate) throws AvailableRoomNotFoundException;

    public Reservation walkInReserve (String roomType, Integer noOfRoom, Date checkInDate, Date checkOutDate, Long employeeId, Long guestId)
            throws RoomRateNotFoundException, RoomTypeAddReservationException, RoomRateAddReservationException, 
            EmployeeAddReservationException, AvailableRoomNotFoundException, RoomRateNotFoundException, ReservationAddRoomException, 
            RoomAddReservationException, InputDataValidationException, EmployeeNotFoundException, GuestNotFoundException, GuestAddReservationException, ReservationExceedAvailableRoomNumberException;

    public BigDecimal getTotalPrice(String roomType, Date checkInDate, Date checkOutDate, Integer numOfRoom) throws RoomRateNotFoundException, AvailableRoomNotFoundException;

    public List<Room> searchAvailableRoomWithLimit(String roomType, Date checkInDate, Date checkOutDate, Integer limit) throws AvailableRoomNotFoundException;

    public void checkOut(Long reservationId) throws ReservationNotFoundException, RoomCheckOutException;

    public Reservation checkIn(Long reservationId) throws ReservationNotFoundException, RoomCheckInException;
    
}
