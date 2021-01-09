package me.boblocker.util;

import me.boblocker.core.CustomTestInstancePostProcessor;
import me.boblocker.core.annotation.ContextForTest;
import me.boblocker.core.annotation.InjectByType;
import me.boblocker.dto.Account;
import me.boblocker.storage.model.AccountModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(CustomTestInstancePostProcessor.class)
@ContextForTest(value = "me.boblocker")
class AccountConverterTest {

    @InjectByType
    private AccountConverter accountConverter;

    @Test
    void testConvertToModel_Account_AccountModel() {
        Account account = Account.builder()
                .id(1L)
                .balance(100)
                .build();

        AccountModel expected = new AccountModel();
        expected.setId(1L);
        expected.setBalance(100);

        AccountModel actual = accountConverter.convertToModel(account);
        assertEquals(expected, actual);
    }

    @Test
    void convertFromModel_AccountModel_Account() {
        AccountModel accountModel = new AccountModel();
        accountModel.setId(1L);
        accountModel.setBalance(100);

        Account expected = Account.builder()
                .id(1L)
                .balance(100)
                .build();

        Account actual = accountConverter.convertFromModel(accountModel);

        assertEquals(expected, actual);
    }
}