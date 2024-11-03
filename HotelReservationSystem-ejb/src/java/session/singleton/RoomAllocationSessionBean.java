/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB31/SingletonEjbClass.java to edit this template
 */
package session.singleton;

import entity.ExceptionItem;
import entity.Reservation;
import entity.Room;
import enumeration.RoomExceptionType;
import java.util.Date;
import java.util.List;
import javax.ejb.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.exception.ReservationAddRoomException;
import util.exception.ReservationAddRoomExceptionItemException;
import util.exception.ReservationForTodayNotFoundException;
import util.exception.RoomAddReservationException;

/**
 *
 * @author Toh Seaw Teng
 */
@Singleton
public class RoomAllocationSessionBean implements RoomAllocationSessionBeanRemote, RoomAllocationSessionBeanLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;
    

    /** 22. Allocate Room to Current Day Reservations
     * get all reservation which check in on current date
     * get room by the required room type
     * If the required room(s) for the reserved room type is not available, raise an exception in the exception report
     * can auto upgrade one level only, if need to upgrade 2 level, don't auto assign
     * trigger every day at 2am
    **/
    
    @Override
    public void allocateReservationRoomDaily() throws ReservationForTodayNotFoundException, RoomAddReservationException, ReservationAddRoomException, ReservationAddRoomExceptionItemException {
        Date today = new Date();
        
        List<Reservation> reservationList = em.createQuery("SELECT r FROM Reservation r WHERE r.checkInDate = :today")
                .setParameter("today", today)
                .getResultList();
        
        if (reservationList.size() < 1) {
            throw new ReservationForTodayNotFoundException("No Reservation found for today.");
        }
        
        for (Reservation r : reservationList) {
            Integer roomRank = r.getRoomType().getRoomRank();
            Integer roomRequired = r.getNumOfRoom();
            
            // get all the available room for this room type
            List<Room> roomList = em.createQuery(
                "SELECT r FROM Room r WHERE r.roomType.name = :roomType AND r.disabled = false OR r.roomStatus != UNAVAILABLE")
                .setParameter("roomType", r.getRoomType().getName())
                .getResultList();
            
            if (roomList.size() > 0) {
                for (Room room : roomList) {
                    List<Reservation> roomReservationList = room.getReservationList();
                    Reservation recentReservation = roomReservationList.get(roomReservationList.size() - 1);
                    // only if isOccupied then need to check the checkout date
                    if (!room.getIsOccupied() || 
                            room.getIsOccupied() && recentReservation.getCheckOutDate().equals(today)) {
                        try {
                            room.setIsOccupied(Boolean.FALSE);
                            room.addReservation(r);
                            r.addRoom(room);
                            roomRequired--;
                        } catch (RoomAddReservationException ex) {
                            throw new RoomAddReservationException("Reservation already added to room");
                        } catch (ReservationAddRoomException ex) {
                            throw new ReservationAddRoomException("Room already added to reservation");
                        }
                    }

                    if (roomRequired < 1) {
                        break;
                    }
                }
            }
            
            // not more available room
            // upgrade
            if (roomRequired > 0 && roomRank != 1) {
                // get available room for next rank
                List<Room> roomNextRankList = em.createQuery(
                    "SELECT r FROM Room r WHERE r.roomType.roomRank = :roomRank AND r.disabled = false OR r.roomStatus != UNAVAILABLE")
                    .setParameter("roomRank", roomRank - 1)
                    .getResultList();
                
                // upgrade the room
                if (roomNextRankList.size() > 0) {
                    for (Room room : roomNextRankList) {
                        List<Reservation> roomReservationList = room.getReservationList();
                        Reservation recentReservation = roomReservationList.get(roomReservationList.size() - 1);
                        // only if isOccupied then need to check the checkout date
                        // create exception
                        if (!room.getIsOccupied() || 
                                room.getIsOccupied() && recentReservation.getCheckOutDate().equals(today)) {
                            try {
                                room.setIsOccupied(Boolean.FALSE);
                                room.addReservation(r);
                                r.addRoom(room);
                                
                                // add room exception
                                ExceptionItem roomException = new ExceptionItem(RoomExceptionType.UPGRADE_ALLOCATED, today);
                                roomException.setReservation(r);
                                r.addExceptionItem(roomException);
                                
                                roomRequired--;
                            } catch (RoomAddReservationException ex) {
                                throw new RoomAddReservationException("Reservation already added to room");
                            } catch (ReservationAddRoomException ex) {
                                throw new ReservationAddRoomException("Room already added to reservation");
                            } catch (ReservationAddRoomExceptionItemException ex) {
                                throw new ReservationAddRoomExceptionItemException("Room exception already added to reservation");
                            }
                        }

                        if (roomRequired < 1) {
                            break;
                        }
                    }
                }
            }
            
            // if still have reservation don't have room allocated
            // create exception report type 2
            if (roomRequired > 0) {
                while (roomRequired > 0) {
                    ExceptionItem roomException = new ExceptionItem(RoomExceptionType.NOT_ALLOCATION, today);
                    roomException.setReservation(r);
                    r.addExceptionItem(roomException);
                    
                    roomRequired--;
                }
            }
        }
    }
}
