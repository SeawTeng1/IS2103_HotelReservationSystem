/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionLocal.java to edit this template
 */
package session.stateless;

import entity.Partner;
import java.util.List;
import javax.ejb.Local;
import util.exception.PartnerExistException;
import util.exception.PartnerInvalidPasswordException;
import util.exception.PartnerNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author priskilarebecca.p
 */
@Local
public interface PartnerSessionBeanLocal {

    public Partner createNewEmployee(Partner newPartner) throws PartnerExistException, UnknownPersistenceException;

    public List<Partner> viewAllPartners();

    public Partner partnerLogin(String username, String password) throws PartnerNotFoundException, PartnerInvalidPasswordException;

    public Partner retrievePartnerbyUsername(String username) throws PartnerNotFoundException;
    
}
