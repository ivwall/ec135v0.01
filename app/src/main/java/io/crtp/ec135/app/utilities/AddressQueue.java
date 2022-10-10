package io.crtp.ec135.app.utilities;

import java.util.Queue;
import java.util.LinkedList;;

public class AddressQueue {

    private Queue<String> addrq = new LinkedList<>();

    public AddressQueue() {}

    public void add(String addr){
        addrq.add(addr);
    }

    public String remove() {
        return addrq.remove();
    }
    
}
