/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Exception.java to edit this template
 */
package util.exception;

/**
 *
 * @author priskilarebecca.p
 */
public class InvalidAccessRightException extends Exception {

    /**
     * Creates a new instance of <code>InvalidAccessRightException</code>
     * without detail message.
     */
    public InvalidAccessRightException() {
    }

    /**
     * Constructs an instance of <code>InvalidAccessRightException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public InvalidAccessRightException(String msg) {
        super(msg);
    }
}
