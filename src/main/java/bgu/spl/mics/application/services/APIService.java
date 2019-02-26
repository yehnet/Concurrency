package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.BookOrderEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;
import javafx.util.Pair;

import java.util.LinkedList;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;

/**
 * APIService is in charge of the connection between a client and the store.
 * It informs the store about desired purchases using {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class APIService extends MicroService{


	//----------------------------------------------------------Fields----------------------------------------//
	private LinkedList<Pair<Integer,String>> orderSchedule;
	private Customer customer;
	private Pair<Integer,String>[] pairs=null;
	private ConcurrentLinkedQueue<Pair<Integer,String>> booksSortedByTicks=new ConcurrentLinkedQueue<>();
	private int tick=0;
	private CountDownLatch latch;
	private CountDownLatch terminateCountDown;


	//----------------------------------------------------------Constructor----------------------------------------//
	public APIService(LinkedList<Pair<Integer,String>> orderSchedule, Customer customer,CountDownLatch latch,CountDownLatch terminateCountDown) {
		super("APIservice");
		this.orderSchedule=orderSchedule;
		this.customer=customer;
		this.pairs=new Pair[orderSchedule.size()];
		this.pairs=sortTheList(orderSchedule);
		this.booksSortedByTicks=new ConcurrentLinkedQueue<>();
		moveOrdersToQueue(pairs);
		this.latch=latch;
		this.terminateCountDown=terminateCountDown;
	}

	//----------------------------------------------------------Methods----------------------------------------//

	@Override
	protected void initialize() {
		subscribeBroadcast(TickBroadcast.class, ev-> {
			Vector<Future<OrderReceipt>> tmpVector = new Vector<>();
			tick=ev.getCurrentTick();
			if(!ev.getIsDone()) {
				while (!booksSortedByTicks.isEmpty() && booksSortedByTicks.peek().getKey() == tick) {
					Pair<Integer, String> tmp = booksSortedByTicks.poll();
					BookOrderEvent event = new BookOrderEvent(tmp.getValue(), customer, tmp.getKey());
					Future<OrderReceipt> tmpFuture=sendEvent(event);
					tmpVector.add(tmpFuture);
				}
				for(Future<OrderReceipt> t: tmpVector){
					if(t.get().getOrderId()!= -1){
						customer.addRecepit(t.get());
					}
				}
			}
			else {
				terminate();
				terminateCountDown.countDown();
			}
		});
		latch.countDown();
	}



	//Order the schedule by ticks
	private Pair<Integer,String>[] sortTheList(LinkedList<Pair<Integer,String>> orderSchedule){
		for(int i=0;i<pairs.length;i++){
			pairs[i]=orderSchedule.get(i);
		}
		for(int i=0;i<pairs.length-1;i++){  //sort the array !!
			for(int j=0;j<pairs.length-1;j++){
				if(pairs[j].getKey()>pairs[j+1].getKey()){
					Pair<Integer,String> tmp=pairs[j];
					pairs[j]=pairs[j+1];
					pairs[j+1]=tmp;
				}
			}
		}
		return pairs;
	}

	//move the ordered list to blocking Queue
	private void moveOrdersToQueue(Pair<Integer,String>[] pairs){
		for(int i=0;i<pairs.length;i++){
			booksSortedByTicks.add(pairs[i]);
		}
	}


}
