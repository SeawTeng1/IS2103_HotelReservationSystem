/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package session.stateless;

import entity.Reservation;
import entity.RoomRate;
import entity.RoomType;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import util.exception.PersistentContextException;
import util.exception.RoomRateExistException;
import util.exception.RoomRateNotFoundException;
import util.exception.RoomTypeRemoveRoomRateException;

@Stateless
public class RoomRateSessionBean implements RoomRateSessionBeanRemote, RoomRateSessionBeanLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;
    
    // 17. Create New Room Rate
    @Override
    public RoomRate createRoomRate(RoomRate roomRate) throws RoomRateExistException, PersistentContextException
    {
        try {
            em.persist(roomRate);
            em.flush();
            return roomRate;
        } catch (PersistenceException e) {
            Long count = em.createQuery("SELECT COUNT(rr) FROM RoomRate rr WHERE rr.name = :name", Long.class)
                .setParameter("name", roomRate.getName())
                .getSingleResult();
            if (count > 0) {
                throw new RoomRateExistException("Room rate with same name exist!");
            } else {
                 throw new PersistentContextException("Persistent Context issue " + e.getMessage());
            }
        }
    }
    
    // 18. View Room Rate Details
    @Override
    public RoomRate retrieveRoomRateById(Long roomRateId) throws RoomRateNotFoundException {
        RoomRate roomRate = em.find(RoomRate.class, roomRateId);
        if (roomRate == null) {
            throw new RoomRateNotFoundException("Room Rate ID " + roomRateId + " not found.");
        }
        return roomRate;
    }
    
    // 19. Update Room Rate
    @Override
    public RoomRate updateRoomRate(Long roomRateId, RoomRate roomRateUpdate) throws RoomRateNotFoundException {
        RoomRate roomRate = em.find(RoomRate.class, roomRateId);
        if (roomRate == null) {
            throw new RoomRateNotFoundException("Room Rate ID " + roomRateId + " not found.");
        }
        
        if (roomRateUpdate != null) {
            roomRate.setName(roomRateUpdate.getName());
            roomRate.setRateType(roomRateUpdate.getRateType());
            roomRate.setRatePerNight(roomRateUpdate.getRatePerNight());
            roomRate.setValidityStart(roomRateUpdate.getValidityStart());
            roomRate.setValidityEnd(roomRateUpdate.getValidityEnd());
        }
        em.merge(roomRate);
        
        return roomRate;
    }
    
    
    // 20. Delete Room Rate
    @Override
    public void deleteRoomRate(Long roomRateId) throws RoomRateNotFoundException, RoomTypeRemoveRoomRateException {
        RoomRate roomRate = em.find(RoomRate.class, roomRateId);
        if (roomRate == null) {
            throw new RoomRateNotFoundException("Room Rate ID " + roomRateId + " not found.");
        }
        
        // if there is any reservation do not delete the room rate because it might still be access in the future
        if (!roomRate.getReservationList().isEmpty()) {
            roomRate.setDisabled(true);
            em.merge(roomRate);
        } else {
            //remove room rate from room type
            roomRate.getRoomType().removeRoomrate(roomRate);
            
            roomRate.setRoomType(null);
            em.remove(roomRate);
        }
    }
}
