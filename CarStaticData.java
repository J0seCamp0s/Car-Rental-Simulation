import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CarStaticData {
    //Supported car types
    public static final String SEDAN = "SEDAN";
    public static final String SUV = "SUV";
    public static final String VAN = "VAN";
    //Alphabet for letters
    public static final List<String> uppercaseLetters = new ArrayList<>(Arrays.asList(
        "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
        "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"
    ));
    //Alphabet for numbers
    public static final List<String> numberStrings = new ArrayList<>(Arrays.asList(
        "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"
    ));
}
