/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Exception.java to edit this template
 */
package util.exception;

/**
 *
 * @author priskilarebecca.p
 */
public class PartnerInvalidPasswordException extends Exception {

    /**
     * Creates a new instance of <code>PartnerInvalidPasswordException</code>
     * without detail message.
     */
    public PartnerInvalidPasswordException() {
    }

    /**
     * Constructs an instance of <code>PartnerInvalidPasswordException</code>
     * with the specified detail message.
     *
     * @param msg the detail message.
     */
    public PartnerInvalidPasswordException(String msg) {
        super(msg);
    }
}
