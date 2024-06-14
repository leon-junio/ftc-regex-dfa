package com.boisbarganhados.ftc.jflap.utils;

import java.util.ArrayList;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

@XmlRootElement(name = "structure")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class Automaton {
    private String type;
    private AutomatonType automaton;

    public Automaton() {
        this.type = "fa";
        this.automaton = new AutomatonType();
        this.automaton.setTransitions(new ArrayList<>());
        this.automaton.setStates(new ArrayList<>());
    }
}
