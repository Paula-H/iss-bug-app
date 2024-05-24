package org.example;

import org.example.interfaces.*;

import java.time.LocalDateTime;

public class AdminService{
    private AdminRepository adminRepository;
    private DeveloperRepository developerRepository;
    private TesterRepository testerRepository;


    public void addEmployee(EmployeeType type, String name,String surname, LocalDateTime dateOfBirth, String username, String password) {
        switch (type) {
            case Admin:
                Admin admin = new Admin(name, surname, dateOfBirth, username, password);
                adminRepository.save(admin);
                break;
            case Developer:
                Developer developer = new Developer(name, surname, dateOfBirth, username, password);
                developerRepository.save(developer);
                break;
            case Tester:
                Tester tester = new Tester(name, surname, dateOfBirth, username, password);
                testerRepository.save(tester);
                break;
        }
    }

    public void deleteEmployee(Employee employee){
        employee.setActive(false);
        if(employee instanceof Admin){
            adminRepository.update((Admin) employee);
        }
        else if(employee instanceof Developer){
            developerRepository.update((Developer) employee);
        }
        else if(employee instanceof Tester){
            testerRepository.update((Tester) employee);
        }
    }
}
