/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionLocal.java to edit this template
 */
package session.singleton;

import javax.ejb.Local;
import util.exception.ReservationAddRoomException;
import util.exception.ReservationAddRoomExceptionItemException;
import util.exception.ReservationForTodayNotFoundException;
import util.exception.RoomAddReservationException;

/**
 *
 * @author Toh Seaw Teng
 */
@Local
public interface RoomAllocationSessionBeanLocal {

    public void allocateReservationRoomDaily() throws ReservationForTodayNotFoundException, RoomAddReservationException, ReservationAddRoomException, ReservationAddRoomExceptionItemException;
    
}
