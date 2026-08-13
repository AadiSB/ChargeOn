package com.logix.view;

import com.logix.view.CreateAccount;
import com.logix.view.ForgotPass;
import com.logix.view.LoginPage;


import javafx.application.*;
public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
        Application.launch(LoginPage.class,args);

    }
}