package bgu.spl.mics;

import java.util.concurrent.TimeUnit;

/**
 * A Future object represents a promised result - an object that will
 * eventually be resolved to hold a result of some operation. The class allows
 * Retrieving the result once it is available.
 * 
 * Only private methods may be added to this class.
 * No public constructor is allowed except for the empty constructor.
 */
public class Future<T> {

	//----------------------------------------------------------Fields----------------------------------------//
	private T result;
	private boolean isSolved;
	private Object lock;

	/**
	 * This should be the the only public constructor in this class.
	 */
	//----------------------------------------------------------Constructor----------------------------------------//
	public Future() {
		isSolved=false;
		lock=new Object();
		result=null;
	}


	//----------------------------------------------------------Methods----------------------------------------//

	/**
     * retrieves the result the Future object holds if it has been resolved.
     * This is a blocking method! It waits for the computation in case it has
     * not been completed.
     * <p>
     * @return return the result of type T if it is available, if not wait until it is available.
     * 	       
     */
	//we put here synchronized to make sure that the event waits until he has result
	public  T get() {
		synchronized (lock) {
			while (!isSolved) {
				try {
					lock.wait();
				} catch (InterruptedException e) {
					resolve(null);
				}
			}
		}
		return result;
	}
	
	/**
     * Resolves the result of this Future object.
     */
	//we put here synchronized to make sure that different results enter at the same time
	public  void  resolve (T result) {
		synchronized (lock){
			if (!isSolved){
				this.result=result;
				isSolved=true;
				lock.notifyAll();
			}
		}
	}
	
	/**
     * @return true if this object has been resolved, false otherwise
     */
	public boolean isDone() {
		return isSolved;
	}
	
	/**
     * retrieves the result the Future object holds if it has been resolved,
     * This method is non-blocking, it has a limited amount of time determined
     * by {@code timeout}
     * <p>
     * @param timeout 	the maximal amount of time units to wait for the result.
     * @param unit		the {@link TimeUnit} time units to wait.
     * @return return the result of type T if it is available, if not, 
     * 	       wait for {@code timeout} TimeUnits {@code unit}. If time has
     *         elapsed, return null.
     */
	public T get(long timeout, TimeUnit unit) {
		synchronized (lock){
			while(!isSolved){
				try{
					if(timeout>=0)
						lock.wait(unit.toMillis(timeout));
					if(!isSolved)
						resolve(null);
				}catch (InterruptedException e){}
			}
		}
		return result;
	}
}
