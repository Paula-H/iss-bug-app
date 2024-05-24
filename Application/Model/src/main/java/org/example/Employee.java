package org.example;

import java.time.LocalDateTime;

public class Employee extends Entity<Integer>{
    private String name;
    private String surname;
    private LocalDateTime dateOfBirth;
    private String username;
    private String password;
    private Boolean active;


    public Employee(String name, String surname, LocalDateTime dateOfBirth, String username, String password) {
        super();
        this.name = name;
        this.surname = surname;
        this.dateOfBirth = dateOfBirth;
        this.username = username;
        this.password = password;
        this.active = true;
    }

    public Employee(){
        super();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public LocalDateTime getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDateTime dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
