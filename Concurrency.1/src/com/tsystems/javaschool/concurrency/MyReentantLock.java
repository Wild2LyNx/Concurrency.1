/**
 * 
 */
package com.tsystems.javaschool.concurrency;

/**
 * @author Lilia Abdulina
 * 
 */
public class MyReentantLock {
	private Object lockObj = new Object();
	private Thread owner;
	private int lockCounter;

	/**
	 * Acquires the lock if it is not held by another thread and returns
	 * immediately, setting the lock hold count to one. If the current thread
	 * already holds the lock then the hold count is incremented by one and the
	 * method returns immediately. If the lock is held by another thread then
	 * the current thread becomes disabled for thread scheduling purposes and
	 * lies dormant until the lock has been acquired.
	 * 
	 * @throws InterruptedException
	 */
	public void lock() throws InterruptedException {
		Thread currentThread = Thread.currentThread();
		synchronized (lockObj) {
			while ((owner != null) && (owner != currentThread)) {
				lockObj.wait();
			}
			if (owner == null) {
				owner = currentThread;
				lockCounter = 1;
			} else if (owner == currentThread) {
				lockCounter++;
			}
		}
	}

	/**
	 * Attempts to release this lock. If the current thread is the holder of
	 * this lock then the hold count is decremented. If the hold count is now
	 * zero then the lock is released. If the current thread is not the holder
	 * of this lock then {@link IllegalStateException} is thrown.
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
					lockObj.notify();
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
}
