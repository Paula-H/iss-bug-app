package org.example;

import org.example.observer.Observer;

import java.time.LocalDateTime;

public interface Service {
    EmployeeDTO logIn(String username, String password, Observer client);
    void addEmployee(EmployeeType type, String name, String surname, LocalDateTime dateOfBirth, String username, String password);
    void deleteEmployee(EmployeeDTO employee);
    Iterable<EmployeeDTO> getEmployees();
    void changePassword(EmployeeDTO employee, String password);
    void deactivateAccount(EmployeeDTO employee);
    void logOut(EmployeeDTO employee);
    void addNewBug(String name, String description, Tester tester);
    void modifyBug(Bug bug, String description);
    Tester findTesterByID(Integer id);
    Developer findDeveloperByID(Integer id);
    Iterable<Bug> getBugs();
    Iterable<Bug> getUnfixedBugs();
    void solveBug(Bug bug, Developer developer);

}
