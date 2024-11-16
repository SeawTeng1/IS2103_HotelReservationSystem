/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package session.stateless;

import entity.Reservation;
import entity.Room;
import entity.RoomAllocationExceptionReport;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.exception.InputDataValidationException;
import util.exception.ReportExistException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author priskilarebecca.p
 */
@Stateless
public class RoomAllocationExceptionReportSessionBean implements RoomAllocationExceptionReportSessionBeanRemote, RoomAllocationExceptionReportSessionBeanLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    public void persist(Object object) {
        em.persist(object);
    }
    
    private final ValidatorFactory validatorFactory;
    private final Validator validator;
    
    
    
    public RoomAllocationExceptionReportSessionBean()
    {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    //16. View Room Allocation Exception Report - Generate a report showing two types of room allocation exception (need methods to create and view all)
    @Override
    public RoomAllocationExceptionReport createReport(RoomAllocationExceptionReport newReport, Long reservationId) throws UnknownPersistenceException, ReportExistException, InputDataValidationException {
        Reservation reservationToReport = em.find(Reservation.class, reservationId);
        Set<ConstraintViolation<RoomAllocationExceptionReport>>constraintViolations = validator.validate(newReport);
        
        if(constraintViolations.isEmpty())
        {
            if (reservationToReport != null) {
                try {
                    newReport.setReservation(reservationToReport);
                    reservationToReport.setReport(newReport);
                    em.persist(newReport);
                    em.flush();
                } catch(PersistenceException ex) {
                    if(ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                        if(ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                            throw new ReportExistException("Room Allocation Exception Report Already Exists!");
                        } else {
                            throw new UnknownPersistenceException(ex.getMessage());
                        }
                    } else {
                        throw new UnknownPersistenceException(ex.getMessage());
                    }
                }

            }
             return newReport;
        } else {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
    }
    
    @Override
    public List<RoomAllocationExceptionReport> viewAllReports() {
        Query query = em.createQuery("SELECT r FROM RoomAllocationExceptionReport r");
        return query.getResultList();
    }
    
    public List<RoomAllocationExceptionReport> viewReportsbyDate(Date reportDate) {
        Date nextDay = new Date(reportDate.getTime() + (23 * 60 * 60 * 1000) + (59 * 60 * 1000));
        
        Query query = em.createQuery("SELECT r FROM RoomAllocationExceptionReport r WHERE r.reportDate >= :reportDate and r.reportDate <= :nextDay");
        query.setParameter("reportDate", reportDate);
        query.setParameter("nextDay", nextDay);
        return query.getResultList();
    }   
    
    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<RoomAllocationExceptionReport>>constraintViolations)
    {
        String msg = "Input data validation error!:";
            
        for(ConstraintViolation constraintViolation:constraintViolations)
        {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }
        
        return msg;
    }
}
