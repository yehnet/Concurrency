package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TickBroadcast;

import static java.lang.Thread.sleep;

/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link Tick Broadcast}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService {

	//----------------------------------------------------------Fields----------------------------------------//
	private int speed;
	private int duration;
	private int currentTick;

	//----------------------------------------------------------Constructor----------------------------------------//
	public TimeService(int speed, int duration) {
		super("TimeService");
		this.speed = speed;
		this.duration = duration;
		this.currentTick = 0;
	}

	//----------------------------------------------------------Methods----------------------------------------//

	@Override
	protected void initialize() {
		//counts the ticks and send broadcast to every Micro Service
		while (currentTick < duration) {
			currentTick++;
			sendBroadcast(new TickBroadcast(currentTick, duration,false));
			if(currentTick==duration){
				sendBroadcast(new TickBroadcast(currentTick,duration,true));
			}
			try {
				sleep(speed);
			} catch (InterruptedException e) {}
		}
		terminate();

	}
}

