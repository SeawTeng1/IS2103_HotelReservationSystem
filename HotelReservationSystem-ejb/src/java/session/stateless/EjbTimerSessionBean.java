/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package session.stateless;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import util.exception.InputDataValidationException;
import util.exception.NoReservationsFoundException;
import util.exception.ReportExistException;
import util.exception.ReservationNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author priskilarebecca.p
 */
@Stateless
public class EjbTimerSessionBean implements EjbTimerSessionBeanRemote, EjbTimerSessionBeanLocal {

    @EJB
    private RoomSessionBeanLocal roomSessionBeanLocal;

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")

    /**
     *
     * @throws ReservationNotFoundException
     * @throws UnknownPersistenceException
     * @throws InputDataValidationException
     * @throws ReportExistException
     */
    
    @Schedule(hour = "2", info = "roomToReservationAllocationTimer")    
    @Override
    public void roomToReservationAllocationTimer() throws ReservationNotFoundException, UnknownPersistenceException, InputDataValidationException, ReportExistException, NoReservationsFoundException {
        // Get current date as LocalDate
        LocalDate todayDate = LocalDate.now();
        
        // Convert LocalDate to Date if needed by the method
        Date todayDateAsDate = Date.from(todayDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        
        roomSessionBeanLocal.allocateRoomToReservation(todayDateAsDate);
    }
}
