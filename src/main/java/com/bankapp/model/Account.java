package com.bankapp.model;

// Импорты необходимых библиотек
import jakarta.persistence.*; // Импортируем JPA аннотации
import lombok.Data; // Аннотация Lombok для автоматической генерации геттеров, сеттеров, toString и др.
import java.util.UUID; // Класс Java для генерации уникальных идентификаторов

// Аннотация Lombok @Data автоматически создает геттеры, сеттеры, equals, hashCode и toString
@Entity // Помечаем как JPA-сущность
@Data
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Генерация ID через БД
    private Long id; // Уникальный идентификатор аккаунта

    @Column(nullable = false, unique = true, length = 12)
    private String accountNumber; // Номер счета (уникальный, 12 символов)

    @Column(nullable = false, unique = true, length = 16)
    private String cardNumber; // Номер карты (уникальный, 16 символов)

    @Column(nullable = false)
    private double balance; // Баланс счета

    @ManyToOne // Связь: многие аккаунты принадлежат одному клиенту
    @JoinColumn(name = "client_id", nullable = false) // Внешний ключ client_id
    private Client client; // Владелец аккаунта

    // Конструктор без параметров
    public Account() {
        // Генерируем номер счета: берем UUID, убираем дефисы и обрезаем до 12 символов
        this.accountNumber = UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        // Генерируем номер карты: берем UUID, убираем дефисы и обрезаем до 16 символов
        this.cardNumber = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }

    // Геттер для ID аккаунта (возвращает уникальный идентификатор)
    public Long getId() {
        return id;
    }

    // Геттер для номера счета (возвращает номер счета)
    public String getAccountNumber() {
        return accountNumber;
    }

    // Геттер для номера карты (возвращает номер карты)
    public String getCardNumber() {
        return cardNumber;
    }

    // Геттер для баланса (возвращает текущий баланс счета)
    public double getBalance() {
        return balance;
    }

    // Сеттер для баланса (позволяет установить новое значение баланса)
    public void setBalance(double balance) {
        this.balance = balance;
    }

    // Геттер и сеттер для клиента

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}