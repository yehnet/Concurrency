package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;

import java.awt.*;

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

	//private List<OrderBookEvent> orderSchedule;

	public APIService(List orderSchedule) {
		super("APIservice");
	}

	@Override
	protected void initialize() {
		// TODO Implement this
		
	}

	//public void run(){}

	//protected <T, E extends Event<T>> void subscribeEvent(Class<E> type, Callback<E> callback){}

}
