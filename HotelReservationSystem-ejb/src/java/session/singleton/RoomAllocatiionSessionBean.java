/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB31/SingletonEjbClass.java to edit this template
 */
package session.singleton;

import javax.ejb.Singleton;

/**
 *
 * @author Toh Seaw Teng
 */
@Singleton
public class RoomAllocatiionSessionBean implements RoomAllocatiionSessionBeanRemote, RoomAllocatiionSessionBeanLocal {

    /** 22. Allocate Room to Current Day Reservations
     * get all reservation which check in on current date
     * get room by the required room type
     * If the required room(s) for the reserved room type is not available, raise an exception in the exception report
     * can auto upgrade one level only, if need to upgrade 2 level, don't auto assign
     * trigger every day at 2am
    **/
    
    
}
