package me.boblocker.storage;

import me.boblocker.core.annotation.Singleton;
import me.boblocker.exception.AccountException;
import me.boblocker.storage.model.AccountModel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class InMemoryAccountStorage implements AccountStorage {
    private final Map<Long, AccountModel> storage = new ConcurrentHashMap<>();

    @Override
    public void save(AccountModel account) {
        AccountModel existing = storage.putIfAbsent(account.getId(), account);

        if (existing != null) {
            throw new AccountException("Account already existing with id " + account.getId());
        }
    }

    @Override
    public AccountModel findById(Long id) {
        return storage.get(id);
    }

    @Override
    public void update(AccountModel account) {
        storage.put(account.getId(), account);
    }

    @Override
    public void delete(Long id) {
        storage.remove(id);
    }
}
