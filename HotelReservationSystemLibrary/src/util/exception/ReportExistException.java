/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Exception.java to edit this template
 */
package util.exception;

/**
 *
 * @author priskilarebecca.p
 */
public class ReportExistException extends Exception {

    /**
     * Creates a new instance of <code>ReportExistException</code> without
     * detail message.
     */
    public ReportExistException() {
    }

    /**
     * Constructs an instance of <code>ReportExistException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public ReportExistException(String msg) {
        super(msg);
    }
}
