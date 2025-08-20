package ru.sinvic.services;

import java.util.List;
import ru.sinvic.model.Equation;

public interface EquationPreparer {
    List<Equation> prepareEquationsFor(int base);
}
