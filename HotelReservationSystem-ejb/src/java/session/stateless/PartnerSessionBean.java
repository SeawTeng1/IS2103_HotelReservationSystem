/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package session.stateless;

import entity.Partner;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import util.exception.EmployeeInvalidPasswordException;
import util.exception.EmployeeNotFoundException;
import util.exception.PartnerExistException;
import util.exception.PartnerInvalidPasswordException;
import util.exception.PartnerNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author priskilarebecca.p
 */
@Stateless
public class PartnerSessionBean implements PartnerSessionBeanRemote, PartnerSessionBeanLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    public void persist(Object object) {
        em.persist(object);
    }
    
    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    
    //HoRs Management Client
    //5. Create New Partner
    @Override
    public Partner createNewEmployee(Partner newPartner) throws PartnerExistException, UnknownPersistenceException {
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
        if(partner.getPassword().equals(password)){
            return partner;
        } else {
            throw new PartnerInvalidPasswordException("Invalid Password please try again!");
        }    
    }
    
    //2. Partner Search Room
    //3. Partner Reserve Room
    //4. View Partner Reservation Details
    //5. View All Partner Reservations
    //Other Methods
    @Override
    public Partner retrievePartnerbyUsername(String username) throws PartnerNotFoundException {
        try{
            Query query = em.createQuery("SELECT p FROM Partner ep WHERE p.username = :inUsername ");
            query.setParameter("inUsername", username);
            return (Partner)query.getSingleResult();
        } catch (NoResultException ex) {
            throw new PartnerNotFoundException("Employee does not exist: " + username);
        }
    }
  
}
