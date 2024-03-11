import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Strategy {
    public AtomicInteger addClient(List<ClientQueue> queues, Client client){
        int minTime = queues.get(0).getWaitingPeriod().intValue();
        int minPoz = 0;
        for(int i=1;i<queues.size();i++){
            if(queues.get(i).getWaitingPeriod().intValue() < minTime){
                minTime = queues.get(i).getWaitingPeriod().intValue();
                minPoz = i;
            }
        }
        ClientQueue buff = queues.get(minPoz);
        buff.addClient(client);
        queues.remove(minPoz);
        queues.add(minPoz, buff);
        return queues.get(minPoz).getWaitingPeriod();
    }
}
