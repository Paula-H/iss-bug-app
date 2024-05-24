package org.example;

import jakarta.persistence.*;
import jakarta.persistence.Entity;

import java.time.LocalDateTime;
@Entity
@Table( name = "Developer" )
public class Developer extends Employee {
    public Developer(){
        super();
    }

    public Developer(String name, String surname, LocalDateTime dateOfBirth,String username, String password){
        super(name,surname,dateOfBirth,username,password);
    }

    @Override
    @Id
    @GeneratedValue(generator="increment")
    public Integer getId() {
        return super.getId();
    }

    public void setId(Integer id){
        super.setId(id);
    }

    @Column(name = "name")
    public String getName(){
        return super.getName();
    }

    public void setName(String name){
        super.setName(name);
    }

    @Column(name = "surname")
    public String getSurname(){
        return super.getSurname();
    }

    public void setSurname(String surname){
        super.setSurname(surname);
    }

    @Column(name = "dateOfBirth")
    public LocalDateTime getDateOfBirth(){
        return super.getDateOfBirth();
    }

    public void setDateOfBirth(LocalDateTime dateOfBirth){
        super.setDateOfBirth(dateOfBirth);
    }

    @Column(name = "username")
    public String getUsername(){
        return super.getUsername();
    }

    public void setUsername(String username){
        super.setUsername(username);
    }

    @Column(name = "password")
    public String getPassword(){
        return super.getPassword();
    }

    public void setPassword(String password){
        super.setPassword(password);
    }

    @Column(name = "active")
    public Boolean getActive(){
        return super.getActive();
    }

    public void setActive(Boolean active){
        super.setActive(active);
    }
}
