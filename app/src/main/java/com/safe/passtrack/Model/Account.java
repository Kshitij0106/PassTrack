package com.safe.passtrack.Model;

public class Account {

    private String accountCategory, accountName;

    public Account() {
    }

    public Account(String accountCategory) {
        this.accountCategory = accountCategory;
    }


    public Account(String accountName, String accountCategory) {
        this.accountName = accountName;
        this.accountCategory = accountCategory;
    }

    public String getAccountCategory() {
        return accountCategory;
    }

    public void setAccountCategory(String accountCategory) {
        this.accountCategory = accountCategory;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

}
