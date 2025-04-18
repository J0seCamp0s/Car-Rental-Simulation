public class Car extends CarTypes{
    private Boolean status;
    private String licensePlate;
    private String carType;
    private Integer distanceTravelled;

    public Car(String plateNumber, Integer type, Integer kmTravelled) {
        status = false;
        licensePlate = plateNumber;
        distanceTravelled = kmTravelled;
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

    public void setDistanceTravelled (Integer newDistance) {
        distanceTravelled = newDistance;
    }

    public String getLicensePlate() {
        return licensePlate;
    }
}
