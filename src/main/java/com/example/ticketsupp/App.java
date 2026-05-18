package com.example.ticketsupp;

import com.example.ticketsupp.model.TicketStore;
import com.example.ticketsupp.view.MainView;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage stage) {
        TicketStore ticketStore = new TicketStore();
        MainView mainView = new MainView(ticketStore, stage);

        Scene scene = new Scene(mainView, 1280, 780);
        stage.setTitle("Customer Support Ticket Management System");
        stage.setMinWidth(1100);
        stage.setMinHeight(700);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
