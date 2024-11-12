/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionRemote.java to edit this template
 */
package session.singleton;

import entity.Room;
import java.util.Date;
import java.util.List;
import javax.ejb.Remote;
import util.exception.InputDataValidationException;
import util.exception.ReservationAddRoomException;
import util.exception.ReservationAddRoomExceptionItemException;
import util.exception.ReservationForTodayNotFoundException;
import util.exception.RoomAddReservationException;

/**
 *
 * @author Toh Seaw Teng
 */
@Remote
public interface RoomAllocationSessionBeanRemote {
    
    public void allocateReservationRoomDaily() throws ReservationForTodayNotFoundException, RoomAddReservationException, 
            ReservationAddRoomException, ReservationAddRoomExceptionItemException, InputDataValidationException;
    
}
