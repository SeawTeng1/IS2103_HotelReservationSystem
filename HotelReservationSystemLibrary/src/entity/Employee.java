/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import enumeration.RoleType;
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
import util.exception.EmployeeAddReservationException;
import util.exception.RoomAddReservationException;

/**
 *
 * @author Toh Seaw Teng
 */
@Entity
public class Employee implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long employeeId;
    @Column(nullable = false, unique=true, length = 24)
    @NotNull
    @Size(min = 4, max = 24)
    private String username;
    @Column(nullable = false, length = 24)
    @NotNull
    @Size(min = 8, max = 24)
    private String password;
    @Column(nullable = false)
    @NotNull
    private RoleType roleType;
    
    @OneToMany(mappedBy = "Employee", cascade = {}, fetch = FetchType.LAZY)
    private List<Reservation> reservationList;

    public Employee() {
    }

    public Employee(String username, String password, RoleType roleType) {
        this.username = username;
        this.password = password;
        this.roleType = roleType;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (employeeId != null ? employeeId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the employeeId fields are not set
        if (!(object instanceof Employee)) {
            return false;
        }
        Employee other = (Employee) object;
        if ((this.employeeId == null && other.employeeId != null) || (this.employeeId != null && !this.employeeId.equals(other.employeeId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Employee[ id=" + employeeId + " ]";
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
     * @return the roleType
     */
    public RoleType getRoleType() {
        return roleType;
    }

    /**
     * @param roleType the roleType to set
     */
    public void setRoleType(RoleType roleType) {
        this.roleType = roleType;
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
    
    public void addReservation(Reservation reservation) throws EmployeeAddReservationException 
    {
        if(reservation != null && !this.getReservationList().contains(reservation))
        {
            this.getReservationList().add(reservation);
        }
        else
        {
            throw new EmployeeAddReservationException("Reservation already added to Employee");
        }
    }
}
