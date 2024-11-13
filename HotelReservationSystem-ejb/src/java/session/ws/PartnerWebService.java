/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/WebServices/EjbWebService.java to edit this template
 */
package session.ws;

import entity.Partner;
import entity.Reservation;
import entity.Room;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import session.stateful.GuestRoomReservationSessionBeanLocal;
import session.stateful.PartnerRoomReservationLocal;
import session.stateless.PartnerSessionBeanLocal;
import util.exception.AvailableRoomNotFoundException;
import util.exception.GuestAddReservationException;
import util.exception.GuestNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.PartnerAddReservationException;
import util.exception.PartnerInvalidPasswordException;
import util.exception.PartnerNotFoundException;
import util.exception.ReservationAddRoomException;
import util.exception.ReservationForPartnerNotFoundException;
import util.exception.ReservationListForPartnerNotFoundException;
import util.exception.RoomAddReservationException;
import util.exception.RoomRateAddReservationException;
import util.exception.RoomRateNotFoundException;
import util.exception.RoomTypeAddReservationException;

/**
 *
 * @author Toh Seaw Teng
 */
@WebService(serviceName = "PartnerWebService")
@Stateless()
public class PartnerWebService {

    @EJB
    private GuestRoomReservationSessionBeanLocal guestRoomReservationSessionBeanLocal;
    @EJB
    private PartnerRoomReservationLocal partnerRoomReservationLocal;
    @EJB
    private PartnerSessionBeanLocal partnerSessionBeanLocal;

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    @WebMethod(operationName = "partnerLogin")
    public Partner partnerLogin(@WebParam(name = "username") String username, @WebParam(name = "password") String password)
                throws PartnerInvalidPasswordException, PartnerNotFoundException {

        Partner partner = this.partnerSessionBeanLocal.partnerLogin(username, password);

        em.detach(partner);

        for (Reservation reservation : partner.getReservationList()) {
            em.detach(reservation);
            reservation.setPartner(null);
            
            em.detach(reservation.getGuest());
            reservation.setGuest(null);
        }
        
        partner.setReservationList(null);

        return partner;
    }

    @WebMethod(operationName = "getReservationDetailByPartner")
    public Reservation getReservationDetailByPartner(@WebParam(name = "partnerId") Long partnerId, @WebParam(name = "reservationId") Long reservationId) throws ReservationForPartnerNotFoundException {

        Reservation reservation = this.partnerSessionBeanLocal.getReservationDetailByPartner(partnerId, reservationId);
        
        em.detach(reservation);
        
        em.detach(reservation.getGuest());
        // reservation.setGuest(null);

        if (reservation.getEmployee() != null) {
            em.detach(reservation.getEmployee());
            reservation.setEmployee(null);
        }

        em.detach(reservation.getPartner());
        reservation.getPartner().setReservationList(null);

        if (reservation.getRoomList().size() > 0) {
            for (Room room : reservation.getRoomList()) {
                em.detach(room);
                room.setReservationList(null);
                
                em.detach(room.getRoomType());
                room.setRoomType(null);
            }
        }
        
        em.detach(reservation.getRoomType());
        if (reservation.getRoomType().getRoomList().size() > 0) {
            for (Room room : reservation.getRoomType().getRoomList()) {
                em.detach(room);
                room.setRoomType(null);
            }
            
            for (Reservation r : reservation.getRoomType().getReservationList()) {
                em.detach(r);
                r.setRoomType(null);
            }
        } 
        reservation.getRoomType().setReservationList(null);
        reservation.getRoomType().setRoomList(null);

        em.detach(reservation.getRoomRate());
        if (reservation.getRoomRate().getReservationList().size() > 0) {
            for (Reservation r : reservation.getRoomRate().getReservationList()) {
                em.detach(r);
                r.setRoomType(null);
            }
        }
        reservation.getRoomRate().setReservationList(null);
        // reservation.setRoomRate(null);

        return reservation;
    }

    @WebMethod(operationName = "getReservationListByPartner")
    public List<Reservation> getReservationListByPartner(@WebParam(name = "partnerId") Long partnerId) throws ReservationListForPartnerNotFoundException {

        List<Reservation> reservationList = this.partnerSessionBeanLocal.getReservationListByPartner(partnerId);
        for (Reservation reservation : reservationList) {
            em.detach(reservation);

            em.detach(reservation.getGuest());
            reservation.getGuest().setReservationList(null);
            
            if (reservation.getEmployee() != null) {
                em.detach(reservation.getEmployee());
                reservation.getEmployee().setReservationList(null);
            }
            
            em.detach(reservation.getPartner());
            reservation.getPartner().setReservationList(null);
            
            if (reservation.getRoomList().size() > 0) {
                for (Room room : reservation.getRoomList()) {
                    em.detach(room);
                    room.setReservationList(null);
                }
            }
            
            em.detach(reservation.getRoomType());
            if (reservation.getRoomType().getRoomList().size() > 0) {
                for (Room room : reservation.getRoomType().getRoomList()) {
                    em.detach(room);
                    room.setRoomType(null);
                }
            } 
            reservation.getRoomType().setReservationList(null);

            em.detach(reservation.getRoomRate());
            reservation.getRoomRate().setReservationList(null);
        }

        return reservationList;
    }

    @WebMethod(operationName = "searchAvailableRoom")
    public List<Room> searchAvailableRoom(
            @WebParam(name = "roomType") String roomType,
            @WebParam(name = "checkInDate") Date checkInDate,
            @WebParam(name = "checkOutDate") Date checkOutDate
    ) throws AvailableRoomNotFoundException {

        List<Room> roomList = this.guestRoomReservationSessionBeanLocal.searchAvailableRoom(roomType, checkInDate, checkOutDate);
        for (Room room : roomList) {
            em.detach(room);

            em.detach(room.getRoomType());
            room.setRoomType(null);
            
            for (Reservation reservation : room.getReservationList()) {
                em.detach(reservation);
                
                em.detach(reservation.getGuest());
                
                for (Reservation r : reservation.getGuest().getReservationList()) {
                    em.detach(reservation);
                }
                reservation.getGuest().setReservationList(null);
                reservation.setGuest(null);
                
                for (Room r : reservation.getRoomList()) {
                    em.detach(room);
                }
                reservation.setRoomList(null);
                
            }
            
            room.setReservationList(null);
        }
        return roomList;
    }

    @WebMethod(operationName = "getTotalPrice")
    public BigDecimal getTotalPrice(
            @WebParam(name = "roomType") String roomType,
            @WebParam(name = "checkInDate") Date checkInDate,
            @WebParam(name = "checkOutDate") Date checkOutDate
    ) throws AvailableRoomNotFoundException, RoomRateNotFoundException {

        BigDecimal roomRate = this.guestRoomReservationSessionBeanLocal.getTotalPrice(roomType, checkInDate, checkOutDate, 1);
        
        return roomRate;
    }

    @WebMethod(operationName = "onlineReserve")
    public Reservation onlineReserve(
            @WebParam(name = "roomType") String roomType,
            @WebParam(name = "noOfRoom") Integer noOfRoom,
            @WebParam(name = "checkInDate") Date checkInDate,
            @WebParam(name = "checkOutDate") Date checkOutDate,
            @WebParam(name = "partnerId") Long partnerId,
            @WebParam(name = "guestId") Long guestId
    ) throws RoomRateNotFoundException, RoomTypeAddReservationException, RoomRateAddReservationException,
            PartnerAddReservationException, RoomAddReservationException, PartnerNotFoundException,
            GuestNotFoundException, GuestAddReservationException, AvailableRoomNotFoundException, InputDataValidationException, ReservationAddRoomException {
        Reservation reservation = this.partnerRoomReservationLocal.onlineReserve(roomType, noOfRoom, checkInDate, checkOutDate, partnerId, guestId);
        
       em.detach(reservation);

        em.detach(reservation.getGuest());
        reservation.getGuest().setReservationList(null);

        em.detach(reservation.getPartner());
        reservation.getPartner().setReservationList(null);

        for (Room room : reservation.getRoomList()) {
            em.detach(room);
            room.setReservationList(null);
        }
        reservation.setRoomList(null);

        em.detach(reservation.getRoomType());
        reservation.getRoomType().setReservationList(null);

        em.detach(reservation.getRoomRate());
        reservation.getRoomRate().setReservationList(null);

        return reservation;
    }
}
