import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LotManager extends BaseRunningProgram{

    private List<Car> carList = new ArrayList<>();
    private String lotName;
    

    public LotManager() {
        Collections.addAll(supportedFlags,
        "lot-name","add-sedan",
        "add-suv","add-van",
        "remove-vehicle");

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

        //Store flag parameters and expected types for flag parameters
        HashMap<String, String> flagParameters = manager.GetFlagParameters();
        HashMap<String, String> expectedTypes = manager.GetExpectedParameterTypes();

        //Check paramter types
        for (Map.Entry<String, String> entry : expectedTypes.entrySet()) {
            String flag = entry.getKey();
            String expectedType = entry.getValue();

            if(manager.CheckFlagParameterType(flag, expectedType)) {
                System.out.println(String.format("Incompatible type for %s flag", flag));
            }
        }

        String carLotData = manager.ReadFile(flagParameters.get("lot-name") + ".txt");
        if(!carLotData.isBlank()) {

        }

        //Add vehicles to lot file
        manager.AddVehicle(CarTypes.SEDAN, Integer.valueOf(flagParameters.getOrDefault("add-sedan", "0")));
        manager.AddVehicle(CarTypes.SUV, Integer.valueOf(flagParameters.getOrDefault("add-suv", "0")));
        manager.AddVehicle(CarTypes.VAN, Integer.valueOf(flagParameters.getOrDefault("add-van", "0")));
        
    }

    public Boolean AddVehicle(String vehicleType, Integer vehicleAmount) {
        String formattedVehicleType = vehicleType.toUpperCase();
        Integer vehicleTypeCode;
        switch(formattedVehicleType) {
            case "SEDAN" -> vehicleTypeCode = 0;
            case "SUV" -> vehicleTypeCode = 1;
            case "VAN" -> vehicleTypeCode = 2;
            default -> {
                return false;
            }
        }
        String licensePlate = "";

        while(licensePlate.isEmpty()) {
            licensePlate = GenerateLicensePlate();
        }

        carList.add(new Car(licensePlate, vehicleTypeCode, 0));
        return true;
    }

    public Boolean AddVehicle(String licensePlate, String vehicleType, Integer distanceTravelled) {
        String formattedVehicleType = vehicleType.toUpperCase();
        Integer vehicleTypeCode;
        switch(formattedVehicleType) {
            case "SEDAN" -> vehicleTypeCode = 0;
            case "SUV" -> vehicleTypeCode = 1;
            case "VAN" -> vehicleTypeCode = 2;
            default -> {
                return false;
            }
        }

        carList.add(new Car(licensePlate, vehicleTypeCode, distanceTravelled));
        return true;
    }

    private String GenerateLicensePlate() {
        //Alphabet for letters
        String[] uppercaseLetters = {
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
            "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"
        };

        //Alphabet for numbers
        String[] numberStrings = {
            "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"
        };

        Random indexRandom = new Random();
        String newLicensePlate = "";

        //Pick frist three characters of license plate
        for (int i = 0; i < 3; i++) {
            newLicensePlate += uppercaseLetters[indexRandom.nextInt(uppercaseLetters.length)];
        }

        newLicensePlate+= "-";

        //Pick last three characters of license plate
        for (int i = 4; i < 6; i++) {
            newLicensePlate += numberStrings[indexRandom.nextInt(uppercaseLetters.length)];
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

        //If licensePlate is part of licensePlatesInUse, return false (cannot be used)
        //Else return true (can be used or file does not exist)
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
                return true;
            }
        }
        //No matching license plate found in carList
        //Return false (car not found)
        return false;
    }

    public Boolean UpdateLotIndexFile() {
        //Try appending to file
        //Create if no file exists
        try {
            EditFile(lotName, "LotIndex.txt", false);
        //If file cannot be created
        } catch (IOException writeE) {
            //Return false (operation was not successful)
            writeE.printStackTrace();
            return false;
        }
        //If no IOException was thrown, return true (operation successful)
        return true;
    }

    public Boolean RetrieveCurrentCarsInLot(String lotData) {
        Integer startIndex = 0, midIndex = 0, endIndex = 0, kmTravelled;
        String licensePlate, type, carString;

        while(startIndex < lotData.length()) {
            endIndex = lotData.indexOf('\n');
            carString = lotData.substring(startIndex, endIndex);

            midIndex = carString.indexOf(',');
            licensePlate = carString.substring(startIndex, midIndex);

            startIndex = midIndex + 1;
            midIndex = carString.indexOf(',', startIndex);
            type = carString.substring(startIndex, midIndex);

            try {
                startIndex = midIndex + 1;
                kmTravelled = Integer.valueOf(carString.substring(startIndex, endIndex));
            } catch (NumberFormatException eNumb) {
                System.out.println("Non-numeric value given for distance travelled!");
                return false;
            }

            AddVehicle(licensePlate, type, kmTravelled);
        }

        return true;
    }

    public void UpdateLotFile() {
        //Implement
    }

    public void RetrieveCarStatus() {
        //Implement
    }

    public Boolean RemoveVehicle() {
        //Implement
        return true;
    }
}