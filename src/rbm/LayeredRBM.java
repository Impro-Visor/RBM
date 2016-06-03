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
import java.io.Serializable;
//
//  LayeredRBM.java
//  
//
//  Created by Sam Bosley on 6/11/09.
//  Copyright 2009 __MyCompanyName__. All rights reserved.
//

public class LayeredRBM implements Serializable {

    static final long serialVersionUID = 1741471844633093007L;

    //member variables
    protected RBM[] layers;
    protected int numLayers;
    protected int inputLength;
    protected int[] layerSizes;
    private transient VisualizerFrame visFrame = null;

    /**
     *
     * @param inputLength - the size of the inpuut layer
     * @param melodyRows - the number of rows in a melody (used for making groups
     * in InputRBM)
     * @param melodyCols - the number of columns in a melody (used for making groups
     * in InputRBM)
     * @param layerSizes - an int[] corresponding the number of nodes to be used
     * for each hidden layer. The size of this array defines the number of layers
     * int this LayeredRBM.
     *
     * Constructor for the LayeredRBM class
     */
    public LayeredRBM(int inputLength, int melodyRows, int melodyCols, int[] layerSizes) {
        this.numLayers = layerSizes.length;
        this.inputLength = inputLength;
        this.layerSizes = layerSizes;

        //generate linked RBM layers
        layers = new RBM[numLayers];

        layers[0] = new InputRBM(inputLength, this.layerSizes[0]);
        makeGroups(layers[0], melodyRows, melodyCols);

        for (int i = 1; i < numLayers; i++) {
            layers[i] = new RBM(layers[i - 1].getHidden(), this.layerSizes[i]);
        }
    } // end of constructor

    /**
     *
     * @return - the input length of the LRBM
     */
    public int getInputLength() {
        return inputLength;
    }

    /**
     *
     * @return - the int[] defining the sizes of hidden layers in the LRBM
     */
    public int[] getLayerSizes() {
        return layerSizes;
    }

    /**
     *
     * @param layerIndex - which layer to get the weights from
     * @return - a 2D float array of weights (note - not a copy,
     * points directly to the actual weight array for this layer
     * in memory).
     */
    public float[][] getWeightsForLayer(int layerIndex) {
        return layers[layerIndex].getWeights();
    }
    /*
    public void setWeightsForLayer(int layerIndex, float[][] newWeights) {
        layers[layerIndex].setWeights(newWeights);
    }//*/

    /**
     *
     * @param startIndex - the index to start clamping nodes at in the top layer,
     * inclusive
     * @param endIndex - the index to stop clamping nodes at in the top layer,
     * exclusive
     *
     * Convenience function, calls the clamp method for InputRBM
     */
    public void clamp(int startIndex, int endIndex) {
        if (layers[0] instanceof InputRBM) {
            ((InputRBM) layers[0]).clamp(startIndex, endIndex);
        }
    }

    /**
     *
     * @param startIndex - the index to start unclamping nodes at in the top layer,
     * inclusive
     * @param endIndex - the index to stop unclamping nodes at in the top layer,
     * exclusive
     *
     * Convenience function, calls the unclamp method for InputRBM
     */
    public void unclamp(int startIndex, int endIndex) {
        if (layers[0] instanceof InputRBM) {
            ((InputRBM) layers[0]).unclamp(startIndex, endIndex);
        }
    }

    /**
     * layeredLearn
     * @param inputs - the array of DataVessels to train on
     * @param numEpochs - the number of epochs to train for
     *
     * Convenience function for calling layeredLearn outside of a GUI - see
     * the description of the other layeredLearn method for more details.
     */
    public void layeredLearn(DataVessel[] inputs, int numEpochs) {
        layeredLearn(inputs, numEpochs, 0, null, null);
    }

    /**
     *
     * @param inputs - the array of DataVessels to train on
     * @param numEpochs - the number of epochs to train for
     * @param startLayer - the layer at which to start the training (used for
     * adding layers to machines with some layers already trained)
     * @param progressIndicator - a ProgressBars panel to update in the GUI
     * @param energyDisplay - an EnergyDisplay panel to update in the GUI
     *
     * Trains layered RBM on a series of input arrays by training each RBM layer
     * in turn.  Layers are trained through the contrastive divergence method
     * implemented in RBM.train().  For each RBM layer after the first, inputs
     * are propagated through previous layers by repeatedly activating hidden
     * nodes. Also updates the GUI through the indicator panels passed in. Call
     * the other layeredLearn function if you need to train outside the GUI.
     */
    public void layeredLearn(DataVessel[] inputs, int numEpochs, int startLayer, ProgressBars progressIndicator, EnergyDisplay energyDisplay) {
        // the progress bar and status indicator are for the GUI
        // if you are not using a GUI, these can be ignored by calling the
        // three parameter layeredLearn method.
        // <editor-fold defaultstate="collapsed" desc="Set Progress Bar Range">
        final long startTime = System.currentTimeMillis();
        final int finalNumEpochs = numEpochs;
        final ProgressBars tempProgressBars = progressIndicator;
        if (progressIndicator != null) {
            final int tempLength = inputs.length;
            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    tempProgressBars.setPartialBarRange(0, finalNumEpochs * tempLength);
                    tempProgressBars.setOverallBarRange(0, numLayers * finalNumEpochs * tempLength);
                }
            });
        } // </editor-fold>


        System.out.println("Learning network weights...");
        for (int currLayer = startLayer; currLayer < numLayers; currLayer++) {

            // <editor-fold defaultstate="collapsed" desc="Update Progress Bar Description">
            if (progressIndicator != null) {
                final int tempLayer = currLayer;
                SwingUtilities.invokeLater(new Runnable() {

                    public void run() {
                        tempProgressBars.setActionDescription("Training layer " + tempLayer);
                    }
                });
            }// </editor-fold>

            System.out.println("\nTraining layer "+currLayer+":");
            for (int epoch = 0; epoch < numEpochs; epoch++) {

                float annealingRate = (((Params.MIN_ANNEALING_RATE-1)/numEpochs)*epoch)+1;
                layers[currLayer].setAnnealingRate(annealingRate);
                if (energyDisplay != null) {
                    energyDisplay.addPoint(currLayer, layers[currLayer].getEnergy());
                }

                // <editor-fold defaultstate="collapsed" desc="If The Progress Has Stopped, Say So">
                for (int currInput = 0; currInput < inputs.length; currInput++) {
                    if (Thread.interrupted()) {
                        if (progressIndicator != null) {
                            SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                    tempProgressBars.setActionDescription("Training stopped");
                                    tempProgressBars.setProgressDescription("");
                                    tempProgressBars.setOverallProgress(0);
                                    tempProgressBars.setPartialProgress(0);
                                }
                            });
                        }
                        return;
                    } // </editor-fold>

                    // <editor-fold defaultstate="collapsed" desc="Update Progress Bar">
                    if (progressIndicator != null) {
                        final int tempLayer = currLayer;
                        final int tempEpoch = epoch;
                        final int tempInput = currInput;
                        final int tempInputLength = inputs.length;
                        final String formattedInput = String.format("%0"+(tempInputLength+"").length()+""+"d", tempInput); //sorry about this line :(

                        SwingUtilities.invokeLater(new Runnable() {

                            public void run() {
                                tempProgressBars.setProgressDescription("Epoch: " + tempEpoch + ", Input: " + formattedInput + " / " + tempInputLength);
                                tempProgressBars.setOverallProgress(tempLayer * finalNumEpochs * tempInputLength + tempEpoch * tempInputLength + tempInput);
                                tempProgressBars.setPartialProgress(tempEpoch * tempInputLength + tempInput);
                            }
                        });
                    } // </editor-fold>

                    if (currLayer == 0) {
                        layers[0].setInput(inputs[currInput].getData());
                        layers[0].train(Params.NUM_TRAINING_CYCLES);
                        layers[0].updateWeights(inputs.length);
                    } else {
                        propagateInput(inputs[currInput].getData(), currLayer);
                        layers[currLayer].train(Params.NUM_TRAINING_CYCLES);
                        layers[currLayer].updateWeights(inputs.length);
                    }
                }
            }
        }
        System.out.println("");

        // <editor-fold defaultstate="collapsed" desc="Reset Progress Bar, Show Finishing Time">
        final long endTime = System.currentTimeMillis();

        if (progressIndicator != null) {
            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    tempProgressBars.setActionDescription("Finished training");
                    double time = (endTime-startTime)/60000.0;
                    java.text.DecimalFormat df = new java.text.DecimalFormat("#.##");
                    tempProgressBars.setProgressDescription("Time elapsed: "+df.format(time)+" minutes");
                    tempProgressBars.setOverallProgress(0);
                    tempProgressBars.setPartialProgress(0);
                }
            });
        } // </editor-fold>

    } // end of method layeredLearn

    /**
     * propagateInput
     * @param input - the input to propagate through the network
     * @param depth - the number of layers to propagate through
     * @return
     *
     * Assigns input array to nodes of first network layer.  Propagates that
     * input through the network by repeatedly activating hidden nodes and
     * assigning the result to the next layers input nodes.
     */
    protected void propagateInput(int[] input, int depth) {
        layers[0].setInput(input);
        for (int i = 0; i < depth; i++) {
            layers[i].activateHidden();
        }
    }

    /**
     *
     * @param seed - the data with which to start the generation. Usually contains
     * chord data selected by user and random melody data.
     * @param numCycles - the number of generation cycles before the data is returned.
     * @return - a DataVessel now containing data generated by the LRBM.
     * NOTE: Directly modifies the passed in seed. Make a copy of seed before
     * generating if you need it afterwards.
     */
    public DataVessel layeredGenerate(DataVessel seed, int numCycles) {
        //variable declarations
        Visualizer vis = null;
        float annealingRate = 1;

        // Graphically display visible nodes
        if (Params.VISUAL_DISPLAY_MODE > 0) {
            if (visFrame == null) {
                visFrame = new VisualizerFrame(seed.getNumRows(), seed.getNumCols());
            }
            vis = new Visualizer(layers[0].getVisible(), seed.getNumRows(), seed.getNumCols());
            visFrame.addVisualizer(vis);
        }

        //testing size of our seed data array
        System.out.println(seed.getData().length + " seed data length");
        layers[0].setInput(seed.getData());
        for (int cycle = 0; cycle < numCycles; cycle++)
        {

            if (Params.GENERATIONAL_ANNEALING)
                //NOTE: annealing rate lowers from 1 to MIN_ANNEALING_RATE over generation
                //annealingRate = ((double)(cycle))/numCycles;
                annealingRate = (((Params.MIN_ANNEALING_RATE-1)/numCycles)*cycle)+1;


            // Propagate inputs forward through hidden layers
            for (int i = 0; i < layers.length; ++i) {
                if (Params.GENERATIONAL_ANNEALING) {
                    layers[i].setAnnealingRate(annealingRate);
                }
                layers[i].activateHidden();
            }

            // Propagate inputs backwards through visible layers
            for (int i = layers.length; i > 0; --i) {
                layers[i - 1].activateVisible();
            }

            // Print graphical display
            if (Params.VISUAL_DISPLAY_MODE > 0 && vis != null) {
                visualizeInputs(vis, seed.getNumRows(), seed.getNumCols());
            }
        }

        //final generation cycle
        //  * don't bother annealing
        //  * perform special visible node activation if wanted
        for (int i = 0; i < layers.length; ++i) {
            layers[i].activateHidden();
        }

        for (int i = layers.length; i > 1; --i) {
            layers[i - 1].activateVisible();
        }

        //final visible node activation:
        switch (Params.FINAL_ACTIVATION_MODE) {
            case Params.MAX_PROB:
                ((InputRBM)layers[0]).activateVisibleMaxProb();
                break;
            case Params.PROB_DIST:
                ((InputRBM)layers[0]).activateVisibleProbDist();
                break;
            default:
                layers[0].activateVisible();
        }
        System.arraycopy(layers[0].getVisible(), 0, seed.getMelody(), 0, seed.getMelodySize());
        if (seed.getChords() != null) {
            System.arraycopy(layers[0].getVisible(), seed.getMelodySize(), seed.getChords(), 0, seed.getChordsSize());
        }
        return seed;
    }

    /**
     * visualizeInputs
     * @param vis - the visualizer
     * 
     * Graphically displays the first layer of a layered RBM
     */
    private void visualizeInputs(Visualizer vis, int rows, int cols) {
        //display stuff
        if (Params.VISUAL_DISPLAY_MODE == Params.NONE) {
            return;
        } else if (Params.VISUAL_DISPLAY_MODE == Params.STATE) {
            vis.visualize(layers[0].getVisible(), rows, cols);
        } else if (Params.VISUAL_DISPLAY_MODE == Params.HINTON) {
            vis.visualize(((InputRBM) layers[0]).getHintonDiagram(rows, cols));
        } else {
            System.err.println("Display parameters set incorrectly");
        }

        //sleep
        try {
            java.lang.Thread.sleep(Params.SLEEP_TIME);
        } catch (InterruptedException e) {
            System.err.println("Problem pausing graphical display");
        }
    }

    /**
     * Sets the visualizer frame for this LRBM to null. Generally should happen
     * after a set of generations takes place.
     */
    public void resetVisFrame() {
        visFrame = null;
    }

    /**
     *
     * @param inrbm - the RBM in which to define groups. NOTE: Should be an
     * instance of InputRBM.
     * @param numRows - the number of rows in a melody
     * @param numCols - the number of columns in a melody
     *
     * Creates the pitch and octave groupings for the InputRBM
     */
    public void makeGroups(RBM inrbm, int numRows, int numCols) {
        // grouping is reserved for InputRBMs only.  Regular RBMs cannot do this
        if (inrbm instanceof InputRBM) {
            InputRBM temp = (InputRBM) inrbm;
            // iterate over the rows
            for (int row = 0; row < numRows; row++) {
                temp.makeGroup((row * numCols), (row * numCols) + (1+1+12)); //group pitch/sustained bits
                temp.makeGroup((row * numCols) + (1+1+12), (row * numCols) + numCols); //group octave bits
            }
        }
    } // end of method makeGroups

    /**
     *
     * @param clustering - use clustering?
     * @param thresholding - use thresholding?
     * @param numRows - the number of rows
     * @param numCols - the number of columns
     * @param filename - the name of the file to write to
     *
     * Writes out a receptive field image based on the current state of the LRBM.
     */
    public void writeReceptiveFieldImage(boolean clustering, boolean thresholding, int numRows, int numCols, String filename) {
        long time = System.currentTimeMillis();
        for (int layer = 0; layer < layers.length; layer++) {
            RBM currLayer = layers[layer];
            float[][] weights = currLayer.weights;
            String currFilename = filename + "_layer_" + layer + "_" + time;

            if(layer == 0){
                ReceptiveFieldImageMaker.makeReceptiveField(weights, numRows, numCols, clustering, thresholding, currFilename);
            }else{
                ReceptiveFieldImageMaker.makeReceptiveField(weights, -1, -1, clustering, thresholding, currFilename);
            }
        }

    }
}
