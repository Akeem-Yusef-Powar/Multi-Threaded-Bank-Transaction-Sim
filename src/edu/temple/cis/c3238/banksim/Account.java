package edu.temple.cis.c3238.banksim;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Cay Horstmann
 * @author Modified by Paul Wolfgang
 * @author Modified by Charles Wang
 * @author Modified by Alexa Delacenserie
 * @author Modified by Tarek Elseify
 */
public class Account {

    private volatile int balance;
    private final int id;
    private ReentrantLock lock;
    private Bank myBank;

    public Account(int id, int initialBalance, Bank myBank) {
        this.id = id;
        this.balance = initialBalance;
        this.myBank = myBank;
    }

    public synchronized int getBalance() {
        return balance;
    }

    public synchronized boolean withdraw(int amount) {
        if (amount <= balance) {
            int currentBalance = balance;
             Thread.yield(); // Try to force collision
            int newBalance = currentBalance - amount;
            balance = newBalance;
            return true;
        } else {
            return false;
        }
    }

    public synchronized void deposit(int amount) {
        int currentBalance = balance;
         Thread.yield();   // Try to force collision
        int newBalance = currentBalance + amount;
        balance = newBalance;
        notifyAll();
    }

    public synchronized void waitForAvailableFunds(int amount) {
        while (!myBank.open() && amount >= balance) {
            try {
                wait();
            } catch (InterruptedException ex) { /*ignore*/ }
        }
    }

    
    @Override
    public String toString() {
        return String.format("Account[%d] balance %d", id, balance);
    }
}
