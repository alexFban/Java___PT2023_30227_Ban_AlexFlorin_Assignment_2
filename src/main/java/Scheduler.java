import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

public class Scheduler {
    private final List<ClientQueue> queues;
    private final int maxNrQueues;
    private final Strategy strategy;

    public Scheduler(int maxNrQueues){
        strategy = new Strategy();
        this.maxNrQueues = maxNrQueues;
        queues = new ArrayList<>(maxNrQueues);
        for(int i=0;i<maxNrQueues;i++){
            ClientQueue buff = new ClientQueue();
            Thread t = new Thread(buff);
            t.start();
            queues.add(buff);
        }
    }
    public AtomicInteger dispatchClient(Client c){return strategy.addClient(queues, c);}
    public void printQueuesToFile(FileWriter f){
        try {
            for (int i = 0; i < queues.size(); i++)
                if (queues.get(i).getClients().isEmpty()) {
                    SimulationManager.dataToPrint += "Queue " + (i + 1) + ": closed\n";
                    f.write("Queue " + (i + 1) + ": closed\n");
                }
                else{
                    Queue<Client> cBuff = queues.get(i).getClients();
                    SimulationManager.dataToPrint += "Queue " + (i+1) +": ";
                    f.write("Queue " + (i+1) + ": ");
                    for(Client c : cBuff){
                        SimulationManager.dataToPrint += "(" + c.getID() + "," + c.getArrTime() + "," + c.getSrvTime() + ");";
                        f.write("(" + c.getID() + "," + c.getArrTime() + "," + c.getSrvTime() + ");");
                    }
                    SimulationManager.dataToPrint += "\n";
                    f.write("\n");
                }
        }
        catch (IOException e){Thread.currentThread().interrupt();}
    }
    public int manageQueues(){
        int nrOfCurrentClients = 0;
        for(int i=0;i<queues.size();i++){
            ClientQueue q = queues.get(i);
            ClientQueue buff = new ClientQueue();
            for(Client c : q.getClients()){
                nrOfCurrentClients++;
                if(q.getClients().peek().equals(c))
                    c.serving();
                if(c.getSrvTime() > 0)
                    buff.addClient(c);
            }
            queues.remove(i);
            queues.add(i, buff);
        }
        return nrOfCurrentClients;
    }
    public boolean areQueuesEmpty(){
        for (ClientQueue queue : queues) if (!queue.getClients().isEmpty()) return false;
        return true;
    }
    public List<ClientQueue> getQueues(){return queues;}
}
