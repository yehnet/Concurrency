package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AcquireVehicleEvent;
import bgu.spl.mics.application.messages.DeliveryEvent;
import bgu.spl.mics.application.messages.ReleaseVehicleEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;

import java.util.concurrent.CountDownLatch;

/**
 * Logistic service in charge of delivering books that have been purchased to customers.
 * Handles {@link DeliveryEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LogisticsService extends MicroService {

	//----------------------------------------------------------Fields----------------------------------------//
	private CountDownLatch latch;
	private CountDownLatch terminateCountDown;

	//----------------------------------------------------------Constructor----------------------------------------//
	public LogisticsService(CountDownLatch latch,CountDownLatch terminateCountDown) {
		super("Logistic");
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
		subscribeEvent(DeliveryEvent.class, ev-> {
			Future<DeliveryVehicle>	tmp = sendEvent(new AcquireVehicleEvent());
			if(tmp.get() !=  null){
				tmp.get().deliver(ev.getAddress(),ev.getDistance());
				sendEvent(new ReleaseVehicleEvent(tmp.get()));
			}
			complete(ev, null);
		});
		latch.countDown();
	}

}
