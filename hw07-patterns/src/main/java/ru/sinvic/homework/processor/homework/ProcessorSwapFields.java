package ru.sinvic.homework.processor.homework;

import ru.sinvic.homework.model.Message;
import ru.sinvic.homework.processor.Processor;

public class ProcessorSwapFields implements Processor {
    @Override
    public Message process(Message message) {
        return message.toBuilder()
                .field11(message.getField12())
                .field12(message.getField11())
                .build();
    }
}
