/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionRemote.java to edit this template
 */
package session.stateless;

import entity.RoomAllocationExceptionReport;
import java.util.Date;
import java.util.List;
import javax.ejb.Remote;
import util.exception.InputDataValidationException;
import util.exception.ReportExistException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author priskilarebecca.p
 */
@Remote
public interface RoomAllocationExceptionReportSessionBeanRemote {
    public RoomAllocationExceptionReport createReport(RoomAllocationExceptionReport newReport, Long reservationId) throws UnknownPersistenceException, ReportExistException, InputDataValidationException;

    public List<RoomAllocationExceptionReport> viewAllReports();
    
     public List<RoomAllocationExceptionReport> viewReportsbyDate(Date reportDate);
    
}
