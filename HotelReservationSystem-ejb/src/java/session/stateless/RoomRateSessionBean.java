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
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import util.exception.PersistentContextException;
import util.exception.RoomRateExistException;
import util.exception.RoomRateNotFoundException;
import util.exception.RoomTypeNotFoundException;
import util.exception.RoomTypeRemoveRoomRateException;
import util.exception.UnknownPersistenceException;

@Stateless
public class RoomRateSessionBean implements RoomRateSessionBeanRemote, RoomRateSessionBeanLocal {

    @EJB
    private RoomTypeSessionBeanLocal roomTypeSessionBeanLocal;

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;
    
    private final ValidatorFactory validatorFactory;
    private final Validator validator;
    
    
    
    public RoomRateSessionBean()
    {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }
    
    // 17. Create New Room Rate
    @Override
    public RoomRate createRoomRate(RoomRate roomRate, String roomTypeName) throws RoomRateExistException, UnknownPersistenceException, InputDataValidationException, RoomTypeNotFoundException
    {
        
        try {
            RoomType roomType;
            roomType = roomTypeSessionBeanLocal.retrieveRoomTypebyName(roomTypeName);
        
            Set<ConstraintViolation<RoomRate>>constraintViolations = validator.validate(roomRate);
            if(constraintViolations.isEmpty())
            {
                try
                {
                    roomRate.setRoomType(roomType);
                    roomType.getRoomRateList().add(roomRate);
                    em.persist(roomRate);
                    em.flush();

                }catch(PersistenceException ex){
                    if(ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException"))
                    {
                        if(ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException"))
                        {
                            throw new RoomRateExistException();
                        } else  {
                            throw new UnknownPersistenceException(ex.getMessage());
                        }
                    } else  {
                        throw new UnknownPersistenceException(ex.getMessage());
                    }
                }
            }else{
                throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
            }
        } catch (RoomTypeNotFoundException ex) {
           throw new RoomTypeNotFoundException(ex.getMessage());
        }
         return roomRate;
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
    
    public Boolean roomRateCheckName(String roomRateName, Long roomRateId) {
        try {
            Query query = em.createQuery("SELECT rr FROM RoomRate rr WHERE rr.name = :inName");
            query.setParameter("inName", roomRateName);
            RoomRate roomType = (RoomRate)query.getSingleResult();
            
            return roomType.getRoomRateId().equals(roomRateId);
        } catch (NoResultException ex) {
            System.out.println("");
        }
        
        return true;
    }
    
    // 19. Update Room Rate
    @Override
    public void updateRoomRate(Long roomRateId, RoomRate roomRateUpdate) throws RoomRateExistException, RoomRateNotFoundException, PersistentContextException, InputDataValidationException {
        if(roomRateUpdate != null)
        {
            Set<ConstraintViolation<RoomRate>>constraintViolations = validator.validate(roomRateUpdate);
        
            if(constraintViolations.isEmpty())
            {
 
                try {
                    RoomRate roomRate = em.find(RoomRate.class, roomRateId);

                    //if (roomRateUpdate != null) {
                    roomRate.setName(roomRateUpdate.getName());
                    roomRate.setRateType(roomRateUpdate.getRateType());
                    roomRate.setRatePerNight(roomRateUpdate.getRatePerNight());
                    roomRate.setValidityStart(roomRateUpdate.getValidityStart());
                    roomRate.setValidityEnd(roomRateUpdate.getValidityEnd());
                    
                    if (!roomRateCheckName(roomRateUpdate.getName(), roomRateId)) {
                        throw new RoomRateExistException("Room Rate with same name exist");
                    }
                    //}

                    em.merge(roomRate);
                    
                } catch (PersistenceException ex) {
                    if(ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException"))
                    {
                        if(ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException"))
                        {
                            throw new RoomRateExistException("Room Rate with same name exist");
                        } else  {
                            throw new PersistentContextException("Persistent Context issue " + ex.getMessage());
                        }
                    } else  {
                        throw new PersistentContextException("Persistent Context issue " + ex.getMessage());
                    }
                }
            } else {
                throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
            }
        }
        //return roomRateUpdate;
    } 
     
    // 20. Delete Room Rate
    @Override
    public void deleteRoomRate(Long roomRateId) throws RoomRateNotFoundException, RoomTypeRemoveRoomRateException, PersistentContextException {
        RoomRate roomRate = em.find(RoomRate.class, roomRateId);
        if (roomRate == null) {
            throw new RoomRateNotFoundException("Room Rate ID " + roomRateId + " not found.");
        }
        
        // if there is any reservation do not delete the room rate because it might still be access in the future
        if (!roomRate.getReservationList().isEmpty()) {
            try {
                roomRate.setDisabled(true);
            em.merge(roomRate);
            } catch (PersistenceException e) {
                throw new PersistentContextException("Persistent Context issue " + e.getMessage());
            }
        } else {
            //remove room rate from room type
            try {
                roomRate.getRoomType().removeRoomrate(roomRate);
                // roomRate.setRoomType(null);
                em.remove(roomRate);
            } catch (RoomTypeRemoveRoomRateException ex) {
                throw new RoomTypeRemoveRoomRateException("Room rate has not been added to room type");
            } catch (PersistenceException e) {
                throw new PersistentContextException("Persistent Context issue " + e.getMessage());
            }
        }
    }
    
    //21. View All Room Rates
    @Override
    public List<RoomRate> viewAllRoomRates()
    {
        Query query = em.createQuery("SELECT rr FROM RoomRate rr");
        
        return query.getResultList();
    }
    
    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<RoomRate>>constraintViolations)
    {
        String msg = "Input data validation error!:";
            
        for(ConstraintViolation constraintViolation:constraintViolations)
        {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }
        
        return msg;
    }
}
