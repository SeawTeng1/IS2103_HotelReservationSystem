/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package horsreservationclient;

import javax.ejb.EJB;
import session.stateful.GuestRoomReservationSessionBeanRemote;
import session.stateless.GuestSessionBeanRemote;

/**
 *
 * @author Toh Seaw Teng
 */
public class Main {

    @EJB(name = "GuestSessionBeanRemote")
    private static GuestSessionBeanRemote guestSessionBeanRemote;

    @EJB(name = "GuestRoomReservationSessionBeanRemote")
    private static GuestRoomReservationSessionBeanRemote guestRoomReservationSessionBeanRemote;

    public static void main(String[] args) {
        MainApp mainApp = new MainApp(guestSessionBeanRemote, guestRoomReservationSessionBeanRemote);
        mainApp.runApp();
    }    
}
