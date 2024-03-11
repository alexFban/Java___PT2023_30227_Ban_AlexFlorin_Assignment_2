import java.util.Random;

public class Client {
    static int globalId = 1;
    private final int ID;
    private final int arrivalTime;
    private int serviceTime;

    public Client(int minArrival, int maxArrival, int minService, int maxService){
        Random rand = new Random();
        ID = globalId;
        globalId++;
        arrivalTime = rand.nextInt(maxArrival - minArrival + 1) + minArrival;
        serviceTime = rand.nextInt(maxService - minService + 1) + minService;
    }

    public int getArrTime(){return arrivalTime;}
    public int getSrvTime(){return serviceTime;}
    public void serving(){serviceTime--;}
    public int getID(){return ID;}
}
