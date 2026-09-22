package com.core2web.controller;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.core2web.dao.WalletDao;
import com.core2web.model.AuthSession;
import com.core2web.model.WalletTransaction;

public class WalletController {

    private final WalletDao walletDao = new WalletDao();


    public double getMyBalance() {
        AuthSession session = AuthSession.getCurrent();
        if (session == null) {
            return 0;
        }
        double balance = 0;
        for (WalletTransaction t : walletDao.getTransactionsForOwner(session.getUid(), session.getIdToken())) {
            balance += t.isTopUp() ? t.getAmount() : -t.getAmount();
        }
        return balance;
    }

    public List<WalletTransaction> getMyTransactions() {
        AuthSession session = AuthSession.getCurrent();
        if (session == null) {
            return new ArrayList<>();
        }
        return walletDao.getTransactionsForOwner(session.getUid(), session.getIdToken());
    }

    public List<WalletTransaction> getMyTransactionsThisMonth() {
        AuthSession session = AuthSession.getCurrent();
        if (session == null) {
            return new ArrayList<>();
        }
        return walletDao.getTransactionsThisMonth(session.getUid(), session.getIdToken());
    }

    public WalletTransaction getTransactionForBooking(String ownerId, String bookingId) {
        AuthSession session = AuthSession.getCurrent();
        if (session == null) {
            return null;
        }
        return walletDao.getTransactionForBooking(ownerId, bookingId, session.getIdToken());
    }

    public boolean addTopUp(double amount, String method) {
        AuthSession session = AuthSession.getCurrent();
        if (session == null) {
            return false;
        }

        WalletTransaction txn = new WalletTransaction(
                null, session.getUid(), "top_up", amount, method,
                Instant.now().toString(), ""
        );
        return walletDao.addTransaction(txn, session.getIdToken()) != null;
    }


    public double getRevenueToday() {
        AuthSession session = AuthSession.getCurrent();
        return session == null ? 0 : walletDao.getRevenueToday(session.getIdToken());
    }

    public double getRevenueThisMonth() {
        AuthSession session = AuthSession.getCurrent();
        return session == null ? 0 : walletDao.getRevenueThisMonth(session.getIdToken());
    }

    public Map<String, Double> getRevenueLast7Days() {
        AuthSession session = AuthSession.getCurrent();
        return session == null ? Map.of() : walletDao.getRevenueLast7Days(session.getIdToken());
    }
    public double getAverageSessionValue() {
    AuthSession session = AuthSession.getCurrent();

    return session == null
            ? 0
            : walletDao.getAverageSessionValue(
                    session.getIdToken()
            );
}
}
