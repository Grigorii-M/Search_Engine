package matiukhin.grigorii.search_engine.algorithms.searching.search_algorithms;

import java.io.File;
import java.util.HashMap;

public class SearchResults {
    private final File[] files;
    private final HashMap<File, String> snippets;

    public SearchResults(File[] files, HashMap<File, String> snippets) {
        this.files = files;
        this.snippets = snippets;
    }

    public File[] getFiles() {
        return files;
    }

    public HashMap<File, String> getSnippets() {
        return snippets;
    }
}
