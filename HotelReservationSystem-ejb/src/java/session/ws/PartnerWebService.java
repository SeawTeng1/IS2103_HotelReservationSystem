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
        }

        return partner;
    }

    @WebMethod(operationName = "getReservationDetailByPartner")
    public Reservation getReservationDetailByPartner(@WebParam(name = "partnerId") Long partnerId, @WebParam(name = "reservationId") Long reservationId) throws ReservationForPartnerNotFoundException {

        Reservation reservation = this.partnerSessionBeanLocal.getReservationDetailByPartner(partnerId, reservationId);

        return reservation;
    }

    @WebMethod(operationName = "getReservationListByPartner")
    public List<Reservation> getReservationListByPartner(@WebParam(name = "partnerId") Long partnerId) throws ReservationListForPartnerNotFoundException {

        List<Reservation> reservationList = this.partnerSessionBeanLocal.getReservationListByPartner(partnerId);
        for (Reservation reservation : reservationList) {
            em.detach(reservation);

            em.detach(reservation.getGuest());
            reservation.getGuest().setReservationList(null);

            em.detach(reservation.getPartner());
            reservation.getPartner().setReservationList(null);

            for (Room room : reservation.getRoomList()) {
                em.detach(room);
                room.setReservationList(null);
            }

            em.detach(reservation.getRoomType());
            reservation.getRoomType().setReservationList(null);

            em.detach(reservation.getRoomRate());
            reservation.getRoomRate().setReservationList(null);

            em.detach(reservation.getReport());
            reservation.getReport().setReservation(null);
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
    public void onlineReserve(
            @WebParam(name = "roomType") String roomType,
            @WebParam(name = "noOfRoom") Integer noOfRoom,
            @WebParam(name = "checkInDate") Date checkInDate,
            @WebParam(name = "checkOutDate") Date checkOutDate,
            @WebParam(name = "partnerId") Long partnerId,
            @WebParam(name = "guestId") Long guestId
    ) throws RoomRateNotFoundException, RoomTypeAddReservationException, RoomRateAddReservationException,
            PartnerAddReservationException, RoomAddReservationException, PartnerNotFoundException,
            GuestNotFoundException, GuestAddReservationException, AvailableRoomNotFoundException, InputDataValidationException {
        this.partnerRoomReservationLocal.onlineReserve(roomType, noOfRoom, checkInDate, checkOutDate, partnerId, guestId);
    }
}
