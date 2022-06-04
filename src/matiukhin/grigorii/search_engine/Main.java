package matiukhin.grigorii.search_engine;

import matiukhin.grigorii.search_engine.ui.MainUIFrame;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        try {
            SwingUtilities.invokeAndWait(MainUIFrame::new);
        } catch (InterruptedException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
