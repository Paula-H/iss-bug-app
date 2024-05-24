package org.example.rpcprotocol;


import org.example.*;
import org.example.observer.Event;
import org.example.observer.Observer;
import org.example.rpcprotocol.dto.PasswordDTO;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ServerRpcProxy implements Service {
    private String host;
    private int port;

    private Observer client;

    private ObjectInputStream input;
    private ObjectOutputStream output;
    private Socket connection = null;

    private BlockingQueue<Response> qresponses;
    private volatile boolean finished;

    public ServerRpcProxy(String serverIP, int serverPort) {

        this.host = serverIP;
        this.port = serverPort;
        qresponses = new LinkedBlockingQueue<Response>();
        //initializeConnection();
    }

    private void closeConnection() {
        finished = true;
        try {
            input.close();
            output.close();
            connection.close();
            client = null;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void sendRequest(Request request) {
        try {
            output.writeObject(request);
            output.flush();
        } catch (IOException e) {
            throw new RuntimeException("Error sending object " + e);
        }

    }

    private Response readResponse() {
        Response response = null;
        try {
            response = qresponses.take();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return response;
    }

    private void initializeConnection(){
        try {
            connection = new Socket(host, port);
            output = new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input = new ObjectInputStream(connection.getInputStream());
            finished = false;
            startReader();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startReader() {
        Thread tw = new Thread(new ReaderThread());
        tw.start();
    }

    @Override
    public EmployeeDTO logIn(String username, String password, Observer client) {
        if(connection == null || connection.isClosed())
            initializeConnection();
        Employee user = new Employee(null,null,null,username,password);
        Request request = new Request.Builder().type(RequestType.LOGIN).data(user).build();
        sendRequest(request);
        Response response = readResponse();
        if (response.type() == ResponseType.OK) {
            this.client = client;
            return (EmployeeDTO) response.data();
        }
        if (response.type() == ResponseType.ERROR) {
            closeConnection();
            throw  new RuntimeException("Authentication failed");
        }

        return null;
    }

    @Override
    public void addEmployee(EmployeeType type, String name, String surname, LocalDateTime dateOfBirth, String username, String password) {
        Request request = new Request.Builder().type(RequestType.ADD_EMPLOYEE).data(new EmployeeDTO(new Employee(name,surname,dateOfBirth,username,password),type)).build();
        sendRequest(request);
        Response response = readResponse();
        if (response.type() == ResponseType.ERROR) {
            throw new RuntimeException("Error adding employee");
        }
    }

    @Override
    public void deleteEmployee(EmployeeDTO employee) {
        Request request = new Request.Builder().type(RequestType.DELETE_EMPLOYEE).data(employee).build();
        sendRequest(request);
        Response response = readResponse();
        if (response.type() == ResponseType.ERROR) {
            throw new RuntimeException("Error deleting employee");
        }

    }

    @Override
    public Iterable<EmployeeDTO> getEmployees() {
        Request request = new Request.Builder().type(RequestType.GET_EMPLOYEES).build();
        sendRequest(request);
        Response response = readResponse();
        if (response.type() == ResponseType.ERROR) {
            throw new RuntimeException("Error getting employees");
        }
        return (Iterable<EmployeeDTO>) response.data();
    }

    @Override
    public void changePassword(EmployeeDTO employee, String password) {
        PasswordDTO passwordDTO = new PasswordDTO(employee, password);
        Request request = new Request.Builder().type(RequestType.CHANGE_PASSWORD).data(passwordDTO).build();
        sendRequest(request);
        Response response = readResponse();
        if (response.type() == ResponseType.ERROR) {
            throw new RuntimeException("Error changing password");
        }
    }

    @Override
    public void deactivateAccount(EmployeeDTO employee) {

    }

    @Override
    public void logOut(EmployeeDTO employee) {
        Request request = new Request.Builder().type(RequestType.LOGOUT).data(employee).build();
        sendRequest(request);
        Response response = readResponse();
        if (response.type() == ResponseType.ERROR) {
            throw new RuntimeException("Error logging out");
        }
    }

    @Override
    public void addNewBug(String name, String description, Tester tester) {
        Request request = new Request.Builder().type(RequestType.ADD_BUG).data(new Bug(name,description,BugStatus.Unfixed,tester)).build();
        sendRequest(request);
        Response response = readResponse();
        if (response.type() == ResponseType.ERROR) {
            throw new RuntimeException("Error adding bug");
        }

    }

    @Override
    public void modifyBug(Bug bug, String description) {
        bug.setDescription(description);
        Request request = new Request.Builder().type(RequestType.MODIFY_BUG).data(bug).build();
        sendRequest(request);
        Response response = readResponse();
        if (response.type() == ResponseType.ERROR) {
            throw new RuntimeException("Error modifying bug");
        }
    }

    @Override
    public Tester findTesterByID(Integer id) {
        Request request = new Request.Builder().type(RequestType.FIND_TESTER).data(id).build();
        sendRequest(request);
        Response response = readResponse();
        if (response.type() == ResponseType.ERROR) {
            throw new RuntimeException("Error finding tester");
        }
        if(response.type() == ResponseType.FOUND_TESTER) {
            return (Tester) response.data();
        }
        return null;
    }

    @Override
    public Developer findDeveloperByID(Integer id) {
        Request request = new Request.Builder().type(RequestType.FIND_DEVELOPER).data(id).build();
        sendRequest(request);
        Response response = readResponse();
        if (response.type() == ResponseType.ERROR) {
            throw new RuntimeException("Error finding developer");
        }
        if(response.type() == ResponseType.FOUND_DEVELOPER) {
            return (Developer) response.data();
        }
        return null;
    }

    @Override
    public Iterable<Bug> getBugs() {
        Request request = new Request.Builder().type(RequestType.GET_BUGS).build();
        sendRequest(request);
        Response response = readResponse();
        if (response.type() == ResponseType.ERROR) {
            throw new RuntimeException("Error getting bugs");
        }
        return (Iterable<Bug>) response.data();
    }

    @Override
    public Iterable<Bug> getUnfixedBugs() {
        Request request = new Request.Builder().type(RequestType.GET_UNFIXED_BUGS).build();
        sendRequest(request);
        Response response = readResponse();
        if (response.type() == ResponseType.ERROR) {
            throw new RuntimeException("Error getting unfixed bugs");
        }
        return (Iterable<Bug>) response.data();
    }

    @Override
    public void solveBug(Bug bug, Developer developer) {
        bug.setStatus(BugStatus.Fixed);
        bug.setSolvedBy(developer.getId());
        Request request = new Request.Builder().type(RequestType.SOLVE_BUG).data(bug).build();
        sendRequest(request);
        Response response = readResponse();
        if (response.type() == ResponseType.ERROR) {
            throw new RuntimeException("Error solving bug");
        }
    }

//    @Override
//    public Volunteer logIn(String username, String password, Observer client) throws ApplicationException {
//        initializeConnection();
//        Volunteer user = new Volunteer(null,null,username,password);
//        Request request = new Request.Builder().type(RequestType.logIn).data(user).build();
//        sendRequest(request);
//        Response response = readResponse();
//        if (response.type() == ResponseType.OK) {
//            this.client = client;
//            return (Volunteer) response.data();
//        }
//        if (response.type() == ResponseType.error) {
//            closeConnection();
//            throw  new ApplicationException("Authentication failed");
//        }
//
//        return null;
//    }
//
//    @Override
//    public Iterable<org.example.model.Donor> getAllDonors() throws ApplicationException {
//
//        Request request = new Request.Builder().type(RequestType.getDonors).build();
//        sendRequest(request);
//        Response response = readResponse();
//        if (response.type() == ResponseType.error) {
//            throw new ApplicationException("Error getting donators");
//        }
//        return (Iterable<Donor>) response.data();
//    }
//
//    @Override
//    public Map<String, Float> getDonationsSumForCharityCases() throws ApplicationException {
//        Request request = new Request.Builder().type(RequestType.getChairtyCases).build();
//        sendRequest(request);
//        Response response = readResponse();
//        if (response.type() == ResponseType.error) {
//            throw new ApplicationException("Error getting charity cases");
//        }
//        return (Map<String, Float>) response.data();
//    }
//
//    @Override
//    public org.example.model.CharityCase findCharityCaseByName(String key) throws ApplicationException {
//
//        Request request = new Request.Builder().type(RequestType.getCharityCaseByName).data(key).build();
//        sendRequest(request);
//        Response response = readResponse();
//        if (response.type() == ResponseType.error) {
//            throw new ApplicationException("Error getting the charity case..");
//        }
//        return (CharityCase) response.data();
//    }
//
//    @Override
//    public void addDonation(Donor currentDonor, CharityCase currentCharityCase, Float sum) throws ApplicationException {
//
//        Donation donation = new Donation(currentDonor, currentCharityCase, sum);
//        Request request = new Request.Builder().type(RequestType.addDonation).data(donation).build();
//        sendRequest(request);
//        Response response = readResponse();
//        if (response.type() == ResponseType.error) {
//            throw new ApplicationException("Error adding the donation ...");
//        }
//       //lient.update();
//    }


    private class ReaderThread implements Runnable {
        public void run() {
            while (!finished) {
                try {
                    Object response = input.readObject();
                    System.out.println("response received " + response);
                    if (isUpdateResponse((Response) response)) {
                        handleUpdate((Response) response);
                    }
                     else {
                        try {
                            qresponses.put((Response) response);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException | ClassNotFoundException e) {
                    System.out.println("Reading error " + e);
                }
            }
        }

        private void handleUpdate(Response response1) {
            try{
                client.update((Event)response1.data());
            } catch (RuntimeException e) {
                throw new RuntimeException(e);
            }
        }

        private boolean isUpdateResponse(Response response1) {
            return response1.type() == ResponseType.UPDATE;
        }
    }
}
