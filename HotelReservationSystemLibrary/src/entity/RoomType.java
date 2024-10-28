/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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
    private String name;
    @Column(nullable = false, length = 150)
    @NotNull
    @Size(max = 150)
    private String description;
    @Column(nullable = false, precision = 11, scale = 2)
    @NotNull
    @DecimalMin("0.00")
    @Digits(integer = 9, fraction = 2)
    private BigDecimal size;
    @Column(nullable = false)
    @NotNull
    @Min(1)
    @Max(5)
    private Integer beds;
    @Column(nullable = false)
    @NotNull
    @Min(1)
    @Max(5)
    private Integer capacity;
    @Column(nullable = false, length = 50)
    @NotNull
    @Size(min = 4, max = 50)
    private String amenities;
    @Column(nullable = false)
    @NotNull
    private Boolean disabled;
    
    @OneToMany(mappedBy = "roomList")
    private List<Room> roomList;
    
    @OneToMany(mappedBy = "reservationList")
    private List<Reservation> reservationList;
    
    @OneToMany(mappedBy = "roomRateList")
    private List<RoomRate> roomRateList;

    public RoomType() {
    }

    public RoomType(String name, String description, BigDecimal size, Integer beds, Integer capacity, String amenities, Boolean disabled) {
        this.name = name;
        this.description = description;
        this.size = size;
        this.beds = beds;
        this.capacity = capacity;
        this.amenities = amenities;
        this.disabled = disabled;
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
    
}
