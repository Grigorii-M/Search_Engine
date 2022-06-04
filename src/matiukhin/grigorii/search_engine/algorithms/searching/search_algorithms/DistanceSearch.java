package matiukhin.grigorii.search_engine.algorithms.searching.search_algorithms;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DistanceSearch extends SearchAlgorithm {

    @Override
    public SearchResults search() {
        if (searchTerms.length == 1) {
            File[] result = new File[5];

            for (int i = 0; i < result.length; i++) {
                try {
                    result[i] = corpus.getInvertedIndex(searchTerms[0]).get(i);
                } catch (NullPointerException e) {
                    result[i] = null;
                }
            }

            return new SearchResults(result, getShortestSnippets(result));
        }

        // Get the documents containing all search terms
        ArrayList<File> filesWithAllTerms = new ArrayList<>();
        for (File file : corpus.getFiles()) {
            if (corpus.getForwardIndex(file).containsAll(List.of(searchTerms))) {
                filesWithAllTerms.add(file);
            }
        }

        if (filesWithAllTerms.isEmpty()) {
            return new SearchResults(new File[0], null);
        } else if (filesWithAllTerms.size() == 1) {
            return new SearchResults(
                    filesWithAllTerms.toArray(new File[0]),
                    getShortestSnippets(filesWithAllTerms.toArray(new File[0]))
            );
        }

        HashMap<File, String> snippets = getShortestSnippets(filesWithAllTerms.toArray(new File[0]));
        HashMap<File, String> snippetsCopy = new HashMap<>(snippets);

        File[] relevantFiles = new File[5];

        for (int i = 0; i < relevantFiles.length; i++) {
            int minSize = Integer.MAX_VALUE;
            File topFile = null;
            for (File file : snippetsCopy.keySet()) {
                if (snippets.get(file).length() < minSize) {
                    minSize = snippetsCopy.get(file).length();
                    relevantFiles[i] = file;
                    topFile = file;
                }
            }

            snippetsCopy.remove(topFile);
        }

        return new SearchResults(relevantFiles, snippets);
    }
}
