package org.example.interfaces;

import org.example.Developer;

public interface DeveloperRepository extends Repository<Developer>{
    Developer findDeveloperByUsernameAndPassword(String username, String password);
}
