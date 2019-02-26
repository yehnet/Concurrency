package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AvialabiltyCheckEvent;
import bgu.spl.mics.application.messages.TakeBookEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.Inventory;

import java.util.concurrent.CountDownLatch;

/**
 * InventoryService is in charge of the book inventory and stock.
 * Holds a reference to the {@link Inventory} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */

public class InventoryService extends MicroService{

	//----------------------------------------------------------Fields----------------------------------------//
	private Inventory inventory;
	private CountDownLatch latch;
	private CountDownLatch terminateCountDown;


	//----------------------------------------------------------Constructor----------------------------------------//
	public InventoryService(CountDownLatch latch,CountDownLatch terminateCountDown) {
		super("InventoryService");
		this.inventory=Inventory.getInstance();
		this.latch=latch;
		this.terminateCountDown=terminateCountDown;
	}

	//----------------------------------------------------------Methods----------------------------------------//
	@Override
	protected void initialize() {
		subscribeBroadcast(TickBroadcast.class, ev->{
			if(ev.getIsDone()){
				terminate();
				terminateCountDown.countDown();
			}
		});
		subscribeEvent(AvialabiltyCheckEvent.class, ev-> {
			complete(ev, inventory.checkAvailabiltyAndGetPrice(ev.getBook()));
		});
		subscribeEvent(TakeBookEvent.class, ev->{
			complete(ev,inventory.take(ev.getBookTitle()));
		});
		latch.countDown();
	}

}
