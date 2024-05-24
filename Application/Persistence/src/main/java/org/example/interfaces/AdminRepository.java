package org.example.interfaces;

import org.example.Admin;

public interface AdminRepository extends Repository<Admin>{
    Admin findAdminByUsernameAndPassword(String username, String password);
}
