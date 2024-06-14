package com.boisbarganhados.ftc.jflap;

import java.io.File;

import com.boisbarganhados.ftc.jflap.utils.Automaton;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

public class XMLController {

    /**
     * Read a XML file and return a Automaton object
     * 
     * @param String xmlFile File type to be read
     * @return Automaton object with the data from the XML file
     * @throws JAXBException
     * @throws Exception
     */
    public static Automaton reader(String filePath) throws JAXBException, Exception {
        var xmlFile = new File(filePath);
        if (!xmlFile.exists()) {
            throw new Exception("File not found");
        }
        JFFProcessor.preProcessJFF(xmlFile);
        xmlFile = new File(filePath);
        JAXBContext jaxbContext = JAXBContext.newInstance(Automaton.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        Automaton automaton = (Automaton) jaxbUnmarshaller.unmarshal(xmlFile);
        return automaton;
    }

    /**
     * Write a Automaton object to a XML file
     * 
     * @param Automaton automaton Automaton object to be written
     * @param File      xmlFile File type to be written
     * @throws JAXBException
     * @throws Exception
     */
    public static void writer(Automaton automaton, String filePath) throws JAXBException, Exception {
        var xmlFile = new File(filePath);
        JAXBContext jaxbContext = JAXBContext.newInstance(Automaton.class);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        jaxbMarshaller.marshal(automaton, xmlFile);
        if (!xmlFile.exists())
            throw new Exception("Error writing file");
        JFFProcessor.postProcessJFF(xmlFile);
    }

}
