/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package session.stateless;

import entity.Reservation;
import entity.Room;
import entity.RoomAllocationExceptionReport;
import entity.RoomType;
import enumeration.RoomStatus;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
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
import util.exception.ReportExistException;
import util.exception.ReservationNotFoundException;
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
    private RoomAllocationExceptionReportSessionBeanLocal roomAllocationExceptionReportSessionBeanLocal;

    @EJB
    private RoomTypeSessionBeanLocal roomTypeSessionBean;
    
    private final ValidatorFactory validatorFactory;
    private final Validator validator;
    
    public RoomSessionBean()
    {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    public void persist(Object object) {
        em.persist(object);
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    
    //12. Create New Room
    @Override
    public Room createNewRoom(Room newRoom, String roomTypeName) throws RoomTypeNotFoundException, UnknownPersistenceException, RoomExistException, RoomTypeDisabledException, InputDataValidationException {
        Set<ConstraintViolation<Room>>constraintViolations = validator.validate(newRoom);
        
        if(constraintViolations.isEmpty())
        {
            try {
                RoomType roomType = roomTypeSessionBean.retrieveRoomTypebyName(roomTypeName);
                if(roomType.getDisabled()){ //first check if room type is disabled
                    throw new RoomTypeDisabledException("This room type is disabled: " + roomTypeName);
                }
                newRoom.setRoomType(roomType);
                em.persist(newRoom);
                em.flush();
                roomType.getRoomList().add(newRoom);
                return newRoom;
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
        } else {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
    }
    
    
    //13. Update Room: update details of room record and/or status
    //@Override
    public void updateRoom(Room room) throws RoomNotFoundException, InputDataValidationException {
        {
        if(room != null && room.getRoomId()!= null)
        {
            Set<ConstraintViolation<Room>>constraintViolations = validator.validate(room);
        
            if(constraintViolations.isEmpty())
            {
                Room roomUpdate = retrieveRoombyId(room.getRoomId());

                roomUpdate.setRoomNumber(room.getRoomNumber());
                roomUpdate.setRoomStatus(room.getRoomStatus());

            } else {
                throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
            }
        } else {
            throw new RoomNotFoundException("Room ID not provided for room to be updated");
        }
    } }
    
    
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
    
     public void allocateRoomToReservation(Date checkinDate) throws ReservationNotFoundException, UnknownPersistenceException, InputDataValidationException, ReportExistException {
        
        Query query = em.createQuery("SELECT res FROM Reservation res WHERE res.checkinDate = :inCheckinDate");
        query.setParameter("inCheckinDate", checkinDate);
        
        List<Reservation> reservations = (List<Reservation>) query.getResultList();
        
        for(Reservation reservation : reservations) {
            
            // Check if reservation is already allocated by checking the size of its rooms list.
            // if size > 0, then it means rooms have been allocated to this reservation, and thus will not go through allocation process again
            Integer currRoomListSize = reservation.getRoomList().size();
            if(currRoomListSize == 0) 
            {
                Integer numberOfRooms = reservation.getNumOfRoom();
                List<Room> assignedRooms = new ArrayList<>();
                RoomType roomType = reservation.getRoomType();
                RoomType higherRoomType = roomType.getHigherRoomType();

                Query queryRoom = em.createQuery("SELECT r FROM Room r WHERE r.roomType = :inRoomType AND r.roomStatus = :inStatus AND r.disabled = :inDisabled");
                queryRoom.setParameter("inRoomType", roomType);
                queryRoom.setParameter("inStatus", RoomStatus.AVAILABLE);
                queryRoom.setParameter("inDisabled", false);

                // roomsQuery 
                List<Room> roomsTemp = (List<Room>) queryRoom.getResultList();
                List<Room> roomsQuery = new ArrayList<>();
                for(Room room: roomsTemp) {
                    if(checkRoomAvail(room)) {
                        roomsQuery.add(room);
                    }
                }
                //

                // enough room of desired type for reservation
                if(roomsQuery.size() >= numberOfRooms) 
                {
                    for(Room room: roomsQuery)
                    {
                        assignedRooms.add(room);
                        if(assignedRooms.size() == numberOfRooms) 
                        {
                            break;
                        }
                    }

                } 
                else if(roomsQuery.size() < numberOfRooms && higherRoomType != null) 
                {
                    // obtain list of all free room of the next higher type
                    Query queryRoomHigher = em.createQuery("SELECT r FROM Room r WHERE r.roomType = :inRoomType AND r.roomStatus = :inStatus AND r.disabled = :inDisabled");
                    queryRoomHigher.setParameter("inRoomType", higherRoomType);
                    queryRoomHigher.setParameter("inStatus", RoomStatus.AVAILABLE);
                    queryRoomHigher.setParameter("inDisabled", false);

                    // roomsQuery 
                    List<Room> roomsTemp2 = (List<Room>) queryRoomHigher.getResultList();
                    List<Room> roomsHigherQuery = new ArrayList<>();
                    for(Room room: roomsTemp2) {
                        if(checkRoomAvail(room)) {
                            roomsHigherQuery.add(room);
                        }
                    }

                    if((roomsQuery.size() + roomsHigherQuery.size()) >= numberOfRooms) // partial upgrade possible
                    {
                        for(Room room: roomsQuery) // add all the free rooms of desired type
                        {
                            assignedRooms.add(room);
                        }

                        // fill in the rest of the number of rooms with rooms of the higher type
                        for(Room roomHigher: roomsHigherQuery)
                        {
                            assignedRooms.add(roomHigher);
                            if(assignedRooms.size() == numberOfRooms) 
                            {
                                break;
                            }
                        }

                        RoomAllocationExceptionReport roomReport = new RoomAllocationExceptionReport();
                        roomReport.setDetails("Room allocation exception, a Room of higher type was assigned!");
                        roomReport = roomAllocationExceptionReportSessionBeanLocal.createReport(roomReport, reservation.getReservationId());

                    }
                    else // nextHigherRoomType exists but not enough total free rooms for reservations
                    {
                        RoomAllocationExceptionReport roomReport = new RoomAllocationExceptionReport();
                        roomReport.setDetails("Room allocation exception, no room allocated");
                        roomReport = roomAllocationExceptionReportSessionBeanLocal.createReport(roomReport, reservation.getReservationId());

                    }
                } // when there is a higher room type
                else // higher room type doesn't exist
                {
                    RoomAllocationExceptionReport roomReport = new RoomAllocationExceptionReport();
                    roomReport.setDetails("Room allocation exception, no room allocated!");
                    roomReport = roomAllocationExceptionReportSessionBeanLocal.createReport(roomReport, reservation.getReservationId());

                }


                // Associating rooms and reservation
                for(Room room: assignedRooms) 
                {
                    reservation.getRoomList().add(room);
                    room.getReservationList().add(reservation);
                }
            } // else room is allocated, do nothing
            
        } // end for reservations loop
        
    }
     
    private Boolean checkRoomAvail(Room room){
        List<Reservation> reservations = room.getReservationList();
        Boolean isFree = true;
        for(Reservation r : reservations){
            if(r.getCheckOutDate().after(new Date())){
                isFree = false;
                break;
            } else {
            }
        }
        return isFree;
    }

    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<Room>>constraintViolations)
    {
        String msg = "Input data validation error!:";
            
        for(ConstraintViolation constraintViolation:constraintViolations)
        {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }
        
        return msg;
    }
    
     
}
