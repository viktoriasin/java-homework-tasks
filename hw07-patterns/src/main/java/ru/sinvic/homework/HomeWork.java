package ru.sinvic.homework;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sinvic.homework.handler.ComplexProcessor;
import ru.sinvic.homework.listener.homework.HistoryListener;
import ru.sinvic.homework.model.Message;
import ru.sinvic.homework.model.ObjectForMessage;
import ru.sinvic.homework.processor.homework.ProcessorSwapFields;
import ru.sinvic.homework.processor.homework.ProcessorThrowExceptionInEvenSecond;

@SuppressWarnings({"java:S125", "java:S3655"})
public class HomeWork {
    private static final Logger log = LoggerFactory.getLogger(HomeWork.class);

    /*
    Реализовать to do:
      1. Добавить поля field11 - field13 (для field13 используйте класс ObjectForMessage)
      2. Сделать процессор, который поменяет местами значения field11 и field12
      3. Сделать процессор, который будет выбрасывать исключение в четную секунду (сделайте тест с гарантированным результатом)
            Секунда должна определяться во время выполнения.
            Тест - важная часть задания
            Обязательно посмотрите пример к паттерну Мементо!
      4. Сделать Listener для ведения истории (подумайте, как сделать, чтобы сообщения не портились)
         Уже есть заготовка - класс HistoryListener, надо сделать его реализацию
         Для него уже есть тест, убедитесь, что тест проходит
    */

    public static void main(String[] args) {
        /*
          по аналогии с Demo.class
          из элеменов "to do" создать new ComplexProcessor и обработать сообщение
        */

        var processors =
                List.of(new ProcessorSwapFields(), new ProcessorThrowExceptionInEvenSecond(LocalDateTime::now));

        var complexProcessor = new ComplexProcessor(processors, ex -> {});
        var listenerHistory = new HistoryListener();
        complexProcessor.addListener(listenerHistory);

        var id = 100L;
        var data = "33";
        var field13 = new ObjectForMessage();
        var field13Data = new ArrayList<String>();
        field13Data.add(data);
        field13.setData(field13Data);

        var message = new Message.Builder(id)
                .field11("field11")
                .field12("field12")
                .field13(field13)
                .build();

        var result = complexProcessor.handle(message);
        log.info("result:{}", result);

        Message message1 = listenerHistory.findMessageById(id).get();

        log.atInfo().log(message1::toString);
    }
}
