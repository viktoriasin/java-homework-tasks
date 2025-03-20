package ru.sinvic.homework.handler;

import ru.sinvic.homework.listener.Listener;
import ru.sinvic.homework.model.Message;

public interface Handler {
    Message handle(Message msg);

    void addListener(Listener listener);

    void removeListener(Listener listener);
}
