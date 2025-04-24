import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class CarRental extends BaseRunningProgram{
    private String location;
    private String allocatedSpaces;
    private Integer earnings = 0;
    private Integer loses = 0;
    
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

        //Check flag parameter types
        rentalShop.CheckFlagParameterTypes();

        //Retrieve car rental shop data
        rentalShop.RetrieveLocationData();

        rentalShop.RunCommands();

    }

    private void SetLocation() {
        location = flagParameters.get("location");
    }

    private void SetAllocatedSpaces() {
        location = flagParameters.get("spaces-available");
    }

    @Override
    protected void RetrieveLocationData() {
        String rentalShopData = ReadFile(flagParameters.get("location") + ".txt");
        if(!rentalShopData.isBlank()) {
            String earningsData, carsData;
            Integer startIndex = 0, midIndex, endIndex;

            midIndex = rentalShopData.indexOf("\n");
            endIndex = rentalShopData.indexOf("\n", midIndex);
            earningsData = rentalShopData.substring(startIndex, endIndex + 1);

            startIndex = endIndex + 1;
            carsData = rentalShopData.substring(startIndex);
            RetrieveEarnings(earningsData);
            RetrieveLocationCars(carsData);
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
        losesAmount = earningsAmount.substring(startIndex, endIndex);

        earnings = Integer.valueOf(earningsAmount);
        loses = Integer.valueOf(losesAmount);
    }

    private void RunCommands() {
        String command = "", commandType, commandParameters;
        Scanner inputReciever = new Scanner(System.in);

        while(!command.equals("EXIT")) {
            System.out.println("Please enter a command:");

            //Receive command from command line/GUI
            command = inputReciever.nextLine();
            command = command.toUpperCase();

            //Parse extract command type from command
            Integer endOfCommandType = command.indexOf(" ");
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
                    if((endOfCommandType + 1) == command.length())
                    {
                        System.out.println("No parameters where given for RENT command!");
                        System.out.println("Unable to perform operation!");
                        continue;
                    }
                    //Extract parameters from command
                    commandParameters = command.substring(endOfCommandType + 1);
                    //Retrieve a car from store or lot
                    Tuple2<Car, Integer> selectedCar = RentCar(commandParameters);
                    if(selectedCar != null) {

                    }
                }
                case "RETURN" -> {
                    //Check if paramters have been passed alongside command
                    if((endOfCommandType + 1) == command.length())
                    {
                        System.out.println("No parameters where given for RENT command!");
                        System.out.println("Unable to perform operation!");
                        continue;
                    }
                    
                    //Extract command paramters from user inputs
                    commandParameters = command.substring(endOfCommandType + 1);
                    String licensePlate, kmString;
                    Integer endOfLicensePlate = commandParameters.indexOf(" ");

                    //Extract license plate and km travelled
                    licensePlate = commandParameters.substring(0, endOfLicensePlate);
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
                    GetList();
                }
                case "TRANSACTION" -> {
                    GetTransactions();
                }
            }
        }
    }

    private Tuple2<Car, Integer> RentCar(String carType) {
        if(!CarStaticData.SEDAN.equals(carType) || !CarStaticData.VAN.equals(carType) || !CarStaticData.SUV.equals(carType)) {
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
                UpdateCarRentalFile();
                return new Tuple2<>(currentCar, 0);
            }
        }

        //Search for car in lots allocated to rental shop
        //Return it if found
        Car searchCar = SearchCarForRent(carType);
        if(searchCar != null) {
            return new Tuple2<>(searchCar, 10);
        }

        //Return null if no car was found in lots
        System.out.println("No car was found for rental in shop or allocated lots.");
        return null;
    }
    
    private void ReturnCar(String licensePlate, Double kilometersTraversed) {
        String rentedCarString = ReadFile("RentedCars.txt");
        if(!rentedCarString.contains(licensePlate)) {
            System.out.println("Car is not part of our rented cars log!");
            System.out.println("Car return denied.");
        }

        Integer discountRate;

        //Remove car from rented car list and apply discount rate if needed
        List<Car> rentedCars = RetrieveRentedCars();
        for (int i = 0; i < rentedCars.size(); i++) {
            if(rentedCars.get(i).GetLicensePlate().equals(licensePlate)) {
                discountRate = rentedCars.get(i).GetDiscountRate();
                rentedCars.remove(i);
            }
        }


        
    }

    private void GetList() {
        System.out.println(String.format("===== Current State of the %s shop =====", location));

        System.out.println(String.format("+Location Earnings: %s", earnings));
        System.out.println(String.format("+Location Loses: %s", loses));

        System.out.println("+Car spaces information");
        String carString;
        for(int i = 0; i < carList.size(); i++) {
            if(carList.get(i) == null) {
                carString = "Empty Space";
            }
            else {
                carString = carList.get(i).ToString();
            }
            System.out.println(String.format("Car space #%d: %s", i, carString));
        }

    }

    private void GetTransactions() {
        System.out.println();
    }

    private void CalculateLoses() {

    }

    private Car SearchCarForRent(String carType) {
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
            lotCar = searchLot.SearchCarType(carType);
            if(lotCar != null) {
                System.out.println(String.format("A car of type %s was found in lot %s!", carType, lot));
                searchLot.UpdateLotFile();
                return lotCar;
            }
        }
        return null;
    }

    private void UpdateCarRentalFile() {

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

    private void UpdateRentedCarsFile(Car rentedCar, Integer DiscountRate) {
        
    }
}
