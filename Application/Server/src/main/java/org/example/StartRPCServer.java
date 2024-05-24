package org.example;

import org.example.hibernate.HibernateAdminRepository;
import org.example.hibernate.HibernateBugRepository;
import org.example.hibernate.HibernateDeveloperRepository;
import org.example.hibernate.HibernateTesterRepository;
import org.example.interfaces.AdminRepository;
import org.example.utils.AbstractServer;
import org.example.utils.RpcConcurentServer;
import org.example.utils.ServerException;

import java.util.Properties;

public class StartRPCServer {
    public static int defaultPort = 55555;

    public static void main(String[] args) {
        Properties serverProps = new Properties();

        try {
            serverProps.load(StartRPCServer.class.getResourceAsStream("/server.config"));
        } catch (Exception e) {
            return;
        }

        HibernateAdminRepository adminRepository = new HibernateAdminRepository();
        HibernateDeveloperRepository developerRepository = new HibernateDeveloperRepository();
        HibernateTesterRepository testerRepository = new HibernateTesterRepository();
        HibernateBugRepository bugRepository = new HibernateBugRepository();
        //System.out.println(testerRepository.findTesterByUsernameAndPassword("tester_la","parola_la1234").getName());

        Service serverImplementation = new Server(adminRepository, developerRepository, testerRepository, bugRepository);
        int serverPort = defaultPort;

        try {
            serverPort = Integer.parseInt(serverProps.getProperty("server.port"));
        } catch (NumberFormatException nfe) {
            System.out.println("Wrong  Port Number");
        }
        AbstractServer server = new RpcConcurentServer(serverPort, serverImplementation);
        try {
            server.start();
        } catch (ServerException e) {
            System.out.println("Error starting server");
        }


    }
}
