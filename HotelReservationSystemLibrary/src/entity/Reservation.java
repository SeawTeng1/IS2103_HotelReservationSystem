/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import util.exception.ReservationAddRoomException;
/**
 *
 * @author Toh Seaw Teng
 */
@Entity
public class Reservation implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reservationId;
    @Column(nullable = false)
    @NotNull
    @FutureOrPresent 
    private Date checkInDate;
    @Column(nullable = false)
    @NotNull
    @FutureOrPresent 
    private Date checkOutDate;
    @Column(nullable = false, precision = 11, scale = 2)
    @NotNull
    @DecimalMin("0.00")
    @Digits(integer = 9, fraction = 2)
    private BigDecimal totalPrice;
    @Column(nullable = false)
    @NotNull
    @Min(1)
    private Integer numOfRoom;
    @Column(nullable = false)
    @NotNull
    private Boolean isCheckIn = false;
    @Column(nullable = false)
    @NotNull
    private Boolean isCheckOut = false;
    
    @ManyToOne(fetch = FetchType.LAZY)
    private Guest guest;
    
    @ManyToOne(fetch = FetchType.LAZY)
    private Employee employee;
    
    @ManyToOne(fetch = FetchType.LAZY)
    private Partner partner;
    
    @ManyToMany
    @JoinTable(name = "ReservationRecord", joinColumns =
    @JoinColumn(name = "ROOM_ID"), inverseJoinColumns = @JoinColumn(name = "RESERVATION_ID"))
    private List<Room> roomList;
    
    @ManyToOne(optional = false, cascade = {}, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false)
    private RoomType roomType;
    
    @ManyToOne(optional = false, cascade = {}, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false)
    private RoomRate roomRate;
    
    @OneToOne(mappedBy="Reservation")
    private RoomAllocationExceptionReport report;
    
    public Reservation() {
        this.roomList = new ArrayList<>();
    }

    public Reservation(Date checkInDate, Date checkOutDate, BigDecimal totalPrice, Integer numOfRoom) {
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.totalPrice = totalPrice;
        this.numOfRoom = numOfRoom;
        this.roomList = new ArrayList<>();
    }

    public Long getReservationId() {
        return reservationId;
    }

    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (reservationId != null ? reservationId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the reservationId fields are not set
        if (!(object instanceof Reservation)) {
            return false;
        }
        Reservation other = (Reservation) object;
        if ((this.reservationId == null && other.reservationId != null) || (this.reservationId != null && !this.reservationId.equals(other.reservationId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Reservation[ id=" + reservationId + " ]";
    }

    /**
     * @return the checkInDate
     */
    public Date getCheckInDate() {
        return checkInDate;
    }

    /**
     * @param checkInDate the checkInDate to set
     */
    public void setCheckInDate(Date checkInDate) {
        this.checkInDate = checkInDate;
    }

    /**
     * @return the checkOutDate
     */
    public Date getCheckOutDate() {
        return checkOutDate;
    }

    /**
     * @param checkOutDate the checkOutDate to set
     */
    public void setCheckOutDate(Date checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    /**
     * @return the totalPrice
     */
    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    /**
     * @param totalPrice the totalPrice to set
     */
    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    /**
     * @return the numOfRoom
     */
    public Integer getNumOfRoom() {
        return numOfRoom;
    }

    /**
     * @param numOfRoom the numOfRoom to set
     */
    public void setNumOfRoom(Integer numOfRoom) {
        this.numOfRoom = numOfRoom;
    }

    /**
     * @return the guest
     */
    public Guest getGuest() {
        return guest;
    }

    /**
     * @param guest the guest to set
     */
    public void setGuest(Guest guest) {
        this.guest = guest;
    }

    /**
     * @return the employee
     */
    public Employee getEmployee() {
        return employee;
    }

    /**
     * @param employee the employee to set
     */
    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    /**
     * @return the partner
     */
    public Partner getPartner() {
        return partner;
    }

    /**
     * @param partner the partner to set
     */
    public void setPartner(Partner partner) {
        this.partner = partner;
    }

    /**
     * @return the roomList
     */
    public List<Room> getRoomList() {
        return roomList;
    }

    /**
     * @param roomList the roomList to set
     */
    public void setRoomList(List<Room> roomList) {
        this.roomList = roomList;
    }

    /**
     * @return the roomRate
     */
    public RoomRate getRoomRate() {
        return roomRate;
    }

    /**
     * @param roomRate the roomRate to set
     */
    public void setRoomRate(RoomRate roomRate) {
        this.roomRate = roomRate;
    }

    /**
     * @return the roomType
     */
    public RoomType getRoomType() {
        return roomType;
    }

    /**
     * @param roomType the roomType to set
     */
    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    /**
     * @return the isCheckIn
     */
    public Boolean getIsCheckIn() {
        return isCheckIn;
    }

    /**
     * @param isCheckIn the isCheckIn to set
     */
    public void setIsCheckIn(Boolean isCheckIn) {
        this.isCheckIn = isCheckIn;
    }

    /**
     * @return the isCheckOut
     */
    public Boolean getIsCheckOut() {
        return isCheckOut;
    }

    /**
     * @param isCheckOut the isCheckOut to set
     */
    public void setIsCheckOut(Boolean isCheckOut) {
        this.isCheckOut = isCheckOut;
    }
    

    public void addRoom(Room room) throws ReservationAddRoomException 
    {
        if(room != null && !this.getRoomList().contains(room))
        {
            this.getRoomList().add(room);
        }
        else
        {
            throw new ReservationAddRoomException("Room already added to reservation");
        }
    }

    public RoomAllocationExceptionReport getReport() {
        return report;
    }

    public void setReport(RoomAllocationExceptionReport report) {
        this.report = report;
    }
}
