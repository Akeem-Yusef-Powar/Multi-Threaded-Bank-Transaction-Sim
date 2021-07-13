package edu.temple.cis.c3238.banksim;
import java.util.concurrent.Semaphore;


class SumBalanceThread extends Thread {

    private final Bank bank;
    private final Semaphore lockPermits;


    public SumBalanceThread (Bank b, Semaphore lp){
        bank = b;
        lockPermits = lp;

    }


    @Override
    public void run() {

        while (bank.open()) { // while bank open

            while (!bank.testTime() && bank.open()) {// if its not time for test and bank open
                synchronized (bank) { // monitor to sync on
                    try {
                        bank.wait(); // wait and then test condition again
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
                    if(!bank.open()){
                        break;
                    }
            try {
                lockPermits.acquire(10);// get lock to account balance
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            bank.test();
            lockPermits.release(10); // give up locks
            bank.testDone(); // reset condition

        }

    }
    }

