/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package session.stateless;

import entity.Room;
import entity.RoomType;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import util.exception.RoomDeleteException;
import util.exception.RoomExistException;
import util.exception.RoomNotFoundException;
import util.exception.RoomTypeDisabledException;
import util.exception.RoomTypeExistException;
import util.exception.RoomTypeNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author priskilarebecca.p
 */
@Stateless
public class RoomSessionBean implements RoomSessionBeanRemote, RoomSessionBeanLocal {

    @EJB
    private RoomTypeSessionBeanLocal roomTypeSessionBean;

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    public void persist(Object object) {
        em.persist(object);
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    
    //12. Create New Room
    @Override
    public Room createNewRoom(Room newRoom, String roomTypeName) throws RoomTypeNotFoundException, UnknownPersistenceException, RoomExistException, RoomTypeDisabledException {
        try {
            RoomType roomType = roomTypeSessionBean.retrieveRoomTypebyName(roomTypeName);
            if(roomType.getDisabled()){ //first check if room type is disabled
                throw new RoomTypeDisabledException("This room type is disabled: " + roomTypeName);
            }
            newRoom.setRoomType(roomType);
            roomType.getRoomList().add(newRoom);
            em.persist(newRoom);
            em.flush();
        } catch(PersistenceException ex) {
            if(ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                if(ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                    throw new RoomExistException("Room Already Exists!");
                } else {
                    throw new UnknownPersistenceException(ex.getMessage());
                }
            } else {
                throw new UnknownPersistenceException(ex.getMessage());
            }
        }
        return newRoom;
    }
    
    
    //13. Update Room: update details of room record and/or status
    @Override
    public void updateRoom(Room room, Integer roomNumber) throws RoomNotFoundException {
        Room roomToUpdate = retrieveRoombyRoomNumber(roomNumber);
        if (roomToUpdate != null) {
            roomToUpdate.setRoomStatus(room.getRoomStatus());
            roomToUpdate.setRoomType(room.getRoomType());
            
        } else {
            throw new RoomNotFoundException("Room to be updated not found!");
        }
        
    }
    
    
    //14. Delete Room -- check if occupied --> if occupied mark as disabled, cannot be reserved anymore
    // when removing room you need to remove it from roomList of a roomType
    @Override
    public void deleteRoom(Long roomId) throws RoomNotFoundException, RoomDeleteException {
        Room roomToDelete = retrieveRoombyId(roomId);
        if (!roomToDelete.getIsOccupied()) { //room is not occupied
            roomToDelete.getRoomType().getRoomList().remove(roomToDelete);
            em.remove(roomToDelete);
        } else { //room is occupied
            roomToDelete.setDisabled(Boolean.TRUE);
            throw new RoomDeleteException("Room is occupied and cannot be deleted! Please not that the room is now disabled!");
        }
    }
    
    //15. View All Rooms
    @Override
    public List<Room> viewAllRooms() {
        Query query = em.createQuery("SELECT r FROM Room r");
        return query.getResultList();
    }
    
    //other methods:
    @Override
    public Room retrieveRoombyId(Long roomId) throws RoomNotFoundException {
        Room room = em.find(Room.class, roomId);
        if (room != null) {
            return room;
        } else {
            throw new RoomNotFoundException("Room does not exist: " + roomId);
        }
    }
    
    @Override
    public Room retrieveRoombyRoomNumber(Integer roomNumber) throws RoomNotFoundException {
        try {
            Query query = em.createQuery("SELECT r FROM Room r WHERE r.roomNumber = :inRoomNumber");
            query.setParameter("inRoomNumber", roomNumber);
            return (Room)query.getSingleResult();
        } catch (NoResultException ex) {
            throw new RoomNotFoundException("Room does not exist: " + roomNumber);
        }
    }
    
     
}
