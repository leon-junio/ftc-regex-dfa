package com.boisbarganhados.ftc.jflap.utils;

import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Data;

@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class AutomatonType {
    @XmlElement(name = "state")
    private List<State> states;

    @XmlElement(name = "transition")
    private List<Transition> transitions;
}
