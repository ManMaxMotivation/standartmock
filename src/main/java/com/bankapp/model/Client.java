package com.bankapp.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // ID будет генерироваться автоматически
    private Long id;  // Заменили UUID на Long для совместимости с базой данных

    @Column(nullable = false)  // Добавлена аннотация для обязательности поля в базе данных
    private String fullName;

    @Column(nullable = false, unique = true)  // Логин клиента должен быть уникальным
    private String phone;

    @Column(nullable = false, unique = true)  // Логин клиента должен быть уникальным
    private String username;

    @Column(nullable = false)  // Пароль клиента обязательное поле
    private String password;

    @OneToMany(mappedBy = "client")  // Ссылается на класс Account, чтобы установить связь один ко многим
    private List<com.bankapp.model.Account> accounts = new ArrayList<>(); // Список счетов клиента

    // Конструктор, сеттеры и геттеры

    public Client() {
    }

    public Client(String fullName, String phone, String username, String password) {
        this.fullName = fullName;
        this.phone = phone;
        this.username = username;
        this.password = password;
    }

    // Геттеры и сеттеры

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<com.bankapp.model.Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<com.bankapp.model.Account> accounts) {
        this.accounts = accounts;
    }
}
