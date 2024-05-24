package org.example.interfaces;

import org.example.Tester;

public interface TesterRepository extends Repository<Tester> {
    Tester findTesterByUsernameAndPassword(String username, String password);
}
