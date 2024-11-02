/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionLocal.java to edit this template
 */
package session.stateless;

import entity.Guest;
import javax.ejb.Local;
import util.exception.GuestExistException;
import util.exception.GuestNotFoundException;
import util.exception.InvalidCredentialException;
import util.exception.PersistentContextException;

/**
 *
 * @author Toh Seaw Teng
 */
@Local
public interface GuestSessionBeanLocal {

    public Guest guestLogin(String passportNumber, String password) throws GuestNotFoundException, InvalidCredentialException;

    public Guest retrieveGuestById(Long guestId) throws GuestNotFoundException;

    public Guest retrieveGuestByPassportNumber(String passportNumber) throws GuestNotFoundException;

    public Guest createGuest(Guest guest) throws PersistentContextException, GuestExistException;
    
}
