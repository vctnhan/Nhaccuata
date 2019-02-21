package com.example.hanwool.saleapp.modal;

import java.io.Serializable;

public class User implements Serializable {
    String name;
    String email;
    String password;
    String phonenumber;
public  User(){

}
    public User(String name, String email, String password, String phonenumber) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phonenumber = phonenumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }
}
