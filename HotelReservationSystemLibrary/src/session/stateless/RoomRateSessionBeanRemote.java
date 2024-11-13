/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionRemote.java to edit this template
 */
package session.stateless;

import entity.RoomRate;
import java.util.List;
import javax.ejb.Remote;
import util.exception.InputDataValidationException;
import util.exception.PersistentContextException;
import util.exception.RoomRateExistException;
import util.exception.RoomRateNotFoundException;
import util.exception.RoomTypeRemoveRoomRateException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author Toh Seaw Teng
 */
@Remote
public interface RoomRateSessionBeanRemote {

    //public RoomRate createRoomRate(RoomRate roomRate) throws RoomRateExistException, PersistentContextException;

    public RoomRate retrieveRoomRateById(Long roomRateId) throws RoomRateNotFoundException;

    public void updateRoomRate(Long roomRateId, RoomRate roomRateUpdate) throws RoomRateNotFoundException, PersistentContextException, InputDataValidationException;

    public void deleteRoomRate(Long roomRateId) throws RoomRateNotFoundException, RoomTypeRemoveRoomRateException, PersistentContextException;

    public RoomRate createRoomRate(RoomRate roomRate, String roomTypeName) throws RoomRateExistException,  UnknownPersistenceException, InputDataValidationException;

    public List<RoomRate> viewAllRoomRates();
    
}
