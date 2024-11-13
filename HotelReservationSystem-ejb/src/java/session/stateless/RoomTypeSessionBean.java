/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package session.stateless;

import entity.Employee;
import entity.Room;
import entity.RoomRate;
import entity.RoomType;
import java.util.List;
import java.util.Set;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.exception.InputDataValidationException;
import util.exception.RoomTypeDeleteException;
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

    private final ValidatorFactory validatorFactory;
    private final Validator validator;



    public RoomTypeSessionBean()
    {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")

    //7. Create New Room Type
    @Override
    public RoomType createNewRoomType(RoomType newRoomType, String higherRoomType) throws RoomTypeExistException, UnknownPersistenceException, InputDataValidationException, RoomTypeNotFoundException {
         Set<ConstraintViolation<RoomType>>constraintViolations = validator.validate(newRoomType);

        if(constraintViolations.isEmpty()) {
            try {
                if(!higherRoomType.equals("None")) {
                    newRoomType.setHigherRoomType(retrieveRoomTypebyName(higherRoomType));
                }
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
        } else {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
    }

    //8. View Room Type Details - handle in main

    //9. Update Room Type takes in the id of the room type you want to update to update the details of a particular existing room record with a new roomtype record
    @Override
    public void updateRoomType(RoomType roomType) throws RoomTypeNotFoundException, InputDataValidationException {
        if(roomType != null && roomType.getRoomTypeId() != null) {

            Set<ConstraintViolation<RoomType>>constraintViolations = validator.validate(roomType);

            if(constraintViolations.isEmpty()) {

                RoomType roomtypeToUpdate = retrieveRoomTypebyId(roomType.getRoomTypeId());

                    roomtypeToUpdate.setName(roomType.getName());
                    roomtypeToUpdate.setDescription(roomType.getDescription());
                    roomtypeToUpdate.setSize(roomType.getSize());
                    roomtypeToUpdate.setBeds(roomType.getBeds());
                    roomtypeToUpdate.setCapacity(roomType.getCapacity());
                    roomtypeToUpdate.setAmenities(roomType.getAmenities());
            } else {
                throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
            }

        } else {
            throw new RoomTypeNotFoundException("Room Type ID not provided for room type to be updated");
        }
    }

    //10. Delete Room Type
    @Override
    public void deleteRoomType(Long roomTypeId) throws RoomTypeNotFoundException, RoomTypeDeleteException {

        RoomType roomTypeToDelete = retrieveRoomTypebyId(roomTypeId);
        // check if any rooms of room type are occupied else if occupied disable room type
        Query query = em.createQuery("SELECT r FROM Room r WHERE r.roomType.roomTypeId = :inRoomType");
        query.setParameter("inRoomType", roomTypeId);
        if(query.getResultList().isEmpty() && roomTypeToDelete.getRoomRateList().isEmpty()) {
            // deleteRoomRank(roomTypeToDelete);
            // If no room = no reservtion, so don't need to cut the relationship
            em.remove(roomTypeToDelete);
        } else { //if some rooms are occupied set disabled
            roomTypeToDelete.setDisabled(Boolean.TRUE);
            throw new RoomTypeDeleteException("Room(s) of Room Type: " + roomTypeId + " are occupied! Please note that the Room Type is now disabled!");
        }
    }

    //11. View All Room Types
    @Override
    public List<RoomType> viewAllRoomTypes() {
        Query query = em.createQuery("SELECT rt FROM RoomType rt");
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
            Query query = em.createQuery("SELECT rt FROM RoomType rt WHERE rt.name = :inName");
            query.setParameter("inName", roomTypeName);
            RoomType roomType = (RoomType)query.getSingleResult();
            roomType.getHigherRoomType();
            roomType.getRoomRateList().size();
            return roomType;
        } catch (NoResultException ex) {
            throw new RoomTypeNotFoundException("Room Type does not exist: " + roomTypeName);
        }
    }


    //need to know how to insert new room rank and need to delete room rank if delete room, and update other room ranks!!!
    /*
    @Override
    public void insertNewRoomRank(Integer rank, RoomType newRoomType) {
        Query query = em.createQuery("SELECT rt FROM RoomType rt ORDER BY rt.roomRank ASC");
        List<RoomType> roomTypesRanked = query.getResultList();
        newRoomType.setRoomRank(rank);
        for (RoomType rt: roomTypesRanked) {
            if (rt.getRoomRank() >= rank) { //need to move up ranks of room types after rank that is inserted.
                rt.setRoomRank(rt.getRoomRank() + 1);
            }
        }
    }

    @Override
    public void deleteRoomRank(RoomType deleteRoomType) {
        List<RoomType> roomTypes = viewAllRoomTypes();
        for (RoomType rt: roomTypes) {
            if (rt.getRoomRank() >= deleteRoomType.getRoomRank()) {
                rt.setRoomRank(rt.getRoomRank() - 1);
            }
        }
    }
    */

    @Override
    public void retrieveRoomRatesForRoomType(RoomType roomType) {
        List<RoomRate> roomRateList = roomType.getRoomRateList();
            for (RoomRate roomRate : roomRateList) {
                System.out.println(" - " + roomRate.getName() + ": " + roomRate.getRatePerNight() + " dollars per night");
            }
    }

    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<RoomType>>constraintViolations)
    {
        String msg = "Input data validation error!:";

        for(ConstraintViolation constraintViolation:constraintViolations)
        {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }

        return msg;
    }
}
