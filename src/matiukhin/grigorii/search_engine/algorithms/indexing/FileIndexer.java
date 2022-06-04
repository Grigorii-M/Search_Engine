package matiukhin.grigorii.search_engine.algorithms.indexing;

import matiukhin.grigorii.search_engine.algorithms.TextSanitizer;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class FileIndexer extends SwingWorker<Corpus, String> {

    private final File[] filesToIndex;
    private final HashMap<File, String> fileContents;
    private final JLabel statusLabel;

    public FileIndexer(File[] files, JLabel statusLabel) {
        filesToIndex = files;
        fileContents = new HashMap<>();
        this.statusLabel = statusLabel;
    }

    @Override
    protected Corpus doInBackground() throws Exception {
        HashMap<File, ArrayList<String>> fwdIndex = calculateForwardIndex();
        HashMap<String, ArrayList<File>> invIndex = calculateInvertedIndex(fwdIndex);

        publish(" ");
        return new Corpus(fwdIndex, invIndex, fileContents);
    }

    private HashMap<File, ArrayList<String>> calculateForwardIndex() {
        HashMap<File, ArrayList<String>> forwardIndex = new HashMap<>();
        for (File file : filesToIndex) {
            publish("Calculating forward index of " + file.getName());
            try {
                if (!file.isDirectory()) {
                    String text = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
                    fileContents.put(file, text);
                    ArrayList<String> words = new ArrayList<>();
                    Collections.addAll(words, TextSanitizer.sanitizeText(text).split("\\s+"));

                    forwardIndex.put(file, words);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return forwardIndex;
    }

    private HashMap<String, ArrayList<File>> calculateInvertedIndex(HashMap<File, ArrayList<String>> forwardIndex) {
        HashMap<String, ArrayList<File>> invertedIndex = new HashMap<>();
        for (File file : forwardIndex.keySet()) {
            publish("Calculating inverted index of " + file.getName());
            ArrayList<String> wordsList = forwardIndex.get(file);
            for (String word : wordsList) {
                word = word.toLowerCase(Locale.ROOT);
                if (invertedIndex.containsKey(word)) {
                    if (!invertedIndex.get(word).contains(file)) {
                        invertedIndex.get(word).add(file);
                    }
                } else {
                    ArrayList<File> fileList = new ArrayList<>();
                    fileList.add(file);
                    invertedIndex.put(word, fileList);
                }
            }
        }

        return invertedIndex;
    }

    @Override
    protected void process(List<String> chunks) {
        for (String chunk : chunks) {
            statusLabel.setText(chunk);
        }
    }
}
