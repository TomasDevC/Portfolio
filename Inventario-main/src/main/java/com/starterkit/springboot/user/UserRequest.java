package com.starterkit.springboot.user;

public class UserRequest {
    private String name;
    private String email;
    private String password;
    private String dNascimento; // formato yyyy-MM-dd

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getdNascimento() { return dNascimento; }
    public void setdNascimento(String dNascimento) { this.dNascimento = dNascimento; }
}
