package ru.sinvic.homework.processor;

import java.time.LocalDateTime;
import ru.sinvic.homework.model.Message;

public class ProcessorThrowExceptionInEvenSecond implements Processor {

    @Override
    public Message process(Message message) {
        while (true) {
            int second = LocalDateTime.now().getSecond();
            if (second % 2 == 0) {
                throw new RuntimeException("Exception at " + second);
            }
        }
    }
}
