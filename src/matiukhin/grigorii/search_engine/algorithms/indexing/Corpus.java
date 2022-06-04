package matiukhin.grigorii.search_engine.algorithms.indexing;

import matiukhin.grigorii.search_engine.algorithms.TextSanitizer;

import java.io.File;
import java.util.*;

public class Corpus {

    private final HashMap<File, String> fileContents;
    private final HashMap<File, ArrayList<String>> forwardIndex;
    private final HashMap<String, ArrayList<File>> invertedIndex;
    private final HashMap<File, HashMap<String, Double>> tfLookupTable;
    private final HashMap<String, Double> idfLookupTable;

    public Corpus(HashMap<File, ArrayList<String>> fwdIndex, HashMap<String, ArrayList<File>> invIndex, HashMap<File, String> fileContents) {
        this.fileContents = new HashMap<>();

        for (File file : fileContents.keySet()) {
            this.fileContents.put(file, TextSanitizer.sanitizeText(fileContents.get(file)));
        }

        forwardIndex = fwdIndex;
        invertedIndex = invIndex;

        tfLookupTable = new HashMap<>();
        idfLookupTable = new HashMap<>();

        calculateTF();
        calculateIDF();
    }

    private void calculateTF() {
        for (File file : forwardIndex.keySet()) {
            ArrayList<String> words = forwardIndex.get(file);
            HashMap<String, Double> map = new HashMap<>();
            for (String word : words) {
                double frequency = Collections.frequency(words, word) / (double) words.size();
                map.put(word, frequency);
            }
            tfLookupTable.put(file, map);
        }
    }

    private void calculateIDF() {
        for (String word : invertedIndex.keySet()) {
            // Add 1 in denominator to evade division by 0
            double idf = Math.log(forwardIndex.keySet().size() / (double) invertedIndex.get(word).size());
            idfLookupTable.put(word, idf);
        }
    }

    public double getTF(File file, String word) {
        try {
            return tfLookupTable.get(file).get(word);
        } catch (NullPointerException e) {
            return 0;
        }
    }

    public double getIDF(String word) {
        try {
            return idfLookupTable.get(word);
        } catch (NullPointerException e) {
            return 0;
        }
    }

    public Set<String> getUniqueWords() {
        return invertedIndex.keySet();
    }

    public Set<File> getFiles() {
        return forwardIndex.keySet();
    }

    public ArrayList<String> getForwardIndex(File file) {
        return forwardIndex.get(file);
    }

    public ArrayList<File> getInvertedIndex(String word) {
        return invertedIndex.get(word);
    }

    public String getContents(File file) {
        return fileContents.get(file);
    }
}
