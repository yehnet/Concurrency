package bgu.spl.mics.application.passiveObjects;

import bgu.spl.mics.Future;
import javafx.util.Pair;

import java.util.LinkedList;

/**
 * Passive object representing the resource manager.
 * You must not alter any of the given public methods of this class.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private methods and fields to this class.
 */
public class ResourcesHolder {

	//----------------------------------------------------------Fields----------------------------------------//
	private static ResourcesHolder resourcesHolder=null;
	private LinkedList<Pair<DeliveryVehicle, Boolean>> vehicles;


	//----------------------------------------------------------Private Constructor----------------------------------------//

	private ResourcesHolder(){
		this.vehicles=new LinkedList<>();
	}

	//----------------------------------------------------------Methods----------------------------------------//


	/**
     * Retrieves the single instance of this class.
     */
	public static ResourcesHolder getInstance() {
		if(resourcesHolder==null){
			resourcesHolder=new ResourcesHolder();
		}
		return resourcesHolder;
	}

	/**
     * Tries to acquire a vehicle and gives a future object which will
     * resolve to a vehicle.
     * <p>
     * @return 	{@link Future<DeliveryVehicle>} object which will resolve to a
     * 			{@link DeliveryVehicle} when completed.
     */
	//We made it non-blocking and just let one by one enter without waiting
	public  Future<DeliveryVehicle> acquireVehicle() {
			synchronized (this){
				Future<DeliveryVehicle> ansVehicle=new Future<>();
				int index=-1;
				index=checkAvailableCarAndGetIndex(this.vehicles, false);
				if(index==-1){ return null;}
				else{
					ansVehicle.resolve(this.vehicles.get(index).getKey());
					return ansVehicle;
				}
			}
		}

	//checks if there is and available Car
	private int checkAvailableCarAndGetIndex(LinkedList<Pair<DeliveryVehicle, Boolean>> vehicles, boolean condition){
		int tmp=0;
		for(int i=0; i<this.vehicles.size() && !condition ; i++){
			if(this.vehicles.get(i).getValue()==false){
				this.vehicles.set(i,new Pair<>(vehicles.get(i).getKey(),true));
				condition=true;
				tmp=i;
			}
		}
		return tmp;
	}
	/**
     * Releases a specified vehicle, opening it again for the possibility of
     * acquisition.
     * <p>
     * @param vehicle	{@link DeliveryVehicle} to be released.
     */
	public void releaseVehicle(DeliveryVehicle vehicle) {
		for(int i=0;i<this.vehicles.size();i++){
			if(this.vehicles.get(i).getKey().equals(vehicle)){
				this.vehicles.set(i,new Pair<>(vehicles.get(i).getKey(),false));
			}
		}
	}

	/**
     * Receives a collection of vehicles and stores them.
     * <p>
     * @param vehicles	Array of {@link DeliveryVehicle} instances to store.
     */
	public void load(DeliveryVehicle[] vehicles) {
		for( int i=0;i<vehicles.length;i++){
			this.vehicles.add(new Pair<>(vehicles[i], false));
		}
	}

}
