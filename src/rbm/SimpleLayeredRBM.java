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

public class SimpleLayeredRBM {
    //member variables
    protected SimpleRBM[] layers;

    /**
     *
     * @param inputLength - the length of the inputs to this lrbm
     * @param layerSizes - an integer array of the number of hidden nodes in
     *                     each layer of this lrbm
     *
     * Constructs a new SimpleLayeredRBM given the above parameters
     */
    public SimpleLayeredRBM(int inputLength, int[] layerSizes)
    {
        layers = new SimpleRBM[layerSizes.length];

        layers[0] = new SimpleRBM(inputLength, layerSizes[0]);
        for (int i = 1; i < layerSizes.length; i++) {
            layers[i] = new SimpleRBM(layers[i - 1].getHidden(), layerSizes[i]);
        }
    }

    /**
     * layeredLearn
     * @param inputs - the array of int arrays to train on
     *
     * Trains layered RBM on a series of input arrays by training each RBM layer
     * in turn.  Layers are trained through the contrastive divergence method
     * implemented in RBM.train().  For each RBM layer after the first, inputs
     * are propagated through previous layers by repeatedly activating hidden
     * nodes.
     */
    public void layeredLearn(int[][] inputs, int numEpochs) {

        for (int currLayer = 0; currLayer < layers.length; currLayer++) {
            for (int epoch = 0; epoch < numEpochs; epoch++) {
                //set annealing rate (falls from 1 to 0 during training)
                float annealingRate = 1 - (1f/numEpochs)*epoch;
                layers[currLayer].setAnnealingRate(annealingRate);

                for (int currInput = 0; currInput < inputs.length; currInput++){
                        propagateInput(inputs[currInput], currLayer);
                        layers[currLayer].train(1);
                        layers[currLayer].updateWeights(inputs.length);
                }
            }
        }
    }

    /**
     *
     * @param seed - the integer array seed to generate from
     * @param numCycles - the number of activation cycles to perform
     * @return a new integer array generated from the given seed
     */
    public int[] layeredGenerate(int[] seed, int numCycles) {

        layers[0].setInput(seed);
        for (int cycle = 0; cycle < numCycles; cycle++)
        {
            // Propagate inputs forward through hidden layers
            for (int i = 0; i < layers.length; ++i) {
                layers[i].activateHidden();
            }

            // Propagate inputs backwards through visible layers
            for (int i = layers.length; i > 0; --i) {
                layers[i - 1].activateVisible();
            }
        }

        return layers[0].getVisible();
    }

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
    private void propagateInput(int[] input, int depth) {
        layers[0].setInput(input);
        for (int i = 0; i < depth; i++) {
            layers[i].activateHidden();
        }
    }
}
