/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import enumeration.RateType;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import util.exception.RoomRateAddReservationException;

/**
 *
 * @author Toh Seaw Teng
 */
@Entity
public class RoomRate implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomRateId;
    @Column(nullable = false, unique = true, length = 50)
    @NotNull
    @Size(min = 4, max = 50)
    private String name;
    @Column(nullable = false)
    @NotNull
    private RateType rateType;
    @Column(nullable = false, precision = 11, scale = 2)
    @NotNull
    @DecimalMin("0.00")
    @Digits(integer = 9, fraction = 2)
    private BigDecimal ratePerNight;
    @Column(nullable = false)
    @NotNull
    @FutureOrPresent 
    private Date validityStart;
    @Column(nullable = false)
    @NotNull
    @FutureOrPresent 
    private Date validityEnd;
    @Column(nullable = false)
    @NotNull
    private Boolean disabled;
    
    @ManyToOne(fetch = FetchType.LAZY)
    private RoomType roomType;
    
    @OneToMany(mappedBy = "RoomRate")
    private List<Reservation> reservationList;

    public RoomRate() {
    }

    public RoomRate(String name, RateType rateType, BigDecimal ratePerNight, Date validityStart, Date validityEnd, Boolean disabled) {
        this.name = name;
        this.rateType = rateType;
        this.ratePerNight = ratePerNight;
        this.validityStart = validityStart;
        this.validityEnd = validityEnd;
        this.disabled = disabled;
    }

    public Long getRoomRateId() {
        return roomRateId;
    }

    public void setRoomRateId(Long roomRateId) {
        this.roomRateId = roomRateId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (roomRateId != null ? roomRateId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the roomRateId fields are not set
        if (!(object instanceof RoomRate)) {
            return false;
        }
        RoomRate other = (RoomRate) object;
        if ((this.roomRateId == null && other.roomRateId != null) || (this.roomRateId != null && !this.roomRateId.equals(other.roomRateId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.RoomRate[ id=" + roomRateId + " ]";
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the rateType
     */
    public RateType getRateType() {
        return rateType;
    }

    /**
     * @param rateType the rateType to set
     */
    public void setRateType(RateType rateType) {
        this.rateType = rateType;
    }

    /**
     * @return the ratePerNight
     */
    public BigDecimal getRatePerNight() {
        return ratePerNight;
    }

    /**
     * @param ratePerNight the ratePerNight to set
     */
    public void setRatePerNight(BigDecimal ratePerNight) {
        this.ratePerNight = ratePerNight;
    }

    /**
     * @return the validityStart
     */
    public Date getValidityStart() {
        return validityStart;
    }

    /**
     * @param validityStart the validityStart to set
     */
    public void setValidityStart(Date validityStart) {
        this.validityStart = validityStart;
    }

    /**
     * @return the validityEnd
     */
    public Date getValidityEnd() {
        return validityEnd;
    }

    /**
     * @param validityEnd the validityEnd to set
     */
    public void setValidityEnd(Date validityEnd) {
        this.validityEnd = validityEnd;
    }

    /**
     * @return the disabled
     */
    public Boolean getDisabled() {
        return disabled;
    }

    /**
     * @param disabled the disabled to set
     */
    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
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
     * @return the reservationList
     */
    public List<Reservation> getReservationList() {
        return reservationList;
    }

    /**
     * @param reservationList the reservationList to set
     */
    public void setReservationList(List<Reservation> reservationList) {
        this.reservationList = reservationList;
    }
    
    public void addReservation(Reservation reservation) throws RoomRateAddReservationException 
    {
        if(reservation != null && !this.getReservationList().contains(reservation))
        {
            this.getReservationList().add(reservation);
        }
        else
        {
            throw new RoomRateAddReservationException("Reservation already added to room rate");
        }
    }
}
