package matiukhin.grigorii.search_engine.algorithms.searching.search_algorithms;

import matiukhin.grigorii.search_engine.algorithms.indexing.Corpus;

import java.io.File;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class SearchAlgorithm {
    protected String[] searchTerms;
    protected File[] files;
    protected Corpus corpus;

    public abstract SearchResults search();

    public void setSearchTerms(String[] searchTerms) {
        this.searchTerms = searchTerms;
    }

    public void setSearchIndex(Corpus corpus) {
        this.corpus = corpus;
        this.files = corpus.getFiles().toArray(new File[0]);
    }

    protected HashMap<File, String> getShortestSnippets(File[] filesToSearch) {

        StringBuilder forwardPattern = new StringBuilder();
        StringBuilder invertedPattern = new StringBuilder();
        for (int i = 0; i < searchTerms.length - 1; i++) {
            String term = searchTerms[i];
            forwardPattern.append(term).append(".*?");
            String oppositeTerm = searchTerms[searchTerms.length - i - 1];
            invertedPattern.append(invertString(oppositeTerm)).append(".*?");
        }
        forwardPattern.append(searchTerms[searchTerms.length - 1]);
        invertedPattern.append(invertString(searchTerms[0]));

        HashMap<File, String> snippets = new HashMap<>();
        for (File file : filesToSearch) {
            if (file == null) {
                continue;
            }
            Matcher termsMatcher = Pattern.compile(String.valueOf(forwardPattern)).matcher(corpus.getContents(file));
            int fileMinSize = Integer.MAX_VALUE;
            while (termsMatcher.find()) {
                String snippet = termsMatcher.group();

                String invertedSnippet = invertString(snippet);
                Matcher invertedSnippetMatcher = Pattern.compile(String.valueOf(invertedPattern)).matcher(invertedSnippet);
                if (invertedSnippetMatcher.find()) {
                    if (invertedSnippetMatcher.group().length() < snippet.length()) {
                        snippet = snippet.substring(snippet.length() - invertedSnippetMatcher.group().length());
                    }
                }

                if (snippet.length() < fileMinSize) {
                    fileMinSize = snippet.length();
                    snippets.put(file, snippet);
                }
            }
        }

        return snippets;
    }

    private String invertString(String input) {
        StringBuilder invertedStr = new StringBuilder();
        for (int j = input.length() - 1; j >= 0; j--) {
            invertedStr.append(input.charAt(j));
        }

        return String.valueOf(invertedStr);
    }
}
