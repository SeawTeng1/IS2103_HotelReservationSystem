/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Exception.java to edit this template
 */
package util.exception;

/**
 *
 * @author priskilarebecca.p
 */
public class RoomTypeDeleteException extends Exception {

    /**
     * Creates a new instance of <code>RoomTypeDeleteException</code> without
     * detail message.
     */
    public RoomTypeDeleteException() {
    }

    /**
     * Constructs an instance of <code>RoomTypeDeleteException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public RoomTypeDeleteException(String msg) {
        super(msg);
    }
}
