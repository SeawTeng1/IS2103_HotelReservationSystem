/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import util.exception.RoomTypeAddReservationException;
import util.exception.RoomTypeAddRoomRateException;
import util.exception.RoomTypeRemoveRoomRateException;

/**
 *
 * @author Toh Seaw Teng
 */
@Entity
public class RoomType implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomTypeId;
    @Column(nullable = false, unique = true, length = 50)
    @NotNull
    @Size(min = 4, max = 50)
    // Deluxe Room, Premier Room, Family Room, Junior Suite and Grand Suite
    private String name;
    @Column(length = 150)
    @Size(max = 150)
    private String description;
    @Column(precision = 11, scale = 2)
    @DecimalMin("0.00")
    @Digits(integer = 9, fraction = 2)
    private BigDecimal size;
    @Column()
    @Min(1)
    @Max(5)
    private Integer beds;
    @Column()
    @Min(1)
    @Max(5)
    private Integer capacity;
    @Column(length = 50)
    @Size(min = 4, max = 50)
    private String amenities;
    @Column(nullable = false)
    @NotNull
    private Boolean disabled = false;
    @Column()
    @Min(1)
    // for the upgrade of room
    private Integer roomRank; 
    
    @OneToMany(mappedBy = "RoomType", cascade = {}, fetch = FetchType.LAZY)
    private List<Room> roomList;
    
    @OneToMany(mappedBy = "RoomType", cascade = {}, fetch = FetchType.LAZY)
    private List<Reservation> reservationList;
    
    @OneToMany(mappedBy = "RoomType", cascade = {}, fetch = FetchType.LAZY)
    private List<RoomRate> roomRateList;
    
    //unidirectional
    @OneToOne(fetch = FetchType.EAGER)
    private RoomType higherRoomType;

    public RoomType() {
        this.reservationList = new ArrayList<>();
        this.roomList = new ArrayList<>();
        this.roomRateList = new ArrayList<>();
    }

    public RoomType(String name, String description, BigDecimal size, Integer beds, Integer capacity, String amenities, Boolean disabled, Integer roomRank) {
        this.name = name;
        this.description = description;
        this.size = size;
        this.beds = beds;
        this.capacity = capacity;
        this.amenities = amenities;
        this.disabled = disabled;
        this.roomRank = roomRank;
        
        this.reservationList = new ArrayList<>();
        this.roomList = new ArrayList<>();
        this.roomRateList = new ArrayList<>();
    }

    public RoomType(String name) {
        this.name = name;
        
        this.reservationList = new ArrayList<>();
        this.roomList = new ArrayList<>();
        this.roomRateList = new ArrayList<>();
    }
        
    public Long getRoomTypeId() {
        return roomTypeId;
    }

    public void setRoomTypeId(Long roomTypeId) {
        this.roomTypeId = roomTypeId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (roomTypeId != null ? roomTypeId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the roomTypeId fields are not set
        if (!(object instanceof RoomType)) {
            return false;
        }
        RoomType other = (RoomType) object;
        if ((this.roomTypeId == null && other.roomTypeId != null) || (this.roomTypeId != null && !this.roomTypeId.equals(other.roomTypeId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.RoomType[ id=" + roomTypeId + " ]";
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
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the size
     */
    public BigDecimal getSize() {
        return size;
    }

    /**
     * @param size the size to set
     */
    public void setSize(BigDecimal size) {
        this.size = size;
    }

    /**
     * @return the beds
     */
    public Integer getBeds() {
        return beds;
    }

    /**
     * @param beds the beds to set
     */
    public void setBeds(Integer beds) {
        this.beds = beds;
    }

    /**
     * @return the capacity
     */
    public Integer getCapacity() {
        return capacity;
    }

    /**
     * @param capacity the capacity to set
     */
    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    /**
     * @return the amenities
     */
    public String getAmenities() {
        return amenities;
    }

    /**
     * @param amenities the amenities to set
     */
    public void setAmenities(String amenities) {
        this.amenities = amenities;
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
    
    public void addReservation(Reservation reservation) throws RoomTypeAddReservationException 
    {
        if(reservation != null && !this.getReservationList().contains(reservation))
        {
            this.getReservationList().add(reservation);
        }
        else
        {
            throw new RoomTypeAddReservationException("Reservation already added to room type");
        }
    }

    /**
     * @return the roomRateList
     */
    public List<RoomRate> getRoomRateList() {
        return roomRateList;
    }

    /**
     * @param roomRateList the roomRateList to set
     */
    public void setRoomRateList(List<RoomRate> roomRateList) {
        this.roomRateList = roomRateList;
    }
    
    public void addRoomRate(RoomRate roomRate) throws RoomTypeAddRoomRateException 
    {
        if(roomRate != null && !this.getRoomRateList().contains(roomRate)) {
            this.getRoomRateList().add(roomRate);
        }
        else {
            throw new RoomTypeAddRoomRateException("Room rate already added to this room type");
        }
    }
    
    public void removeRoomrate(RoomRate roomRate) throws RoomTypeRemoveRoomRateException
    {
        if(roomRate != null && this.getRoomRateList().contains(roomRate))
        {
            this.getRoomRateList().remove(roomRate);
        }
        else
        {
            throw new RoomTypeRemoveRoomRateException("Room rate has not been added to room type");
        }
    }

    /**
     * @return the roomRank
     */
    public Integer getRoomRank() {
        return roomRank;
    }

    /**
     * @param roomRank the roomRank to set
     */
    public void setRoomRank(Integer roomRank) {
        this.roomRank = roomRank;
    }

    /**
     * @return the higherRoomType
     */
    public RoomType getHigherRoomType() {
        return higherRoomType;
    }

    /**
     * @param higherRoomType the higherRoomType to set
     */
    public void setHigherRoomType(RoomType higherRoomType) {
        this.higherRoomType = higherRoomType;
    }

}
