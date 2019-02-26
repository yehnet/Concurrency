package bgu.spl.mics.application;

import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.*;
import javafx.util.Pair;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.CountDownLatch;

/** This is the Main class of the application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */
public class BookStoreRunner {
    public static void main(String[] args) {
        Gson gson = new GsonBuilder().create();
        String path = args[0];
        JsonObject jsObj = null;
        try{
            jsObj = (JsonObject) new JsonParser().parse(new FileReader(path));
        }
        catch (FileNotFoundException e){}

        HashMap<Integer,Customer>customer_output=new HashMap<>();
        Pair<LinkedList<Pair<Integer,String>>[] ,Customer[]> customersArrays = null;
        int timeSpeed =0, timeDuration =0;
        int numOfSellingS =0,numOfInventoryS=0,numOfLogisticsS=0,numOfResources=0;
        // load from json file
        if(jsObj!=null){
            loadInventory(jsObj);
            loadResources(jsObj);
            customersArrays = loadCustomers(jsObj);
            for(int i=0; i<customersArrays.getValue().length;i++){
                customer_output.put(customersArrays.getValue()[i].getId(),customersArrays.getValue()[i]);
            }
            JsonObject jsServices = jsObj.get("services").getAsJsonObject();
            //load timeService details
            JsonElement jsTime = jsServices.get("time");
            timeSpeed = jsTime.getAsJsonObject().get("speed").getAsInt();
            timeDuration = jsTime.getAsJsonObject().get("duration").getAsInt();
            //load number of each service
            numOfSellingS = jsServices.get("selling").getAsInt();
            numOfInventoryS = jsServices.get("inventoryService").getAsInt();
            numOfLogisticsS = jsServices.get("logistics").getAsInt();
            numOfResources = jsServices.get("resourcesService").getAsInt();
        }

        TimeService timeService = new TimeService(timeSpeed,timeDuration);
        Thread timeT = new Thread(timeService);

        int numOfCustomers = customersArrays.getKey().length;
        //put all the threads from all the kinds in one array
        Thread[] threadsArray = new Thread[numOfInventoryS+numOfLogisticsS+numOfResources+numOfSellingS +numOfCustomers];

        CountDownLatch latch = new CountDownLatch(threadsArray.length);
        CountDownLatch terminateCountDown = new CountDownLatch(threadsArray.length);

        //initialize threads by the details we got
        for (int i = 0 ; i < numOfCustomers; i++){
            threadsArray[i] = new Thread(new APIService(customersArrays.getKey()[i],customersArrays.getValue()[i],latch,terminateCountDown));
        }
        for (int i = numOfCustomers ; i < numOfCustomers + numOfInventoryS ; i ++ ){
            threadsArray[i] = new Thread(new InventoryService(latch,terminateCountDown));
        }
        for (int i = numOfCustomers + numOfInventoryS ; i < numOfCustomers + numOfInventoryS+numOfLogisticsS; i++){
            threadsArray[i] = new Thread(new LogisticsService(latch,terminateCountDown));
        }
        for(int i = numOfCustomers + numOfInventoryS+numOfLogisticsS ; i < numOfCustomers + numOfInventoryS+numOfLogisticsS + numOfResources ; i++ ) {
            threadsArray[i] = new Thread (new ResourceService(latch,terminateCountDown));
        }
        for (int i = numOfCustomers + numOfInventoryS+numOfLogisticsS + numOfResources ; i < numOfCustomers + numOfInventoryS+numOfLogisticsS + numOfResources+numOfSellingS ; i++){
            threadsArray[i] = new Thread( new SellingService(latch,terminateCountDown));
        }
        //start the threads and then the timeService thread
        for (Thread t : threadsArray){
            t.start();
        }

        try{
            latch.await();
        }catch (InterruptedException e){}

        timeT.start(); // start the time service as the last one

        try{
            terminateCountDown.await();
        }catch (InterruptedException e){}

        //export the database to receieved filenames
        putCustomers(args[1],customer_output);
        Inventory.getInstance().printInventoryToFile(args[2]);
        MoneyRegister.getInstance().printOrderReceipts(args[3]);
        putMoney_Register(MoneyRegister.getInstance(),args[4]);
    }

    //------------Export Files-----------------------------------------------------------------------------------------//
    private static void putCustomers(String file_name_customer, HashMap<Integer,Customer> customer_output){
        try{
            FileOutputStream fos=new FileOutputStream(file_name_customer);
            ObjectOutputStream oos=new ObjectOutputStream(fos);
            oos.writeObject(customer_output);
            oos.close();
            fos.close();
        }catch (IOException e){
            System.out.println("cant create file-customers");
        }finally {

        }
    }

    private static void putMoney_Register(MoneyRegister moneyRegister, String filename){
        try{
            FileOutputStream fos=new FileOutputStream(filename);
            ObjectOutputStream oos=new ObjectOutputStream(fos);
            oos.writeObject(moneyRegister);
            oos.close();
            fos.close();
        }catch (IOException e){
            System.out.println("cant create file-moneyRegister");
        }
    }



    //------------Load from json-----------------------------------------------------------------------------------------//

    private static void loadInventory(JsonObject jsObj){
        //initialize inventory
        JsonElement jsBooks = jsObj.get("initialInventory");
        int invSize = jsBooks.getAsJsonArray().size();
        BookInventoryInfo[] books = new BookInventoryInfo[invSize];
        JsonObject jsBook;
        for (int i = 0 ; i < invSize ; i++){
            jsBook = jsBooks.getAsJsonArray().get(i).getAsJsonObject();
            String book = jsBook.get("bookTitle").getAsString();
            int amount = jsBook.get("amount").getAsInt();
            int price = jsBook.get("price").getAsInt();
            books[i] = new BookInventoryInfo(book,amount, price);
        }
        Inventory.getInstance().load(books);
    }

    private static void loadResources(JsonObject jsObj){
        //initilize resources
        JsonElement jsResources = jsObj.get("initialResources");
        int vecSize1=jsResources.getAsJsonArray().get(0).getAsJsonObject().get("vehicles").getAsJsonArray().size();
        DeliveryVehicle[] vehicles = new DeliveryVehicle[vecSize1];
        JsonObject jsVehicle;
        for (int i = 0 ; i < vecSize1 ; i++ ){
            jsVehicle = jsResources.getAsJsonArray().get(0).getAsJsonObject().get("vehicles").getAsJsonArray().get(i).getAsJsonObject();
            int license = jsVehicle.get("license").getAsInt();
            int speed = jsVehicle.get("speed").getAsInt();
            vehicles[i] = new DeliveryVehicle(license,speed);
        }
        ResourcesHolder.getInstance().load(vehicles);
    }
    // load customers and their orders, return linkedlist of the customers orders and array of the customers
    private static Pair<LinkedList<Pair<Integer,String>>[] ,Customer[]>  loadCustomers(JsonObject jsObj){
        JsonElement jsCustomers = jsObj.get("services").getAsJsonObject().get("customers");
        int custSize = jsCustomers.getAsJsonArray().size();
        Customer[] customers = new Customer[custSize];
        LinkedList<Pair<Integer,String>>[] orderScheduleArray= new LinkedList[custSize];
        JsonObject jsCustomer;
        //load the customers
        for (int i = 0 ; i < custSize ; i ++){
            jsCustomer = jsCustomers.getAsJsonArray().get(i).getAsJsonObject();
            int id = jsCustomer.get("id").getAsInt();
            String name = jsCustomer.get("name").getAsString();
            String address = jsCustomer.get("address").getAsString();
            int distance = jsCustomer.get("distance").getAsInt();
            int CCNum = jsCustomer.get("creditCard").getAsJsonObject().get("number").getAsInt();
            int CCAmount = jsCustomer.get("creditCard").getAsJsonObject().get("amount").getAsInt();

            LinkedList<Pair<Integer,String>> orderSchedule = new LinkedList<>();
            int orderSize = jsCustomers.getAsJsonArray().get(i).getAsJsonObject().get("orderSchedule").getAsJsonArray().size();
            JsonObject jsOrder;
            //load the customers order details
            for (int j = 0 ; j < orderSize ; j ++ ) {
                jsOrder = jsCustomers.getAsJsonArray().get(i).getAsJsonObject().get("orderSchedule").getAsJsonArray().get(j).getAsJsonObject();
                String bookTitle = jsOrder.get("bookTitle").getAsString();
                Integer tick = jsOrder.get("tick").getAsInt();
                orderSchedule.add(new Pair<>(tick,bookTitle));
            }
            customers[i] = new Customer(name, id , address , distance, CCNum , CCAmount );
            orderScheduleArray[i] = orderSchedule;
        }
        return new Pair<LinkedList<Pair<Integer,String>>[] ,Customer[]>(orderScheduleArray,customers);
    }
}