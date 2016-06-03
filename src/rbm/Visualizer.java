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

import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;

/**
 * @author Peter Swire
 * class Visualizer
 * Prints out the current state of an RBM as rectangles
 */
public class Visualizer extends JPanel {

    // member variable: the state of the rbm 
    private float[][] state;

    /**
     * @param state - an array of binary integers to be visualized
     * @param numRows - the number of rows to break the state array into
     * @param numCols - the number of columns to break the state array into
     *
     * Constructs a new Visualizer sized according to numRows and numCols,
     * visualizing the given state array
     */
    public Visualizer(int[] state, int numRows, int numCols){
        this(convertArray(state, numRows, numCols));
    }

    /**
     * @param state - a 2D array of floats to be visualized
     *
     * Constructs a new Visualizer sized proportionally to the given array,
     * visualizing said array
     */
    public Visualizer(float[][] state){

        this.state = state;
        setBackground(Color.lightGray);

        int width = Params.BOX_WIDTH * state[0].length;
        int height = Params.BOX_WIDTH * state.length;
        this.setSize(new Dimension(width, height));
    } // end of constructor
    
    /**
     * Repaints the Visualizer
     */
    public void visualize(){
        repaint();
    }
    
    /**
     *
     * @param state - an array of binary integers to be visualized
     * @param numRows - the number of rows to break the state array into
     * @param numCols - the number of columns to break the state array into
     *
     * Repaints the Visualizer with the given state array, broken up into the
     * given number of rows and columns
     */
    public void visualize(int[] state, int numRows, int numCols){
        float[][] newState = convertArray(state, numRows, numCols);
        this.state = newState;
        visualize();
    }

    /**
     *
     * @param state - a 2D array of floats to be visualized
     *
     * Repaints the Visualizer with the given state array
     */
    public void visualize(float[][] state){
        this.state = state;
        visualize();
    }

   /**
     *
     * This method should never be called.  It overwrites a JPanel method
     * dictating what happens when repaint() is called.  All painting code
     * occurs here.
     */
    @Override
    public void paint(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        Dimension dimension = getSize();

        //Calculate block size
        int rectWidth = dimension.width / state[0].length;
        int rectHeight = dimension.height / state.length;

        //Draw each of the blocks
        for (int i=0; i<state.length; ++i)
            for (int j=0; j<state[0].length; ++j)
            {
                g2.setPaint(makeColor(state[i][j]));
                g2.fill(new Rectangle2D.Double(j * rectWidth,
                        i * rectHeight,
                        rectWidth,
                        rectHeight));
            }
        } // end of method paint

    /*
     * Converts an integer array into a 2D float array for visualizing given.
     * numRows and numCols are used to correctly break up the array.
     */
    private static float[][] convertArray(int[] array, int numRows, int numCols)
    {
        float[][] newArray = new float[numRows][numCols];
        for (int i = 0; i < newArray.length; ++i) {
            for (int j = 0; j < newArray[0].length; ++j) {
                newArray[i][j] = (float) array[(i * numCols) + j];
            }
        }
        return newArray;
    }

    /*
     * Converts a float into its corresponding grayscale color
     */
    private static Color makeColor(float fShade)
    {
        return new Color(fShade, fShade, fShade);
    }
} // end of class Visualizer
