import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public abstract class BaseRunningProgram {

    protected List<String> supportedFlags = new ArrayList<>(){};
    protected HashMap<String, String> flagParameters = new HashMap<>();
    protected HashMap<String, String> expectedParameterTypes = new HashMap<>();

    protected HashMap<String, String> getFlagParameters() {
        return flagParameters;
    }

    protected HashMap<String, String> getExpectedParameterTypes() {
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


    //FIX: Check using type casts instead of using the class name
    //Everything is stored as a string in the dictionary
    protected Boolean CheckFlagParameterType(String flag, String desiredType) {
        Object flagParameter = flagParameters.getOrDefault(flag, "1");
        return(flagParameter.getClass().getName().equals(desiredType));
    }

    protected String ReadFile(String filePath) throws FileNotFoundException {
        String completeFileString = "";
        try {
            File inputFile = new File(filePath);
            Scanner fileReader = new Scanner(inputFile);

            while(fileReader.hasNextLine())
            {
                completeFileString += (fileReader.nextLine());
            }
            fileReader.close();
        } catch(FileNotFoundException e){
            System.out.println("Error, file not found");
            throw e;
        }
        return completeFileString;   
    }

    protected void EditFile(String newFileContent, String filePath, Boolean mode) throws IOException{
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, mode));
            writer.write(newFileContent);
            writer.close();
        } catch (IOException e) {
            System.out.println("Couldn't write to file");
            throw e;
        }
    }
}