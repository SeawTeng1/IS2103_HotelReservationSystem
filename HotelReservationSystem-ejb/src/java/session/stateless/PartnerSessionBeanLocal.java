/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionLocal.java to edit this template
 */
package session.stateless;

import entity.Partner;
import entity.Reservation;
import java.util.List;
import javax.ejb.Local;
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
@Local
public interface PartnerSessionBeanLocal {

    public Partner createNewPartner(Partner newPartner) throws PartnerExistException, UnknownPersistenceException, InputDataValidationException;

    public List<Partner> viewAllPartners();

    public Partner partnerLogin(String username, String password) throws PartnerNotFoundException, PartnerInvalidPasswordException;

    public Partner retrievePartnerbyUsername(String username) throws PartnerNotFoundException;

    public List<Reservation> getReservationListByPartner(Long partnerId) throws ReservationListForPartnerNotFoundException;

    public Reservation getReservationDetailByPartner(Long partnerId, Long reservationId) throws ReservationForPartnerNotFoundException;

}
