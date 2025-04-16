import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public abstract class FileManager {
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

    protected void EditFile(String newFileContent, String filePath) throws IOException{
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
            writer.write(newFileContent);
            writer.close();
        } catch (IOException e) {
            System.out.println("Couldn't write to file");
            throw e;
        }
    }
}