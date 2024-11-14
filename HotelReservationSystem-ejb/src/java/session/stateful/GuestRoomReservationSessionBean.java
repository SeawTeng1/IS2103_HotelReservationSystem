/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatefulEjbClass.java to edit this template
 */
package session.stateful;

import entity.Guest;
import entity.Reservation;
import entity.Room;
import entity.RoomRate;
import entity.RoomType;
import enumeration.RateType;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
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
import util.exception.ReservationAddRoomException;
import util.exception.RoomAddReservationException;
import util.exception.RoomRateAddReservationException;
import util.exception.RoomRateNotFoundException;
import util.exception.RoomTypeAddReservationException;
import util.exception.ReservationExceedAvailableRoomNumberException;

/**
 *
 * @author Toh Seaw Teng
 */
@Stateful
public class GuestRoomReservationSessionBean implements GuestRoomReservationSessionBeanRemote, GuestRoomReservationSessionBeanLocal {

    @EJB
    private WalkInRoomReservationLocal walkInRoomReservation;

    @PersistenceContext()
    private EntityManager em;

    // bean validation
    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public GuestRoomReservationSessionBean() {
        this.validatorFactory = Validation.buildDefaultValidatorFactory();
        this.validator = validatorFactory.getValidator();
    }

    /*
        3. Search Hotel Room
        Search an available room across all room types offered by the hotel according to the check-in date and check-out date.
        The reservation amount should be calculated based on the available prevailing rate of that room type.
        The system needs to ensure that the hotel has sufficient room inventory to fulfil the new reservation
    */
    @Override
    public List<Room> searchAvailableRoom(String roomType, Date checkInDate, Date checkOutDate) throws AvailableRoomNotFoundException {
        try {
            List<Room> roomList = walkInRoomReservation.searchAvailableRoom(roomType, checkInDate, checkOutDate);
            return roomList;
        } catch (AvailableRoomNotFoundException e) {
            throw e;
        }
    }

    @Override
    public List<Room> searchAvailableRoomWithLimit(String roomType, Date checkInDate, Date checkOutDate, Integer limit) throws AvailableRoomNotFoundException {
        try {
            List<Room> roomList = walkInRoomReservation.searchAvailableRoomWithLimit(roomType, checkInDate, checkOutDate, limit);
            return roomList;
        } catch (AvailableRoomNotFoundException e) {
            throw e;
        }
    }

    @Override
    public BigDecimal getTotalPrice(String roomType, Date checkInDate, Date checkOutDate, Integer numOfRoom)
            throws RoomRateNotFoundException, AvailableRoomNotFoundException {
        BigDecimal total = new BigDecimal(0);

        try {
            List<Room> availableRoom = walkInRoomReservation.searchAvailableRoom(roomType, checkInDate, checkOutDate);
            if (availableRoom.size() >= numOfRoom) {
                try {
                    RoomRate rate = this.getCorrectRoomRate(roomType, checkInDate, checkOutDate);

                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
                    LocalDate date1 = LocalDate.parse(checkInDate.toString(), dtf);
                    LocalDate date2 = LocalDate.parse(checkOutDate.toString(), dtf);
                    long daysBetween = ChronoUnit.DAYS.between(date1, date2);


                    total = rate.getRatePerNight().multiply(new BigDecimal(daysBetween)).multiply(new BigDecimal(numOfRoom));
                } catch (RoomRateNotFoundException e) {
                    throw new RoomRateNotFoundException("Room rate for current room not found");
                }

            }
        } catch (AvailableRoomNotFoundException ex) {
            throw new AvailableRoomNotFoundException("No available room found, please try again.");
        }

        return total;
    }

    /*
        get rate
    */
    @Override
    public RoomRate getCorrectRoomRate(String roomType, Date checkInDate, Date checkOutDate) throws RoomRateNotFoundException {
        // get rate type normal for this room type
        // then search for any room rate that have validity start and end within the check in and check out
        RoomRate normalRate = (RoomRate) em.createQuery(
                "SELECT rr FROM RoomRate rr WHERE rr.roomType.name = :roomType AND rr.rateType = :rateType")
            .setParameter("roomType", roomType)
            .setParameter("rateType", RateType.NORMAL)
            .getSingleResult();

        List<RoomRate> specialRateList = em.createQuery(
                "SELECT rr FROM RoomRate rr WHERE "
                        + "rr.roomType.name = :roomType AND (rr.rateType = :peakType OR rr.rateType = :promoType)"
                        + "AND rr.validityStart BETWEEN :checkInDate AND :checkOutDate AND rr.validityEnd BETWEEN :checkInDate AND :checkOutDate"
            )
            .setParameter("roomType", roomType)
            .setParameter("peakType", RateType.PEAK)
            .setParameter("promoType", RateType.PROMOTION)
            .setParameter("checkInDate", checkInDate)
            .setParameter("checkOutDate", checkOutDate)
            .getResultList();

        if (normalRate == null) {
            throw new RoomRateNotFoundException("Room rate for current room not found");
        }

        RoomRate specialRate = null;
        if (specialRateList != null) {
            for (RoomRate rate : specialRateList) {
                if (specialRate == null || rate.getRateType().equals(RateType.PROMOTION)) {
                    specialRate = rate;
                }
            }
        }

        return specialRate != null ? specialRate : normalRate;
    }

    /*
        4. Reserve Hotel Room
        Reserve a room offered in the search results (see use case
        It is possible for a guest to reserve more than one room.
        For same day check-in, allocate the required room(s) immediately if reservation is made after 2 am.
    */

    @Override
    public Reservation onlineReserve (String roomType, Integer noOfRoom, Date checkInDate, Date checkOutDate, Long guestId)
            throws RoomRateNotFoundException, RoomTypeAddReservationException,
            RoomRateAddReservationException, GuestAddReservationException, RoomAddReservationException,
            GuestNotFoundException, InputDataValidationException, AvailableRoomNotFoundException, ReservationAddRoomException, ReservationExceedAvailableRoomNumberException {

        List<Room> selectedRoom = this.searchAvailableRoomWithLimit(roomType, checkInDate, checkOutDate, noOfRoom);

        if (selectedRoom.size() < noOfRoom) {
            throw new ReservationExceedAvailableRoomNumberException("There is insufficient rooms to be reserved");
        }

        BigDecimal total = new BigDecimal(0);
        total = this.getTotalPrice(roomType, checkInDate, checkOutDate, noOfRoom);

        Reservation reservation = new Reservation(checkInDate, checkOutDate, total, noOfRoom);
        Set<ConstraintViolation<Reservation>>constraintViolations = validator.validate(reservation);
        if (constraintViolations.isEmpty()) {
            // Reservation: add roomList; roomType; roomRate;
            // add roomList only if the reservation is same day check in and is after 2am
            LocalDateTime now = LocalDateTime.now();

            // get room rate
            RoomRate rate = this.getCorrectRoomRate(roomType, checkInDate, checkOutDate);

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

            // Room: add reservationList;
            // Room Rate: add reservationList;
            // Room Type: reservationList
            // Guest: add reservation
            try {
                RoomType type = selectedRoom.get(0).getRoomType();
                type.addReservation(reservation);
                rate.addReservation(reservation);
                guest.addReservation(reservation);

                em.persist(reservation);
                em.flush();
            } catch (RoomTypeAddReservationException ex) {
                throw new RoomTypeAddReservationException("Reservation already added to room type");
            } catch (RoomRateAddReservationException ex) {
                throw new RoomRateAddReservationException("Reservation already added to room rate");
            } catch (GuestAddReservationException ex) {
                throw new GuestAddReservationException("Reservation already added to Employee");
            }

            return reservation;
        }  else {
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

