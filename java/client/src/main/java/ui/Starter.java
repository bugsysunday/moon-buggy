package ui;


import controller.MainFrameController;

import javax.swing.*;

/**
 * Created by okn on 2/12/14.
 */
public class Starter {
    public static void main(String[] args)
    {
        try
        {
            MoonBuggyControlFrame statisticsMainFrame = new MoonBuggyControlFrame("roboControl", new MainFrameController());
            statisticsMainFrame.setVisible(true);
            statisticsMainFrame.setExtendedState(statisticsMainFrame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
            statisticsMainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            statisticsMainFrame.pack();

        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
        }
    }
}
