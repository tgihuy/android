package models;

import java.io.Serializable;

public class Account implements Serializable {
    private String name;
    private String email;
    private String pass;
    private String wishlist;
    private String image;
    private int roleId;
    private String roleName;

    public Account() {
    }

    public Account(String name, String email, String image, int roleId, String roleName) {
        this.name = name;
        this.email = email;
        this.image = image;
        this.roleId = roleId;
        this.roleName = roleName;
    }

    public Account(String name, String email, String pass, String wishlist, String image, int roleId, String roleName) {
        this.name = name;
        this.email = email;
        this.pass = pass;
        this.wishlist = wishlist;
        this.image = image;
        this.roleId = roleId;
        this.roleName = roleName;
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

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getWishlist() {
        return wishlist;
    }

    public void setWishlist(String wishlist) {
        this.wishlist = wishlist;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}

