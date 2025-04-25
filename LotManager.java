import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Random;

public class LotManager extends BaseRunningProgram{
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

        //Set lot's name once flags are parsed
        manager.SetLotName();

        //Store flag parameters for easier access in main
        HashMap<String, String> flagParameters = manager.GetFlagParameters();

        //Check flag parameter types
        manager.CheckFlagParameterTypes();

        //Retrieve all cars already in lot 
        manager.RetrieveLocationData();

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

    @Override
    protected void RetrieveLocationData() {
        String carLotData = ReadFile(lotName + ".txt");
        if(!carLotData.isBlank()) {
            carList.clear();
            RetrieveLocationCars(carLotData);
        }
        else {
            System.out.println(String.format("The lot file for current lot (%s) does not exist yet.", lotName));
        }
    }

    //Used in LotManager's main
    public void SetLotName() {
        lotName = flagParameters.get("lot-name");
    }

    //Used when creating a LotManager entity in CarRental methods
    public void SetLotName(String newLotName) {
        lotName = newLotName;
    }

    public Integer GetCarListSize() {
        return carList.size();
    }

    //Used when looking for a specific car type
    public Car SearchCar(String type) {
        for(Car currentCar: carList) {
            //If car type is the same as required type
            //Remove it from lot's cars
            //Return it
            if (currentCar.GetCarType().equals(type)) {
                RemoveVehicle(currentCar.GetLicensePlate());
                return currentCar;
            }
        }
        //Return null if no car found
        return null;
    }

    //Used when only needs a car, regardless of type
    public Car SearchCar() {
        try {
            //Retreive first car from carList
            //Return it
            Car currentCar = carList.getFirst();
            RemoveVehicle(currentCar.GetLicensePlate());
            return currentCar;
        } catch (NoSuchElementException e) {
            //Return null if list is empty
            return null;
        }
       
    }

    //Used when vehicle is being created
    private Boolean AddVehicle(String vehicleType, Integer vehicleAmount) {
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

            carList.add(new Car(licensePlate, vehicleTypeCode, 0.0, false));
        }
        
        return true;
    }

    private String GenerateLicensePlate() {

        Random indexRandom = new Random();
        String newLicensePlate = "";

        //Pick frist three characters of license plate
        for (int i = 0; i < 3; i++) {
            newLicensePlate += CarStaticData.uppercaseLetters.get(indexRandom.nextInt(CarStaticData.uppercaseLetters.size()));
        }

        newLicensePlate+= "-";

        //Pick last three characters of license plate
        for (int i = 4; i < 7; i++) {
            newLicensePlate += CarStaticData.numberStrings.get(indexRandom.nextInt(CarStaticData.numberStrings.size()));
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
            System.out.println("The file UsedLicensePlates.txt does not exist yet!");
            return true;
        }

        //If licensePlate is part of licensePlatesInUse, return false (cannot be used)
        //Else return true (can be used)
        return !licensePlatesInUse.contains(licensePlate); 
    }

    private Boolean RemoveVehicle(String licensePlate) {
        //Iterate over all cars in lot
        for(Car currentCar: carList) {
            //If licensePlate == currentCar's licensePlate
            if(currentCar.GetLicensePlate().equals(licensePlate)) {
                //Matching license plate found in currentCar
                //Return true (car found and removed)
                carList.remove(currentCar);
                System.out.println(String.format("Car with license plate %s found and removed from current lot (%s)", licensePlate, lotName));
                return true;
            }
        }
        //No matching license plate found in carList
        //Return false (car not found)
        System.out.println(String.format("Car with license plate %s not found in current lot (%s).", licensePlate, lotName));
        return false;
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

    private void UpdateUsedLicensePlatesFile() {
        String licensePlatesInUse = ReadFile("UsedLicensePlates.txt");

        for(Car currentCar: carList) {
            if(!licensePlatesInUse.contains(currentCar.GetLicensePlate())) {
                licensePlatesInUse += currentCar.GetLicensePlate() + "\n";
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
            lotIndex += lotName;
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