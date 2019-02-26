package bgu.spl.mics;

import javafx.util.Pair;

import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {


	//----------------------------------------------------------Fields----------------------------------------//
	private static MessageBusImpl messageBus=null;

	//Messages for each MicroService
	private ConcurrentHashMap<MicroService, LinkedBlockingQueue<Message>>  messageServiceQueue;

	//RoundRobin
	private ConcurrentHashMap<Class<? extends Event>, LinkedBlockingQueue<MicroService>> roundRobin;

	//Events and their futures
	private ConcurrentHashMap<Event,Future> futures;

	//Micro Services and the Events and Broadcasts that he subscribes
	private ConcurrentHashMap<MicroService, Pair<Vector<Class<? extends Broadcast>>,Vector<Class<? extends Event>>>> listOfSubscibeMessages;


	//----------------------------------------------------------Constructor----------------------------------------//
	private MessageBusImpl(){
		this.messageServiceQueue=new ConcurrentHashMap<>();
		this.roundRobin=new ConcurrentHashMap<>();
		this.futures=new ConcurrentHashMap<>();
		this.listOfSubscibeMessages=new ConcurrentHashMap<>();
	}

	//----------------------------------------------------------Methods----------------------------------------//
	public static MessageBusImpl getInstance() {
		if (messageBus==null){
			messageBus=new MessageBusImpl();
		}
		return messageBus;
	}



	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		if(type!=null) {
			listOfSubscibeMessages.get(m).getValue().add(type);
			//make sure that we don't make 2 different queue for the same event
			synchronized (roundRobin) {
				if (!roundRobin.containsKey(type)) {
					roundRobin.put(type, new LinkedBlockingQueue<>());
				}
				try { roundRobin.get(type).put(m);
				} catch (InterruptedException e) {}
			}
		}
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		listOfSubscibeMessages.get(m).getKey().add(type);
	}

	@Override
	public  <T> void complete(Event<T> e, T result) {
			if (e != null) {
				futures.get(e).resolve(result);
				futures.remove(e);
			}
	}

	@Override
	public  void sendBroadcast(Broadcast b) {
		for(Map.Entry<MicroService, Pair<Vector<Class<? extends Broadcast>>,Vector<Class<? extends Event>>>> s: listOfSubscibeMessages.entrySet()){
			if(s.getValue().getKey().contains(b.getClass())){
				//adds to micro service that support this broadcast to his message queue
				messageServiceQueue.get(s.getKey()).add(b);
			}
		}
	}


	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		Future<T> ansFuture=new Future<>();
		futures.put(e,ansFuture);
		try {
			synchronized (roundRobin.get(e.getClass())) {
				if(!roundRobin.get(e.getClass()).isEmpty()) {
					//make the round robin loop
					MicroService nextMicroservice = roundRobin.get(e.getClass()).take();
					roundRobin.get(e.getClass()).put(nextMicroservice);
					messageServiceQueue.get(nextMicroservice).add(e);
				}
			}
		}
		catch (Exception ex){return null;}
		return ansFuture;
	}

	@Override
	public void register(MicroService m) {
		Vector<Class<? extends Event>> event=new Vector<>();
		Vector<Class<? extends Broadcast>> broadcast=new Vector<>();
		Pair<Vector<Class<? extends Broadcast>>,Vector<Class<? extends Event>>> vectorMessagesPair=new Pair<>(broadcast,event);
		listOfSubscibeMessages.put(m,vectorMessagesPair);
		messageServiceQueue.put(m,new LinkedBlockingQueue<>());
	}

	@Override
	public void unregister(MicroService m) {
			for (Message s : messageServiceQueue.get(m)) {
				if (Event.class.isAssignableFrom(s.getClass())) {
					if (futures.containsKey(s)) {
						futures.get(s).resolve(null);
					}
				}
			}
			messageServiceQueue.remove(m);
			listOfSubscibeMessages.remove(m);
			for (LinkedBlockingQueue<MicroService> services : roundRobin.values()) {
				if (services.contains(m)) {
					services.remove(m);
				}
			}
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
			Message ans = messageServiceQueue.get(m).take();
			return ans;
	}



}
