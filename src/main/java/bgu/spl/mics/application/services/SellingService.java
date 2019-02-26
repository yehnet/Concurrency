package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;
import bgu.spl.mics.application.passiveObjects.OrderResult;

import java.util.concurrent.CountDownLatch;

/**
 * Selling service in charge of taking orders from customers.
 * Holds a reference to the {@link MoneyRegister} singleton of the store.
 * Handles {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class SellingService extends MicroService{

	//----------------------------------------------------------Fields----------------------------------------//
	private MoneyRegister moneyRegister;
	int tick;
	private CountDownLatch latch;
	private CountDownLatch terminateCountDown;


	//----------------------------------------------------------Constructor----------------------------------------//
	public SellingService(CountDownLatch latch,CountDownLatch terminateCountDown) {
		super("SellingService");
		this.moneyRegister=MoneyRegister.getInstance();
		this.tick=0;
		this.latch=latch;
		this.terminateCountDown=terminateCountDown;
	}

	//----------------------------------------------------------Methods----------------------------------------//
	@Override
	protected void initialize() {
		subscribeBroadcast(TickBroadcast.class, ev->{
			tick=ev.getCurrentTick();
			if(ev.getIsDone()){
				terminate();
				terminateCountDown.countDown();
			}
		});

		subscribeEvent(BookOrderEvent.class, ev->{
			int tickProccess=tick;
			//first we checks if the book is available , else dont order
			Future<Integer> checkAvialability=sendEvent(new AvialabiltyCheckEvent(ev.getBookFromStore()));
			checkAvialability.get();
			if (ev.getCustomer().getAvailableCreditAmount() < checkAvialability.get()) {
				OrderReceipt orderReceipt = new OrderReceipt(-1, ev.getCustomer().getName(), ev.getCustomer().getId(), ev.getBookFromStore(), 0, tick, ev.getOrderTicks(), tickProccess);
				complete(ev, orderReceipt);
			}
			else {
				Future<OrderResult> takeBook=new Future<>();
				//we do synchronized on customer to make sure he is not charged twice or get book that he dont have money for
				synchronized (ev.getCustomer()) {
					if (ev.getCustomer().getAvailableCreditAmount() >= checkAvialability.get()) {
						moneyRegister.chargeCreditCard(ev.getCustomer(), checkAvialability.get());
						takeBook = sendEvent(new TakeBookEvent(ev.getBookFromStore()));
						takeBook.get();
					}
					else{
						takeBook.resolve(null);
					}
				}
				//if everything want well make a recepit and send delivery event
				if (checkAvialability.get() != -1 & takeBook.get() == OrderResult.SUCCESSFULLY_TAKEN) {
					OrderReceipt orderReceipt = new OrderReceipt(0, getName(), ev.getCustomer().getId(), ev.getBookFromStore(), checkAvialability.get(), tick, ev.getOrderTicks(), tickProccess);
						moneyRegister.file(orderReceipt);
						sendEvent(new DeliveryEvent(ev.getCustomer().getAddress(), ev.getCustomer().getDistance()));
						complete(ev, orderReceipt);
				}
				else {
					OrderReceipt orderReceipt = new OrderReceipt(-1, ev.getCustomer().getName(), ev.getCustomer().getId(), ev.getBookFromStore(), 0, tick, ev.getOrderTicks(), tickProccess);
					complete(ev, orderReceipt);
				}
			}
		});
		latch.countDown();
	}

}
