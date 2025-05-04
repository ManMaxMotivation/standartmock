package com.bankapp.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class CustomMetricsService {

    private final Counter registerCounter;
    private final Counter loginCounter;
    private final Counter logoutCounter;
    private final Counter isLoggedCounter;
    private final Counter getUserCounter;
    private final Counter createAccountCounter; // Добавлено
    private final Counter getClientsCounter; // Добавлено
    private final Counter selectRecipientCounter; // Добавлено
    private final Counter transferCounter; // Добавлено
    private final Counter sayHelloCounter; // Добавлено

    private final DistributionSummary registerSummary;
    private final DistributionSummary loginSummary;
    private final DistributionSummary logoutSummary;
    private final DistributionSummary isLoggedSummary;
    private final DistributionSummary getUserSummary;
    private final DistributionSummary createAccountSummary; // Добавлено
    private final DistributionSummary getClientsSummary; // Добавлено
    private final DistributionSummary selectRecipientSummary; // Добавлено
    private final DistributionSummary transferSummary; // Добавлено
    private final DistributionSummary sayHelloSummary; // Добавлено

    private final Timer registerTimer;
    private final Timer loginTimer;
    private final Timer logoutTimer;
    private final Timer isLoggedTimer;
    private final Timer getUserTimer;
    private final Timer createAccountTimer; // Добавлено
    private final Timer getClientsTimer; // Добавлено
    private final Timer selectRecipientTimer; // Добавлено
    private final Timer transferTimer; // Добавлено
    private final Timer sayHelloTimer; // Добавлено

    private final AtomicInteger registerCount = new AtomicInteger(0);
    private final AtomicInteger loginCount = new AtomicInteger(0);
    private final AtomicInteger logoutCount = new AtomicInteger(0);
    private final AtomicInteger isLoggedCount = new AtomicInteger(0);
    private final AtomicInteger getUserCount = new AtomicInteger(0);
    private final AtomicInteger createAccountCount = new AtomicInteger(0); // Добавлено
    private final AtomicInteger getClientsCount = new AtomicInteger(0); // Добавлено
    private final AtomicInteger selectRecipientCount = new AtomicInteger(0); // Добавлено
    private final AtomicInteger transferCount = new AtomicInteger(0); // Добавлено
    private final AtomicInteger sayHelloCount = new AtomicInteger(0); // Добавлено

    public CustomMetricsService(MeterRegistry meterRegistry) {
        // Counters
        this.registerCounter = Counter.builder("auth_register_total")
                .description("Total number of registration attempts")
                .tags("environment", "development")
                .register(meterRegistry);
        this.loginCounter = Counter.builder("auth_login_total")
                .description("Total number of login attempts")
                .tags("environment", "development")
                .register(meterRegistry);
        this.logoutCounter = Counter.builder("auth_logout_total")
                .description("Total number of logout attempts")
                .tags("environment", "development")
                .register(meterRegistry);
        this.isLoggedCounter = Counter.builder("auth_is_logged_total")
                .description("Total number of isLogged checks")
                .tags("environment", "development")
                .register(meterRegistry);
        this.getUserCounter = Counter.builder("auth_get_user_total")
                .description("Total number of getUser requests")
                .tags("environment", "development")
                .register(meterRegistry);
        this.createAccountCounter = Counter.builder("account_create_total") // Добавлено
                .description("Total number of account creation attempts")
                .tags("environment", "development")
                .register(meterRegistry);
        this.getClientsCounter = Counter.builder("transaction_get_clients_total") // Добавлено
                .description("Total number of get clients requests")
                .tags("environment", "development")
                .register(meterRegistry);
        this.selectRecipientCounter = Counter.builder("transaction_select_recipient_total") // Добавлено
                .description("Total number of select recipient attempts")
                .tags("environment", "development")
                .register(meterRegistry);
        this.transferCounter = Counter.builder("transaction_transfer_total") // Добавлено
                .description("Total number of transfer attempts")
                .tags("environment", "development")
                .register(meterRegistry);
        this.sayHelloCounter = Counter.builder("hello_say_hello_total") // Добавлено
                .description("Total number of say hello requests")
                .tags("environment", "development")
                .register(meterRegistry);

        // Distribution Summaries
        this.registerSummary = DistributionSummary.builder("auth_register_summary")
                .description("Summary of registration data (e.g., name length)")
                .tags("environment", "development")
                .register(meterRegistry);
        this.loginSummary = DistributionSummary.builder("auth_login_summary")
                .description("Summary of login data (e.g., password length)")
                .tags("environment", "development")
                .register(meterRegistry);
        this.logoutSummary = DistributionSummary.builder("auth_logout_summary")
                .description("Summary of logout data")
                .tags("environment", "development")
                .register(meterRegistry);
        this.isLoggedSummary = DistributionSummary.builder("auth_is_logged_summary")
                .description("Summary of isLogged checks")
                .tags("environment", "development")
                .register(meterRegistry);
        this.getUserSummary = DistributionSummary.builder("auth_get_user_summary")
                .description("Summary of getUser requests")
                .tags("environment", "development")
                .register(meterRegistry);
        this.createAccountSummary = DistributionSummary.builder("account_create_summary") // Добавлено
                .description("Summary of account creation data (e.g., clientId length)")
                .tags("environment", "development")
                .register(meterRegistry);
        this.getClientsSummary = DistributionSummary.builder("transaction_get_clients_summary") // Добавлено
                .description("Summary of get clients requests")
                .tags("environment", "development")
                .register(meterRegistry);
        this.selectRecipientSummary = DistributionSummary.builder("transaction_select_recipient_summary") // Добавлено
                .description("Summary of select recipient data (e.g., username length)")
                .tags("environment", "development")
                .register(meterRegistry);
        this.transferSummary = DistributionSummary.builder("transaction_transfer_summary") // Добавлено
                .description("Summary of transfer data (e.g., amount)")
                .tags("environment", "development")
                .register(meterRegistry);
        this.sayHelloSummary = DistributionSummary.builder("hello_say_hello_summary") // Добавлено
                .description("Summary of say hello data (e.g., name length)")
                .tags("environment", "development")
                .register(meterRegistry);

        // Timers
        this.registerTimer = Timer.builder("auth_register_duration")
                .description("Time taken for registration requests")
                .tags("environment", "development")
                .register(meterRegistry);
        this.loginTimer = Timer.builder("auth_login_duration")
                .description("Time taken for login requests")
                .tags("environment", "development")
                .register(meterRegistry);
        this.logoutTimer = Timer.builder("auth_logout_duration")
                .description("Time taken for logout requests")
                .tags("environment", "development")
                .register(meterRegistry);
        this.isLoggedTimer = Timer.builder("auth_is_logged_duration")
                .description("Time taken for isLogged checks")
                .tags("environment", "development")
                .register(meterRegistry);
        this.getUserTimer = Timer.builder("auth_get_user_duration")
                .description("Time taken for getUser requests")
                .tags("environment", "development")
                .register(meterRegistry);
        this.createAccountTimer = Timer.builder("account_create_duration") // Добавлено
                .description("Time taken for account creation requests")
                .tags("environment", "development")
                .register(meterRegistry);
        this.getClientsTimer = Timer.builder("transaction_get_clients_duration") // Добавлено
                .description("Time taken for get clients requests")
                .tags("environment", "development")
                .register(meterRegistry);
        this.selectRecipientTimer = Timer.builder("transaction_select_recipient_duration") // Добавлено
                .description("Time taken for select recipient requests")
                .tags("environment", "development")
                .register(meterRegistry);
        this.transferTimer = Timer.builder("transaction_transfer_duration") // Добавлено
                .description("Time taken for transfer requests")
                .tags("environment", "development")
                .register(meterRegistry);
        this.sayHelloTimer = Timer.builder("hello_say_hello_duration") // Добавлено
                .description("Time taken for say hello requests")
                .tags("environment", "development")
                .register(meterRegistry);
    }

    // Counter methods
    public void incrementRegisterCounter() {
        registerCounter.increment();
        registerCount.incrementAndGet();
    }

    public void incrementLoginCounter() {
        loginCounter.increment();
        loginCount.incrementAndGet();
    }

    public void incrementLogoutCounter() {
        logoutCounter.increment();
        logoutCount.incrementAndGet();
    }

    public void incrementIsLoggedCounter() {
        isLoggedCounter.increment();
        isLoggedCount.incrementAndGet();
    }

    public void incrementGetUserCounter() {
        getUserCounter.increment();
        getUserCount.incrementAndGet();
    }

    public void incrementCreateAccountCounter() { // Добавлено
        createAccountCounter.increment();
        createAccountCount.incrementAndGet();
    }

    public void incrementGetClientsCounter() { // Добавлено
        getClientsCounter.increment();
        getClientsCount.incrementAndGet();
    }

    public void incrementSelectRecipientCounter() { // Добавлено
        selectRecipientCounter.increment();
        selectRecipientCount.incrementAndGet();
    }

    public void incrementTransferCounter() { // Добавлено
        transferCounter.increment();
        transferCount.incrementAndGet();
    }

    public void incrementSayHelloCounter() { // Добавлено
        sayHelloCounter.increment();
        sayHelloCount.incrementAndGet();
    }

    // DistributionSummary methods
    public void recordRegisterSummary(double value) {
        registerSummary.record(value);
    }

    public void recordLoginSummary(double value) {
        loginSummary.record(value);
    }

    public void recordLogoutSummary(double value) {
        logoutSummary.record(value);
    }

    public void recordIsLoggedSummary(double value) {
        isLoggedSummary.record(value);
    }

    public void recordGetUserSummary(double value) {
        getUserSummary.record(value);
    }

    public void recordCreateAccountSummary(double value) { // Добавлено
        createAccountSummary.record(value);
    }

    public void recordGetClientsSummary(double value) { // Добавлено
        getClientsSummary.record(value);
    }

    public void recordSelectRecipientSummary(double value) { // Добавлено
        selectRecipientSummary.record(value);
    }

    public void recordTransferSummary(double value) { // Добавлено
        transferSummary.record(value);
    }

    public void recordSayHelloSummary(double value) { // Добавлено
        sayHelloSummary.record(value);
    }

    // Timer methods
    public void recordRegisterTimer(long durationNanos) {
        registerTimer.record(durationNanos, TimeUnit.NANOSECONDS);
    }

    public void recordLoginTimer(long durationNanos) {
        loginTimer.record(durationNanos, TimeUnit.NANOSECONDS);
    }

    public void recordLogoutTimer(long durationNanos) {
        logoutTimer.record(durationNanos, TimeUnit.NANOSECONDS);
    }

    public void recordIsLoggedTimer(long durationNanos) {
        isLoggedTimer.record(durationNanos, TimeUnit.NANOSECONDS);
    }

    public void recordGetUserTimer(long durationNanos) {
        getUserTimer.record(durationNanos, TimeUnit.NANOSECONDS);
    }

    public void recordCreateAccountTimer(long durationNanos) { // Добавлено
        createAccountTimer.record(durationNanos, TimeUnit.NANOSECONDS);
    }

    public void recordGetClientsTimer(long durationNanos) { // Добавлено
        getClientsTimer.record(durationNanos, TimeUnit.NANOSECONDS);
    }

    public void recordSelectRecipientTimer(long durationNanos) { // Добавлено
        selectRecipientTimer.record(durationNanos, TimeUnit.NANOSECONDS);
    }

    public void recordTransferTimer(long durationNanos) { // Добавлено
        transferTimer.record(durationNanos, TimeUnit.NANOSECONDS);
    }

    public void recordSayHelloTimer(long durationNanos) { // Добавлено
        sayHelloTimer.record(durationNanos, TimeUnit.NANOSECONDS);
    }

    // Gauge support
    public int getRegisterCount() {
        return registerCount.get();
    }

    public int getLoginCount() {
        return loginCount.get();
    }

    public int getLogoutCount() {
        return logoutCount.get(); // Исправлено: было loginCount
    }

    public int getIsLoggedCount() {
        return isLoggedCount.get();
    }

    public int getGetUserCount() {
        return getUserCount.get();
    }

    public int getCreateAccountCount() { // Добавлено
        return createAccountCount.get();
    }

    public int getGetClientsCount() { // Добавлено
        return getClientsCount.get();
    }

    public int getSelectRecipientCount() { // Добавлено
        return selectRecipientCount.get();
    }

    public int getTransferCount() { // Добавлено
        return transferCount.get();
    }

    public int getSayHelloCount() { // Добавлено
        return sayHelloCount.get();
    }
}