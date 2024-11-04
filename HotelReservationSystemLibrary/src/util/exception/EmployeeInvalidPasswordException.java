/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Exception.java to edit this template
 */
package util.exception;

/**
 *
 * @author priskilarebecca.p
 */
public class EmployeeInvalidPasswordException extends Exception {

    /**
     * Creates a new instance of <code>EmployeeInvalidPasswordException</code>
     * without detail message.
     */
    public EmployeeInvalidPasswordException() {
    }

    /**
     * Constructs an instance of <code>EmployeeInvalidPasswordException</code>
     * with the specified detail message.
     *
     * @param msg the detail message.
     */
    public EmployeeInvalidPasswordException(String msg) {
        super(msg);
    }
}
