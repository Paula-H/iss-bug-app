package org.example.rpcprotocol.dto;

import org.example.EmployeeDTO;

import java.io.Serializable;

public class PasswordDTO implements Serializable {
    public EmployeeDTO employee;
    public String password;

    public PasswordDTO(EmployeeDTO employee, String password) {
        this.employee = employee;
        this.password = password;
    }
}
