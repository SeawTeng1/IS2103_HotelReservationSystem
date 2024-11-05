/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import enumeration.RoomAllocationExceptionReportType;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

/**
 *
 * @author priskilarebecca.p
 */
@Entity
public class RoomAllocationExceptionReport implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomAllocationExceptionReportId;  
    @Column(nullable = false)
    @NotNull
    private RoomAllocationExceptionReportType type;
    @Column(nullable = false)
    @NotNull
    private String details;
    
    @OneToOne(mappedBy="roomAllocationExceptionReport")
    private Reservation reservation;

    public Long getRoomAllocationExceptionReportId() {
        return roomAllocationExceptionReportId;
    }

    public void setRoomAllocationExceptionReportId(Long roomAllocationExceptionReportId) {
        this.roomAllocationExceptionReportId = roomAllocationExceptionReportId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (roomAllocationExceptionReportId != null ? roomAllocationExceptionReportId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the roomAllocationExceptionReportId fields are not set
        if (!(object instanceof RoomAllocationExceptionReport)) {
            return false;
        }
        RoomAllocationExceptionReport other = (RoomAllocationExceptionReport) object;
        if ((this.roomAllocationExceptionReportId == null && other.roomAllocationExceptionReportId != null) || (this.roomAllocationExceptionReportId != null && !this.roomAllocationExceptionReportId.equals(other.roomAllocationExceptionReportId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.RoomAllocationExceptionReport[ id=" + roomAllocationExceptionReportId + " ]";
    }
    
}
