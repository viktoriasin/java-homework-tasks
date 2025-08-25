package ru.sinvic.config;

import ru.sinvic.appcontainer.api.AppComponent;
import ru.sinvic.appcontainer.api.AppComponentsContainerConfig;
import ru.sinvic.services.*;

@AppComponentsContainerConfig(order = 2)
public class AppConfig2 {

    @AppComponent(order = 1, name = "playerService")
    public PlayerService playerService(IOService ioService) {
        return new PlayerServiceImpl(ioService);
    }

    @AppComponent(order = 2, name = "gameProcessor")
    public GameProcessor gameProcessor(
            IOService ioService, PlayerService playerService, EquationPreparer equationPreparer) {
        return new GameProcessorImpl(ioService, equationPreparer, playerService);
    }
}
