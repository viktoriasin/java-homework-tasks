package ru.sinvic.config;

import ru.sinvic.appcontainer.api.AppComponent;
import ru.sinvic.appcontainer.api.AppComponentsContainerConfig;
import ru.sinvic.services.EquationPreparer;
import ru.sinvic.services.EquationPreparerImpl;
import ru.sinvic.services.IOService;
import ru.sinvic.services.IOServiceStreams;

@AppComponentsContainerConfig(order = 1)
public class AppConfig1 {

    @AppComponent(order = 0, name = "equationPreparer")
    public EquationPreparer equationPreparer() {
        return new EquationPreparerImpl();
    }

    @SuppressWarnings("squid:S106")
    @AppComponent(order = 0, name = "ioService")
    public IOService ioService() {
        return new IOServiceStreams(System.out, System.in);
    }
}
