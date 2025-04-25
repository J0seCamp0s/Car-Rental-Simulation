public class Car extends CarStaticData{
    private String licensePlate;
    private String carType;
    private Double distanceTravelled;
    private Integer discountRate;

    public Car(String plateNumber, Integer type, Double kmTravelled, Boolean discountApplied) {
        licensePlate = plateNumber;
        distanceTravelled = kmTravelled;
        switch(type) {
            case 0-> carType = SEDAN;
            case 1-> carType = SUV;
            case 2-> carType = VAN;
        }
        if(discountApplied) {
            discountRate = 10;
        }
        else {
            discountRate = 0;
        }
    }

    public String ToString() {
        String carString = licensePlate + "," + carType + "," + Double.toString(distanceTravelled) + "\n";
        return carString;
    }

    public void SetDistanceTravelled (Double newDistance) {
        distanceTravelled = newDistance;
    }

    public void SetDiscountRate(Integer discount) {
        discountRate = discount;
    }

    public String GetLicensePlate() {
        return licensePlate;
    }

    public String GetCarType()  {
        return carType;
    }

    public Integer GetDiscountRate() {
        return discountRate;
    }
}
