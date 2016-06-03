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
import javax.swing.*;
import java.util.*;

/**
 *
 * @author Greg Bickerman
 */
public class EnergyDisplay extends JPanel{

    //constants
    private static final int RADIUS = 4;
    private static int LEFT_BOUND = 50;
    private static final Color[] COLORS = {Color.red, Color.blue, Color.green,
                                        Color.cyan, Color.magenta, Color.orange,
                                        Color.pink, Color.darkGray, Color.white,
                                        Color.yellow};

    //member variables
    private ArrayList<Float>[] data;
    private int numEpochs;
    private float minValue;
    private float maxValue;

    /**
     *
     * @param width - the width of the EnergyDisplay panel
     * @param height - the height of the EnergyDisplay panel
     *
     * constructs a new EnergyDisplay panel
     */
    public EnergyDisplay(int width, int height) {
        Dimension size = new Dimension(width, height);
        this.setMaximumSize(size);
        this.setMinimumSize(size);
        this.setPreferredSize(size);   
    }

    /**
     * 
     * @param numLayers - the number of layers to be graphed
     * @param numEpochs - the number of training epochs for those layers
     * 
     * Clears and initializes EnergyDisplay for graphing a new set of energy
     * data.  Must be called before addPoint will work properly
     */
    @SuppressWarnings("unchecked")  // suppress the warning for instantiating
                                    // an array of typed objects below
    public void reset(int numLayers, int numEpochs){
        this.minValue = Integer.MAX_VALUE;
        this.maxValue = Integer.MIN_VALUE;

        this.numEpochs = numEpochs;
        data = new ArrayList[numLayers];
        for (int i=0; i<data.length; ++i)
            data[i] = new ArrayList<Float>(); //warning surpressed above
        repaint();
    }

    /**
     *
     * @param layer - the layer to add a new datapoint to
     * @param value - the energy value of the new datapoint
     *
     * Adds a new datapoint to the graph attached to the given layer with the
     * given value.
     */
    public void addPoint(int layer, float value)
    {
        data[layer].add(value);
        if (value<minValue)
            minValue = value;
        if (value>maxValue)
            maxValue = value;
        repaint();
    }

    /**
     *
     * This method should never be called.  It overwrites a JPanel method
     * dictating what happens when repaint() is called.  All painting code
     * occurs here.
     */
    @Override
    public void paint(Graphics g){
        super.paintComponent(g);
        g.setColor(Color.black);

        //write max, median, and min axis values
        String max = Math.round(maxValue)+"";
        String mid = Math.round((maxValue+minValue)/2)+"";
        String min = Math.round(minValue)+"";

        g.drawString(max, LEFT_BOUND-g.getFontMetrics().stringWidth(max)-5, 15);
        g.drawString(mid, LEFT_BOUND-g.getFontMetrics().stringWidth(mid)-5, this.getHeight()/2);
        g.drawString(min, LEFT_BOUND-g.getFontMetrics().stringWidth(min)-5, this.getHeight()-5);

        //draw axis
        g.drawLine(LEFT_BOUND, 0, LEFT_BOUND, this.getHeight()-RADIUS/2);
        g.drawLine(LEFT_BOUND, this.getHeight()-RADIUS/2, this.getWidth(), this.getHeight()-RADIUS/2);

        //draw data (if it exists)
        if (data != null) {
            for (int i = 0; i<data.length; ++i){
                g.setColor(COLORS[i%COLORS.length]);
                ArrayList<Float> currData = data[i];
                for (int x = 0; x<currData.size(); ++x){
                    g.fillOval(normX(x), normY(currData.get(x)), RADIUS, RADIUS);
                }
            }
        }
    }

    //Normalizes an x-value to be within the bounds of the plot
    private int normX(int x){
        return LEFT_BOUND+Math.round(x*(this.getWidth()-LEFT_BOUND)/numEpochs);
    }

    //Normalizes a y-value to be within the bounds of the plot
    private int normY(float y){

        int normY = Math.round((y-minValue)*(this.getHeight()-RADIUS)/(maxValue-minValue));
        return this.getHeight()-RADIUS-normY;
    }
}
