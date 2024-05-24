package org.example;

import java.io.Serializable;

public class EmployeeDTO implements Serializable {
    public Employee employee;
    public EmployeeType type;

    public EmployeeDTO(Employee employee, EmployeeType type) {
        this.employee = employee;
        this.type = type;
    }

    @Override
    public String toString() {
        if(!employee.getActive())
            return "INACTIVE "+type.toString()+" "+employee.getName()+" "+employee.getSurname();
        return type.toString()+" "+employee.getName()+" "+employee.getSurname();
    }
}
