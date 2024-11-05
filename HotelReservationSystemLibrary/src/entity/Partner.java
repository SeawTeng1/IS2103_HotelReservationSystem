/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import util.exception.PartnerAddReservationException;

/**
 *
 * @author Toh Seaw Teng
 */
@Entity
public class Partner implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long partnerId;
    @Column(nullable = false, unique=true, length = 24)
    @NotNull
    @Size(min = 4, max = 24)
    private String username;
    @Column(nullable = false, length = 24)
    @NotNull
    @Size(min = 8, max = 24)
    private String password;
    
    @OneToMany(mappedBy = "Partner", cascade = {}, fetch = FetchType.LAZY)
    private List<Reservation> reservationList;

    public Partner() {
    }

    public Partner(String username, String password) {
        this.username = username;
        this.password = password;
    }
    
    public Long getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(Long partnerId) {
        this.partnerId = partnerId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (partnerId != null ? partnerId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the partnerId fields are not set
        if (!(object instanceof Partner)) {
            return false;
        }
        Partner other = (Partner) object;
        if ((this.partnerId == null && other.partnerId != null) || (this.partnerId != null && !this.partnerId.equals(other.partnerId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Partner[ id=" + partnerId + " ]";
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
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
    
    public void addReservation(Reservation reservation) throws PartnerAddReservationException 
    {
        if(reservation != null && !this.getReservationList().contains(reservation))
        {
            this.getReservationList().add(reservation);
        }
        else
        {
            throw new PartnerAddReservationException("Reservation already added to room");
        }
    }
}
