/**
 * 
 */
package com.tsystems.javaschool.concurrency;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Lilia Abdulina
 * 
 */
public class MyFairLock {
	private Object lockObj = new Object();
	private Thread owner;
	private List<LockObj> waiters = new ArrayList<LockObj>();
	private int lockCounter;

	/**
	 * Acquires the lock if it is not held by another thread and returns
	 * immediately, setting the lock hold count to one. If the current thread
	 * already holds the lock then the hold count is incremented by one and the
	 * method returns immediately. If the lock is held by another thread then
	 * the current thread will be enqueued and it becomes disabled for thread
	 * scheduling purposes and lies dormant until the lock has been acquired.
	 * 
	 * @throws InterruptedException
	 */
	public void lock() throws InterruptedException {
		Thread currentThread = Thread.currentThread();
		synchronized (lockObj) {
			if (owner == currentThread) {
				lockCounter++;
				return;
			}
		}
		LockObj waiter = new LockObj();
		synchronized (lockObj) {
			waiters.add(waiter);
		}

		while ((owner != null) || (waiter != waiters.get(0))) {
			synchronized (lockObj) {
				if ((owner == null) && (waiter == waiters.get(0))) {
					waiters.remove(waiter);
					owner = Thread.currentThread();
					lockCounter = 1;
					return;
				}
			}

			try {
				waiter.await();
			} catch (InterruptedException e) {
				synchronized (this) {
					waiters.remove(waiter);
				}
				throw e;
			}

		}
	}

	/**
	 * Attempts to release this lock. If the current thread is the holder of
	 * this lock then the hold count is decremented. If the hold count is now
	 * zero then the lock is released and waiting thread will be notified in
	 * turn if it is. If the current thread is not the holder of this lock then
	 * {@link IllegalStateException} is thrown.
	 * 
	 * @throws IllegalStateException
	 */
	public void releaseLock() {
		Thread currentThread = Thread.currentThread();
		synchronized (lockObj) {
			if (owner == currentThread) {
				lockCounter--;
				if (lockCounter == 0) {
					owner = null;
					if (waiters.size() > 0){
						waiters.get(0).signal();
					}
				}
				return;
			} else {
				// Only the owner can release the lock
				throw new IllegalStateException();
			}
		}
	}

	/**
	 * Acquires the lock only if it is not held by another thread at the time of
	 * invocation and returns {@code true}. If the lock is held by another
	 * thread or even by the current thread then this method will return
	 * immediately with the value {@code false}.
	 * 
	 * @return {@code true} if the lock was free and was acquired by the current
	 *         thread, and {@code false} otherwise
	 * @throws InterruptedException
	 */
	public boolean tryLock() throws InterruptedException {
		Thread currentThread = Thread.currentThread();
		synchronized (lockObj) {
			if (owner != null) {
				return false;
			}
			owner = currentThread;
			lockCounter = 1;
			return true;
		}
	}

	/**
	 * Auxiliary class for operating with threads.
	 * 
	 */
	public class LockObj {
		private boolean signaled = false;

		/**
		 * Analog of wait()
		 * 
		 * @throws InterruptedException
		 * 
		 */
		public void await() throws InterruptedException {
			while (!signaled) {
				this.wait();
			}
			this.signaled = false;
		}

		/**
		 * Analog of notify()
		 */
		public synchronized void signal() {
			this.signaled = true;
			this.notify();
		}
	}
}
