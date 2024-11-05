/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import java.io.Serializable;
import java.util.Date;
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
    //@Column(nullable = false)
    //@NotNull
    //private RoomAllocationExceptionReportType type;
    @Column(nullable = false)
    @NotNull
    private String details;
    @Column(nullable = false)
    @NotNull
    private Date reportDate;
    
    @OneToOne()
    private Reservation reservation;

    public RoomAllocationExceptionReport(String details, Reservation reservation, Date reportDate) {
        this.details = details;
        this.reservation = reservation;
        this.reportDate = reportDate;
    }

    public RoomAllocationExceptionReport() {
    }
    

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

    /**
     * @return the reservation
     */
    public Reservation getReservation() {
        return reservation;
    }

    /**
     * @param reservation the reservation to set
     */
    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    /**
     * @return the details
     */
    public String getDetails() {
        return details;
    }

    /**
     * @param details the details to set
     */
    public void setDetails(String details) {
        this.details = details;
    }

    /**
     * @return the type
     */
    //public RoomAllocationExceptionReportType getType() {
        //return type;
    //}

    /**
     * @param type the type to set
     */
    //public void setType(RoomAllocationExceptionReportType type) {
        //this.type = type;
    //}

    /**
     * @return the reportDate
     */
    public Date getReportDate() {
        return reportDate;
    }

    /**
     * @param reportDate the reportDate to set
     */
    public void setReportDate(Date reportDate) {
        this.reportDate = reportDate;
    }
    
}
