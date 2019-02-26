package bgu.spl.mics.application.services;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AcquireVehicleEvent;
import bgu.spl.mics.application.messages.ReleaseVehicleEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

/**
 * ResourceService is in charge of the store resources - the delivery vehicles.
 * Holds a reference to the {@link ResourceHolder} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class ResourceService extends MicroService{

	//----------------------------------------------------------Fields----------------------------------------//
	private ResourcesHolder resourcesHolder;
	private CountDownLatch latch;
	private CountDownLatch terminateCountDown;
	private ArrayList<Event> unCompletedAcquireEvents;


	//----------------------------------------------------------Constructor----------------------------------------//
	public ResourceService(CountDownLatch latch,CountDownLatch terminateCountDown) {
		super("ResourceService");
		this.resourcesHolder=ResourcesHolder.getInstance();
		this.latch=latch;
		this.terminateCountDown=terminateCountDown;
		this.unCompletedAcquireEvents=new ArrayList<>();
	}

	//----------------------------------------------------------Methods----------------------------------------//
	@Override
	protected void initialize() {
		subscribeBroadcast(TickBroadcast.class, ev->{
			if(ev.getIsDone()){
				for(int i=0;i<unCompletedAcquireEvents.size();i++){
					this.complete(unCompletedAcquireEvents.get(i), null);
				}
				terminate();
				terminateCountDown.countDown();
			}
			else { TryToAcquireCarAgain(this.unCompletedAcquireEvents);}

		});
		subscribeEvent(AcquireVehicleEvent.class, ev->{
			Future<DeliveryVehicle> ans=this.resourcesHolder.acquireVehicle();
			if(ans != null){ complete(ev, ans.get());}
			else{unCompletedAcquireEvents.add(ev);}
		});
		subscribeEvent(ReleaseVehicleEvent.class,ev->{
			resourcesHolder.releaseVehicle(ev.getDeliveryVehicle());
			complete(ev,null);
		});
		latch.countDown();
	}

	//try to acquire car again for all the vehicles that didn't succeed
	private void TryToAcquireCarAgain(ArrayList<Event> unCompletedAcquireEvents){
		for (int i = 0; i < unCompletedAcquireEvents.size(); i++) {
			Future<DeliveryVehicle> ansFuture = this.resourcesHolder.acquireVehicle();
			if (ansFuture != null) {
				this.complete(unCompletedAcquireEvents.get(i), ansFuture.get());
				unCompletedAcquireEvents.remove(i);
			}
		}
	}

}
