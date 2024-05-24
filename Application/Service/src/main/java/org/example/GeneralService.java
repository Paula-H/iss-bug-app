package org.example;

import org.example.interfaces.*;

public class GeneralService{
    private AdminRepository adminRepository;
    private DeveloperRepository developerRepository;
    private TesterRepository testerRepository;
    private BugRepository bugRepository;

    public Employee logIn(String username, String password) {
        if(adminRepository.findAdminByUsernameAndPassword(username,password)!=null){
            return adminRepository.findAdminByUsernameAndPassword(username,password);
        }
        else if(developerRepository.findDeveloperByUsernameAndPassword(username,password)!=null){
            return developerRepository.findDeveloperByUsernameAndPassword(username,password);
        }
        else if(testerRepository.findTesterByUsernameAndPassword(username,password)!=null){
            return testerRepository.findTesterByUsernameAndPassword(username,password);
        }
        return null;
    }

    public Iterable<Bug> getBugs(){
        return bugRepository.findAll();
    }
}
