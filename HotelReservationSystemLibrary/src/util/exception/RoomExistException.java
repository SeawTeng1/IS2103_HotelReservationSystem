/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Exception.java to edit this template
 */
package util.exception;

/**
 *
 * @author priskilarebecca.p
 */
public class RoomExistException extends Exception {

    /**
     * Creates a new instance of <code>RoomExistException</code> without detail
     * message.
     */
    public RoomExistException() {
    }

    /**
     * Constructs an instance of <code>RoomExistException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public RoomExistException(String msg) {
        super(msg);
    }
}
