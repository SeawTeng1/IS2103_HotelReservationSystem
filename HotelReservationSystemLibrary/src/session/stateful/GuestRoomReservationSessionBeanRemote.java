/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionRemote.java to edit this template
 */
package session.stateful;

import entity.Room;
import entity.RoomRate;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.ejb.Remote;
import util.exception.AvailableRoomNotFoundException;
import util.exception.GuestAddReservationException;
import util.exception.GuestNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.RoomAddReservationException;
import util.exception.RoomRateAddReservationException;
import util.exception.RoomRateNotFoundException;
import util.exception.RoomTypeAddReservationException;

/**
 *
 * @author Toh Seaw Teng
 */
@Remote
public interface GuestRoomReservationSessionBeanRemote {
    
    public BigDecimal getTotalPrice(String roomType, Date checkInDate, Date checkOutDate, Integer numOfRoom) throws RoomRateNotFoundException, AvailableRoomNotFoundException;

    public RoomRate getCorrectRoomRate(String roomType, Date checkInDate, Date checkOutDate) throws RoomRateNotFoundException;

    public void onlineReserve(String roomType, Integer noOfRoom, Date checkInDate, Date checkOutDate, Long guestId) throws RoomRateNotFoundException, RoomTypeAddReservationException, RoomRateAddReservationException, GuestAddReservationException, RoomAddReservationException, GuestNotFoundException, InputDataValidationException;
    
    public List<Room> searchAvailableRoom(String roomType, Date checkInDate, Date checkOutDate) throws AvailableRoomNotFoundException;

    public List<Room> searchAvailableRoomWithLimit(String roomType, Date checkInDate, Date checkOutDate, Integer limit) throws AvailableRoomNotFoundException;
   
}
