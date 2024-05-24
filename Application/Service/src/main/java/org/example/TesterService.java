package org.example;

import org.example.interfaces.BugRepository;
import org.example.interfaces.TesterRepository;

public class TesterService {
    private TesterRepository testerRepository;
    private BugRepository bugRepository;
    public void addBug(Tester tester, String name,String description){
        Bug bug = new Bug(name,description,BugStatus.Unfixed,tester);
        bugRepository.save(bug);
    }
}
