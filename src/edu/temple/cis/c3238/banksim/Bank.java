package edu.temple.cis.c3238.banksim;

import java.util.concurrent.Semaphore;

/**
 * @author Cay Horstmann
 * @author Modified by Paul Wolfgang
 * @author Modified by Charles Wang
 * @author Modified by Alexa Delacenserie
 * @author Modified by Tarek Elseify
 */

public class Bank {

    public static final int NTEST = 5000;
    private final Account[] accounts;
    private long numTransactions = 0;
    private final int initialBalance;
    private final int numAccounts;

    Semaphore lock = new Semaphore(10); //locks for acc 0-9
    Semaphore lock4test = new Semaphore(1); //locks for acc 0-9
    private boolean testTime = false; // not time for test
    private boolean bankOpen = true; // is open

    public Bank(int numAccounts, int initialBalance) {
        this.initialBalance = initialBalance;
        this.numAccounts = numAccounts;
        accounts = new Account[numAccounts];
        for (int i = 0; i < accounts.length; i++) {
            accounts[i] = new Account(i, initialBalance, this);
        }
        numTransactions = 0;
    }

    public void transfer(int from, int to, int amount) {
         accounts[from].waitForAvailableFunds(amount);
        if (accounts[from].withdraw(amount)) {
       //     synchronized (accounts[to]) {
                accounts[to].deposit(amount);
                numTransactions++;
            }

     //   }
        
        // Uncomment line when race condition in test() is fixed.
    }
    public int getNumAccounts() {
        return numAccounts;
    }

    public void test() {
        int totalBalance = 0;
        for (Account account : accounts) {
            System.out.printf("%-30s %s%n",
                    Thread.currentThread().toString(), account.toString());
            totalBalance += account.getBalance();
        }
        System.out.printf("%-30s Total balance: %d\n", Thread.currentThread().toString(), totalBalance);
        if (totalBalance != numAccounts * initialBalance) {
            System.out.printf("%-30s Total balance changed!\n", Thread.currentThread().toString());
            System.exit(0);
        } else {

            System.out.printf("%-30s Total balance unchanged.\n\n", Thread.currentThread().toString());

        }

    }

    public synchronized boolean testTime() {
        if (++numTransactions % NTEST == 0) {
                testTime = true;
                this.notifyAll();
        }
        return testTime;
    }

    public synchronized void testDone(){
        testTime = false;
    //    workerSignal = true; // worker signal sent
        this.notifyAll();

    }

    public boolean open(){
        synchronized (this) {
            return bankOpen;
        }
    }



    public void bankClosed() {
                 {
                    synchronized (this) {
                        bankOpen = false;
                        this.notifyAll(); // update all to new condition
                    }
                }
        }

}
