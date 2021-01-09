package me.boblocker.service;

import me.boblocker.dto.Account;
import me.boblocker.dto.TransferRequest;

public interface AccountService {
    void createAccount(Account account);

    Account getAccount(long id);

    void deleteAccount(long id);

    void transferMoney(TransferRequest transferInfo);
}
