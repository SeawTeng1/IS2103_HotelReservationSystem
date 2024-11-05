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
@Local
public interface PartnerRoomReservationLocal {

    public void onlineReserve(List<Room> selectedRoom, Date checkInDate, Date checkOutDate, BigDecimal total, Long partnerId, Long guestId) throws RoomRateNotFoundException, RoomTypeAddReservationException, RoomRateAddReservationException, PartnerAddReservationException, RoomAddReservationException, PartnerNotFoundException, GuestNotFoundException, GuestAddReservationException;
    
}
