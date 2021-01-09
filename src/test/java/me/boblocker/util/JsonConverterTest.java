package me.boblocker.util;

import me.boblocker.core.CustomTestInstancePostProcessor;
import me.boblocker.core.annotation.ContextForTest;
import me.boblocker.core.annotation.InjectByType;
import me.boblocker.dto.Account;
import me.boblocker.exception.JsonConverterException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(CustomTestInstancePostProcessor.class)
@ContextForTest(value = "me.boblocker")
class JsonConverterTest {
    @InjectByType
    private JsonConverter jsonConverter;

    @Test
    void testToJson_ValidInputForAccount() {
        String expected = "{\"id\":1,\"balance\":100}";
        Account account = Account.builder()
                .id(1L)
                .balance(100)
                .build();

        String actual = jsonConverter.toJson(account);
        assertEquals(expected, actual);
    }

    @Test
    void testFromJson_ValidInputForAccount() {
        String json = "{\"id\":1,\"balance\":100}";
        Account actual = jsonConverter.fromJson(json, Account.class);
        Account expected = Account.builder()
                .id(1L)
                .balance(100)
                .build();

        assertEquals(expected, actual);
    }

    @Test
    void testFromJson_InvalidInputForAccount() {
        String invalidJson = "{\"id\":\"INVALID_ID\",\"balance\":100}";
        assertThrows(JsonConverterException.class, () -> jsonConverter.fromJson(invalidJson, Account.class));
    }
}