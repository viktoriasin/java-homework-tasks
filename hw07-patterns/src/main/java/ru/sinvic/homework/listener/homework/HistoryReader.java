package ru.sinvic.homework.listener.homework;

import java.util.Optional;
import ru.sinvic.homework.model.Message;

public interface HistoryReader {

    Optional<Message> findMessageById(long id);
}
