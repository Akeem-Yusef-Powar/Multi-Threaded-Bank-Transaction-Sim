package edu.temple.cis.c3238.banksim;
import java.util.concurrent.Semaphore;

/**
 * @author Cay Horstmann
 * @author Modified by Paul Wolfgang
 * @author Modified by Charles Wang
 * @author Modified by Alexa Delacenserie
 * @author Modified by Tarek Elseify
 */
class TransferThread extends Thread {

    private final Bank bank;

    private final int fromAccount;
    private final int maxAmount;



    public TransferThread(Bank b, int from, int max) {
        bank = b;
        fromAccount = from;
        maxAmount = max;


    }
    /**
     * Threads can get the same toAccount but can only call bank.transfer(acc A,acc B,$$$) with lock
     * only one thread can transfer at one time */
    @Override
    public void run() {
        for (int i = 0; i < 10000; i++) {

            while(bank.testTime() && bank.open()) { //while test time && bank open && !signal missed condition
                synchronized (bank) {
                    try {
                        bank.wait();// wait for SumBalanceThread to signal wake up
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            if(!bank.open()) { // while bank is closed
                break;
            }

            int toAccount = (int) (bank.getNumAccounts() * Math.random());
            int amount = (int) (maxAmount * Math.random());
            try {
                bank.lock.acquire(1); // get 1/10 account lock
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            bank.transfer(fromAccount, toAccount, amount);
            bank.lock.release();


            try {
                bank.lock4test.acquire(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            bank.testTime(); // checking if its time for test is a crit section
            bank.lock4test.release(1);
        }

            bank.bankClosed(); // first thread to exit loop will close bank and notify all. rest should interrupt after loop
            System.out.printf("%-30s Account[%d] has finished with its transactions. Bank closed.\n", Thread.currentThread().toString(), fromAccount);
    }


}
