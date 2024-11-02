/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionLocal.java to edit this template
 */
package session.stateless;

import entity.RoomRate;
import javax.ejb.Local;
import util.exception.PersistentContextException;
import util.exception.RoomRateExistException;
import util.exception.RoomRateNotFoundException;
import util.exception.RoomTypeRemoveRoomRateException;

/**
 *
 * @author Toh Seaw Teng
 */
@Local
public interface RoomRateSessionBeanLocal {

    public RoomRate createRoomRate(RoomRate roomRate) throws RoomRateExistException, PersistentContextException;

    public RoomRate retrieveRoomRateById(Long roomRateId) throws RoomRateNotFoundException;

    public RoomRate updateRoomRate(Long roomRateId, RoomRate roomRateUpdate) throws RoomRateNotFoundException, PersistentContextException;

    public void deleteRoomRate(Long roomRateId) throws RoomRateNotFoundException, RoomTypeRemoveRoomRateException, PersistentContextException;
    
}
