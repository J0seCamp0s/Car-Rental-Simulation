import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class CarRental extends BaseRunningProgram{
    private String location;
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
            command = inputReciever.nextLine();
            command = command.toUpperCase();
            Integer endOfCommandType = command.indexOf(" ");

            commandType = command.substring(0, endOfCommandType);

            if(!supportedCommands.contains(commandType)) {
                System.out.println(String.format("Error! %s is not a supported command", commandType));
                continue;
            }
            switch(commandType) {
                case "RENT" -> {
                    if((endOfCommandType + 1) == command.length())
                    {
                        System.out.println("No parameters where given for RENT command!");
                        System.out.println("Unable to perform operation!");
                        continue;
                    }
                    commandParameters = command.substring(endOfCommandType + 1);
                    RentCar(commandParameters);
                }
                case "RETURN" -> {
                    if((endOfCommandType + 1) == command.length())
                    {
                        System.out.println("No parameters where given for RENT command!");
                        System.out.println("Unable to perform operation!");
                        continue;
                    }
                    commandParameters = command.substring(endOfCommandType + 1);
                    String licensePlate, kmString;
                    Integer endOfLicensePlate = commandParameters.indexOf(" ");
                    licensePlate = commandParameters.substring(0, endOfLicensePlate);
                    kmString = commandParameters.substring(endOfLicensePlate + 1);
                    Integer kmTravelled;
                    try {
                        kmTravelled = Integer.valueOf(kmString);
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

    private void RentCar(String carType) {

    }

    private void ReturnCar(String licensePlate, Integer kilometersTraversed) {

    }

    private void GetList() {

    }

    private void GetTransactions() {

    }

    private Car SearchCarForRent(String carType) {
        return null;
    }

    private void UpdateCarAllocationFile(Car updatedCar, String previousLocation, String newLocation, Boolean status, Integer DiscountRate) {

    }
}
