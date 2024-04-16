package site.khmelinin;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);
    private static final int ACCOUNTS = 4;
    private static final int THREADS = 2;
    private static final int MAX_TRANSACTIONS = 30;

    public static void main(String[] args) {
        List<Account> accounts = new ArrayList<>();
        for (int i = 0; i < ACCOUNTS; i++) {
            accounts.add(new Account(10000));
        }

        ExecutorService executor = Executors.newFixedThreadPool(THREADS);
        Random random = new Random();

        for (int i = 0; i < MAX_TRANSACTIONS; i++) {

            int finalTransactions = i + 1;

            executor.submit(() -> {

                int fromIndex;
                int toIndex;

                do {
                    fromIndex = random.nextInt(accounts.size());
                    toIndex = random.nextInt(accounts.size());
                } while (fromIndex == toIndex);

                Account fromAccount = accounts.get(fromIndex);
                Account toAccount = accounts.get(toIndex);

                int amount = random.nextInt(10000) + 1;

                Account firstLock = fromIndex < toIndex ? fromAccount : toAccount;
                Account secondLock = fromIndex < toIndex ? toAccount : fromAccount;

                firstLock.getLock().lock();
                secondLock.getLock().lock();
                try {
                    fromAccount.transfer(toAccount, amount);
                    logger.info("Transaction {}: transferred {} from account {} to account {}", finalTransactions, amount, fromAccount.getId(), toAccount.getId());
                } finally {
                    secondLock.getLock().unlock();
                    firstLock.getLock().unlock();
                }
                try {
                    Thread.sleep(random.nextInt(1000) + 1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        executor.shutdown();
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }

        for (Account account : accounts) {
            logger.info("Account ID: {} - Final balance: {}", account.getId(), account.getMoney());
        }
        System.exit(0);
    }
}
