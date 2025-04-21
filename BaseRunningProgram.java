import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public abstract class BaseRunningProgram {

    protected List<String> supportedFlags = new ArrayList<>(){};
    protected HashMap<String, String> flagParameters = new HashMap<>();
    protected HashMap<String, String> expectedParameterTypes = new HashMap<>();
    protected List<Car> carList = new ArrayList<>();

    protected HashMap<String, String> GetFlagParameters() {
        return flagParameters;
    }

    protected HashMap<String, String> GetExpectedParameterTypes() {
        return expectedParameterTypes;
    }
    
    protected Boolean ParseFlag(String inputFlag) {

        if(!"--".equals(inputFlag.substring(0, 2))) {
           System.out.println("Flag format error! All flags must start with '--'.");
           return false;
        }
        Integer flagFinish = inputFlag.indexOf('=');
        String flagName = inputFlag.substring(2,flagFinish);

        if(!supportedFlags.contains(flagName)) {
            System.out.println(String.format("Unsupported flag! Your flag: %s is not supported", flagName));
            return false;
        }   
        else if (flagFinish == -1) {
            System.out.println("Flag format error! All flags must contain a '=' to denote inputs.");
            return false;
        }
             
        String flagParameter = inputFlag.substring(flagFinish+1);
        flagParameters.put(flagName, flagParameter);
        return true;
    }

    protected Boolean CheckFlagParameterTypes() {
        //Check paramter types
        for (Map.Entry<String, String> entry : expectedParameterTypes.entrySet()) {
            String flag = entry.getKey();
            String expectedType = entry.getValue();

            //If not expected type
            if(!CheckParameterType(flag, expectedType)) {
                System.out.println(String.format("Incompatible type for %s flag!", flag));
                return false;
            }
        }
        //If no unmatiching type was found
        return true;
    }

    protected Boolean CheckParameterType(String flag, String desiredType) {
        switch(desiredType) {
            case "Integer" -> {
                try {
                    Integer testInteger = Integer.valueOf(flagParameters.getOrDefault(flag, "0"));
                } catch (NumberFormatException e) {
                    return false;
                }
                return true;
            }
            default -> {
                Object flagParameter = flagParameters.getOrDefault(flag, "");
                return(flagParameter.getClass().getSimpleName().equals(desiredType));
            }
        }
    }

    protected abstract void RetrieveLocationData();

    //Used when car already existed
    protected Boolean AddVehicle(String licensePlate, String vehicleType, Integer distanceTravelled) {

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

    protected Boolean RetrieveLocationCars(String locationCars) {
        Integer startIndex = 0, midIndex, endIndex, kmTravelled;
        String licensePlate, type;

        while(startIndex < locationCars.length()) {
            endIndex = locationCars.indexOf('\n', startIndex);

            midIndex = locationCars.indexOf(',', startIndex);
            licensePlate = locationCars.substring(startIndex, midIndex);

            startIndex = midIndex + 1;
            midIndex = locationCars.indexOf(',', startIndex);
            type = locationCars.substring(startIndex, midIndex);

            try {
                startIndex = midIndex + 1;
                kmTravelled = Integer.valueOf(locationCars.substring(startIndex, endIndex));
            } catch (NumberFormatException eNumb) {
                System.out.println("Non-numeric value given for distance travelled!");
                return false;
            }

            AddVehicle(licensePlate, type, kmTravelled);
            startIndex = endIndex + 1;
        }

        return true;
    }

    protected Boolean CheckLincensePlateFormat(String licensePlate) {
        //If license plate is longer than 7 characters "ABC-123"
        if(licensePlate.length() < 7) {
            return false;
        } 

        //If first three characters are not letters
        for (int i = 0; i < 3; i++) {
            if(!CarStaticData.uppercaseLetters.contains(licensePlate.substring(i,i+1))) {
                return false;
            }
        }

        //If character at index 3 is not "-"
        if(!licensePlate.substring(3,4).equals("-")) {
            return false;
        }

        //If last three characters are not numbers
        for (int i = 4; i < 7; i++) {
            if(!CarStaticData.numberStrings.contains(licensePlate.substring(i,i+1))) {
                return false;
            }
        }

        //If not format errors found
        return true;
    }

    protected String ReadFile(String filePath) {
        String completeFileString = "";
        try {
            File inputFile = new File("TextFiles\\" + filePath);
            Scanner fileReader = new Scanner(inputFile);

            while(fileReader.hasNextLine())
            {
                completeFileString += (fileReader.nextLine()) + "\n";
            }
            fileReader.close();
        } catch(FileNotFoundException e){
            System.out.println("Error, file not found!");
        }
        return completeFileString;   
    }

    protected void EditFile(String newFileContent, String filePath, Boolean mode) throws IOException{
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("TextFiles\\" + filePath, mode));
            writer.write(newFileContent);
            writer.close();
        } catch (IOException e) {
            System.out.println("Couldn't write to file!");
            throw e;
        }
    }
}