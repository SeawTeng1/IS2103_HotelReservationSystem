/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package session.stateless;

import entity.Guest;
import entity.Partner;
import entity.Reservation;
import java.util.List;
import java.util.Set;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.exception.InputDataValidationException;
import util.exception.PartnerExistException;
import util.exception.PartnerInvalidPasswordException;
import util.exception.PartnerNotFoundException;
import util.exception.ReservationForPartnerNotFoundException;
import util.exception.ReservationListForPartnerNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author priskilarebecca.p
 */
@Stateless
public class PartnerSessionBean implements PartnerSessionBeanRemote, PartnerSessionBeanLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;
    
    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public PartnerSessionBean() {
        this.validatorFactory = Validation.buildDefaultValidatorFactory();
        this.validator = validatorFactory.getValidator();
    }
    
    //HoRs Management Client
    //5. Create New Partner
    @Override
    public Partner createNewEmployee(Partner newPartner) throws PartnerExistException, UnknownPersistenceException, InputDataValidationException {
        Set<ConstraintViolation<Partner>>constraintViolations = validator.validate(newPartner);
        if (constraintViolations.isEmpty()) {
            try { 
                em.persist(newPartner);
                em.flush();

            } catch(PersistenceException ex) {
                if(ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                    if(ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                        throw new PartnerExistException("Employee Already Exists!");
                    } else {
                        throw new UnknownPersistenceException(ex.getMessage());
                    }
                } else {
                    throw new UnknownPersistenceException(ex.getMessage());
                }
            }
            return newPartner;
        } else {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
    }
    
    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<Partner>>constraintViolations) {
        String msg = "Input data validation error!:";
            
        for(ConstraintViolation constraintViolation:constraintViolations)
        {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }
        
        return msg;
    }
    
    //6. View All Partners

    @Override
    public List<Partner> viewAllPartners() {
        Query query = em.createQuery("SELECT p FROM Partner p");
        return query.getResultList();
    }
    
    //Holiday Reservation System
    //1. Partner Login
    @Override
    public Partner partnerLogin(String username, String password) throws PartnerNotFoundException, PartnerInvalidPasswordException {
        Partner partner = retrievePartnerbyUsername(username);
        if (partner.getPassword().equals(password)){
            return partner;
        } else {
            throw new PartnerInvalidPasswordException("Invalid credential please try again!");
        }    
    }
    
    //2. Partner Search Room
    //3. Partner Reserve Room
    //4. View Partner Reservation Details
    //5. View All Partner Reservations
    //Other Methods
    @Override
    public Partner retrievePartnerbyUsername(String username) throws PartnerNotFoundException {
        try {
            Query query = em.createQuery("SELECT p FROM Partner ep WHERE p.username = :inUsername ");
            query.setParameter("inUsername", username);
            return (Partner)query.getSingleResult();
        } catch (NoResultException ex) {
            throw new PartnerNotFoundException("Employee does not exist: " + username);
        }
    }
    
    /*
        5. View All Partner Reservations
        Display a list of reservation records for the partner.
    */
    
    @Override
    public List<Reservation> getReservationListByPartner(Long partnerId) throws ReservationListForPartnerNotFoundException {
        List<Reservation> reservationList = em.createQuery(
                "SELECT r FROM Reservation r WHERE r.partner.partnerId = :partnerId")
            .setParameter("partnerId", partnerId)
            .getResultList();
        
        if (reservationList.size() < 1) {
            throw new ReservationListForPartnerNotFoundException("Reservation not found for this partner.");
        }
        
        return reservationList;
    }
    
    public Reservation getReservationDetailByPartner(Long partnerId, Long reservationId) throws ReservationForPartnerNotFoundException {
        try {
            Reservation reservation = (Reservation) em.createQuery(
                "SELECT r FROM Reservation r WHERE r.partner.partnerId = :partnerId AND r.reservationId = :reservationId")
            .setParameter("partnerId", partnerId)
            .setParameter("reservationId", reservationId)
            .getSingleResult();
            
            return reservation;
        } catch(NoResultException | NonUniqueResultException e) {
            throw new ReservationForPartnerNotFoundException("Reservation id " + reservationId +  " not found for this guest.");
        }
    }
}
