/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionLocal.java to edit this template
 */
package session.stateless;

import entity.Room;
import java.util.List;
import javax.ejb.Local;
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
@Local
public interface RoomSessionBeanLocal {

    public Room createNewRoom(Room newRoom, String roomTypeName) throws RoomTypeNotFoundException, UnknownPersistenceException, RoomExistException, RoomTypeDisabledException;

    public void updateRoom(Room room, Integer roomNumber) throws RoomNotFoundException;

    public void deleteRoom(Long roomId) throws RoomNotFoundException, RoomDeleteException;

    public List<Room> viewAllRooms();

    public Room retrieveRoombyId(Long roomId) throws RoomNotFoundException;

    public Room retrieveRoombyRoomNumber(Integer roomNumber) throws RoomNotFoundException;
    
}
