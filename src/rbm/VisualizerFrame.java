/**
 * This Java Class is part of the RBM-provisor Application
 * which, in turn, is part of the Intelligent Music Software
 * project at Harvey Mudd College, under the directorship of Robert Keller.
 *
 * Copyright (C) 2009 Robert Keller and Harvey Mudd College
 *
 * RBM-provisor is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * RBM-provisor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * merchantability or fitness for a particular purpose.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with RBM-provisor; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */


package rbm;
import javax.swing.*;
import java.awt.*;
/**
 *
 * @author Greg Bickerman
 */
public class VisualizerFrame extends JFrame {

    private JTabbedPane tabs;

    /**
     *
     * @param rows - the number of rows of data to be visualized
     * @param cols - the number of columns of data to be visualized
     *
     * Constructs a new VisualizerFrame, with width and height based on the
     * given number of rows and cols
     */
    public VisualizerFrame(int rows, int cols) {
        super("Visualizers");
        this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        Dimension dim = new Dimension(Params.BOX_WIDTH*cols+100,
                                      Params.BOX_WIDTH*rows+100);
        this.setMinimumSize(dim);
        this.setMaximumSize(dim);
        this.setPreferredSize(dim);

        this.add(tabs = new JTabbedPane());
        this.pack();
        this.setVisible(true);
    }

    /**
     *
     * @param vis - the new Visualizer to be added
     *
     * Addes the given Visualizer to the VisualizerFrame
     */
    public void addVisualizer(Visualizer vis) {
        tabs.add("Generation "+(tabs.getTabCount()+1), vis);
        tabs.setSelectedIndex(tabs.getTabCount()-1);
    }
}
