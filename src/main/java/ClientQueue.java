import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientQueue implements Runnable{
    private final Queue<Client> clients;
    private final AtomicInteger waitingPeriod;

    public ClientQueue(){
        clients = new ArrayBlockingQueue<>(10);
        waitingPeriod = new AtomicInteger(0);
    }
    public void addClient(Client newClient){
        clients.offer(newClient);
        waitingPeriod.set(waitingPeriod.get() + newClient.getSrvTime());
    }
    public AtomicInteger getWaitingPeriod(){return waitingPeriod;}
    public Queue<Client> getClients(){return clients;}
    public void run(){
        try {
            while(true) {
                if(!clients.isEmpty()) {
                    Client buff = clients.remove();
                    Thread.sleep(buff.getSrvTime());
                    waitingPeriod.set(waitingPeriod.get() - buff.getSrvTime());
                }
            }
        }
        catch (InterruptedException e){
            Thread.currentThread().interrupt();
        }
    }
}
