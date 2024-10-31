/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package session.stateless;

import javax.ejb.Stateless;

/**
 *
 * @author Toh Seaw Teng
 */
@Stateless
public class GuestSessionBean implements GuestSessionBeanRemote, GuestSessionBeanLocal {

    /*
        25. Check-in Guest
        Check-in a guest by informing him/her of the allocated room
        Room allocation exception needs to be handled manually
    */
    
     /*
        26. Check-out Guest
        Check-out a guest to indicate the end of his/her visit to the hotel.
    */
}
