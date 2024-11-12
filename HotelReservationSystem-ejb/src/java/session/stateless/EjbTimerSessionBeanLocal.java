/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionLocal.java to edit this template
 */
package session.stateless;

import javax.ejb.Local;
import util.exception.InputDataValidationException;
import util.exception.ReportExistException;
import util.exception.ReservationNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author priskilarebecca.p
 */
@Local
public interface EjbTimerSessionBeanLocal {

    public void roomToReservationAllocationTimer() throws ReservationNotFoundException, UnknownPersistenceException, InputDataValidationException, ReportExistException;
    
}
