package com.boisbarganhados.ftc.jflap.utils;

import com.boisbarganhados.ftc.dfa.DFAState;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Data;

@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class State {
    @XmlAttribute
    private int id;
    @XmlAttribute
    private String name;
    private double x;
    private double y;
    @XmlElement(name = "initial")
    private boolean stateInitial;
    @XmlElement(name = "final")
    private boolean stateFinal;

    public DFAState toDfaState() {
        var dfaState = new DFAState(this.getId());
        dfaState.setFinalState(this.isStateFinal());
        dfaState.setInitialState(this.isStateInitial());
        dfaState.setName(this.getName());
        return dfaState;
    }

}
