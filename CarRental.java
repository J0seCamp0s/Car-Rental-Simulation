import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import javax.swing.SwingUtilities;

public class CarRental extends BaseRunningProgram{
    private String location;
    private Integer allocatedSpaces;
    private Double earnings = 0.0;
    private Double loses = 0.0;
    
    private List<String> lotList; 
    private List<String> supportedCommands = new ArrayList<>(){};
    
    public CarRental() {
        //Add supporte flags to list
        Collections.addAll(supportedFlags,
        "location","spaces-available",
        "lots");

        //Add supported commands to list
        Collections.addAll(supportedCommands, "RENT", 
        "RETURN", "LIST", "TRANSACTIONS", "EXIT");

        //Assign expected types to flags
        expectedParameterTypes.put("location","String");
        expectedParameterTypes.put("spaces-available","Integer");
        expectedParameterTypes.put("lots","String");
    }

    public static void main(String[] args) {
        CarRental rentalShop = new CarRental();

        //Parse flags passed into the program
        for(String flag: args) {
            if(!rentalShop.ParseFlag(flag)) {
                return;
            }
        }
        //Set rental shop's location once flags are parsed
        rentalShop.SetLocation();
        rentalShop.SetAllocatedSpaces();
        rentalShop.SetLots();

        //Check flag parameter types
        rentalShop.CheckFlagParameterTypes();

        //Retrieve car rental shop data
        rentalShop.RetrieveLocationData();

        //Remove cars from shop if allocated spaces were changed
        rentalShop.UpdateShopCars();

        //Listen for inputs from user
        //rentalShop.RunCommands();

        SwingUtilities.invokeLater(() -> new CarRentalGUI(rentalShop));

    }

    private void SetLocation() {
        location = flagParameters.get("location");
        
    }

    private void SetAllocatedSpaces() {
        allocatedSpaces = Integer.valueOf(flagParameters.getOrDefault("spaces-available", "3"));
    }

    private void SetLots() {
        String[] lots = flagParameters.getOrDefault("lots", location).split(",");
        lotList = new ArrayList<>(Arrays.asList(lots));
        String allAvailableLots = ReadFile("LotIndex.txt");
        if(allAvailableLots.isBlank()) {
            System.out.println("There are no lots defined yet!");
            System.out.println("This may affect the performance of the shop, please use the LotManager program to define some lots first!");
            return;
        }

        for(String lot: lotList) {
            if(!allAvailableLots.contains(lot)) {
                System.out.println(String.format("The lot %s is not part of our lot index, cannot be part of allocated lots list", lot));
            }
        }
    }

    @Override
    protected void RetrieveLocationData() {
        String rentalShopData = ReadFile(location + ".txt");
        if(!rentalShopData.isBlank()) {
            String earningsData, carsData, lotsData, carAmount;
            Integer startIndex = 0, midIndex, endIndex;
            endIndex = rentalShopData.indexOf("\n");
            lotsData = rentalShopData.substring(startIndex, endIndex);

            startIndex = endIndex + 1;
            endIndex = rentalShopData.indexOf("\n", startIndex);
            carAmount = rentalShopData.substring(startIndex, endIndex);

            startIndex = endIndex + 1;
            midIndex = rentalShopData.indexOf("\n", startIndex);
            endIndex = rentalShopData.indexOf("\n", midIndex + 1);
            earningsData = rentalShopData.substring(startIndex, endIndex + 1);

            startIndex = endIndex + 1;
            carsData = rentalShopData.substring(startIndex);

            RetrieveLots(lotsData);
            RetrieveCarSpaces(carAmount);
            RetrieveEarnings(earningsData);
            if(!RetrieveLocationCars(carsData)) {
                System.out.println(String.format("Format errors ecountered in %sShop.txt file, some vehicles might have not been retrieved", location));
            }
        }
        else {
            System.out.println(String.format("The car rental file for current location (%s) does not exist yet.", flagParameters.get("location")));
        }
    }

    private void RetrieveEarnings(String earningsData) {
        String earningsAmount, losesAmount;
        Integer startIndex = 0, endIndex;
        endIndex = earningsData.indexOf("\n");
        earningsAmount = earningsData.substring(startIndex, endIndex);
        startIndex = endIndex + 1;
        endIndex = earningsData.indexOf("\n", startIndex);
        losesAmount = earningsData.substring(startIndex, endIndex);
        try {
            earnings = Double.valueOf(earningsAmount);
            loses = Double.valueOf(losesAmount);
        } catch (NumberFormatException e) {
            System.out.println(String.format("An error was detected in the current shop's (%s) file data!", location));
            System.out.println("Defaulting to 0.0 earnings and 0.0 loses");
        }
        
    }

    private void RetrieveLots(String lotString) {
        String[] lotsFromFile = lotString.split(",");

        if(lotsFromFile.length == 0) {
            System.out.println(String.format("No lots found in %s.txt, using lots passed in flag parameters", location));
        }
        else if (lotsFromFile[0].isBlank()) {
            System.out.println(String.format("No lots found in %s.txt, using lots passed in flag parameters", location));
        }

        lotList = new ArrayList<>(Arrays.asList(lotsFromFile));

        String allAvailableLots = ReadFile("LotIndex.txt");
        if(allAvailableLots.isBlank()) {
            System.out.println("There are no lots defined yet!");
            System.out.println("This may affect the performance of the shop, please use the LotManager program to define some lots first!");
            return;
        }

        for(String lot: lotsFromFile) {
            if(!allAvailableLots.contains(lot)) {
                System.out.println(String.format("The lot %s is not part of our lot index, cannot be part of allocated lots list", lot));
            }
        }
        
    }

    private void RetrieveCarSpaces(String carAmount) {
        if(carAmount.isBlank()) {
            System.out.println(String.format("No car amount was found in %s.txt", location));
            System.out.println("Using value specified in flag parameters");
            return;
        }

        Integer carAmountInteger;
        try {
            carAmountInteger = Integer.valueOf(carAmount);
        } catch (NumberFormatException e) {
            System.out.println(String.format("Error in car amount format at %s.txt", location));
            System.out.println("Using value specified in flag parameters");
            return;
        }
        allocatedSpaces = carAmountInteger;
    } 

    private void RunCommands() {
        String command = "", commandType, commandParameters;
        try (Scanner inputReciever = new Scanner(System.in)) {
            while(!command.contains("EXIT")) {
                System.out.println("Please enter a command:");
                
                //Receive command from command line/GUI
                command = inputReciever.nextLine();
                
                //format command properly
                command = command.toUpperCase();
                command = command.trim();
                
                //Parse extract command type from command
                Integer endOfCommandType = command.indexOf(" ");
                if(endOfCommandType == -1) {
                    endOfCommandType = command.length();
                }
                commandType = command.substring(0, endOfCommandType);
                
                //Check command is supported
                if(!supportedCommands.contains(commandType)) {
                    System.out.println(String.format("Error! %s is not a supported command", commandType));
                    continue;
                }
                
                //Take action based on command type
                switch(commandType) {
                    case "RENT" -> {
                        //Check if paramters have been passed alongside command
                        if((endOfCommandType) == command.length())
                        {
                            System.out.println("No parameters where given for RENT command!");
                            System.out.println("Unable to perform operation!");
                            continue;
                        }
                        //Extract parameters from command
                        commandParameters = command.substring(endOfCommandType + 1);
                        //Retrieve a car from store or lot
                        Car selectedCar = RentCar(commandParameters);
                        if(selectedCar != null) {
                            System.out.println(String.format("Car with license plate %s has been rented out sucessfully!", selectedCar.GetLicensePlate()));
                            UpdateRentedCarsFile(selectedCar, true);
                        }
                    }
                    case "RETURN" -> {
                        //Check if paramters have been passed alongside command
                        if((endOfCommandType) == command.length())
                        {
                            System.out.println("No parameters where given for RENT command!");
                            System.out.println("Unable to perform operation!");
                            continue;
                        }
                        
                        //Extract command paramters from user inputs
                        commandParameters = command.substring(endOfCommandType + 1);
                        String licensePlate, kmString;
                        Integer endOfLicensePlate = commandParameters.indexOf(" ");
                        
                        //Check if km travelled were given as parameter
                        if(endOfLicensePlate < 0) {
                            System.out.println("No kilometers travelled were specified for returned car!");
                            System.out.println("Unable to perform operation!");
                            continue;
                        }
                        
                        //Extract license plate
                        licensePlate = commandParameters.substring(0, endOfLicensePlate);
                        if(!CheckLincensePlateFormat(licensePlate)) {
                            System.out.println(String.format("%s is not a valid license plate format!", licensePlate));
                            System.out.println("Unable to perform operation!");
                            continue;
                        }
                        
                        //Extract km travelled
                        kmString = commandParameters.substring(endOfLicensePlate + 1);
                        Double kmTravelled;
                        
                        try {
                            kmTravelled = Double.valueOf(kmString);
                        }
                        catch (NumberFormatException e) {
                            System.out.println("Non-numeric value given for distance travelled!");
                            continue;
                        }
                        ReturnCar(licensePlate, kmTravelled);
                    }
                    case "LIST" -> {
                        System.out.println(String.format("Retreiving current status of %s shop", location));
                        GetList();
                    }
                    case "TRANSACTIONS" -> {
                        System.out.println(String.format("Retreiving transactions from %s shop", location));
                        GetTransactions();
                    }
                }
                //Update allocated cars for shop
                UpdateShopCars();
            }
        }
    }

    public Car RentCar(String carType) {
        if(!CarStaticData.SEDAN.equals(carType) && !CarStaticData.VAN.equals(carType) && !CarStaticData.SUV.equals(carType)) {
            System.out.println(String.format("%s is not a supported car type!", carType));
            return null;
        }

        //Look for car in stores car list
        for(Car currentCar: carList) {
            //If car found
            //Remove it from carList
            //Update car rental's data file
            //Return it
            if(currentCar.GetCarType().equals(carType)) {
                carList.remove(currentCar);
                currentCar.SetDiscountRate(0);
                return currentCar;
            }
        }

        //Search for car in lots allocated to rental shop
        //Return it if found
        Car searchCar = RetrieveCarFromLot(carType);
        if(searchCar != null) {
            searchCar.SetDiscountRate(10);
            return searchCar;
        }

        //Return null if no car was found in lots
        System.out.println("No car was found for rental in shop or allocated lots.");
        return null;
    }
    
    public void ReturnCar(String licensePlate, Double kilometersTraversed) {
        String formatedLicensePlate = licensePlate.toUpperCase();
        String rentedCarString = ReadFile("RentedCars.txt");
        if(!rentedCarString.contains(formatedLicensePlate)) {
            System.out.println("Car is not part of our rented cars log!");
            System.out.println("Car return denied.");
            return;
        }

        Integer discountRate;

        //Remove car from rented car list and apply discount rate if needed
        List<Car> rentedCars = RetrieveRentedCars();
        for (int i = 0; i < rentedCars.size(); i++) {
            if(rentedCars.get(i).GetLicensePlate().equals(formatedLicensePlate)) {
                discountRate = rentedCars.get(i).GetDiscountRate();

                Tuple2<Double, Double> transactionEarnings = CalculateLoses(discountRate, kilometersTraversed);  
                loses += transactionEarnings.GetItem1();
                earnings += transactionEarnings.GetItem2();

                UpdateTransactionsFile(rentedCars.get(i).GetDiscountRate(), rentedCars.get(i), transactionEarnings.GetItem2());
                UpdateRentedCarsFile(rentedCars.get(i), false);
                rentedCars.remove(i);
            }
        }
    }

    public void GetList() {
        System.out.println(String.format("===== Current State of the %s shop =====", location));

        System.out.println(String.format("+Location Earnings: %s", earnings));
        System.out.println(String.format("+Location Loses: %s", loses));

        System.out.println("+Car spaces information");
        String carString;
        for(int i = 0; i < allocatedSpaces; i++) {
            if(i <= carList.size() -1) {
                carString = carList.get(i).ToString();
                carString = carString.replace("\n", "");
            }
            else {
                carString = "Empty Space";
            }
            System.out.println(String.format("++Car space #%d: %s", i, carString));
        }

    }

    public void GetTransactions() {
        System.out.println(String.format("Total Earnings from %s shop: %f", location, earnings));
        String transactions = ReadFile(location + "Transactions.txt");
        System.out.println("Transactions list:");
        System.out.println(transactions);
    }

    private Tuple2<Double, Double> CalculateLoses(Integer discountRate, Double kmTravelled) {
        Double discount = kmTravelled * (discountRate/100);
        Double earned = kmTravelled - discount;

        return new Tuple2<>(discount, earned);
    }

    private void UpdateTransactionsFile(Integer discountRate, Car returnedCar, Double transactionEarnings) {
        String newTransaction = "";
        String discountApplied;
        if(discountRate == 0) {
            discountApplied = "false";
        }
        else {
            discountApplied = "true";
        }
        newTransaction += "====================================\n";
        newTransaction += "Discount applied = " + discountApplied + "\n";
        newTransaction += "Earnings from transaction  = " + transactionEarnings.toString() + "\n";
        newTransaction += "Car information = " + returnedCar.ToString() + "\n";
        try {
            EditFile(newTransaction, location + "Transactions.txt", true);
        }
        catch(IOException e) {
            System.out.println(String.format("Unable to update %sTransactions.txt file", location));
        }
       
    }

    private List<Car> RetrieveRentedCars() {
        List<Car> rentedCars = new ArrayList<>(){};
        String rentedCarString = ReadFile("RentedCars.txt");

        Integer startIndex = 0, endIndex;
        Boolean discountRate;
        String carString;

        while(startIndex < rentedCarString.length()) {
            //Retrieve discount rate from line above car info
            endIndex = rentedCarString.indexOf('\n', startIndex);
            discountRate = Boolean.valueOf(rentedCarString.substring(startIndex, endIndex));

            //Retrieve car info in next line
            startIndex = endIndex + 1;
            endIndex = rentedCarString.indexOf('\n', startIndex);
            carString = rentedCarString.substring(startIndex, endIndex);
            
            //Store car info into carTuple
            Tuple3<String, String, Double> carTuple = ParseCarString(carString);

            //Return early if format in RentedCars is wrong
            if(carTuple.GetItem1() == null) {
                System.out.println("There is a format error in the file RentedCars.txt");
                return null;
            }
            
            //Add new car to rentedCars list if no problems encountered
            AddVehicle(carTuple.GetItem1(), carTuple.GetItem2(), carTuple.GetItem3(), rentedCars, discountRate);
            //Go to next line
            startIndex = endIndex + 1;
        }
        return rentedCars;
    }

    private void UpdateRentedCarsFile(Car rentedCar, Boolean mode) {
        String discountRate = rentedCar.GetDiscountRate().toString();
        String carString = rentedCar.ToString();
        String rentedCarString = discountRate + "\n" + carString;

        //mode == true, add new car to RentedCars.txt file
        if(mode) {
            try {
                EditFile(rentedCarString, "RentedCars.txt", true);
            } catch (IOException e) {
                System.out.println(String.format("Error when adding car with license plate %s to RentedCars.txt!", rentedCar.GetLicensePlate()));
            }
        }

        //mode == false, remove car from RentedCars.txt file
        else {
            System.out.println(String.format("Removing car with license plate %s from RentedCars.txt", rentedCar.GetLicensePlate()));
            String rentedCars = ReadFile("RentedCars.txt");
            rentedCars = rentedCars.replace(rentedCarString,"");
            try {
                EditFile(rentedCars, "RentedCars.txt", false);
            } catch (IOException e) {
                System.out.println(String.format("Error when removing car with license plate %s from RentedCars.txt!", rentedCar.GetLicensePlate()));
            }
        }
    }

    public void UpdateShopCars() {
        while(carList.size() > allocatedSpaces - 2 && !carList.isEmpty()) {
            //Send first car to lot
            SendCarToLot(carList.get(0));
        }
        if (carList.isEmpty()){
            //Retrieve car from lot list if possible
            Car retrievedCar = RetrieveCarFromLot();
            if(retrievedCar != null) {
                carList.add(retrievedCar);
            }
        }
        UpdateRentalShopFile();
    }

    private void UpdateRentalShopFile() {
        String rentalShopString = "";
        String lotString = "";
        for (String lot : lotList) {
            lotString += lot + ",";
        }
        rentalShopString += lotString + "\n";
        rentalShopString += allocatedSpaces.toString() + "\n";
        rentalShopString += earnings.toString() + "\n" + loses.toString() + "\n";

        for (int i = 0; i < allocatedSpaces; i++) {
            //If i is a valid index for carList
            //Add car from index i to rentaShopString
            if(i <= carList.size() - 1) {
                rentalShopString += carList.get(i).ToString();
            }
            else {
                rentalShopString += "#\n";
            }
        }

        try {
            EditFile(rentalShopString, location + ".txt", false);
        } catch (IOException e) {
            System.out.println(String.format("Error trying to update %s.txt file!", location));
        }

    }

    private void SendCarToLot(Car sentCar) {
        carList.remove(sentCar);
        String carString = sentCar.ToString();

        String lotIndex = ReadFile("LotIndex.txt");
        String[] lots = lotIndex.split("\n");
        Integer smallestLotIndex = 0,  carAmount, smallestCarAmount = Integer.MAX_VALUE;
        
        LotManager manager = new LotManager();
        for (int i = 0; i < lots.length; i++) {
            manager.SetLotName(lots[i]);
            manager.RetrieveLocationData();
            carAmount = manager.GetCarListSize();
            if(carAmount < smallestCarAmount) {
                smallestCarAmount = carAmount;
                smallestLotIndex = i;
            }
        }
        String selectedLot = lots[smallestLotIndex];

        try {
            EditFile(carString, selectedLot + ".txt", true);
        } catch (IOException e) {
            System.out.println(String.format("Error when transefirng car with license plate %s from current shop to lot %s!", sentCar.GetLicensePlate(), selectedLot));
        }
        UpdateRentalShopFile();
    }

    //Used for retrieving a car when car amount falls bellow treshold
    private Car RetrieveCarFromLot() {
        if(lotList.isEmpty()) {
            System.out.println("No lots have been allocated to this shop!");
            System.out.println("Unable to search for rental car.");
            return null;
        }

        LotManager searchLot = new LotManager();
        Car lotCar;
        for(String lot: lotList) {
            searchLot.SetLotName(lot);
            searchLot.RetrieveLocationData();
            lotCar = searchLot.SearchCar();
            if(lotCar != null) {
                System.out.println(String.format("Retreiving a car of type %s from lot %s to keep a car in stock", lotCar.GetCarType(), lot));
                searchLot.UpdateLotFile();
                return lotCar;
            }
        }
        UpdateRentalShopFile();
        return null;
    }

    //Used for retrieving a car when doing a search for a rent
    private Car RetrieveCarFromLot(String carType) {
        if(lotList.isEmpty()) {
            System.out.println("No lots have been allocated to this shop!");
            System.out.println("Unable to search for rental car.");
            return null;
        }

        LotManager searchLot = new LotManager();
        Car lotCar;
        for(String lot: lotList) {
            searchLot.SetLotName(lot);
            searchLot.RetrieveLocationData();
            lotCar = searchLot.SearchCar(carType);
            if(lotCar != null) {
                System.out.println(String.format("A car of type %s was found in lot %s!", carType, lot));
                searchLot.UpdateLotFile();
                return lotCar;
            }
            else {
                System.out.println(String.format("No car of the type %s was found in lot %s.", carType, lot));
            }
        }
        UpdateRentalShopFile();
        return null;
    }
}
