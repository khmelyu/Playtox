package site.khmelinin;

import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Account {
    private final String id;
    private int money;
    private final Lock lock = new ReentrantLock();

    public Account(int money) {
        this.id = UUID.randomUUID().toString();
        this.money = money;
    }

    public String getId() {
        return id;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public Lock getLock() {
        return lock;
    }

    public void transfer(Account toAccount, int amount) {
        if (amount > 0 && this.money >= amount) {
            this.money -= amount;
            toAccount.setMoney(toAccount.getMoney() + amount);
        }
    }
}
