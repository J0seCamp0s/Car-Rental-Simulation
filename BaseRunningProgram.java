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


    protected Boolean CheckFlagParameterType(String flag, String desiredType) {
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