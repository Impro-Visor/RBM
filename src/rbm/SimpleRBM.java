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

import java.util.*;

/**
 *
 * @author Greg Bickerman
 */
public class SimpleRBM {
    //member variables

    int[] visibleNodes;
    int[] hiddenNodes;
    float[][] weights;
    float[][] dPos;    // accumulates positive weight changes
    float[][] dNeg;    // accumulates negative weight changes
    float annealingRate; // multiplier in sigmoid function

    /*
     *  Method: constructor
     *
     *  constructs a new RBM
     */
    public SimpleRBM(int numVisibleNodes, int numHiddenNodes) {
        //initialize nodes
        this.visibleNodes = new int[numVisibleNodes + 1]; //add one spot for bias
        this.hiddenNodes = new int[numHiddenNodes + 1];
        visibleNodes[visibleNodes.length - 1] = 1; //bias is always on
        hiddenNodes[hiddenNodes.length - 1] = 1;

        //initialize weights and weight change matrices
        this.weights = new float[numVisibleNodes + 1][numHiddenNodes + 1];
        this.dPos = new float[numVisibleNodes + 1][numHiddenNodes + 1];
        this.dNeg = new float[numVisibleNodes + 1][numHiddenNodes + 1];
        Random rand = new Random(); //randomly initialize weights
        for (int i = 0; i < numVisibleNodes + 1; ++i) {
            for (int j = 0; j < numHiddenNodes + 1; ++j) {
                weights[i][j] = new Float(0.1 * rand.nextGaussian());
                dPos[i][j] = 0;
                dNeg[i][j] = 0;
            }
        }
    }

    /*
     * Method: constructor
     *
     * constructs a new SimpleRBM using a pre-defined set of input nodes.
     * useful when layering RBMs.
     */
    public SimpleRBM(int[] visibleNodes, int numHiddenNodes) {
        this.visibleNodes = visibleNodes;

        this.hiddenNodes = new int[numHiddenNodes + 1];
        this.hiddenNodes[hiddenNodes.length - 1] = 1;

        //initialize weights and weight change matrices
        this.weights = new float[visibleNodes.length][numHiddenNodes + 1];
        this.dPos = new float[visibleNodes.length][numHiddenNodes + 1];
        this.dNeg = new float[visibleNodes.length][numHiddenNodes + 1];
        Random rand = new Random(); //randomly initialize weights
        for (int i = 0; i < visibleNodes.length; ++i) {
            for (int j = 0; j < numHiddenNodes + 1; ++j) {
                weights[i][j] = new Float(0.1 * rand.nextGaussian());
                dPos[i][j] = 0;
                dNeg[i][j] = 0;
            }
        }
    }

    public int[] getVisible() {
        return visibleNodes;
    }

    /*
     * Method: setInput
     *
     * To be called only in LayeredRBM for first layer
     */
    public void setInput(int[] newInput) {
        for (int i = 0; i < visibleNodes.length - 1 && i < newInput.length; i++) {
            visibleNodes[i] = newInput[i];
        }
    }

    public int[] getHidden() {
        return hiddenNodes;
    }

    public float[][] getWeights() {
        return weights;
    }

    public void setWeights(float[][] weights) {
        this.weights = weights;
    }

    public void setAnnealingRate(float newRate) {
        annealingRate = newRate;
    }

    /*
     *  Method: train
     *
     *  Trains the network on a single input (assumes input has already
     *  been set) and adds initial and final contrastive divergences to accumulators
     *
     */
    public void train(int numCycles) {
        activateHidden();
        accumulatePos();
        for (int i = 0; i < numCycles; ++i) {
            activateVisible();
            activateHidden();
        }
        accumulateNeg();
    }

    /*
     *  Method: activateVisible / activateHidden
     *
     *  Chooses whether to activate each input/hidden node based on the
     *  activation states and weights of the other nodes
     */
    public void activateVisible() {

        //initialize random number generator
        Random rand = new Random();

        for (int i = 0; i < visibleNodes.length - 1; ++i) {
            //compute weighted sum
            float sum = computeVisibleWeightedSum(i);
            //probabalistically activate node based on sigmoid computation
            if (rand.nextDouble() < logsig(sum, 1)) {
                visibleNodes[i] = 1;
            } else {
                visibleNodes[i] = 0;
            }
        }
    }

    public void activateHidden() {
        Random rand = new Random();
        for (int i = 0; i < hiddenNodes.length - 1; ++i) {
            //compute weighted sum
            float sum = 0;
            for (int j = 0; j < visibleNodes.length; ++j) {
                if (visibleNodes[j] == 1) {
                    sum += weights[j][i];
                }
            }

            //probabalistically activate node based on sigmoid computation
            if (rand.nextDouble() < logsig(sum, annealingRate)) {
                hiddenNodes[i] = 1;
            } else {
                hiddenNodes[i] = 0;
            }
        }
    }

    //Computes the weighted sum for a visible node
    public float computeVisibleWeightedSum(int index) {
        float sum = 0;
        for (int j = 0; j < hiddenNodes.length; j++) {
            if (hiddenNodes[j] == 1) {
                sum += weights[index][j];
            }
        }
        return sum;
    }

    /*
     * Method: accumulatePos / accumulateNeg
     *
     * Increments positive/negative weight change matrices by product of input
     * node states and hidden node states
     *
     */
    protected void accumulatePos() {
        for (int i = 0; i < visibleNodes.length; ++i) {
            if (visibleNodes[i] == 1) {
                for (int j = 0; j < hiddenNodes.length; ++j) {
                    dPos[i][j] += hiddenNodes[j];
                }
            }
        }
    }

    protected void accumulateNeg() {
        for (int i = 0; i < visibleNodes.length; ++i) {
            if (visibleNodes[i] == 1) {
                for (int j = 0; j < hiddenNodes.length; ++j) {
                    dNeg[i][j] += hiddenNodes[j];
                }
            }
        }
    }


    /*
     * Method: updateWeights
     *
     * increments/decrements weights by positive and negative weight change
     * matrices respectively (divided by the total number of inputs).  Also
     * resets weight change matrices.
     *
     */
    public void updateWeights(int numInputs) {
        for (int i = 0; i < weights.length; ++i) {
            for (int j = 0; j < weights[0].length; ++j) {
                weights[i][j] += (Params.ALPHA * dPos[i][j] / numInputs);
                weights[i][j] -= (Params.ALPHA * dNeg[i][j] / numInputs);

                dPos[i][j] = 0;
                dNeg[i][j] = 0;
            }
        }
    }

    /*
     * Method: getEnergy
     *
     * returns the the current energy state of the network, computed by summing
     * all weights between active nodes
     *
     */
    public float getEnergy() {
        float energy = 0;
        for (int i = 0; i < visibleNodes.length; ++i) {
            for (int j = 0; j < hiddenNodes.length; ++j) {
                energy -= visibleNodes[i] * hiddenNodes[j] * weights[i][j];
            }
        }
        return energy;
    }

    /*
     * Method: logsig
     *
     * computes the logsig of the input number
     *
     * logsig is a sigmoidal function with range between 0 and 1
     *
     * input is adjusted by annealing rate, which should be between 0 and 1
     *
     */
    protected static float logsig(float x, float annealingRate) {
        return 1 / (1 + ((float) Math.exp(-x / annealingRate)));
    }
}
