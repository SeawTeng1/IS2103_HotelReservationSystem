/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionLocal.java to edit this template
 */
package session.stateless;

import entity.Room;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author Toh Seaw Teng
 */
@Local
public interface WalkInRoomReservationLocal {

    public List<Room> searchAvailableRoom(String roomType, Date checkInDate, Date checkOutDate);

    public void walkInReserve(List<Room> selectedRoom, Date checkInDate, Date checkOutDate, BigDecimal total);

    public BigDecimal getTotalPrice(String roomType, Date checkInDate, Date checkOutDate, Integer numOfRoom);
    
}
