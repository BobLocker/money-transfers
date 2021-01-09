package me.boblocker.controller;

import io.javalin.http.Context;
import io.javalin.plugin.openapi.annotations.HttpMethod;
import me.boblocker.core.annotation.InjectByType;
import me.boblocker.core.annotation.RequestMapping;
import me.boblocker.core.annotation.RestController;
import me.boblocker.dto.Account;
import me.boblocker.exception.RequestParamException;
import me.boblocker.service.AccountService;
import me.boblocker.util.JsonConverter;

@RestController
public class AccountController {
    @InjectByType
    private AccountService accountService;
    @InjectByType
    private JsonConverter jsonConverter;

    @RequestMapping(path = "/create", method = HttpMethod.POST)
    public void createAccount(Context context) {
        String body = context.body();
        Account account = jsonConverter.fromJson(body, Account.class);
        accountService.createAccount(account);
        context.result("Account created with id: " + account.getId());
    }

    @RequestMapping(path = "/account/:id", method = HttpMethod.GET)
    public void getAccount(Context context) {
        long id = extractIdFromPathParam(context);
        Account account = accountService.getAccount(id);
        String accountInJson = jsonConverter.toJson(account);
        context.result(accountInJson);
    }

    @RequestMapping(path = "/account/:id", method = HttpMethod.DELETE)
    public void deleteAccount(Context context) {
        long id = extractIdFromPathParam(context);
        accountService.deleteAccount(id);
        context.result("Account deleted with id: " + id);
    }

    private long extractIdFromPathParam(Context context) {
        String pathParam = context.pathParam("id");
        try {
            return Long.parseLong(pathParam);
        } catch (NumberFormatException ex) {
            throw new RequestParamException("Bad param for id");
        }
    }
}
