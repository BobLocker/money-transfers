package me.boblocker;

import me.boblocker.core.ApplicationRunner;
import okhttp3.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.HttpURLConnection;

import static org.junit.jupiter.api.Assertions.*;

class ApplicationTestIT {

    private final String BASE_URL = "http://localhost:8081";
    private final OkHttpClient client = new OkHttpClient();
    public static final MediaType MEDIA_TYPE_JSON = MediaType.get("application/json; charset=utf-8");

    @BeforeAll
    public static void runApplication() {
        ApplicationRunner.run(Application.class);
    }

    @Test
    public void testCreateAccount_ValidAccount_Success() throws Exception {
        int ID = 1;
        Response response = createAccount(ID, 100);

        assertAll(
                () -> {
                    assertNotNull(response.body());
                    assertEquals("Account created with id: " + ID, response.body().string());
                },
                () -> assertEquals(HttpURLConnection.HTTP_OK, response.code())
        );

        deleteAccount(ID);
    }

    @Test
    public void testCreateAccount_InvalidAccount_BadRequest() throws Exception {
        String BAD_JSON = "{\"id\":\"ID_AS_STRING\", \"balance\":100}";
        Request request = new Request.Builder()
                .url(BASE_URL + "/create")
                .post(RequestBody.create(MEDIA_TYPE_JSON, BAD_JSON))
                .build();
        Response response = client.newCall(request).execute();

        assertAll(
                () -> {
                    assertNotNull(response.body());
                    assertEquals("Exception with converting from JSON", response.body().string());
                },
                () -> assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, response.code())
        );
    }


    @Test
    public void testCreateAccount_NegativeBalance_BadRequest() throws Exception {
        Response response = createAccount(1L, -100);

        assertAll(
                () -> assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, response.code()),
                () -> {
                    assertNotNull(response.body());
                    assertEquals("Can't create account with negative balance", response.body().string());
                }
        );
    }

    @Test
    public void testCreateAccount_AccountAlreadyExists_BadRequest() throws Exception {
        long ID = 1L;
        Response responseFirst = createAccount(ID, 100);
        Response responseSecond = createAccount(ID, 500);

        assertAll(
                () -> assertEquals(HttpURLConnection.HTTP_OK, responseFirst.code()),
                () -> assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, responseSecond.code()),
                () -> {
                    assertNotNull(responseSecond.body());
                    assertEquals("Account already existing with id " + ID, responseSecond.body().string());
                }
        );

        deleteAccount(ID);
    }

    @Test
    public void testGetAccount_ValidId_ReturnAccount() throws Exception {
        long ID = 1L;
        createAccount(ID, 100);

        Response response = getAccount(ID);

        assertAll(
                () -> assertEquals(HttpURLConnection.HTTP_OK, response.code()),
                () -> {
                    assertNotNull(response.body());
                    assertEquals("{\"id\":1,\"balance\":100}", response.body().string());
                }
        );

        deleteAccount(ID);
    }

    @Test
    public void testGetAccount_NoAccountWithId_NotFound() throws Exception {
        long INVALID_ID = 1L;
        Response response = getAccount(INVALID_ID);

        assertAll(
                () -> assertEquals(HttpURLConnection.HTTP_NOT_FOUND, response.code()),
                () -> {
                    assertNotNull(response.body());
                    assertEquals("Account not found with id " + INVALID_ID, response.body().string());
                }
        );
    }

    @Test
    public void testGetAccount_InvalidId_BadRequest() throws Exception {
        Request request = new Request.Builder()
                .url(BASE_URL + "/account/INVALID_PARAM_ID")
                .get()
                .build();

        Response response = client.newCall(request).execute();

        assertAll(
                () -> assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, response.code()),
                () -> {
                    assertNotNull(response.body());
                    assertEquals("Bad param for id", response.body().string());
                }
        );
    }

    @Test
    public void testDeleteAccount_ValidId_Success() throws Exception {
        long ID = 1L;
        createAccount(ID, 100);
        Response response = deleteAccount(ID);

        assertAll(
                () -> assertEquals(HttpURLConnection.HTTP_OK, response.code()),
                () -> {
                    assertNotNull(response.body());
                    assertEquals("Account deleted with id: " + ID, response.body().string());
                }
        );
    }

    @Test
    public void testDeleteAccount_NoAccountWithId_NotFound() throws Exception {
        long INVALID_ID = 1L;
        Response response = deleteAccount(INVALID_ID);

        assertAll(
                () -> assertEquals(HttpURLConnection.HTTP_NOT_FOUND, response.code()),
                () -> {
                    assertNotNull(response.body());
                    assertEquals("Account not found with id " + INVALID_ID, response.body().string());
                }
        );
    }

    @Test
    public void testDeleteAccount_InvalidId_BadRequest() throws Exception {
        Request request = new Request.Builder()
                .url(BASE_URL + "/account/INVALID_PARAM_ID")
                .delete()
                .build();

        Response response = client.newCall(request).execute();

        assertAll(
                () -> assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, response.code()),
                () -> {
                    assertNotNull(response.body());
                    assertEquals("Bad param for id", response.body().string());
                }
        );
    }

    @Test
    public void testTransfer_ValidData_Success() throws Exception {
        long idFrom = 1L;
        long idTo = 2L;
        createAccount(idFrom, 100);
        createAccount(idTo, 300);

        Response response = transfer(idFrom, idTo, 50);

        assertAll(
                () -> {
                    assertNotNull(response.body());
                    assertEquals("Transfer success", response.body().string());
                },
                () -> assertEquals(HttpURLConnection.HTTP_OK, response.code())
        );

        deleteAccount(idFrom);
        deleteAccount(idTo);
    }

    @Test
    public void testTransfer_TransferYourSelf_BadRequest() throws Exception {
        long id = 1L;
        createAccount(id, 100);

        Response response = transfer(id, id, 50);

        assertAll(
                () -> {
                    assertNotNull(response.body());
                    assertEquals("Could not transfer money to yourself", response.body().string());
                },
                () -> assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, response.code())
        );

        deleteAccount(id);
    }

    @Test
    public void testTransfer_TransferNegativeAmount_BadRequest() throws Exception {
        long idFrom = 1L;
        long idTo = 2L;
        createAccount(idFrom, 100);
        createAccount(idTo, 300);

        Response response = transfer(idFrom, idTo, -50);

        assertAll(
                () -> {
                    assertNotNull(response.body());
                    assertEquals("Could not transfer negative sum or zero", response.body().string());
                },
                () -> assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, response.code())
        );

        deleteAccount(idFrom);
        deleteAccount(idTo);
    }

    @Test
    public void testTransfer_TransferZero_BadRequest() throws Exception {
        long idFrom = 1L;
        long idTo = 2L;
        createAccount(idFrom, 100);
        createAccount(idTo, 300);

        Response response = transfer(idFrom, idTo, 0);

        assertAll(
                () -> {
                    assertNotNull(response.body());
                    assertEquals("Could not transfer negative sum or zero", response.body().string());
                },
                () -> assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, response.code())
        );

        deleteAccount(idFrom);
        deleteAccount(idTo);
    }

    @Test
    public void testTransfer_NotEnoughMoneyToTransfer_BadRequest() throws Exception {
        long idFrom = 1L;
        long idTo = 2L;
        createAccount(idFrom, 100);
        createAccount(idTo, 300);

        Response response = transfer(idFrom, idTo, 150);

        assertAll(
                () -> {
                    assertNotNull(response.body());
                    assertEquals("Account don't have enough money to debit", response.body().string());
                },
                () -> assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, response.code())
        );

        deleteAccount(idFrom);
        deleteAccount(idTo);
    }

    @Test
    public void testTransfer_FromNotFoundAccount_BadRequest() throws Exception {
        long NOT_REAL_ID = 1L;
        long idTo = 2L;
        createAccount(idTo, 300);

        Response response = transfer(NOT_REAL_ID, idTo, 150);

        assertAll(
                () -> {
                    assertNotNull(response.body());
                    assertEquals("Account not found with id " + NOT_REAL_ID, response.body().string());
                },
                () -> assertEquals(HttpURLConnection.HTTP_NOT_FOUND, response.code())
        );

        deleteAccount(idTo);
    }

    @Test
    public void testTransfer_NotFoundAccountTo_BadRequest() throws Exception {
        long NOT_REAL_ID = 1L;
        long idFrom = 2L;
        createAccount(idFrom, 300);

        Response response = transfer(idFrom, NOT_REAL_ID, 150);

        assertAll(
                () -> {
                    assertNotNull(response.body());
                    assertEquals("Account not found with id " + NOT_REAL_ID, response.body().string());
                },
                () -> assertEquals(HttpURLConnection.HTTP_NOT_FOUND, response.code())
        );

        deleteAccount(idFrom);
    }


    private Response createAccount(long id, int balance) throws IOException {
        Request request = new Request.Builder()
                .url(BASE_URL + "/create")
                .post(RequestBody.create(
                        MEDIA_TYPE_JSON,
                        String.format("{\"id\":%d, \"balance\":%d}", id, balance)))
                .build();
        return client.newCall(request).execute();
    }

    private Response getAccount(long id) throws IOException {
        Request request = new Request.Builder()
                .url(BASE_URL + "/account/" + id)
                .get()
                .build();
        return client.newCall(request).execute();
    }

    private Response deleteAccount(long id) throws IOException {
        Request request = new Request.Builder()
                .url(BASE_URL + "/account/" + id)
                .delete()
                .build();
        return client.newCall(request).execute();
    }

    private Response transfer(long idFrom, long idTo, int amount) throws IOException {
        Request request = new Request.Builder()
                .url(BASE_URL + "/transfer")
                .post(RequestBody.create(
                        MEDIA_TYPE_JSON,
                        String.format("{\"idFrom\":%d, \"idTo\":%d, \"amount\":%d}",idFrom, idTo, amount)))
                .build();
        return client.newCall(request).execute();
    }

}