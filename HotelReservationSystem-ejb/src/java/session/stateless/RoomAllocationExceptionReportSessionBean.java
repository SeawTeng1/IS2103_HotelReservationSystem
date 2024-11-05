/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package session.stateless;

import entity.Reservation;
import entity.RoomAllocationExceptionReport;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
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

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    //16. View Room Allocation Exception Report - Generate a report showing two types of room allocation exception (need methods to create and view all)
    @Override
    public RoomAllocationExceptionReport createReport(RoomAllocationExceptionReport newReport, Long reservationId) throws UnknownPersistenceException, ReportExistException {
        Reservation reservationToReport = em.find(Reservation.class, reservationId);
        
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
    }
    
    @Override
    public List<RoomAllocationExceptionReport> viewAllReports() {
        Query query = em.createQuery("SELECT r FROM RoomAllocationExceptionReport r");
        return query.getResultList();
    }
}
