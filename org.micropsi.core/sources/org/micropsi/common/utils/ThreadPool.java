/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/common/utils/ThreadPool.java,v 1.5 2005/07/12 12:55:16 vuine Exp $
 */
package org.micropsi.common.utils;

import java.util.Vector;
import java.lang.IllegalArgumentException;
import java.lang.NumberFormatException;
import java.lang.InterruptedException;
import java.lang.Thread;
import java.lang.Runnable;

/**
 * 
 * This is, generally, the ThreadPool implementation from the october 2000
 * issue of the Java Pro magazine. The static default instance was added,
 * and some debug / statistic code was removed. 
 */ 

public class ThreadPool {
	
	private static ThreadPool defaultInstance;
	
	public static ThreadPool getDefaultInstance() {
		return defaultInstance;
	}
	
	public static void initDefaultInstance(int max, int min, int idle) {
		defaultInstance = new ThreadPool(max,min,idle);
	}
	
	// max number of threads that can be created
	private int _maxThreads = -1;
	// min number of threads that must always be present
	private int _minThreads = -1;
	// max idle time after which a thread may be killed
	private int _maxIdleTime = -1;
	// A list i.e. vector of pending jobs
	private Vector<Runnable> _pendingJobs = new Vector<Runnable>();
	// A list i.e. vector of all available threads
	private Vector<ThreadElement> _availableThreads = new Vector<ThreadElement>();

	// This class represents an element in the 
	// available threads vector.
	private class ThreadElement {
		// if true then the thread can process a new job
		private boolean _idle;
		// serves as the key 
		private Thread _thread;

		public ThreadElement(Thread thread) {
			_thread = thread;
			_idle = true;
		}
	}

	// Each thread in the pool is an instance of 
	// this class
	private class PoolThread extends Thread {
		
		public PoolThread() {
			super("ThreadPoolElement");
		}
		
		private Object _lock;

		// pass in the pool instance for synchronization.
		public PoolThread(Object lock) {
			_lock = lock;
		}

		// This is where all the action is...
		public void run() {
			Runnable job = null;                  

			while( true ) {
				while( true ) {
					synchronized(_lock) {
						// Keep processing jobs 
						//until none availble
						if( _pendingJobs.size()	== 0 ) {
							int index = findMe();
							if( index == -1 ) return;
							_availableThreads.get(index)._idle = true;
							break;
						}

						// Remove the job from the pending list.
						job = _pendingJobs.firstElement();
						_pendingJobs.removeElementAt(0);
					}               

					// run the job
					job.run();
					job = null;
				}            

				try {
					synchronized(this) {
						// if no idle time specified, 
						// wait till notified.
						if( _maxIdleTime == -1 )
							wait();
						else
							wait(_maxIdleTime);
					}                        
				}
				catch( InterruptedException e ) {
					// Cleanup if interrupted
					synchronized(_lock) {
						removeMe();
					}
					return;
				}

				// Just been notified or the wait timed out
				synchronized(_lock) {
					// If there are no jobs, that means we "idled" out.
					if( _pendingJobs.size() == 0 ) {
						if( _minThreads != -1 && 
							_availableThreads.size() > _minThreads ) {
							removeMe();
							return;    
						}
					}
				}
			}
		}
	}

	public ThreadPool(int maxThreads, int minThreads, int maxIdleTime) throws NumberFormatException, IllegalArgumentException
	{

		if( maxThreads < 1 ) throw new IllegalArgumentException("maxThreads must be an integral value greater than 0");
		_maxThreads = maxThreads;

		if( minThreads < 0 ) throw new IllegalArgumentException( "minThreads must be an integral value greater than or equal to 0");
		if( minThreads > _maxThreads ) throw new IllegalArgumentException("minThreads cannot be greater than maxThreads");
		_minThreads = minThreads;

		if( maxIdleTime < 1 ) throw new IllegalArgumentException("maxIdleTime must be an integral value greater than 0");
		_maxIdleTime = maxIdleTime;

	}

	synchronized public void addJob(java.lang.Runnable job) {
		_pendingJobs.add(job);             
		int index = findFirstIdleThread();
		if( index == -1 ) {
			// All threads are busy

			if( _maxThreads == -1 || 
				_availableThreads.size() < _maxThreads ) {
				// We can create another thread...
				ThreadElement e = 
				new ThreadElement(new PoolThread(this));
				e._idle = false;
				e._thread.start();
				_availableThreads.add(e);
				return;
			}

			// We are not allowed to create any more threads
			// So, just return.
			// When one of the busy threads is done, 
			// it will check the pending queue and will see this job.
		}
		else {
			// There is at least one idle thread.
			_availableThreads.get(index)._idle = false;
			synchronized(_availableThreads.get(index)._thread) {
				_availableThreads.get(index)._thread.notify();
			}
		}
	}

	//*****************************************//   
	// Important...
	// All private methods must always be 
	// called from a synchronized method!!
	//*****************************************//   

	// Called by the thread pool to find the number of idle threads
/*	private int findNumIdleThreads() {
		int idleThreads = 0;
		int size = _availableThreads.size();
		for( int i=0; i<size; i++ ) {
			if( ((ThreadElement)_availableThreads.get(i))._idle )
				idleThreads++;
		}
		return(idleThreads);
	}*/

	// Called by the thread pool to find the first idle thread
	private int findFirstIdleThread() {
		int size = _availableThreads.size();
		for( int i=0; i<size; i++ ) {
			if( _availableThreads.get(i)._idle )
				return(i);
		}
		return(-1);
	}

	// Called by a pool thread to find itself in the 
	// vector of available threads.
	private int findMe() {
		int size = _availableThreads.size();
		for( int i=0; i<size; i++ ) {
			if( _availableThreads.get(i)._thread 
				== Thread.currentThread() )
				return(i);
		}
		return(-1);
	}

	// Called by a pool thread to remove itself from
	// the vector of available threads
	private void removeMe() {
		int size = _availableThreads.size();
		for( int i=0; i<size; i++ ) {
			if( _availableThreads.get(i)._thread 
				== Thread.currentThread() ) {
				_availableThreads.remove(i);
				return;
			}
		}      
	}
}
