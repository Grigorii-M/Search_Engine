package matiukhin.grigorii.search_engine.ui;

import matiukhin.grigorii.search_engine.algorithms.searching.search_algorithms.SearchResults;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResultPanel extends JPanel {
    private final JPanel filesPanel;
    private final JTextPane snippetArea;
    private final JPanel snippetPanel;
    private final JLabel nameLabel;

    private File[] filesToShow;
    private boolean isSnippedPanelOpened;
    private HashMap<File, String> snippets;

    public ResultPanel() {
        setLayout(new GridBagLayout());

        filesPanel = new JPanel();
        filesPanel.setLayout(new GridBagLayout());
        filesPanel.setMinimumSize(new Dimension(200, 100));
        filesPanel.setMaximumSize(this.getSize());

        snippetArea = new JTextPane() {
            public boolean getScrollableTracksViewportWidth() {
                return getParent().getSize().width > 100;
            }
        };
        snippetArea.setFont(new Font(Font.DIALOG, Font.PLAIN, 14));
        snippetArea.setEditable(false);
        snippetArea.setMinimumSize(new Dimension(200, 100));
        snippetPanel = new JPanel();
        snippetPanel.setLayout(new GridBagLayout());

        nameLabel = new JLabel();
        add(snippetArea);

        showFilesPanel();
    }

    public void showFilesPanel() {
        removeAll();
        filesPanel.removeAll();
        updateUI();
        isSnippedPanelOpened = false;

        GridBagConstraints c = new GridBagConstraints();
        if (filesToShow == null || filesToShow.length == 0) {
            filesPanel.setLayout(new GridBagLayout());
            JLabel noFilesLabel1 = new JLabel("No files here");
            noFilesLabel1.setHorizontalTextPosition(SwingConstants.HORIZONTAL);
            noFilesLabel1.setFont(new Font(Font.MONOSPACED, Font.ITALIC, 18));
            c.anchor = GridBagConstraints.CENTER;
            c.gridy = 0;
            filesPanel.add(noFilesLabel1, c);

            JLabel noFilesLabel2 = new JLabel("Try searching for some");
            noFilesLabel2.setHorizontalTextPosition(SwingConstants.HORIZONTAL);
            noFilesLabel2.setFont(new Font(Font.MONOSPACED, Font.ITALIC, 18));
            c.gridy = 1;
            filesPanel.add(noFilesLabel2, c);
        } else {
            filesPanel.setLayout(new WrapLayout(FlowLayout.LEADING));
            for (File file : filesToShow) {
                if (file != null && !file.isDirectory()) {
                    JButton button = new JButton();
                    button.setIcon(FileSystemView.getFileSystemView().getSystemIcon(file));
                    button.setText(file.getName());
                    button.setHorizontalTextPosition(JButton.CENTER);
                    button.setVerticalTextPosition(JButton.BOTTOM);
                    button.addActionListener(e -> showSnippet(button.getText()));
                    filesPanel.add(button);
                }
            }
        }

        JScrollPane scrollPane = new JScrollPane(filesPanel);
        scrollPane.setSize(this.getSize());
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        GridBagConstraints c2 = new GridBagConstraints();
        c2.weightx = 1;
        c2.weighty = 1;
        c2.fill = GridBagConstraints.BOTH;

        add(scrollPane, c2);
    }

    private void showSnippet(String name) {
        for (File file : filesToShow) {
            if (file == null) {
                continue;
            }

            if (file.getName().equals(name)) {
                try {
                    String text = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
                    snippetArea.setText("");
                    nameLabel.setText(file.getName());

                    StyledDocument doc = snippetArea.getStyledDocument();
                    Style snippetHighlightStyle = snippetArea.addStyle("snippetHighlightStyle", null);
                    StyleConstants.setBackground(snippetHighlightStyle, Color.CYAN);
                    Style normalTextStyle = snippetArea.addStyle("normalTextStyle", null);

                    if (snippets != null && snippets.containsKey(file) && snippets.get(file) != null) {
                        String snippet = snippets.get(file);

                        Matcher highlightTextMatcher = Pattern.compile(snippet, Pattern.CASE_INSENSITIVE).matcher(text);
                        if (highlightTextMatcher.find()) {
                            String originalSnippet = highlightTextMatcher.group();

                            String[] dividedText = text.split(originalSnippet);
                            int offset = 0;
                            for (int i = 0; i < dividedText.length; i++) {
                                doc.insertString(offset, dividedText[i], normalTextStyle);
                                offset += dividedText[i].length();

                                if (i != dividedText.length - 1 || dividedText.length == 1) {
                                    doc.insertString(offset, originalSnippet, snippetHighlightStyle);
                                    offset += originalSnippet.length();
                                }
                            }
                        }
                    } else {
                        doc.insertString(0, text, normalTextStyle);
                    }
                } catch (IOException | BadLocationException e) {
                    e.printStackTrace();
                }
            }
        }


        if (!isSnippedPanelOpened) {
            addSnippetPanel();
        }
        // Force the scroll pane to get to the beginning of the text
        snippetArea.select(0, 0);
    }

    public void addSnippetPanel() {
        removeAll();
        snippetPanel.removeAll();
        updateUI();

        GridBagConstraints c = new GridBagConstraints();
        JScrollPane scrollPane = new JScrollPane(snippetArea);
        scrollPane.setMinimumSize(snippetArea.getMinimumSize());
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 0;
        snippetPanel.add(nameLabel, c);

        Button closeSnippet = new Button("X");
        closeSnippet.addActionListener(e -> closeSnippetPanel());
        c.gridx = 1;
        c.gridy = 0;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 0;
        snippetPanel.add(closeSnippet, c);

        c.gridx = 0;
        c.gridy = 1;
        c.weighty = 1;
        c.gridwidth = GridBagConstraints.REMAINDER;
        snippetPanel.add(scrollPane, c);

        JScrollPane scrollPane2 = new JScrollPane(filesPanel);
        scrollPane2.setMinimumSize(filesPanel.getMinimumSize());
        scrollPane2.setSize(this.getSize());
        scrollPane2.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane2.getVerticalScrollBar().setUnitIncrement(16);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setLeftComponent(scrollPane2);
        splitPane.setRightComponent(snippetPanel);
        GridBagConstraints c1 = new GridBagConstraints();
        c1.weightx = 1;
        c1.weighty = 1;
        c1.fill = GridBagConstraints.BOTH;

        add(splitPane, c1);
        revalidate();
        repaint();
        isSnippedPanelOpened = true;
    }

    public void closeSnippetPanel() {
        showFilesPanel();
        isSnippedPanelOpened = false;
    }

    public void setFilesToShow(File[] files) {
        filesToShow = files;
        showFilesPanel();
    }

    public void setSearchResults(SearchResults searchResults) {
        setFilesToShow(searchResults.getFiles());
        this.snippets = searchResults.getSnippets();
    }
}
