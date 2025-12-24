package com.example.salonmanagementsystem.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CsvExporter {
    public static File exportToFile(String[] headers, String[][] rows, String filepath)
            throws IOException {
        try (FileWriter writer = new FileWriter(filepath)) {
            writer.write(String.join(",", headers) + "\n");
            for (String[] row : rows) {
                writer.write(String.join(",", row) + "\n");
            }
        }
        return new File(filepath);
    }
}