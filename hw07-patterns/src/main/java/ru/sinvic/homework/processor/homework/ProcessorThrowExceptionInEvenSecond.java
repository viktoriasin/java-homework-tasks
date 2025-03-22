package ru.sinvic.homework.processor.homework;

import ru.sinvic.homework.model.Message;
import ru.sinvic.homework.processor.Processor;

@SuppressWarnings("java:S112")
public class ProcessorThrowExceptionInEvenSecond implements Processor {
    private final DateTimeProvider dateTimeProvider;

    public ProcessorThrowExceptionInEvenSecond(DateTimeProvider dateTimeProvider) {
        this.dateTimeProvider = dateTimeProvider;
    }

    @Override
    public Message process(Message message) {
        if (dateTimeProvider.getDate().getSecond() % 2 == 0) {
            throw new RuntimeException("Exception because of even second");
        }
        return message;
    }
}
