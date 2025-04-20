import java.util.Collections;
import java.util.List;

public class CarRental extends BaseRunningProgram{
    private String city;
    private List<Car> carList;
    private List<String> lotList; 
    
    public CarRental() {
        //Add supporte flags to list
        Collections.addAll(supportedFlags,
        "location","spaces-available",
        "lots");

        //Assign expected types to flags
        expectedParameterTypes.put("location","String");
        expectedParameterTypes.put("spaces-available","Integer");
        expectedParameterTypes.put("lots","String");

    }

    public static void main(String[] args) {
        
    }

    public void RentCar(String carType) {

    }

    public void ReturnCar(String licensePlate, Integer kilometersTraversed) {

    }

    public void GetList() {

    }

    public void GetTransactions() {

    }

    public Car SearchCarForRent(String carType) {
        return null;
    }

    public void UpdateCarAllocationFile(Car updatedCar, String previousLocation, String newLocation, Boolean status, Integer DiscountRate) {

    }
}
