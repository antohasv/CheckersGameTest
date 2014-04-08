package messageSystem;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import base.Abonent;
import base.Address;
import base.AddressService;
import base.MessageSystem;
import base.Msg;


public class MessageSystemImpl implements MessageSystem {

    private Map<Address, ConcurrentLinkedQueue<Msg>> messages =
            new HashMap<Address, ConcurrentLinkedQueue<Msg>>();
    private AddressService addressService = new AddressServiceImpl();

    public MessageSystemImpl() {
    }

    public void addService(Abonent abonent, String name) {
        messages.put(abonent.getAddress(), new ConcurrentLinkedQueue<Msg>());
        addressService.addService(abonent, name);
    }

    public Address getAddressByName(String name) {
        return addressService.getAddressByName(name);
    }

    public void putMsg(Address to, Msg msg) {
        messages.get(to).add(msg);
    }

    public Map<Address, ConcurrentLinkedQueue<Msg>> getMessages() {
        return messages;
    }

    public void execForAbonent(Abonent abonent) {
        ConcurrentLinkedQueue<Msg> messageQueue = messages.get(abonent.getAddress());
        while (!messageQueue.isEmpty()) {
            Msg message = messageQueue.poll();
            message.exec(abonent);
        }
    }
}