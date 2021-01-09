package me.boblocker.util;

import me.boblocker.core.annotation.Singleton;
import me.boblocker.dto.Account;
import me.boblocker.storage.model.AccountModel;

@Singleton
public class AccountConverter {

    public AccountModel convertToModel(Account account) {
        AccountModel accountModel = new AccountModel();
        accountModel.setId(account.getId());
        accountModel.setBalance(account.getBalance());

        return accountModel;
    }

    public Account convertFromModel(AccountModel accountModel) {
        Account account = new Account();
        account.setId(accountModel.getId());
        account.setBalance(accountModel.getBalance());

        return account;
    }
}
