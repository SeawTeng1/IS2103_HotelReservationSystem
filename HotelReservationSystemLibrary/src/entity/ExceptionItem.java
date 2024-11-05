/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import enumeration.RoomExceptionType;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Toh Seaw Teng
 */
@Entity
public class ExceptionItem implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long exceptionItemId;
    @Column(nullable = false)
    @NotNull
    private RoomExceptionType exceptionType;
    @Column(nullable = false)
    @NotNull
    private Date exceptionDate;
    
    @ManyToOne(optional = false, cascade = {}, fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Reservation reservation;

    public ExceptionItem() {
    }

    public ExceptionItem(RoomExceptionType exceptionType, Date exceptionDate) {
        this.exceptionType = exceptionType;
        this.exceptionDate = exceptionDate;
    }

    public Long getExceptionItemId() {
        return exceptionItemId;
    }

    public void setExceptionItemId(Long exceptionItemId) {
        this.exceptionItemId = exceptionItemId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (exceptionItemId != null ? exceptionItemId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the exceptionItemId fields are not set
        if (!(object instanceof ExceptionItem)) {
            return false;
        }
        ExceptionItem other = (ExceptionItem) object;
        if ((this.exceptionItemId == null && other.exceptionItemId != null) || (this.exceptionItemId != null && !this.exceptionItemId.equals(other.exceptionItemId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.ExceptionItem[ id=" + exceptionItemId + " ]";
    }

    /**
     * @return the exceptionType
     */
    public RoomExceptionType getExceptionType() {
        return exceptionType;
    }

    /**
     * @param exceptionType the exceptionType to set
     */
    public void setExceptionType(RoomExceptionType exceptionType) {
        this.exceptionType = exceptionType;
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
    
}
