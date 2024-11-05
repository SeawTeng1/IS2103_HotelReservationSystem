/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Exception.java to edit this template
 */
package util.exception;

/**
 *
 * @author priskilarebecca.p
 */
public class RoomTypeDisabledException extends Exception {

    /**
     * Creates a new instance of <code>RoomTypeDisabledException</code> without
     * detail message.
     */
    public RoomTypeDisabledException() {
    }

    /**
     * Constructs an instance of <code>RoomTypeDisabledException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public RoomTypeDisabledException(String msg) {
        super(msg);
    }
}
