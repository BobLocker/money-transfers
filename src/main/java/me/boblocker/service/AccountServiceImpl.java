package me.boblocker.service;

import me.boblocker.core.annotation.InjectByType;
import me.boblocker.core.annotation.Singleton;
import me.boblocker.dto.Account;
import me.boblocker.dto.TransferRequest;
import me.boblocker.exception.AccountException;
import me.boblocker.exception.AccountNotFoundException;
import me.boblocker.exception.TransferException;
import me.boblocker.storage.AccountStorage;
import me.boblocker.storage.model.AccountModel;
import me.boblocker.util.AccountConverter;
import org.jetbrains.annotations.NotNull;


@Singleton
public class AccountServiceImpl implements AccountService{

    @InjectByType
    private AccountStorage accountStorage;
    @InjectByType
    private AccountConverter accountConverter;

    @Override
    public void createAccount(Account account) {
        checkBalance(account);
        AccountModel accountToSave = accountConverter.convertToModel(account);
        accountStorage.save(accountToSave);
    }

    private void checkBalance(Account account) {
        if (account.getBalance() < 0 )
            throw new AccountException("Can't create account with negative balance");
    }


    @Override
    public Account getAccount(long id) {
        AccountModel accountFromDb = getAccountByIdOrThrowException(id);

        return accountConverter.convertFromModel(accountFromDb);
    }

    @Override
    public void deleteAccount(long id) {
        getAccountByIdOrThrowException(id);
        accountStorage.delete(id);
    }

    @Override
    public void transferMoney(TransferRequest transferRequest) {
        checkTransferRequest(transferRequest);

        long idFrom = transferRequest.getIdFrom();
        long idTo = transferRequest.getIdTo();
        int amount = transferRequest.getAmount();

        AccountModel from = getAccountByIdOrThrowException(idFrom);
        AccountModel to = getAccountByIdOrThrowException(idTo);

        /*
            We always use synchronized in the same order, that there would not be deadlock.
            In this case ordered by account ID.
         */
        if (idFrom > idTo) {
            synchronized (from.getTieLock()) {
                synchronized (to.getTieLock()) {
                    transfer(from, to, amount);
                }
            }
        } else {
            synchronized (to.getTieLock()) {
                synchronized (from.getTieLock()) {
                    transfer(from, to, amount);
                }
            }
        }
    }

    private void checkTransferRequest(TransferRequest transferRequest) {
        long idFrom = transferRequest.getIdFrom();
        long idTo = transferRequest.getIdTo();
        if (idFrom == idTo) {
            throw new TransferException("Could not transfer money to yourself");
        }

        if (transferRequest.getAmount() <= 0 ) {
            throw new TransferException("Could not transfer negative sum or zero");
        }

    }

    private void transfer(AccountModel from, AccountModel to, int amount) {
        debit(from, amount);
        credit(to, amount);

        accountStorage.update(from);
        accountStorage.update(to);
    }

    private void debit(AccountModel account, int amount) {
        int balance = account.getBalance();
        if (balance < amount) {
            throw new TransferException("Account don't have enough money to debit");
        }
        balance -= amount;
        account.setBalance(balance);
    }

    private void credit(AccountModel account, int amount) {
        int balance = account.getBalance();
        balance += amount;
        account.setBalance(balance);
    }

    @NotNull
    private AccountModel getAccountByIdOrThrowException(long id) {
        AccountModel accountFromDb = accountStorage.findById(id);
        if (accountFromDb == null) {
            throw new AccountNotFoundException("Account not found with id " + id);
        }
        return accountFromDb;
    }
}
