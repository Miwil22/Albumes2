package org.example.users.dto;
public record RegisterRequest(String username, String password, String nombre, String apellidos, String email) {}