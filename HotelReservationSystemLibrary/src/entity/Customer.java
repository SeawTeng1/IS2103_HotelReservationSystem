/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 *
 * @author Toh Seaw Teng
 */
@Entity
public class Customer extends Guest {

    public Customer() {
        super();
    }
    
    @Override
    public String toString() {
        return "entity.Customer[ id=" + super.getGuestId() + " ]";
    }
}
