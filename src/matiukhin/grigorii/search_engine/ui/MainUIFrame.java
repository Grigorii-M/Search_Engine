package matiukhin.grigorii.search_engine.ui;

import matiukhin.grigorii.search_engine.algorithms.indexing.Corpus;
import matiukhin.grigorii.search_engine.algorithms.indexing.FileIndexer;
import matiukhin.grigorii.search_engine.algorithms.searching.search_algorithms.SearchAlgorithm;
import matiukhin.grigorii.search_engine.algorithms.searching.SearchWorker;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.io.File;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class MainUIFrame extends JFrame {

    private File activeDirectory;
    private final ResultPanel resultPanel;
    private final JLabel statusLabel;
    private final JComboBox algorithmComboBox;
    private Corpus corpus;

    public MainUIFrame() {
        setTitle("Search Engine");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 450);
        setMinimumSize(new Dimension(400, 300));
        setLocationRelativeTo(null);

        // Set up menu bar
        JMenuBar menuBar = new JMenuBar();
        JMenu preferencesMenu = new JMenu("Preferences");
        JMenuItem activeDirectoryMenuItem = new JMenuItem("Set active directory");
        activeDirectoryMenuItem.addActionListener(e -> selectActiveDirectory());
        preferencesMenu.add(activeDirectoryMenuItem);
        menuBar.add(preferencesMenu);
        setJMenuBar(menuBar);

        // Set up main window
        setLayout(new GridBagLayout());
        JTextField searchField = new JTextField();
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.LINE_START;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        add(searchField, c);

        algorithmComboBox = new JComboBox(SearchWorker.AlgorithmsEnum.values());
        algorithmComboBox.setToolTipText("Select an algorithm to use");
        c.gridx = 1;
        c.weightx = 0;
        add(algorithmComboBox, c);

        JButton beginSearch = new JButton("Search");
        beginSearch.addActionListener(e -> search(searchField.getText()));
        c.gridx = 2;
        add(beginSearch, c);

        resultPanel = new ResultPanel();
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = 1;
        add(resultPanel, c);

        statusLabel = new JLabel(" ");
        statusLabel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        c.gridy = 2;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 0;
        add(statusLabel, c);

        setVisible(true);

        // Ask for an active directory
        selectActiveDirectory();
    }

    private void search(String searchQuery) {
        if (searchQuery == null) {
            File[] files = activeDirectory.listFiles();
            resultPanel.setFilesToShow(files);
            FileIndexer indexer = new FileIndexer(files, statusLabel);
            indexer.execute();
            try {
                corpus = indexer.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        } else if (!searchQuery.matches("\\s+") || !searchQuery.isEmpty()) {
            SearchAlgorithm algorithm = ((SearchWorker.AlgorithmsEnum) Objects.requireNonNull(algorithmComboBox.getSelectedItem())).getAlgorithm();
            SearchWorker searchWorker = new SearchWorker(corpus, searchQuery, algorithm, statusLabel);
            searchWorker.execute();

            try {
                resultPanel.setSearchResults(searchWorker.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    private void selectActiveDirectory() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setDialogTitle("Select active directory");
        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            activeDirectory = fileChooser.getSelectedFile();
            search(null);
        }
    }
}
