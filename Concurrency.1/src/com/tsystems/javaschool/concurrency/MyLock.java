package com.tsystems.javaschool.concurrency;


/**
 * @author Lilia Abdulina
 * 
 */
public class MyLock {
	private Object lockObj = new Object();
	private Thread owner;

	/**
	 * Acquires the lock if it is not held by another thread. If the lock is
	 * held by another thread then the current thread becomes disabled for
	 * thread scheduling purposes and lies dormant until the lock has been
	 * acquired.
	 * 
	 * @throws InterruptedException
	 * 
	 **/
	public void lock() throws InterruptedException {
		Thread currentThread = Thread.currentThread();
		synchronized (lockObj) {
			while (owner != null) {
				lockObj.wait();
			}
			owner = currentThread;
		}
	}

	/**
	 * Attempts to release this lock. If the current thread is the holder of
	 * this lock the lock is released. If the current thread is not the holder
	 * of this lock then {@link IllegalStateException} is thrown.
	 * 
	 * @throws IllegalStateException
	 */
	public void unlock() {
		Thread currentThread = Thread.currentThread();
		synchronized (lockObj) {
			if (owner == currentThread) {
				owner = null;
				lockObj.notify();
			} else {
				// Only the owner can release the lock
				throw new IllegalStateException();
			}
		}
	}

	/**
	 * Acquires the lock only if it is not held by another thread at the time of
	 * invocation and returns {@code true}. If the lock is held by another
	 * thread then this method will return immediately with the value
	 * {@code false}.
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
			return true;
		}
	}
}
