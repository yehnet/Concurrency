package bgu.spl.mics.application.passiveObjects;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * Passive data-object representing a customer of the store.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class Customer implements Serializable {

	//----------------------------------------------------------Fields----------------------------------------//
	private String name;
	private int id;
	private String address;
	private int distance;
	private int creditNumber;
	private int creditAmount;
	private LinkedList<OrderReceipt> customerRecepitList;

	//----------------------------------------------------------Constructor----------------------------------------//
	public Customer(String name, int id, String address, int distance, int creditNumber, int creditAmount){
		this.name=name;
		this.id=id;
		this.address=address;
		this.distance=distance;
		this.creditNumber=creditNumber;
		this.creditAmount=creditAmount;
		this.customerRecepitList=new LinkedList<>();
	}


	//----------------------------------------------------------Methods----------------------------------------//
	/**
     * Retrieves the name of the customer.
     */
	public String getName() {
		return name;
	}

	/**
     * Retrieves the ID of the customer  . 
     */
	public int getId() {
		return id;
	}
	
	/**
     * Retrieves the address of the customer.  
     */
	public String getAddress() {
		return address;
	}
	
	/**
     * Retrieves the distance of the customer from the store.  
     */
	public int getDistance() {
		return distance;
	}

	
	/**
     * Retrieves a list of receipts for the purchases this customer has made.
     * <p>
     * @return A list of receipts.
     */
	public List<OrderReceipt> getCustomerReceiptList() {
		return customerRecepitList;
	}
	
	/**
     * Retrieves the amount of money left on this customers credit card.
     * <p>
     * @return Amount of money left.   
     */
	public int getAvailableCreditAmount() {
		return creditAmount;
	}
	
	/**
     * Retrieves this customers credit card serial number.    
     */
	public int getCreditNumber() {
		return creditNumber;
	}


	//Sets the Amount of the customer on th credit
	public void setCreditAmount(int amount){
		creditAmount=creditAmount-amount;
	}


	//adds receipt to his list
	public void addRecepit(OrderReceipt r){
		this.customerRecepitList.add(r);
	}

	
}
