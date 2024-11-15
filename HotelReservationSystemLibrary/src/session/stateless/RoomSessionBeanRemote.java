/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionRemote.java to edit this template
 */
package session.stateless;

import entity.Room;
import java.util.Date;
import java.util.List;
import javax.ejb.Remote;
import util.exception.InputDataValidationException;
import util.exception.NoReservationsFoundException;
import util.exception.ReportExistException;
import util.exception.ReservationNotFoundException;
import util.exception.RoomDeleteException;
import util.exception.RoomExistException;
import util.exception.RoomNotFoundException;
import util.exception.RoomTypeDisabledException;
import util.exception.RoomTypeNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author priskilarebecca.p
 */
@Remote
public interface RoomSessionBeanRemote {
    public Room createNewRoom(Room newRoom, String roomTypeName) throws RoomTypeNotFoundException, UnknownPersistenceException, RoomExistException, RoomTypeDisabledException, InputDataValidationException;

    public void updateRoom(Room room) throws RoomNotFoundException, InputDataValidationException, RoomExistException;

    public void deleteRoom(Long roomId) throws RoomNotFoundException, RoomDeleteException;

    public List<Room> viewAllRooms();

    public Room retrieveRoombyId(Long roomId) throws RoomNotFoundException;

    public Room retrieveRoombyRoomNumber(String roomNumber) throws RoomNotFoundException;
    
    public void allocateRoomToReservation(Date checkinDate) throws ReservationNotFoundException, UnknownPersistenceException, InputDataValidationException, ReportExistException, NoReservationsFoundException;
}
