package org.example;


import org.example.interfaces.*;
import org.example.observer.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;

public class Server implements Service{
    private AdminRepository adminRepository;
    private DeveloperRepository developerRepository;
    private TesterRepository testerRepository;
    private BugRepository bugRepository;

    private Map<String, Observer> loggedClients;

    public Server(AdminRepository adminRepository, DeveloperRepository developerRepository, TesterRepository testerRepository, BugRepository bugRepository) {
        this.adminRepository = adminRepository;
        this.developerRepository = developerRepository;
        this.testerRepository = testerRepository;
        this.bugRepository = bugRepository;
        loggedClients = new java.util.concurrent.ConcurrentHashMap<>();
    }

    @Override
    public EmployeeDTO logIn(String username, String password, Observer client) {
        if(adminRepository.findAdminByUsernameAndPassword(username,password)!=null){
            if(client != null)
                loggedClients.put(username,client);
            return new EmployeeDTO(adminRepository.findAdminByUsernameAndPassword(username,password), EmployeeType.Admin);
        }
        else if(developerRepository.findDeveloperByUsernameAndPassword(username,password)!=null){
            if(client != null)
                loggedClients.put(username,client);
            return new EmployeeDTO(developerRepository.findDeveloperByUsernameAndPassword(username,password),EmployeeType.Developer);
        }
        else if(testerRepository.findTesterByUsernameAndPassword(username,password)!=null){
            if(client != null)
                loggedClients.put(username,client);
            return new EmployeeDTO(testerRepository.findTesterByUsernameAndPassword(username,password),EmployeeType.Tester);
        }
        return null;
    }



    @Override
    public void addEmployee(EmployeeType type, String name, String surname, LocalDateTime dateOfBirth, String username, String password) {
        switch (type){
            case Admin:
                adminRepository.save(new Admin(name,surname,dateOfBirth,username,password));
                break;
            case Developer:
                developerRepository.save(new Developer(name,surname,dateOfBirth,username,password));
                break;
            case Tester:
                testerRepository.save(new Tester(name,surname,dateOfBirth,username,password));
                break;
        }
        notifyEmployeeObservers();
    }

    @Override
    public void deleteEmployee(EmployeeDTO employee) {
        employee.employee.setActive(false);
        switch (employee.type){
            case Admin:
                adminRepository.update((Admin) employee.employee);
                break;
            case Developer:
                developerRepository.update((Developer) employee.employee);
                break;
            case Tester:
                testerRepository.update((Tester) employee.employee);
                break;
        }
        notifyEmployeeObservers();

    }

    @Override
    public Iterable<EmployeeDTO> getEmployees() {
        ArrayList<EmployeeDTO> employees = new ArrayList<>();
        this.adminRepository.findAll().forEach(admin -> employees.add(new EmployeeDTO(admin,EmployeeType.Admin)));
        this.developerRepository.findAll().forEach(developer -> employees.add(new EmployeeDTO(developer,EmployeeType.Developer)));
        this.testerRepository.findAll().forEach(tester -> employees.add(new EmployeeDTO(tester,EmployeeType.Tester)));
        return employees;
    }

    @Override
    public void changePassword(EmployeeDTO employee, String password) {
        employee.employee.setPassword(password);
        switch (employee.type){
            case Admin:
                adminRepository.update((Admin) employee.employee);
                break;
            case Developer:
                developerRepository.update((Developer) employee.employee);
                break;
            case Tester:
                testerRepository.update((Tester) employee.employee);
                break;
        }
    }

    @Override
    public void deactivateAccount(EmployeeDTO employee) {
        employee.employee.setActive(false);
        switch (employee.type){
            case Admin:
                adminRepository.update((Admin) employee.employee);
                break;
            case Developer:
                developerRepository.update((Developer) employee.employee);
                break;
            case Tester:
                testerRepository.update((Tester) employee.employee);
                break;
        }
        notifyEmployeeObservers();
    }

    @Override
    public void logOut(EmployeeDTO employee) {
        loggedClients.remove(employee.employee.getUsername());
    }

    @Override
    public void addNewBug(String name, String description, Tester tester) {
        if(bugRepository.findBugByName(name)!=null)
            throw new RuntimeException("Bug already exists!");
        bugRepository.save(new Bug(name,description,BugStatus.Unfixed,tester));
        notifyBugObservers();
    }

    @Override
    public void modifyBug(Bug bug, String description) {
        bug.setDescription(description);
        bugRepository.update(bug);
        notifyBugObservers();
    }

    @Override
    public Tester findTesterByID(Integer id) {
        return testerRepository.findById(id);
    }

    @Override
    public Developer findDeveloperByID(Integer id) {
        return developerRepository.findById(id);
    }

    @Override
    public Iterable<Bug> getBugs() {
        return this.bugRepository.findAll();
    }

    @Override
    public Iterable<Bug> getUnfixedBugs() {
        return this.bugRepository.getUnfixedBugs();
    }

    @Override
    public void solveBug(Bug bug, Developer developer) {
        bug.setStatus(BugStatus.Fixed);
        bug.setSolvedBy(developer.getId());
        bugRepository.update(bug);
        notifyBugObservers();
    }

    public void notifyBugObservers(){
        loggedClients.forEach((s, observer) -> observer.update(this.getCurrentBugEvent()));
    }

    public void notifyEmployeeObservers(){
        loggedClients.forEach((s, observer) -> observer.update(this.getCurrentEmployeesEvent()));
    }

    public Event getCurrentBugEvent(){
        Event event = new Event();
        event.setBugs(this.getUnfixedBugs());
        ArrayList<EmployeeDTO> employees = new ArrayList<>();
        this.adminRepository.findAll().forEach(admin -> employees.add(new EmployeeDTO(admin,EmployeeType.Admin)));
        this.developerRepository.findAll().forEach(developer -> employees.add(new EmployeeDTO(developer,EmployeeType.Developer)));
        this.testerRepository.findAll().forEach(tester -> employees.add(new EmployeeDTO(tester,EmployeeType.Tester)));
        event.setData(employees);
        event.setType(EventType.refresh_bugs);
        return event;
    }

    public Event getCurrentEmployeesEvent(){
        Event event = new Event();
        event.setBugs(this.getUnfixedBugs());
        ArrayList<EmployeeDTO> employees = new ArrayList<>();
        this.adminRepository.findAll().forEach(admin -> employees.add(new EmployeeDTO(admin,EmployeeType.Admin)));
        this.developerRepository.findAll().forEach(developer -> employees.add(new EmployeeDTO(developer,EmployeeType.Developer)));
        this.testerRepository.findAll().forEach(tester -> employees.add(new EmployeeDTO(tester,EmployeeType.Tester)));
        event.setData(employees);
        event.setType(EventType.refresh_employees);
        return event;
    }


}
