package com.bankapp.service;

import com.bankapp.model.Account;
import com.bankapp.model.Client;
import com.bankapp.repository.AccountRepository;
import com.bankapp.repository.ClientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final ClientRepository clientRepository;

    public AccountService(AccountRepository accountRepository,
                          ClientRepository clientRepository) {
        this.accountRepository = accountRepository;
        this.clientRepository = clientRepository;
    }

    // Добавленный метод для совместимости с контроллером
    @Transactional
    public Account createAccount(Long clientId) {
        return createAccount(clientId, 0.0); // Вызов существующего метода с балансом по умолчанию
    }

    @Transactional
    public Account createAccount(Long clientId, double initialBalance) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client not found with id: " + clientId));

        Account account = new Account();
        account.setBalance(initialBalance);
        account.setClient(client);
        client.getAccounts().add(account);

        return accountRepository.save(account);
    }

    public Optional<Account> findByAccountNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber);
    }

    public List<Account> findAccountsByClientName(String fullName) {
        return accountRepository.findByClientFullName(fullName);
    }

    public List<Account> findAccountsWithBalanceGreaterThan(double amount) {
        return accountRepository.findByBalanceGreaterThan(amount);
    }

    @Transactional(readOnly = true)
    public List<Account> findAccountsByClientId(Long clientId) {
        return accountRepository.findByClientId(clientId);
    }

    @Transactional
    public void transferMoney(String fromAccountNumber,
                              String toAccountNumber,
                              double amount) {
        Account fromAccount = accountRepository.findByAccountNumber(fromAccountNumber)
                .orElseThrow(() -> new RuntimeException("Source account not found"));

        Account toAccount = accountRepository.findByAccountNumber(toAccountNumber)
                .orElseThrow(() -> new RuntimeException("Target account not found"));

        if (fromAccount.getBalance() < amount) {
            throw new RuntimeException("Insufficient funds");
        }

        fromAccount.setBalance(fromAccount.getBalance() - amount);
        toAccount.setBalance(toAccount.getBalance() + amount);

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);
    }

    @Transactional
    public Account updateBalance(String accountNumber, double newBalance) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        account.setBalance(newBalance);
        return accountRepository.save(account);
    }
}