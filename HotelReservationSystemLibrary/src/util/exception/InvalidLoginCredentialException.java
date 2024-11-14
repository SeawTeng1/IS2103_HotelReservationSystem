/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Exception.java to edit this template
 */
package util.exception;

/**
 *
 * @author priskilarebecca.p
 */
public class InvalidLoginCredentialException extends Exception {

    /**
     * Creates a new instance of <code>InvalidLoginCredentialException</code>
     * without detail message.
     */
    public InvalidLoginCredentialException() {
    }

    /**
     * Constructs an instance of <code>InvalidLoginCredentialException</code>
     * with the specified detail message.
     *
     * @param msg the detail message.
     */
    public InvalidLoginCredentialException(String msg) {
        super(msg);
    }
}
