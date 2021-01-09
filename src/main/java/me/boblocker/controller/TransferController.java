package me.boblocker.controller;

import io.javalin.http.Context;
import io.javalin.plugin.openapi.annotations.HttpMethod;
import me.boblocker.core.annotation.InjectByType;
import me.boblocker.core.annotation.RequestMapping;
import me.boblocker.core.annotation.RestController;
import me.boblocker.dto.TransferRequest;
import me.boblocker.service.AccountService;
import me.boblocker.util.JsonConverter;

@RestController
public class TransferController {
    @InjectByType
    private AccountService accountService;
    @InjectByType
    private JsonConverter jsonConverter;

    @RequestMapping(path = "/transfer", method = HttpMethod.POST)
    public void transfer(Context context) {
        String body = context.body();
        TransferRequest transferRequest = jsonConverter.fromJson(body, TransferRequest.class);
        accountService.transferMoney(transferRequest);
        context.result("Transfer success");
    }
}
