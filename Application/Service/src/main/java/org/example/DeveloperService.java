package org.example;

import org.example.interfaces.*;

public class DeveloperService{
    private DeveloperRepository developerRepository;
    private BugRepository bugRepository;
    public void solveBug(Developer developer,Bug bug){
        bug.setStatus(BugStatus.Fixed);
        bug.setSolvedBy(developer.getId());
        this.bugRepository.update(bug);

    }
}
