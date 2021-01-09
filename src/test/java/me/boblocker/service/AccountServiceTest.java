package me.boblocker.service;

import me.boblocker.core.CustomTestInstancePostProcessor;
import me.boblocker.core.annotation.ContextForTest;
import me.boblocker.core.annotation.InjectByType;
import me.boblocker.dto.Account;
import me.boblocker.dto.TransferRequest;
import me.boblocker.exception.AccountException;
import me.boblocker.exception.AccountNotFoundException;
import me.boblocker.exception.TransferException;
import me.boblocker.storage.InMemoryAccountStorage;
import me.boblocker.storage.model.AccountModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(CustomTestInstancePostProcessor.class)
@ContextForTest(value = "me.boblocker")
class AccountServiceTest {

    @InjectByType
    private AccountService accountService;
    @Mock
    private InMemoryAccountStorage accountStorage;

    @Test
    void testCreateAccount_NegativeBalance_ThrowException() {
        Account accountToCreate = Account.builder()
                .id(1L)
                .balance(-100)
                .build();
        assertThrows(AccountException.class, () -> accountService.createAccount(accountToCreate));
    }

    @Test
    void testCreateAccount_ValidAccount_AccountStorageCallSave() {
        Account accountToCreate = Account.builder()
                .id(1L)
                .balance(100)
                .build();

        accountService.createAccount(accountToCreate);
        Mockito.verify(accountStorage, Mockito.times(1)).save(any());
    }

    @Test
    void testGetAccount_NotRealId_ThrowException() {
        long NOT_REAL_ACCOUNT_ID = 1L;
        Mockito.when(accountStorage.findById(NOT_REAL_ACCOUNT_ID)).thenReturn(null);
        assertThrows(AccountNotFoundException.class, () -> accountService.getAccount(NOT_REAL_ACCOUNT_ID));
    }

    @Test
    void testGetAccount_ValidId_ReturnAccount() {
        long VALID_ACCOUNT_ID = 1L;
        int initialBalance = 100;

        Account expected = Account.builder()
                .id(VALID_ACCOUNT_ID)
                .balance(initialBalance)
                .build();

        AccountModel accountModelReturnedFromStorage = new AccountModel();
        accountModelReturnedFromStorage.setId(VALID_ACCOUNT_ID);
        accountModelReturnedFromStorage.setBalance(initialBalance);
        Mockito.when(accountStorage.findById(VALID_ACCOUNT_ID)).thenReturn(accountModelReturnedFromStorage);

        Account actual = accountService.getAccount(VALID_ACCOUNT_ID);

        assertEquals(expected, actual);
    }

    @Test
    void testDeleteAccount_NotRealId_ThrowException() {
        long NOT_REAL_ACCOUNT_ID = 1L;
        Mockito.when(accountStorage.findById(NOT_REAL_ACCOUNT_ID)).thenReturn(null);
        assertThrows(AccountNotFoundException.class, () -> accountService.deleteAccount(NOT_REAL_ACCOUNT_ID));
    }

    @Test
    void testTransferMoney_AccountFromNotFound_ThrowException() {
        long NOT_REAL_ACCOUNT_ID = 1L;
        Mockito.when(accountStorage.findById(NOT_REAL_ACCOUNT_ID)).thenReturn(null);

        TransferRequest transferRequest = TransferRequest.builder()
                .idFrom(NOT_REAL_ACCOUNT_ID)
                .idTo(2L)
                .amount(100)
                .build();

        assertThrows(AccountNotFoundException.class, () -> accountService.transferMoney(transferRequest));
    }

    @Test
    void testTransferMoney_AccountToNotFound_ThrowException() {
        long NOT_REAL_ACCOUNT_ID = 1L;
        Mockito.when(accountStorage.findById(NOT_REAL_ACCOUNT_ID)).thenReturn(null);

        TransferRequest transferRequest = TransferRequest.builder()
                .idFrom(2L)
                .idTo(NOT_REAL_ACCOUNT_ID)
                .amount(100)
                .build();

        assertThrows(AccountNotFoundException.class, () -> accountService.transferMoney(transferRequest));
    }

    @Test
    void testTransferMoney_AccountsToAndFromAreSame_ThrowException() {
        long ID_FOR_BOTH_ACCOUNTS = 1L;
        TransferRequest transferRequest = TransferRequest.builder()
                .idFrom(ID_FOR_BOTH_ACCOUNTS)
                .idTo(ID_FOR_BOTH_ACCOUNTS)
                .amount(100)
                .build();

        assertThrows(TransferException.class, () -> accountService.transferMoney(transferRequest));
    }

    @Test
    void testTransferMoney_NegativeAmount_ThrowException() {
        int NEGATIVE_AMOUNT_TO_TRANSFER = -100;
        TransferRequest transferRequest = TransferRequest.builder()
                .idFrom(1L)
                .idTo(2L)
                .amount(NEGATIVE_AMOUNT_TO_TRANSFER)
                .build();

        assertThrows(TransferException.class, () -> accountService.transferMoney(transferRequest));
    }

    @Test
    void testTransferMoney_NotEnoughMoneyToTransfer_ThrowException() {
        long idFrom = 1L;
        int balanceFrom = 100;
        long idTo = 2L;
        int balanceTo = 50;

        int TRANSFER_AMOUNT = balanceFrom + 1;

        AccountModel accountModelFrom = new AccountModel();
        accountModelFrom.setId(idFrom);
        accountModelFrom.setBalance(balanceFrom);
        Mockito.when(accountStorage.findById(idFrom)).thenReturn(accountModelFrom);

        AccountModel accountModelTo = new AccountModel();
        accountModelTo.setId(idTo);
        accountModelTo.setBalance(balanceTo);
        Mockito.when(accountStorage.findById(idTo)).thenReturn(accountModelTo);

        TransferRequest transferRequest = TransferRequest.builder()
                .idFrom(idFrom)
                .idTo(idTo)
                .amount(TRANSFER_AMOUNT)
                .build();

        assertThrows(TransferException.class, () -> accountService.transferMoney(transferRequest));
    }

    @Test
    void testTransferMoney_EnoughMoneyToTransfer_SuccessTransfer() {
        long idFrom = 1L;
        int balanceFrom = 100;
        long idTo = 2L;
        int balanceTo = 50;
        int TRANSFER_AMOUNT = 40;

        AccountModel accountModelFrom = new AccountModel();
        accountModelFrom.setId(idFrom);
        accountModelFrom.setBalance(balanceFrom);
        Mockito.when(accountStorage.findById(idFrom)).thenReturn(accountModelFrom);

        AccountModel accountModelTo = new AccountModel();
        accountModelTo.setId(idTo);
        accountModelTo.setBalance(balanceTo);
        Mockito.when(accountStorage.findById(idTo)).thenReturn(accountModelTo);

        TransferRequest transferRequest = TransferRequest.builder()
                .idFrom(idFrom)
                .idTo(idTo)
                .amount(TRANSFER_AMOUNT)
                .build();

        accountService.transferMoney(transferRequest);

        assertAll(
                () -> Mockito.verify(accountStorage, Mockito.times(1)).update(accountModelFrom),
                () -> Mockito.verify(accountStorage, Mockito.times(1)).update(accountModelTo),
                () -> assertEquals(balanceFrom - TRANSFER_AMOUNT, accountModelFrom.getBalance()),
                () -> assertEquals(balanceTo + TRANSFER_AMOUNT, accountModelTo.getBalance())
        );
    }
}