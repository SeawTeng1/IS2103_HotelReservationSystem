/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatefulEjbClass.java to edit this template
 */
package session.stateful;

import entity.Guest;
import entity.Partner;
import entity.Reservation;
import entity.Room;
import entity.RoomRate;
import entity.RoomType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.exception.AvailableRoomNotFoundException;
import util.exception.GuestAddReservationException;
import util.exception.GuestNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.PartnerAddReservationException;
import util.exception.PartnerNotFoundException;
import util.exception.ReservationAddRoomException;
import util.exception.RoomAddReservationException;
import util.exception.RoomRateAddReservationException;
import util.exception.RoomRateNotFoundException;
import util.exception.RoomTypeAddReservationException;

/**
 *
 * @author Toh Seaw Teng
 */
@Stateful
public class PartnerRoomReservation implements PartnerRoomReservationRemote, PartnerRoomReservationLocal {

    @PersistenceContext()
    private EntityManager em;

    @EJB
    private GuestRoomReservationSessionBeanLocal guestRoomReservationSessionBeanLocal;

    // bean validation
    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public PartnerRoomReservation() {
        this.validatorFactory = Validation.buildDefaultValidatorFactory();
        this.validator = validatorFactory.getValidator();
    }

    /*
        3. Partner Reserve Room
        Reserve a room offered in the search results
        It is possible to reserve more than one room.
        The reservation is done by a partner employee on behalf of a customer of the partner organisation.
        Such a reservation would not be visible in the HoRS Reservation Client when the partner customer login with
        his/her HoRS guest account.
        For same day check-in, allocate the required room
    */
    @Override
    public Reservation onlineReserve(String roomType, Integer noOfRoom, Date checkInDate, Date checkOutDate, Long partnerId, Long guestId)
            throws RoomRateNotFoundException, RoomTypeAddReservationException, RoomRateAddReservationException,
            PartnerAddReservationException, RoomAddReservationException, PartnerNotFoundException,
            GuestNotFoundException, GuestAddReservationException, AvailableRoomNotFoundException, InputDataValidationException, ReservationAddRoomException {
        List<Room> selectedRoom = guestRoomReservationSessionBeanLocal.searchAvailableRoomWithLimit(roomType, checkInDate, checkOutDate, noOfRoom);

        BigDecimal total = new BigDecimal(0);
        total = guestRoomReservationSessionBeanLocal.getTotalPrice(roomType, checkInDate, checkOutDate, noOfRoom);

        Reservation reservation = new Reservation(checkInDate, checkOutDate, total, noOfRoom);
        Set<ConstraintViolation<Reservation>>constraintViolations = validator.validate(reservation);
        if (constraintViolations.isEmpty()) {
            // Reservation: add roomList; roomType; roomRate;
            // add roomList only if the reservation is same day check in and is after 2am
            LocalDateTime now = LocalDateTime.now();

            // get room rate
            RoomRate rate = guestRoomReservationSessionBeanLocal.getCorrectRoomRate(roomType, checkInDate, checkOutDate);

            Partner partner = em.find(Partner.class, partnerId);
            if (partner == null) {
                throw new PartnerNotFoundException("Partner ID " + partnerId + " not found.");
            }

            Guest guest = em.find(Guest.class, guestId);
            if (guest == null) {
                throw new GuestNotFoundException("Guest ID " + guestId + " not found.");
            }

            if (rate == null) {
                throw new RoomRateNotFoundException("Published room rate for current room not found");
            }
            
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
            LocalDateTime parsedDateTime = LocalDateTime.parse(checkInDate.toString(), dtf);
            LocalDateTime today = LocalDateTime.parse((new Date()).toString(), dtf);

            if (parsedDateTime.toLocalDate().isEqual(today.toLocalDate()) &&
                    (now.toLocalTime().equals(LocalTime.of(2, 0)) || now.toLocalTime().isAfter(LocalTime.of(2, 0)))) {
                for (Room r : selectedRoom) {
                    reservation.addRoom(r);
                    r.addReservation(reservation);
                }
            }

            reservation.setRoomType(selectedRoom.get(0).getRoomType());
            reservation.setRoomRate(rate);
            reservation.setGuest(guest);
            reservation.setPartner(partner);

            // Room: add reservationList;
            // Room Rate: add reservationList;
            // Room Type: reservationList
            // Guest: add reservation
            try {
                RoomType type = selectedRoom.get(0).getRoomType();
                type.addReservation(reservation);
                rate.addReservation(reservation);
                partner.addReservation(reservation);
                guest.addReservation(reservation);
                
                em.persist(reservation);
                em.flush();
            } catch (RoomTypeAddReservationException ex) {
                throw new RoomTypeAddReservationException("Reservation already added to room type");
            } catch (RoomRateAddReservationException ex) {
                throw new RoomRateAddReservationException("Reservation already added to room rate");
            } catch (PartnerAddReservationException ex) {
                throw new PartnerAddReservationException("Reservation already added to Employee");
            }  catch (GuestAddReservationException ex) {
                throw new GuestAddReservationException("Reservation already added to Employee");
            }
            
            return reservation;
        }   else {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
    }

    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<Reservation>>constraintViolations) {
        String msg = "Input data validation error!:";

        for(ConstraintViolation constraintViolation:constraintViolations)
        {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }

        return msg;
    }
}
