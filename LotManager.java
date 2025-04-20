import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class LotManager extends BaseRunningProgram{

    private List<Car> carList = new ArrayList<>();
    private String lotName;
    

    public LotManager() {
        //Add supporte flags to list
        Collections.addAll(supportedFlags,
        "lot-name","add-sedan",
        "add-suv","add-van",
        "remove-vehicle");

        //Assign expected types to flags
        expectedParameterTypes.put("lot-name","String");
        expectedParameterTypes.put("add-sedan","Integer");
        expectedParameterTypes.put("add-suv","Integer");
        expectedParameterTypes.put("add-van","Integer");
        expectedParameterTypes.put("remove-vehicle","String");
    }

    public static void main(String[] args) {
        LotManager manager = new LotManager();

        //Parse flags passed into the program
        for(String flag: args) {
            if(!manager.ParseFlag(flag)) {
                return;
            }
        }

        manager.SetLotName();

        //Store flag parameters and expected types for flag parameters
        HashMap<String, String> flagParameters = manager.GetFlagParameters();
        HashMap<String, String> expectedTypes = manager.GetExpectedParameterTypes();

        //Check paramter types
        for (Map.Entry<String, String> entry : expectedTypes.entrySet()) {
            String flag = entry.getKey();
            String expectedType = entry.getValue();

            if(!manager.CheckFlagParameterType(flag, expectedType)) {
                System.out.println(String.format("Incompatible type for %s flag!", flag));
                return;
            }
        }

        //Retrieve all cars already in lot 
        String carLotData = manager.ReadFile(flagParameters.get("lot-name") + ".txt");
        if(!carLotData.isBlank()) {
            manager.RetrieveCurrentCarsInLot(carLotData);
        }
        else {
            System.out.println(String.format("The lot file for current lot (%s) does not exist yet.", flagParameters.get("lot-name")));
        }

        //Add vehicles to lot file
        manager.AddVehicle(CarStaticData.SEDAN, Integer.valueOf(flagParameters.getOrDefault("add-sedan", "0")));
        manager.AddVehicle(CarStaticData.SUV, Integer.valueOf(flagParameters.getOrDefault("add-suv", "0")));
        manager.AddVehicle(CarStaticData.VAN, Integer.valueOf(flagParameters.getOrDefault("add-van", "0")));
        
        //Remove vehicle if license plate is given
        if(!flagParameters.getOrDefault("remove-vehicle", "").isBlank()) {
            manager.RemoveVehicle(flagParameters.get("remove-vehicle"));
        }

        //Update files
        manager.UpdateLotFile();
        manager.UpdateUsedLicensePlatesFile();
        manager.UpdateLotIndexFile();
    }

    public void SetLotName() {
        lotName = flagParameters.get("lot-name");
    }

    //Used when vehicle is being created
    public Boolean AddVehicle(String vehicleType, Integer vehicleAmount) {
        String formattedVehicleType = vehicleType.toUpperCase();
        Integer vehicleTypeCode;
        switch(formattedVehicleType) {
            case "SEDAN" -> vehicleTypeCode = 0;
            case "SUV" -> vehicleTypeCode = 1;
            case "VAN" -> vehicleTypeCode = 2;
            default -> {
                System.out.println(String.format("Unspported car type %s", formattedVehicleType));
                return false;
            }
        }
        //Add specified amount of cars to list
        for (int i = 0; i < vehicleAmount; i++) {
            String licensePlate = "";

            while(licensePlate.isEmpty()) {
                licensePlate = GenerateLicensePlate();
            }

            carList.add(new Car(licensePlate, vehicleTypeCode, 0));
        }
        
        return true;
    }

    //Used when car already existed
    public Boolean AddVehicle(String licensePlate, String vehicleType, Integer distanceTravelled) {

        //Pass in vehicle code based on car type
        String formattedVehicleType = vehicleType.toUpperCase();
        Integer vehicleTypeCode;
        switch(formattedVehicleType) {
            case "SEDAN" -> vehicleTypeCode = 0;
            case "SUV" -> vehicleTypeCode = 1;
            case "VAN" -> vehicleTypeCode = 2;
            //Not supported type was passed in, return false (operation failed)
            default -> {
                System.out.println(String.format("Unspported car type %s", formattedVehicleType));
                return false;
            }
        }

        carList.add(new Car(licensePlate, vehicleTypeCode, distanceTravelled));
        return true;
    }

    private String GenerateLicensePlate() {

        Random indexRandom = new Random();
        String newLicensePlate = "";

        //Pick frist three characters of license plate
        for (int i = 0; i < 3; i++) {
            newLicensePlate += CarStaticData.uppercaseLetters[indexRandom.nextInt(CarStaticData.uppercaseLetters.length)];
        }

        newLicensePlate+= "-";

        //Pick last three characters of license plate
        for (int i = 4; i < 7; i++) {
            newLicensePlate += CarStaticData.numberStrings[indexRandom.nextInt(CarStaticData.numberStrings.length)];
        }

        //Check if the license plate generated is valid
        if(CheckLincensePlateUsage(newLicensePlate)) {
            //Return license plate if valid
            return newLicensePlate;
        }
        else {
            //Return empty string if invalid
            return "";
        }
    }

    private Boolean CheckLincensePlateUsage(String licensePlate) {

        //Read file storing all currently used license plates
        String licensePlatesInUse = ReadFile("UsedLicensePlates.txt");

        //If file does not exist
        if(licensePlatesInUse.isBlank()) {
            System.out.println("The file LicensePlatesInUse.txt does not exist yet!");
            return true;
        }

        //If licensePlate is part of licensePlatesInUse, return false (cannot be used)
        //Else return true (can be used)
        return !licensePlatesInUse.contains(licensePlate); 
    }

    public Boolean RemoveVehicle(String licensePlate) {
        //Iterate over all cars in lot
        for(Car currentCar: carList) {
            //If licensePlate == currentCar's licensePlate
            if(currentCar.getLicensePlate().equals(licensePlate)) {
                //Matching license plate found in currentCar
                //Return true (car found and removed)
                carList.remove(currentCar);
                System.out.println(String.format("Car with license plate %s found and removed from current lot", licensePlate));
                return true;
            }
        }
        //No matching license plate found in carList
        //Return false (car not found)
        System.out.println(String.format("Car with license plate %s not found in current lot", licensePlate));
        return false;
    }

    public Boolean RetrieveCurrentCarsInLot(String lotData) {
        Integer startIndex = 0, midIndex, endIndex, kmTravelled;
        String licensePlate, type;

        while(startIndex < lotData.length()) {
            endIndex = lotData.indexOf('\n', startIndex);

            midIndex = lotData.indexOf(',', startIndex);
            licensePlate = lotData.substring(startIndex, midIndex);

            startIndex = midIndex + 1;
            midIndex = lotData.indexOf(',', startIndex);
            type = lotData.substring(startIndex, midIndex);

            try {
                startIndex = midIndex + 1;
                kmTravelled = Integer.valueOf(lotData.substring(startIndex, endIndex));
            } catch (NumberFormatException eNumb) {
                System.out.println("Non-numeric value given for distance travelled!");
                return false;
            }

            AddVehicle(licensePlate, type, kmTravelled);
            startIndex = endIndex + 1;
        }

        return true;
    }

    public void UpdateLotFile() {
        //Create new string to write into file
        String updatedLotData = "";

        //Convert each car in carList into string form
        //append to updatedLotData
        for(Car currentCar: carList) {
            updatedLotData += currentCar.ToString();
        }

        //Try writting to lot's file
        String fileName = lotName + ".txt";
        try {
            EditFile(updatedLotData, fileName, false);
        }
        //Print error message and return in case file couldn't be opened
        catch(IOException e) {
            System.out.println("Couldn't update lot file!");
            return;
        }

        System.out.println("Lot file updated successfully.");
    }

    public void UpdateUsedLicensePlatesFile() {
        String licensePlatesInUse = ReadFile("UsedLicensePlates.txt");

        for(Car currentCar: carList) {
            if(!licensePlatesInUse.contains(currentCar.getLicensePlate())) {
                licensePlatesInUse += currentCar.getLicensePlate() + "\n";
            }
        }
        try {
            EditFile(licensePlatesInUse, "UsedLicensePlates.txt", false);
        } catch (IOException e) {
            System.out.println("Error when trying to edit file UsedLicensePlates.txt");
        }
    }

    public void UpdateLotIndexFile() {

        String lotIndex = ReadFile("LotIndex.txt");
        if(!lotIndex.contains(lotName)) {
            lotIndex += lotName + "\n";
        }

        try {
            EditFile(lotIndex, "LotIndex.txt", false);
        //If file cannot be created
        } catch (IOException writeE) {
            //Return false (operation was not successful)
            System.out.println("Error when trying to edit file LotIndex.txt");
        }
    }
}