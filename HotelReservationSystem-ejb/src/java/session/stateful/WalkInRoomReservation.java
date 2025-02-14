/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatefulEjbClass.java to edit this template
 */
package session.stateful;

import entity.Employee;
import entity.Guest;
import entity.Reservation;
import entity.Room;
import entity.RoomRate;
import entity.RoomType;
import enumeration.RateType;
import enumeration.RoomStatus;
import java.math.BigDecimal;
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
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.exception.AvailableRoomNotFoundException;
import util.exception.EmployeeAddReservationException;
import util.exception.EmployeeNotFoundException;
import util.exception.GuestAddReservationException;
import util.exception.GuestNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.ReservationAddRoomException;
import util.exception.ReservationExceedAvailableRoomNumberException;
import util.exception.ReservationNotFoundException;
import util.exception.RoomAddReservationException;
import util.exception.RoomCheckInException;
import util.exception.RoomCheckOutException;
import util.exception.RoomRateAddReservationException;
import util.exception.RoomRateNotFoundException;
import util.exception.RoomTypeAddReservationException;
import util.exception.RoomTypeNotFoundException;

/**
 *
 * @author Toh Seaw Teng
 */
@Stateful
public class WalkInRoomReservation implements WalkInRoomReservationRemote, WalkInRoomReservationLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    // bean validation
    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public WalkInRoomReservation() {
        this.validatorFactory = Validation.buildDefaultValidatorFactory();
        this.validator = validatorFactory.getValidator();
    }

    // 23. Walk-in Search Room
    /*
        get available room across all room types offered by the hotel according to the check-in date and check-out date
        reservation amount should be calculated based on the prevailing published rate
        need to check that there is enough inventory for new reservation for that room type
    */

    @Override
    public List<Room> searchAvailableRoom(String roomType, Date checkInDate, Date checkoutDate) throws AvailableRoomNotFoundException {
        // get room that can be used
        List<Room> availableRoom = new ArrayList<Room>();
        try{
            Query query = em.createQuery("SELECT rt FROM RoomType rt WHERE rt.name = :inName");
            query.setParameter("inName", roomType);
            RoomType type = (RoomType)query.getSingleResult();
            if (type.getDisabled()) {
                return availableRoom;
            }
        } catch (NoResultException ex) {
            throw new AvailableRoomNotFoundException("No available room found, please try again.");
        }
        
        List<Room> roomList = em.createQuery(
                "SELECT r FROM Room r WHERE r.roomType.name = :roomType AND r.disabled = :disabled AND r.roomStatus != :roomStatus")
            .setParameter("roomType", roomType)
            .setParameter("disabled", Boolean.FALSE)
            .setParameter("roomStatus", RoomStatus.UNAVAILABLE)
            .getResultList();

        if (roomList.size() < 1) {
            throw new AvailableRoomNotFoundException("No available room found, please try again.");
        }

        // get reservation that have not be checkin
        List<Reservation> currReservation = em.createQuery("SELECT r FROM Reservation r WHERE r.roomType.name = :roomType AND r.isCheckOut = :isCheckOut AND ((r.checkInDate BETWEEN :checkInDate AND :checkoutDate) OR (r.checkOutDate BETWEEN :checkInDate AND :checkoutDate) OR (r.checkInDate <= :checkInDate AND r.checkOutDate >= :checkoutDate))")
            .setParameter("roomType", roomType)
            .setParameter("checkInDate", checkInDate)
            .setParameter("checkoutDate", checkoutDate)
            .setParameter("isCheckOut", Boolean.FALSE)
            .getResultList();
        
        Integer totalReservation = 0;
        for (Reservation res : currReservation) {
            totalReservation += res.getNumOfRoom();
        }

        for (Room r : roomList) {
            // get the reservation for current room
            List<Reservation> resList = r.getReservationList();

            if (totalReservation >= roomList.size()) {
                break;
            }

            if (!resList.isEmpty()) {
                Reservation res = resList.get(resList.size() - 1);
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
                LocalDateTime parsedDateTime = LocalDateTime.parse(res.getCheckOutDate().toString(), dtf);
                LocalDateTime today = LocalDateTime.parse(checkInDate.toString(), dtf);

                if (
                    checkInDate.after(res.getCheckOutDate()) ||
                    parsedDateTime.toLocalDate().isEqual(today.toLocalDate()) ||
                    res.getIsCheckOut() == Boolean.TRUE
                ) {
                    if (!availableRoom.contains(r)) {
                        availableRoom.add(r);
                    }
                }

                if ((roomList.size() - totalReservation) == availableRoom.size()) {
                    break;
                }
            } else {
                if (!availableRoom.contains(r)) {
                    availableRoom.add(r);
                }

                if ((roomList.size() - totalReservation) == availableRoom.size()) {
                    break;
                }
            }
        }

        // for inventory can just check if the number of room <= availableRoom.size()
        return availableRoom;
    }

    @Override
    public List<Room> searchAvailableRoomWithLimit(String roomType, Date checkInDate, Date checkoutDate, Integer limit) throws AvailableRoomNotFoundException {
        List<Room> availableRoom = new ArrayList<Room>();
        try{
            Query query = em.createQuery("SELECT rt FROM RoomType rt WHERE rt.name = :inName");
            query.setParameter("inName", roomType);
            RoomType type = (RoomType)query.getSingleResult();
            if (type.getDisabled()) {
                return availableRoom;
            }
        } catch (NoResultException ex) {
            throw new AvailableRoomNotFoundException("No available room found, please try again.");
        }
        
        Query query = em.createQuery(
                "SELECT r FROM Room r WHERE r.roomType.name = :roomType AND r.disabled = :disabled AND r.roomStatus != :roomStatus")
            .setParameter("roomType", roomType)
            .setParameter("disabled", Boolean.FALSE)
            .setParameter("roomStatus", RoomStatus.UNAVAILABLE);

        List<Room> roomList = query.getResultList();
        if (roomList.size() < 1) {
            throw new AvailableRoomNotFoundException("No available room found, please try again.");
        }

        // get reservation that have not be checkin
        List<Reservation> currReservation = em.createQuery("SELECT r FROM Reservation r WHERE r.roomType.name = :roomType AND r.isCheckOut = :isCheckOut AND ((r.checkInDate BETWEEN :checkInDate AND :checkoutDate) OR (r.checkOutDate BETWEEN :checkInDate AND :checkoutDate) OR (r.checkInDate <= :checkInDate AND r.checkOutDate >= :checkoutDate))")
            .setParameter("roomType", roomType)
            .setParameter("checkInDate", checkInDate)
            .setParameter("checkoutDate", checkoutDate)
            .setParameter("isCheckOut", Boolean.FALSE)
            .getResultList();
        
        Integer totalReservation = 0;
        for (Reservation res : currReservation) {
            totalReservation += res.getNumOfRoom();
        }

        for (Room r : roomList) {
            // get the reservation for current room
            List<Reservation> resList = r.getReservationList();

            if (totalReservation >= roomList.size()) {
                break;
            }

            if (!resList.isEmpty()) {
                Reservation res = resList.get(resList.size() - 1);
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
                LocalDateTime parsedDateTime = LocalDateTime.parse(res.getCheckOutDate().toString(), dtf);
                LocalDateTime today = LocalDateTime.parse(checkInDate.toString(), dtf);
                if (
                    checkInDate.after(res.getCheckOutDate()) ||
                    parsedDateTime.toLocalDate().isEqual(today.toLocalDate()) ||
                    res.getIsCheckOut() == Boolean.TRUE
                ) {
                    if (!availableRoom.contains(r)) {
                        availableRoom.add(r);
                    }
                }

                if ((roomList.size() - totalReservation) == availableRoom.size()) {
                    break;
                }
            } else {
                if (!availableRoom.contains(r)) {
                    availableRoom.add(r);
                }

                if ((roomList.size() - totalReservation) == availableRoom.size()) {
                    break;
                }
            }

            if (availableRoom.size() == limit) {
                break;
            }
        }

        // for inventory can just check if the number of room <= availableRoom.size()
        return availableRoom;
    }

    @Override
    public BigDecimal getTotalPrice(String roomType, Date checkInDate, Date checkOutDate, Integer numOfRoom)
            throws RoomRateNotFoundException, AvailableRoomNotFoundException {
        BigDecimal total = new BigDecimal(0);

        try {
            RoomRate rate = (RoomRate) em.createQuery(
                "SELECT rr FROM RoomRate rr WHERE rr.roomType.name = :roomType AND rr.rateType = :rateType AND rr.disabled = :disabled")
            .setParameter("roomType", roomType)
            .setParameter("rateType", RateType.PUBLISHED)
            .setParameter("disabled", Boolean.FALSE)
            .getSingleResult();

            try {
                List<Room> availableRoom = this.searchAvailableRoom(roomType, checkInDate, checkOutDate);
                if (availableRoom.size() >= numOfRoom) {
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
                    LocalDate date1 = LocalDate.parse(checkInDate.toString(), dtf);
                    LocalDate date2 = LocalDate.parse(checkOutDate.toString(), dtf);
                    long daysBetween = ChronoUnit.DAYS.between(date1, date2);


                    total = rate.getRatePerNight().multiply(new BigDecimal(daysBetween)).multiply(new BigDecimal(numOfRoom));
                }
            } catch (AvailableRoomNotFoundException ex) {
                throw new AvailableRoomNotFoundException("No available room found, please try again.");
            }

        } catch (NoResultException ex) {
            throw new RoomRateNotFoundException("Published room rate for current room type not found");
        }

        return total;
    }

    /*
        24. Walk-in Reserve Room
        walk-in guest can reserve more than one room.
        A walk-in guest does not need to be a registered guest of the hotel.
        For same day check-in, allocate the required room(s) immediately if reservation is made after 2 am.
    */
    @Override
    public Reservation walkInReserve (String roomType, Integer noOfRoom, Date checkInDate, Date checkOutDate, Long employeeId, Long guestId)
            throws RoomRateNotFoundException, RoomTypeAddReservationException, RoomRateAddReservationException,
            EmployeeAddReservationException, AvailableRoomNotFoundException, RoomRateNotFoundException, ReservationAddRoomException,
            RoomAddReservationException, InputDataValidationException, EmployeeNotFoundException, GuestNotFoundException, GuestAddReservationException, ReservationExceedAvailableRoomNumberException {

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

            RoomRate rate;
            try {
                rate = (RoomRate) em.createQuery(
                        "SELECT rr FROM RoomRate rr WHERE rr.roomType.name = :roomType AND rr.rateType = :rateType")
                    .setParameter("roomType", roomType)
                    .setParameter("rateType", RateType.PUBLISHED)
                    .getSingleResult();
            }  catch (NoResultException ex) {
                throw new RoomRateNotFoundException("Published room rate for current room not found");
            }

            Employee employee = em.find(Employee.class, employeeId);
            if (employee == null) {
                throw new EmployeeNotFoundException("Employee with id " + employeeId + " not found.");
            }

            Guest guest = null;
            if (guestId != null) {
                guest = em.find(Guest.class, guestId);
                if (guest == null) {
                    throw new GuestNotFoundException("Guest with id " + guestId + " not found.");
                }
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
            reservation.setEmployee(employee);

            if (guest != null) {
                reservation.setGuest(guest);
            }

            // Room: add reservationList;
            // Room Rate: add reservationList;
            // Room Type: reservationList
            // Employee: add reservation
            try {
                RoomType type = selectedRoom.get(0).getRoomType();
                type.addReservation(reservation);
                rate.addReservation(reservation);
                employee.addReservation(reservation);
                if (guest != null) {
                    guest.addReservation(reservation);
                }

                em.persist(reservation);
                em.flush();
            } catch (RoomTypeAddReservationException ex) {
                throw new RoomTypeAddReservationException("Reservation already added to room type");
            } catch (RoomRateAddReservationException ex) {
                throw new RoomRateAddReservationException("Reservation already added to room rate");
            } catch (GuestAddReservationException ex) {
                throw new GuestAddReservationException(ex.getMessage());
            }

            return reservation;
        } else {
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

    @Override
    public Reservation checkIn(Long reservationId) throws ReservationNotFoundException, RoomCheckInException  {
        // get all the reservation for guest which will be check in today
        Reservation reservation = em.find(Reservation.class, reservationId);

        if (reservation == null) {
            throw new ReservationNotFoundException("Reservation Id " + reservationId + " not found.");
        } else {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
            LocalDateTime parsedCheckInDate = LocalDateTime.parse(reservation.getCheckInDate().toString(), dtf);
            LocalDateTime parsedCheckoutDate = LocalDateTime.parse(reservation.getCheckInDate().toString(), dtf);
            LocalDateTime today = LocalDateTime.parse((new Date()).toString(), dtf);

            // user cannot check in if the checkindate is after today / check out date is over
            if (reservation.getIsCheckIn()) {
                throw new RoomCheckInException("This reservation have already been checked in");
            }
            
            if (
                    reservation.getRoomList().isEmpty() &&
                    reservation.getReport() == null &&
                    (parsedCheckInDate.toLocalDate().isAfter(today.toLocalDate()) ||
                    parsedCheckoutDate.toLocalDate().isBefore(today.toLocalDate()))

            ) {
                throw new RoomCheckInException("Room is not available to check in");
            }
            
            List<Room> roomList = reservation.getRoomList();
            if (roomList.size() > 0) {
                for (Room room : roomList) {
                    room.setIsOccupied(Boolean.TRUE);
                }
            }

            reservation.setIsCheckIn(Boolean.TRUE);
            em.merge(reservation);
            
            if (roomList.size() > 0) {
                for (Room room : roomList) {
                    em.merge(room);
                }
            }

            return reservation;
        }
    }

    @Override
    public void checkOut(Long reservationId) throws ReservationNotFoundException, RoomCheckOutException {
        Reservation reservation = em.find(Reservation.class, reservationId);

        if (reservation == null) {
            throw new ReservationNotFoundException("Reservation Id " + reservationId + " not found.");
        } else {
            if (
                reservation.getIsCheckIn() == false
            ) {
                throw new RoomCheckOutException("Room have not yet be check in");
            }
            
            if (reservation.getIsCheckOut()) {
                throw new RoomCheckOutException("This reservation have already been checked out");
            }
            
            List<Room> roomList = reservation.getRoomList();
            if (roomList.size() > 0) {
                for (Room room : roomList) {
                    room.setIsOccupied(Boolean.FALSE);
                }
            }

            reservation.setIsCheckOut(Boolean.TRUE);
            em.merge(reservation);
            
            if (roomList.size() > 0) {
                for (Room room : roomList) {
                    em.merge(room);
                }
            }
        }
    }
}
