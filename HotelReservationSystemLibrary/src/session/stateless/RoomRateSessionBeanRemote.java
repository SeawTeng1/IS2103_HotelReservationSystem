/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionRemote.java to edit this template
 */
package session.stateless;

import entity.RoomRate;
import javax.ejb.Remote;
import util.exception.PersistentContextException;
import util.exception.RoomRateExistException;
import util.exception.RoomRateNotFoundException;
import util.exception.RoomTypeRemoveRoomRateException;

/**
 *
 * @author Toh Seaw Teng
 */
@Remote
public interface RoomRateSessionBeanRemote {

    public RoomRate createRoomRate(RoomRate roomRate) throws RoomRateExistException, PersistentContextException;

    public RoomRate retrieveRoomRateById(Long roomRateId) throws RoomRateNotFoundException;

    public RoomRate updateRoomRate(Long roomRateId, RoomRate roomRateUpdate) throws RoomRateNotFoundException;

    public void deleteRoomRate(Long roomRateId) throws RoomRateNotFoundException, RoomTypeRemoveRoomRateException;
    
}
