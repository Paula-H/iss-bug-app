package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import org.example.controller.LoginController;
import org.example.rpcprotocol.ServerRpcProxy;

import java.io.IOException;
import java.util.Properties;

public class StartClient extends Application {
    private static int defaultChatPort = 55555;
    private static String defaultServer = "localhost";

    @Override
    public void start(Stage primaryStage) throws Exception {
        Properties clientProps = new Properties();
        try {
            clientProps.load(StartClient.class.getResourceAsStream("/client.config"));
            clientProps.list(System.out);
        } catch (IOException e) {
            System.out.println("Cannot find chatclient.properties " + e);
            return;
        }
        String serverIP = clientProps.getProperty("chat.server.host", defaultServer);
        int serverPort = defaultChatPort;

        try {
            serverPort = Integer.parseInt(clientProps.getProperty("chat.server.port"));
        } catch (NumberFormatException ex) {
            System.out.println("Wrong port number " + ex);
        }

        Service server = new ServerRpcProxy(serverIP, serverPort);
        //Services server = new ServerProtoBufProxy(serverIP, serverPort);

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/login-view.fxml"));
        Parent root = loader.load();
        LoginController controller = loader.getController();
        controller.setServer(server);
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Login");
        primaryStage.show();

    }
}

