/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package session.stateless;

import entity.Employee;
import entity.RoomType;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import util.exception.RoomTypeExistException;
import util.exception.RoomTypeNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author priskilarebecca.p
 */
@Stateless
public class RoomTypeSessionBean implements RoomTypeSessionBeanRemote, RoomTypeSessionBeanLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    public void persist(Object object) {
        em.persist(object);
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    
    //7. Create New Room Type
    public RoomType createNewRoomType(RoomType newRoomType) throws RoomTypeExistException, UnknownPersistenceException {
        try { 
            em.persist(newRoomType);
            em.flush();
            
        } catch(PersistenceException ex) {
            if(ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                if(ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                    throw new RoomTypeExistException("Room Type Already Exists!");
                } else {
                    throw new UnknownPersistenceException(ex.getMessage());
                }
            } else {
                throw new UnknownPersistenceException(ex.getMessage());
            }
        }
        return newRoomType;
    }
    
    //8. View Room Type Details - handle in main
    
    //9. Update Room Type takes in the id of the room type you want to update to update the details of a particular existing room record with a new roomtype record
    @Override
    public void updateRoomType(RoomType roomType) throws RoomTypeNotFoundException {
        RoomType roomTypeUpdate = retrieveRoomTypebyId(roomType.getRoomTypeId()); //this is the "old" room type we intend to update
        if (roomTypeUpdate != null) { //if unchanged in main it will be blank and roomTypeUpdate's detail wont be updated?
            roomTypeUpdate.setName(roomType.getName());
            roomTypeUpdate.setDescription(roomType.getDescription());
            roomTypeUpdate.setSize(roomType.getSize());
            roomTypeUpdate.setBeds(roomType.getBeds());
            roomTypeUpdate.setCapacity(roomType.getCapacity());
            roomTypeUpdate.setAmenities(roomType.getAmenities());
            
        } else {
            throw new RoomTypeNotFoundException("Room Type to be updated not found!");
        }
    }
    
    //10. Delete Room Type
    //public void deleteRoomType(Long roomTypeId) throws RoomTypeNotFoundException {
        //RoomType roomTypeToDelete = retrieveRoomTypebyId(roomTypeId);
        
    //}
    
    //11. View All Room Types
    @Override
    public List<RoomType> viewAllRoomTypes() {
        Query query = em.createQuery("SELECT r FROM RoomType r");
        return query.getResultList();
    }
     
    //Other methods
    @Override
    public RoomType retrieveRoomTypebyId(Long roomTypeId) throws RoomTypeNotFoundException {
      RoomType roomType = em.find(RoomType.class, roomTypeId);
      if (roomType != null) {
          return roomType;
      } else {
          throw new RoomTypeNotFoundException("Room Type does not exist: " + roomTypeId);
      }
    }
    
    @Override
    public RoomType retrieveRoomTypebyName(String roomTypeName) throws RoomTypeNotFoundException {
        try{
            Query query = em.createQuery("SELECT r FROM RoomType r WHERE r.name = :inName ");
            query.setParameter("inName", roomTypeName);
            return (RoomType)query.getSingleResult();
        } catch (NoResultException ex) {
            throw new RoomTypeNotFoundException("Room Type does not exist: " + roomTypeName);
        }
    }
}
