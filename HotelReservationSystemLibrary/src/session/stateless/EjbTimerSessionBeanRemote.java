/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionRemote.java to edit this template
 */
package session.stateless;

import javax.ejb.Remote;
import util.exception.InputDataValidationException;
import util.exception.NoReservationsFoundException;
import util.exception.ReportExistException;
import util.exception.ReservationNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author priskilarebecca.p
 */
@Remote
public interface EjbTimerSessionBeanRemote {
    public void roomToReservationAllocationTimer() throws ReservationNotFoundException, UnknownPersistenceException, InputDataValidationException, ReportExistException, NoReservationsFoundException;
}
