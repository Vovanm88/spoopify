package com.musicservice.model;

import org.springframework.security.core.GrantedAuthority;

import lombok.Getter;

/**
 * Перечисление ролей пользователей в системе.
 * Реализует GrantedAuthority для интеграции со Spring Security.
 */
@Getter
public enum UserRole implements GrantedAuthority {
    ADMIN("ROLE_ADMIN", "Администратор системы"),
    USER("ROLE_USER", "Обычный пользователь");

    private final String role;
    private final String description;

    UserRole(String role, String description) {
        this.role = role;
        this.description = description;
    }

    /**
     * Получение authority для Spring Security.
     * @return строковое представление роли
     */
    @Override
    public String getAuthority() {
        return role;
    }

    /**
     * Получение роли по её строковому представлению.
     * @param role строковое представление роли
     * @return объект UserRole или null, если роль не найдена
     */
    public static UserRole fromString(String role) {
        for (UserRole userRole : UserRole.values()) {
            if (userRole.getRole().equals(role) || 
                userRole.name().equals(role)) {
                return userRole;
            }
        }
        throw new IllegalArgumentException("Неизвестная роль: " + role);
    }

    /**
     * Проверка, является ли роль административной.
     * @return true, если роль административная
     */
    public boolean isAdmin() {
        return this == ADMIN;
    }

    @Override
    public String toString() {
        return role;
    }
} 