package matiukhin.grigorii.search_engine.algorithms.searching.search_algorithms;

import java.io.File;
import java.util.HashMap;

public class TFIDF_Search extends SearchAlgorithm {

    @Override
    public SearchResults search() {
        HashMap<File, Double> files = new HashMap<>();
        for (File file : corpus.getFiles()) {
            double sum = 0;
            for (String term : searchTerms) {
                double termValue = corpus.getIDF(term) * corpus.getTF(file, term);
                sum += termValue;
            }
            files.put(file, sum);
        }

        File[] relevantFiles = new File[5];

        for (int i = 0; i < relevantFiles.length; i++) {
            double maxRelevancy = Integer.MIN_VALUE;
            File relevantFile = null;
            for (File file : files.keySet()) {
                if (files.get(file) > maxRelevancy) {
                    relevantFile = file;
                    maxRelevancy = files.get(file);
                }
            }
            relevantFiles[i] = relevantFile;
            files.remove(relevantFile);
        }

        return new SearchResults(relevantFiles, getShortestSnippets(relevantFiles));
    }
}
