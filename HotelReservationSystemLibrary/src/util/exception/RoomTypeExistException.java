/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Exception.java to edit this template
 */
package util.exception;

/**
 *
 * @author priskilarebecca.p
 */
public class RoomTypeExistException extends Exception {

    /**
     * Creates a new instance of <code>RoomTypeExistException</code> without
     * detail message.
     */
    public RoomTypeExistException() {
    }

    /**
     * Constructs an instance of <code>RoomTypeExistException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public RoomTypeExistException(String msg) {
        super(msg);
    }
}
