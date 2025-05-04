package main.Tests.test_mapreduce;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FilesReader {

    public static List<List<String>> readFiles() {
        String pathFiles = "src/main/Files";
        List<List<String>> textCollections = new ArrayList<>();

        try {
            Path folderPath = Paths.get(pathFiles);
            DirectoryStream<Path> fileStream = Files.newDirectoryStream(folderPath);

            // Load each text into a position in the list
            for (Path path : fileStream) {
                String filePath = path.toString();
                List<String> text = new ArrayList<>();
                File file = new File(filePath);

                // Each text is loaded into a list of strings
                try (Scanner scanner = new Scanner(file)) {
                    String word;
                    while (scanner.hasNext()) {
                        word = scanner.next();
                        text.add(word);
                    }
                } catch (FileNotFoundException e) {
                    System.out.println("IOException: " + e.getMessage());
                }

                textCollections.add(text);
            }

            fileStream.close();
        } catch (IOException e) {
            System.out.println("There was a problem loading the texts");
        }

        return textCollections;
    }
    
}
