public class Car extends CarTypes{
    private Boolean status;
    private String licensePlate;
    private String carType;
    private float distanceTravelled;

    public Car(String plateNumber, Integer type) {
        status = false;
        licensePlate = plateNumber;
        distanceTravelled = 0;
        switch(type) {
            case 0:
                carType = SEDAN;
            case 1:
                carType = SUV;
            case 2: 
                carType = VAN;
        }
    }

    public void setStatus(Boolean newStatus) {
        status = newStatus;
    }

    public void setDistanceTravelled (float newDistance) {
        distanceTravelled = newDistance;
    }

    public String getLicensePlate() {
        return licensePlate;
    }
}
