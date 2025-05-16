package com.bankapp.repository;

import com.bankapp.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    // 1. Поиск по номеру счёта
    Optional<Account> findByAccountNumber(String accountNumber);

    // 2. Поиск по балансу
    List<Account> findByBalanceGreaterThan(double balance);

    // 3. Поиск по имени клиента
    @Query("SELECT a FROM Account a WHERE a.client.fullName = :fullName")
    List<Account> findByClientFullName(@Param("fullName") String fullName);

    // 4. Новый метод: поиск счетов по ID клиента (добавлен сейчас)
    List<Account> findByClientId(Long clientId);
}