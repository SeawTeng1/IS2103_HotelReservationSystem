/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionRemote.java to edit this template
 */
package session.stateless;

import entity.RoomType;
import java.util.List;
import javax.ejb.Remote;
import util.exception.RoomTypeExistException;
import util.exception.RoomTypeNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author priskilarebecca.p
 */
@Remote
public interface RoomTypeSessionBeanRemote {
        public List<RoomType> viewAllRoomTypes();

    public RoomType retrieveRoomTypebyId(Long roomTypeId) throws RoomTypeNotFoundException;

    public RoomType retrieveRoomTypebyName(String roomTypeName) throws RoomTypeNotFoundException;

    public void updateRoomType(RoomType roomType) throws RoomTypeNotFoundException;

    public RoomType createNewRoomType(RoomType newRoomType) throws RoomTypeExistException, UnknownPersistenceException;
}
