package bgu.spl.mics.application.passiveObjects;

import java.util.ArrayList;
import java.util.List;

/**
 * Passive data-object representing a customer of the store.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class Customer {

	//Fields
	private String name;
	private int id;
	private String address;
	private int distance;
	private int creditNumber;
	private int creditAmount;
	private List<OrderReceipt> customerRecepitList;

	//Constructor
	public Customer(String name, int id, String address, int distance, int creditNumber, int creditAmount, List<OrderReceipt> customerRecepitList){
		this.name=name;
		this.id=id;
		this.address=address;
		this.distance=distance;
		this.creditNumber=creditNumber;
		this.creditAmount=creditAmount;
		this.customerRecepitList= new ArrayList<>();
	}

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

	//Add methods
	/**
	 * increase the amount in the credit
	 */
	public void increaseCreditAmount(int amount) {
		this.creditAmount=this.creditNumber+amount;
	}

	/**
	 * decrease the amount in the credit
	 */
	public void decreaseCreditAmount(int amount) {
		this.creditAmount=this.creditNumber-amount;
	}

	
}
