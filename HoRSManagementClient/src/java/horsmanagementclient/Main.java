/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package horsmanagementclient;

import javax.ejb.EJB;
import session.stateful.WalkInRoomReservationRemote;
import session.stateless.EmployeeSessionBeanRemote;
import session.stateless.GuestSessionBeanRemote;
import session.stateless.PartnerSessionBeanRemote;
import session.stateless.RoomAllocationExceptionReportSessionBeanRemote;
import session.stateless.RoomRateSessionBeanRemote;
import session.stateless.RoomSessionBeanRemote;
import session.stateless.RoomTypeSessionBeanRemote;

/**
 *
 * @author Toh Seaw Teng
 */
public class Main {

    @EJB
    private static WalkInRoomReservationRemote walkInRoomReservationRemote;

    @EJB
    private static RoomAllocationExceptionReportSessionBeanRemote roomAllocationExceptionReportSessionBeanRemote;

    @EJB
    private static RoomSessionBeanRemote roomSessionBeanRemote;

    @EJB
    private static RoomRateSessionBeanRemote roomRateSessionBeanRemote;
    
    
    @EJB
    private static RoomTypeSessionBeanRemote roomTypeSessionBeanRemote;

    @EJB
    private static GuestSessionBeanRemote guestSessionBeanRemote;

    @EJB
    private static PartnerSessionBeanRemote partnerSessionBeanRemote;

    @EJB
    private static EmployeeSessionBeanRemote employeeSessionBeanRemote;
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        MainApp mainApp = new MainApp(employeeSessionBeanRemote,  partnerSessionBeanRemote, roomTypeSessionBeanRemote, roomRateSessionBeanRemote, roomSessionBeanRemote, roomAllocationExceptionReportSessionBeanRemote, walkInRoomReservationRemote, guestSessionBeanRemote);
        mainApp.runApp();
    }
    
}
