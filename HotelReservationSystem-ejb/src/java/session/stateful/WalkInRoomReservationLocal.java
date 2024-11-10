/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionLocal.java to edit this template
 */
package session.stateful;

import entity.Room;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;
import util.exception.AvailableRoomNotFoundException;
import util.exception.EmployeeAddReservationException;
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

    public void walkInReserve (List<Room> selectedRoom, Date checkInDate, Date checkOutDate, BigDecimal total, Long employeeId) throws RoomRateNotFoundException, RoomTypeAddReservationException, RoomRateAddReservationException, EmployeeAddReservationException;

    public BigDecimal getTotalPrice(String roomType, Date checkInDate, Date checkOutDate, Integer numOfRoom) throws RoomRateNotFoundException, AvailableRoomNotFoundException;

    public List<Room> searchAvailableRoomWithLimit(String roomType, Date checkInDate, Date checkOutDate, Integer limit) throws AvailableRoomNotFoundException;
    
}
