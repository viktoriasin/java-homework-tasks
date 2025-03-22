package ru.sinvic.homework.processor;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.sinvic.homework.model.Message;
import ru.sinvic.homework.processor.homework.DateTimeProvider;
import ru.sinvic.homework.processor.homework.ProcessorThrowExceptionInEvenSecond;

@ExtendWith(MockitoExtension.class)
class ProcessorThrowExceptionInEvenSecondTest {
    @Mock
    private DateTimeProvider dateTimeProvider;

    Message message;

    @BeforeEach
    void startUp() {
        message = new Message.Builder(1L).field2("x").build();
    }

    @Test
    void testProcessorThrowExceptionInEvenSecond() {
        when(dateTimeProvider.getDate()).thenReturn(LocalDateTime.of(2025, 1, 1, 22, 22, 22));
        ProcessorThrowExceptionInEvenSecond processorThrowExceptionInEvenSecond =
                new ProcessorThrowExceptionInEvenSecond(dateTimeProvider);
        assertThrows(RuntimeException.class, () -> processorThrowExceptionInEvenSecond.process(message));
    }

    @Test
    void testProcessorDoesNotThrowExceptionInUnEvenSecond() {
        when(dateTimeProvider.getDate()).thenReturn(LocalDateTime.of(2025, 1, 1, 22, 22, 23));
        ProcessorThrowExceptionInEvenSecond processorThrowExceptionInEvenSecond =
                new ProcessorThrowExceptionInEvenSecond(dateTimeProvider);
        assertDoesNotThrow(() -> processorThrowExceptionInEvenSecond.process(message));
    }
}
