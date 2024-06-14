package com.boisbarganhados.ftc.jflap;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JFFProcessor {

    /**
     * Pre-process the JFF file to change the <initial/> and <final/> tags to
     * <initial>true</initial> and <final>true</final>
     * 
     * @param jffFile the JFF file to be pre-processed
     * @throws IOException if an I/O error occurs
     */
    public static void preProcessJFF(File jffFile) throws IOException {
        List<String> lines = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(jffFile))) {
            lines = new ArrayList<>();
            for (String line : reader.lines().toList()) {
                if (line.contains("<initial/>")) {
                    lines.add("<initial>true</initial>");
                } else if (line.contains("<final/>")) {
                    lines.add("<final>true</final>");
                } else {
                    lines.add(line);
                }
            }
        }
        if (lines != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(jffFile))) {
                for (String line : lines) {
                    writer.write(line);
                    writer.newLine();
                }
            }
        }
    }

    /**
     * Post-process the JFF file to change the <initial>true</initial> and
     * <final>true</final> tags to <initial/> and <final/>
     * 
     * @param jffFile the JFF file to be post-processed
     * @throws IOException if an I/O error occurs
     */
    public static void postProcessJFF(File jffFile) throws IOException {
        List<String> lines = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(jffFile))) {
            lines = new ArrayList<>();
            for (String line : reader.lines().toList()) {
                if (line.contains("<initial>")) {
                    if (line.contains("true"))
                        lines.add("<initial/>");
                } else if (line.contains("<final>")) {
                    if (line.contains("true"))
                        lines.add("<final/>");
                } else {
                    lines.add(line);
                }
            }
        }
        if (lines != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(jffFile))) {
                for (String line : lines) {
                    writer.write(line);
                    writer.newLine();
                }
            }
        }
    }
}
