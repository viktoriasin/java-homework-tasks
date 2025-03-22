package ru.sinvic.homework.listener.homework;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import ru.sinvic.homework.listener.Listener;
import ru.sinvic.homework.model.Message;

public class HistoryListener implements Listener, HistoryReader {

    private final Map<Long, Message> messageStorage = new HashMap<>();

    @Override
    public void onUpdated(Message msg) {
        Message.Builder builder = msg.toBuilder();
        builder.field13(msg.getField13().copy());
        messageStorage.put(msg.getId(), builder.build());
    }

    @Override
    public Optional<Message> findMessageById(long id) {
        return Optional.ofNullable(messageStorage.get(id));
    }
}
