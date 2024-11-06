package models;

import java.util.List;

public class Role {
    private String name;
    private List<Account> accounts;

    public Role() {
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }

    public String getName() {
        return name;
    }

    public void setName(String roleName) {
        this.name = roleName;
    }

    public Role(int id, String name, List<Account> accounts) {
        this.name = name;
        this.accounts = accounts;
    }
}
