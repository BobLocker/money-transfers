package me.boblocker.storage.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
public class AccountModel {
    private long id;
    private int balance;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private final Object tieLock = new Object();
}
