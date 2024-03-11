import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class SimulationManager implements Runnable{
    public static String dataToPrint;
    public int timeLimit;
    public int maxProcessingTime;
    public int minProcessingTime;
    public int maxArrivalTime;
    public int minArrivalTime;
    public int numberOfClientQueues;
    public int numberOfClients;
    public AtomicInteger totalTime;
    public int peakHour;
    public int peakAmount;
    public int currentAmount;
    public Scheduler scheduler;
    private final List<Client> generatedClients;

    public SimulationManager(String[] args){
        peakHour = 0;
        peakAmount = 0;
        totalTime = new AtomicInteger(0);
        this.timeLimit = Integer.parseInt(args[0]);
        this.minProcessingTime = Integer.parseInt(args[1]);
        this.maxProcessingTime = Integer.parseInt(args[2]);
        this.minArrivalTime = Integer.parseInt(args[3]);
        this.maxArrivalTime = Integer.parseInt(args[4]);
        this.numberOfClientQueues = Integer.parseInt(args[5]);
        this.numberOfClients = Integer.parseInt(args[6]);
        generatedClients = new ArrayList<>(numberOfClients);
        scheduler = new Scheduler(numberOfClientQueues);
        for(int i=0;i<numberOfClients;i++)
            generatedClients.add(new Client(minArrivalTime, maxArrivalTime, minProcessingTime, maxProcessingTime));
        generatedClients.sort(Comparator.comparingInt(Client::getArrTime));
    }
    public void printWaitingClients(FileWriter f){
        try {
            for (Client c : generatedClients) {
                dataToPrint += "(" + c.getID() + "," + c.getArrTime() + "," + c.getSrvTime() + ");";
                f.write("(" + c.getID() + "," + c.getArrTime() + "," + c.getSrvTime() + ");");
            }
            dataToPrint += "\n";
            f.write("\n");
        }
        catch (IOException e){Thread.currentThread().interrupt();}
    }
    @Override
    public void run(){
        try {
            FileWriter log = new FileWriter("log.txt");
            int currentTime = 0;
            while (currentTime < timeLimit && (generatedClients.size() > 0 || !scheduler.areQueuesEmpty())) {
                dataToPrint = "Time " + currentTime + "\nWaiting clients: ";
                log.write("Time " + currentTime + "\nWaiting clients: ");
                for (int i = 0; i < generatedClients.size(); i++) {
                    Client c = generatedClients.get(i);
                    if (c.getArrTime() == currentTime) {
                        totalTime.getAndAdd(scheduler.dispatchClient(c).intValue());
                        generatedClients.remove(c);
                        i--;
                    }
                }
                printWaitingClients(log);
                scheduler.printQueuesToFile(log);
                Thread t = new Thread(SimulationFrame::getData);
                t.start();
                try{
                    t.join();
                }catch (java.lang.InterruptedException e){Thread.currentThread().interrupt();}
                currentTime++;
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                //readyToCopy = false;
                currentAmount = scheduler.manageQueues();
                if(currentAmount > peakAmount){
                    peakAmount = currentAmount;
                    peakHour = currentTime - 1;
                }
            }
            log.close();
        }
        catch (IOException e){Thread.currentThread().interrupt();}
    }
    public static void main(String[] args){
        SimulationManager gen = new SimulationManager(args);

        Thread t = new Thread(gen);
        t.start();
        try {
            t.join();
        }
        catch (InterruptedException e){
            Thread.currentThread().interrupt();
        }
        float avgWaitingTime = (float)gen.totalTime.intValue()/gen.numberOfClients;
        try{
            Files.write(Paths.get("log.txt"), ("\nAvg. waiting time: " + avgWaitingTime + "\nPeak hour: TIME " + gen.peakHour + "\n").getBytes(), StandardOpenOption.APPEND);
        }catch (java.io.IOException e){Thread.currentThread().interrupt();}
    }
}
