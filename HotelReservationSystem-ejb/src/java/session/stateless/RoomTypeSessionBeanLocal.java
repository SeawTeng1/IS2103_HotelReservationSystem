/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionLocal.java to edit this template
 */
package session.stateless;

import entity.RoomType;
import java.util.List;
import javax.ejb.Local;
import util.exception.InputDataValidationException;
import util.exception.RoomTypeDeleteException;
import util.exception.RoomTypeExistException;
import util.exception.RoomTypeNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author priskilarebecca.p
 */
@Local
public interface RoomTypeSessionBeanLocal {



    public List<RoomType> viewAllRoomTypes();

    public RoomType retrieveRoomTypebyId(Long roomTypeId) throws RoomTypeNotFoundException;

    public RoomType retrieveRoomTypebyName(String roomTypeName) throws RoomTypeNotFoundException;
    
    public RoomType createNewRoomType(RoomType newRoomType, String higherRoomType) throws RoomTypeExistException, UnknownPersistenceException, InputDataValidationException, RoomTypeNotFoundException;

    public void updateRoomType(RoomType roomType) throws RoomTypeNotFoundException, InputDataValidationException, RoomTypeExistException;
    
    public void deleteRoomType(Long roomTypeId) throws RoomTypeNotFoundException, RoomTypeDeleteException;

    //public void insertNewRoomRank(Integer rank, RoomType newRoomType);

    // public void deleteRoomRank(RoomType deleteRoomType);
    
    public void viewAllRoomTypeNames();

    public List<String> getAllRoomTypeNames();

}
