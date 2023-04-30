package com.example.roadsafety20;

public class Users {

    String email, mobileNumber,userName;

    public Users() {
    }

    public Users(String email, String mobileNumber, String userName) {
        this.email = email;
        this.mobileNumber = mobileNumber;
        this.userName = userName;
    }


    public String getEmail() {
        return email;
    }
    public String getUserName() {
        return userName;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

}
