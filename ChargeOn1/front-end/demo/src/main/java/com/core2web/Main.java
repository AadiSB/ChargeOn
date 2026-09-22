package com.core2web;

import com.core2web.view.AdminDashboard;
import com.core2web.view.DriverDashboard;
import com.core2web.view.LoginPage;
import com.core2web.view.OwnerDashboard;

import javafx.application.Application;

public class Main {
    public static void main(String[] args) {
        try {
            Class.forName("com.core2web.config.FirebaseInitialize");
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("Hello world!");
        Application.launch(LoginPage.class, args);
    }
}
