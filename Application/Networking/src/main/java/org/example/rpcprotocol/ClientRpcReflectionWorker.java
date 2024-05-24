package org.example.rpcprotocol;



import org.example.*;
import org.example.observer.Event;
import org.example.observer.Observer;
import org.example.rpcprotocol.dto.PasswordDTO;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;

public class ClientRpcReflectionWorker implements  Runnable, Observer {

    private Service server;
    private Socket connection;

    private ObjectInputStream input;
    private ObjectOutputStream output;
    private volatile boolean connected;

    private static Response okResponse = new Response.Builder().type(ResponseType.OK).build();
    private static Response errorResponse = new Response.Builder().type(ResponseType.ERROR).build();

    public ClientRpcReflectionWorker(Service chatServer, Socket client) {
        this.server = chatServer;
        this.connection = client;
        try {
            output = new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input = new ObjectInputStream(connection.getInputStream());
            connected = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (connected) {
            try {
                    Object request = input.readObject();
                    Response response = handleRequest((Request) request);
                    if (response != null) {
                        sendResponse(response);
                    }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }

        }
        try {
            input.close();
            output.close();
            connection.close();
        } catch (IOException e) {
            System.out.println("Error " + e);
        }
    }

    private Response handleRequest(Request request) {
        Response response = null;
        String handlerName = "handle" + (request).type();
        System.out.println("HandlerName " + handlerName);
        try {
            Method method = this.getClass().getDeclaredMethod(handlerName, Request.class);
            response = (Response) method.invoke(this, request);
            System.out.println("Method " + handlerName + " invoked");
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return response;
    }


    public Response handleLOGIN(Request request) {
        System.out.println("Login request ..." + request.type());
        Employee user = (Employee) request.data();
        try {
            var loginDTO = server.logIn(user.getUsername(), user.getPassword(), this);
            if (loginDTO!=null) {
                return new Response.Builder().type(ResponseType.OK).data(loginDTO).build();
            }
            else {
                connected = false;
                return new Response.Builder().type(ResponseType.ERROR).data("Authentication failed").build();

            }
        } catch (RuntimeException e) {
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }

    public void handleADD_BUG(Request request){
        System.out.println("Add bug request ..." + request.type());
        Bug bug = (Bug) request.data();
        try {
            Tester tester = server.findTesterByID(bug.getFoundBy());
            server.addNewBug(bug.getName(), bug.getDescription(), tester);
            sendResponse(okResponse);
        } catch (RuntimeException e) {
            Response response = new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
            sendResponse(response);
        }
    }

    public void handleGET_BUGS(Request request){
        System.out.println("Get bugs request ..." + request.type());
        try {
            Iterable<Bug> bugs = server.getBugs();
            Response response = new Response.Builder().type(ResponseType.GET_BUGS).data(bugs).build();
            sendResponse(response);
        } catch (RuntimeException e) {
            Response response = new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
            sendResponse(response);
        }
    }

    public void handleGET_UNFIXED_BUGS(Request request){
        System.out.println("Get unfixed bugs request ..." + request.type());
        try {
            Iterable<Bug> bugs = server.getUnfixedBugs();
            Response response = new Response.Builder().type(ResponseType.GET_UNFIXED_BUGS).data(bugs).build();
            sendResponse(response);
        } catch (RuntimeException e) {
            Response response = new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
            sendResponse(response);
        }
    }

    public void handleFIND_DEVELOPER(Request request){
        System.out.println("Find developer request ..." + request.type());
        Integer id = (Integer) request.data();
        try {
            Developer developer = server.findDeveloperByID(id);
            Response response = new Response.Builder().type(ResponseType.FOUND_DEVELOPER).data(developer).build();
            sendResponse(response);
        } catch (RuntimeException e) {
            Response response = new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
            sendResponse(response);
        }
    }

    public void handleSOLVE_BUG(Request request){
        System.out.println("Solve bug request ..." + request.type());
        Bug bug = (Bug) request.data();
        try {
            Developer developer = server.findDeveloperByID(bug.getSolvedBy());
            server.solveBug(bug, developer);
            sendResponse(okResponse);
        } catch (RuntimeException e) {
            Response response = new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
            sendResponse(response);
        }
    }

    public void handleMODIFY_BUG(Request request){
        System.out.println("Modify bug request ..." + request.type());
        Bug bug = (Bug) request.data();
        try {
            server.modifyBug(bug, bug.getDescription());
            sendResponse(okResponse);
        } catch (RuntimeException e) {
            Response response = new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
            sendResponse(response);
        }
    }

    public void handleFIND_TESTER(Request request){
        System.out.println("Find tester request ..." + request.type());
        Integer id = (Integer) request.data();
        try {
            Tester tester = server.findTesterByID(id);
            Response response = new Response.Builder().type(ResponseType.FOUND_TESTER).data(tester).build();
            sendResponse(response);
        } catch (RuntimeException e) {
            Response response = new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
            sendResponse(response);
        }
    }

    public void handleGET_EMPLOYEES(Request request) {
        System.out.println("Get employees request ..." + request.type());
        try {
            Iterable<EmployeeDTO> employees = server.getEmployees();
            Response response = new Response.Builder().type(ResponseType.GET_EMPLOYEES).data(employees).build();
            sendResponse(response);
        } catch (RuntimeException e) {
            Response response = new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
            sendResponse(response);
        }
    }

    public void handleDELETE_EMPLOYEE(Request request) {
        System.out.println("Delete employee request ..." + request.type());
        EmployeeDTO employee = (EmployeeDTO) request.data();
        try {
            server.deleteEmployee(employee);
            sendResponse(okResponse);
        } catch (RuntimeException e) {
            Response response = new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
            sendResponse(response);
        }
    }

    public void handleADD_EMPLOYEE(Request request) {
        System.out.println("Add employee request ..." + request.type());
        EmployeeDTO employee = (EmployeeDTO) request.data();
        try {
            server.addEmployee(employee.type, employee.employee.getName(), employee.employee.getSurname(), employee.employee.getDateOfBirth(), employee.employee.getUsername(), employee.employee.getPassword());
            sendResponse(okResponse);
        } catch (RuntimeException e) {
            Response response = new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
            sendResponse(response);
        }
    }

    public void handleCHANGE_PASSWORD(Request request) {
        System.out.println("Change password request ..." + request.type());
        PasswordDTO employee = (PasswordDTO) request.data();
        try {
            server.changePassword(employee.employee, employee.password);
            sendResponse(okResponse);
        } catch (RuntimeException e) {
            Response response = new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
            sendResponse(response);
        }
    }

    public void handleLOGOUT(Request request) {
        System.out.println("Logout request ..." + request.type());
        EmployeeDTO employee = (EmployeeDTO) request.data();
        try {
            server.logOut(employee);
            sendResponse(okResponse);
        } catch (RuntimeException e) {
            Response response = new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
            sendResponse(response);
        }
    }
//
//    public Response handlegetDonors(Request request) {
//        System.out.println("Get all cazuri caritabile request ..." + request.type());
//        try {
//            return new Response.Builder().type(ResponseType.OK).data(server.getAllDonors()).build();
//        } catch (ApplicationException e) {
//            e.printStackTrace();
//            return new Response.Builder().type(ResponseType.error).data(e.getMessage()).build();
//        }
//    }
//
//    public Response handlegetChairtyCases(Request request) {
//        System.out.println("Get donation sum for caz request ..." + request.type());
//        try {
//            return new Response.Builder().type(ResponseType.OK).data(server.getDonationsSumForCharityCases()).build();
//        }
//        catch (ApplicationException e) {
//            e.printStackTrace();
//            return new Response.Builder().type(ResponseType.error).data(e.getMessage()).build();
//        }
//    }
//
//    public Response handlegetCharityCaseByName(Request request){
//        System.out.println("Get charity case by name request ..." + request.type());
//        String key = (String)request.data();
//        try {
//            return new Response.Builder().type(ResponseType.OK).data((CharityCase)server.findCharityCaseByName(key)).build();
//        } catch (ApplicationException e) {
//            e.printStackTrace();
//            return new Response.Builder().type(ResponseType.error).data(e.getMessage()).build();
//        }
//
//    }
//
//
//    public Response handleaddDonation(Request request) throws SQLException, ApplicationException {
//        System.out.println("Add donation request ..." + request.type());
//        if (request.data() == null) {
//            return new Response.Builder().type(ResponseType.error).data("Invalid request data").build();
//        }
//        Donation donation = (Donation) request.data();
//        server.addDonation(donation.getDonor(), donation.getCharityCase(), donation.getSum());
//        //update();
//        return new Response.Builder().type(ResponseType.addDonation).data(donation).build();
//    }
    private synchronized void sendResponse(Response response) {
        try {
            output.writeObject(response);
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Event event) {
        System.out.println("Update called ...");
        Response response = new Response.Builder().type(ResponseType.UPDATE).data(event).build();
        sendResponse(response);
    }

//    @Override
//    public void updateCharityCases(DonationsSumsDonorsDTO map) throws ApplicationException {
//        System.out.println("Update called ...");
//        System.out.println("A ajuns aici 1");
//        Map<String,Float> charitySums = server.getDonationsSumForCharityCases();
//        Iterable<Donor> donors = server.getAllDonors();
//DonationsSumsDonorsDTO donationsSumsDonorsDTO = new DonationsSumsDonorsDTO(charitySums, donors);
//        Response response = new Response.Builder().type(ResponseType.update).data(donationsSumsDonorsDTO).build();
//        sendResponse(response);
//    }

}
