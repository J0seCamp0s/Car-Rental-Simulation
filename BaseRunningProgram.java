import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
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
    protected Boolean AddVehicle(String licensePlate, String vehicleType, Double distanceTravelled, List<Car> cList, Boolean discountApplied) {

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

        cList.add(new Car(licensePlate, vehicleTypeCode, distanceTravelled, false));
        return true;
    }

    protected Tuple3<String, String, Double> ParseCarString(String carString) {
        Double kmTravelled;
        String licensePlate, type;

        String[] carAttributes = carString.split(",");
        licensePlate = carAttributes[0];
        type = carAttributes[1];

        try {
            kmTravelled = Double.valueOf(carAttributes[2]);
        } catch (NumberFormatException eNumb) {
            System.out.println("Non-numeric value given for distance travelled!");
            return new Tuple3<>(null, null, null);
        }
        return new Tuple3<>(licensePlate, type, kmTravelled);
    }

    protected Boolean RetrieveLocationCars(String locationCars) {

        String[] carStrings = locationCars.split("\n");
        for(String carString: carStrings) {
            if(carString.contains("#")) {
                continue;
            }
            
            Tuple3<String, String, Double> carTuple = ParseCarString(carString);
            if(carTuple.GetItem1() == null) {
                return false;
            }
            AddVehicle(carTuple.GetItem1(), carTuple.GetItem2(), carTuple.GetItem3(), carList, false);
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

    /*
     * Chat-GPT prompt to add locking into the method:
     * Please edit this method so that it can support file locking for exclusive access
     * during reading: <Previous version of method>
     */
    protected String ReadFile(String filePath) {
        String completeFileString = "";
        File file = new File("TextFiles\\" + filePath);
    
        try (
            RandomAccessFile raf = new RandomAccessFile(file, "r");             // 
            FileChannel channel = raf.getChannel();                                  //
            FileLock lock = channel.lock(0L, Long.MAX_VALUE, true);  // 
            Scanner fileReader = new Scanner(file)
        ) {
            while (fileReader.hasNextLine()) {
                completeFileString += fileReader.nextLine() + "\n";
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error, file not found!");
        } catch (IOException e) {                                                     //
            System.out.println("Error while locking/reading file!");                //
        }
    
        return completeFileString;
    }
    
    /*
     * Chat-GPT prompt to add locking into the method:
     * Please edit this method so that it can support file locking for exclusive access
     * during writting: <Previous version of method>
     */
    protected void EditFile(String newFileContent, String filePath, Boolean mode) throws IOException {
        File file = new File("TextFiles\\" + filePath);
    
        try (
            RandomAccessFile raf = new RandomAccessFile(file, "rw");   //
            FileChannel channel = raf.getChannel();                         //
            FileLock lock = channel.lock()                                  // 
        ) {
            if (mode) {                                                     //
                raf.seek(raf.length());                                     // 
            } else {
                raf.setLength(0);                                 // 
                raf.seek(0);                                            //
            }
            raf.writeBytes(newFileContent);                                 //
        } catch (IOException e) {
            System.out.println("Couldn't write to file!");
            throw e;
        }
    }
    
}