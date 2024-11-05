/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionLocal.java to edit this template
 */
package session.stateless;

import entity.Room;
import entity.RoomRate;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;
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
@Local
public interface GuestRoomReservationSessionBeanLocal {

    public BigDecimal getTotalPrice(String roomType, Date checkInDate, Date checkOutDate, Integer numOfRoom) throws RoomRateNotFoundException, AvailableRoomNotFoundException;

    public RoomRate getCorrectRoomRate(String roomType, Date checkInDate, Date checkOutDate) throws RoomRateNotFoundException;

    public void onlineReserve(List<Room> selectedRoom, Date checkInDate, Date checkOutDate, BigDecimal total, Long guestId) throws RoomRateNotFoundException, RoomTypeAddReservationException, RoomRateAddReservationException, GuestAddReservationException, RoomAddReservationException, GuestNotFoundException;
    
}
