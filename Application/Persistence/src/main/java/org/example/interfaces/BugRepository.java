package org.example.interfaces;

import org.example.Bug;

public interface BugRepository extends Repository<Bug>{
    Bug findBugByName(String name);
    Iterable<Bug> getUnfixedBugs();
}
