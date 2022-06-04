package matiukhin.grigorii.search_engine.algorithms.searching;

import matiukhin.grigorii.search_engine.algorithms.TextSanitizer;
import matiukhin.grigorii.search_engine.algorithms.indexing.Corpus;
import matiukhin.grigorii.search_engine.algorithms.searching.search_algorithms.DistanceSearch;
import matiukhin.grigorii.search_engine.algorithms.searching.search_algorithms.SearchAlgorithm;
import matiukhin.grigorii.search_engine.algorithms.searching.search_algorithms.SearchResults;
import matiukhin.grigorii.search_engine.algorithms.searching.search_algorithms.TFIDF_Search;

import javax.swing.*;
import java.util.List;

public class SearchWorker extends SwingWorker<SearchResults, String> {

    private final Corpus corpus;
    private final String searchQuery;
    private final String[] searchTerms;
    private final JLabel statusLabel;
    private SearchAlgorithm algorithm;

    public SearchWorker(Corpus corpus, String query, SearchAlgorithm algorithm, JLabel statusLabel) {
        searchQuery = query;
        searchTerms = TextSanitizer.sanitizeText(query).split("\\s+");

        this.algorithm = algorithm;
        this.corpus = corpus;

        algorithm.setSearchTerms(searchTerms);
        algorithm.setSearchIndex(corpus);
        this.statusLabel = statusLabel;
    }

    @Override
    protected SearchResults doInBackground() throws Exception {
        publish("Searching for [" + searchQuery + "]");
        // Use TFIDF algorithm as default
        if (algorithm == null) {
            this.algorithm = new TFIDF_Search();
            algorithm.setSearchTerms(searchTerms);
            algorithm.setSearchIndex(corpus);
        }

        publish(" ");
        return algorithm.search();
    }

    @Override
    protected void process(List<String> chunks) {
        for (String chunk : chunks) {
            statusLabel.setText(chunk);
        }
    }

    public enum AlgorithmsEnum {
        TFIDF("TF*IDF", new TFIDF_Search()),
        DISTANCE("distance", new DistanceSearch());

        private final String name;
        private final SearchAlgorithm algorithm;

        AlgorithmsEnum(String name, SearchAlgorithm algorithm) {
            this.name = name;
            this.algorithm = algorithm;
        }

        public SearchAlgorithm getAlgorithm() {
            return algorithm;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
