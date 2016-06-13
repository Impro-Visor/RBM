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


import java.io.Serializable;
import java.util.Random;
import java.util.ArrayList;
import encoding.Group;


/**
 * @author Peter Swire
 * class InputRBM extends RBM
 * The top layer in a LayeredRBM
 */
public class InputRBM extends RBM  implements Serializable{

    static final long serialVersionUID = -8694907460910482444L;

    Node[] visibleNodes;
    ArrayList<Group> groups;
    int[] ungrouped;

    /*
      * Constructor for InputRBM
      */
    public InputRBM(int numVisibleNodes, int numHiddenNodes){
        super(numVisibleNodes, numHiddenNodes);
        super.visibleNodes = null;   // we use our own, any use of the other
                                     // will be an error

        this.visibleNodes = new Node[numVisibleNodes + 1];
        for(int i = 0; i < visibleNodes.length; i++){
            visibleNodes[i] = new Node(0, false);
        }

        // bias nodes are always 1
        visibleNodes[ visibleNodes.length - 1 ].setValue(1);
        visibleNodes[ visibleNodes.length - 1 ].clamped = true;

        groups = new ArrayList<Group>();
        computeUngroupedFromGroups();

    } // end of constructor

    /**
     *
     * @param startIndex - the index to start clamping at, inclusive
     * @param endIndex - the index to stop clamping at, exclusive
     *
     * Sets the nodes corresponding to the array of indices to clamped.
     * For example, giving it [7,9] would clamp the input nodes at index [7,9).
     *
     */
    public void clamp(int startIndex, int endIndex){
        for(int i = startIndex; i < endIndex; i++){
            if(i >= visibleNodes.length || i < 0)
                continue;

            visibleNodes[i].clamped = true;
        }
    } // end of method clamp


    /**
     *
     * @param index - the index to clamp
     *
     * Sets the clamp for a single index
     */
    public void clamp(int index){
        if(index >= visibleNodes.length)
            return;

        visibleNodes[index].clamped = true;
    } // end of method clamp

    /**
     *
     * @param startIndex - the index to start unclamping at, inclusive
     * @param endIndex - the index to stop unclamping at, exclusive
     *
     * Sets the nodes corresponding to the array of indices to unclamped.
     */
    public void unclamp(int startIndex, int endIndex){
        for(int i = startIndex; i < endIndex; i++){
            if(i >= visibleNodes.length || i < 0)
                continue;

            visibleNodes[i].clamped = false;
        }
    } // end of method clamp


    /**
     *
     * @param index - the index to unclamp
     *
     * Releases the clamp for a single index
     */
    public void unclamp(int index){
        if(index >= visibleNodes.length)
            return;

        visibleNodes[index].clamped = false;
    } // end of method clamp


    /**
     *
     * Unclamps all nodes in the visible layer.
     */
    public void unclampAll(){
        for(Node n: this.visibleNodes){
            n.clamped = false;
        }
    }

    /**
     *
     * @param startIndex - the starting index of the new group, inclusive
     * @param endIndex - the ending index of the new group, exclusive
     *
     * Makes a new "group" and stores it in the groups arraylist.
     * Groups are used to associate certain nodes with one another
     * and give us greater control over activation (for example,
     * we can activate only one bit from a given group - see
     * activateVisibleMaxProb or activateVisibleProbDist).
     */
    public void makeGroup(int startIndex, int endIndex){
        this.groups.add(new Group(startIndex, endIndex));


        // eliminate the covered groups from the list of uncovered nodes
        for(int i = startIndex; i < endIndex; i++){
            this.ungrouped[i] = -1;
        }

    } // end of method makeGroup


    /**
     * Probabilistically activates each node in the visible layer based upon
     * the weighted sum accummulated from the hidden layer.
     */
    @Override
    public void activateVisible()
    {
        Random rand = new Random();
        for (int i=0; i<visibleNodes.length; ++i){
            if(visibleNodes[i].clamped == false){
                //computed weighted sum
                float sum = computeVisibleWeightedSum(i);

                //(call logsig function with annealing rate set to 1)
                if (rand.nextDouble() < logsig(sum, 1))
                    visibleNodes[i].setValue(1);
                else
                    visibleNodes[i].setValue(0);
            }
        }
    }

    /**
     * Probabilistically activates each node in the hidden layer based upon
     * the weighted sum accummulated from the visible layer.
     */
    @Override
    public void activateHidden()
    {
        Random rand = new Random();
        for (int i=0; i<hiddenNodes.length-1; ++i)
        {
            //compute weighted sum
            float sum = 0;
            for (int j=0; j<visibleNodes.length; ++j)
                if (visibleNodes[j].value == 1)
                    sum += weights[j][i];

            //probabalistically activate node based on sigmoid computation
            if (rand.nextDouble() < logsig(sum, annealingRate))
                hiddenNodes[i] = 1;
            else
                hiddenNodes[i] = 0;
        }
    }

    /**
     * For each group, activates the one bit from that group with the highest
     * probability of activation. All other bits in the group are set to 0.
     */
    public void activateVisibleMaxProb() {
        double prob = 0;
        double maxProb = 0;
        int bestIndex = 0;

        // the grouped nodes are to be processed before anything else
        for (Group group : groups) {

            maxProb = 0;
            bestIndex = 0;
            prob = 0;

            // set all the nodes in the group to zero, so that later the
            // chosen one will stand out
            for (int i = group.startIndex; i < group.endIndex; i++) {
                this.visibleNodes[i].setValue(0);
            }


            for (int i = group.startIndex; i < group.endIndex; i++) {
                prob = logsig(computeVisibleWeightedSum(i), 1);
                if (prob > maxProb) {
                    maxProb = prob;
                    bestIndex = i;
                }
            }
            this.visibleNodes[bestIndex].setValue(1);
            
            
            //for(int i = group.startIndex; i < group.endIndex; i++)
                //System.out.print(this.visibleNodes[i].value + " ");
            //System.out.println("line");


        } // end of groups foreach

        // activate everything else separately
        activateUngrouped();

    } // end of method activateVisibleMaxProb

    /**
     * For each group, probabilistically chooses one bit from that group to
     * activate based on the probability distribution defined by each bit's
     * probability of activation. Once one bit is chosen, all other bits in the
     * group are set to 0.
     */
    public void activateVisibleProbDist() {
        Random rand = new Random();
        float[] probs;
        int bestIndex = 0;

        for (Group group : groups) {
            probs = new float[group.endIndex - group.startIndex];

            for (int i = 0; i < probs.length; i++) {
                probs[i] = computeVisibleWeightedSum(i + group.startIndex);
                probs[i] = logsig(probs[i], 1);
            }

            normalizeProbabilities(probs);

            // set all the nodes in the group to zero, so that later the
            // chosen one will stand out
            for (int i = group.startIndex; i < group.endIndex; i++) {
                this.visibleNodes[i].setValue(0);
            }

            // randomly choose a bit based on the probability distribution
            double check = rand.nextDouble();
            for (int i = 0; i < probs.length; i++) {
                if (check < probs[i]) {
                    bestIndex = i + group.startIndex;
                    break;
                }
            }

            this.visibleNodes[bestIndex].setValue(1);

        } // end of group foreach

        activateUngrouped();
    } // end of method activateVisibleProbDist

    /**
     *
     * @param probabilities - the array of probabilites that define a distribution
     *
     * Helper method to normalize the probabilities and create a distribution for
     * the activateVisibleProbDist function.
     */
    private static void normalizeProbabilities(float[] probabilities) {
        float sum = 0;
        for (int i = 0; i < probabilities.length; i++) {
            sum += probabilities[i];
        }
        //Normalize probabilities by dividing by sum
        for (int i = 0; i < probabilities.length; i++) {
            probabilities[i] = probabilities[i]/sum;
        }
        float lastValue = 0;
        //Create distribution - distance between each value in the array will
        //correspond to that bit's likelihood of being picked.
        for (int i = 0; i < probabilities.length; i++) {
            probabilities[i] += lastValue;
            lastValue = probabilities[i];
        }
    }
    
    /*
     * Activate the nodes that are not in groups
     * the ungrouped are to be processed last
     */
    public void activateUngrouped(){
        Random rand = new Random();
        double prob;

        for(int i = 0; i < this.ungrouped.length; i++){
            // ignore those already covered by the groups
            if(this.ungrouped[i] == -1){
                continue;
            }
            //probabalistically activate node based on sigmoid computation
            prob = logsig(computeVisibleWeightedSum(i), 1);
            if(rand.nextDouble() < prob){
                this.visibleNodes[i].setValue(1);
            }

        }
    } // end of method activateUngrouped

    /**
     * Computes the list of ungrouped nodes based on all the currently
     * defined groups
     */
    public void computeUngroupedFromGroups(){
        this.ungrouped = new int[visibleNodes.length];

        for(int i = 0; i < ungrouped.length; i++){
            this.ungrouped[i] = i;
        }

        // turn off indices covered by groups.  -1 is off in this instance
        for(Group group: groups){
            if(group.isOneHot()) {
                for(int i = group.startIndex; i < group.endIndex; i++){
                    this.ungrouped[i] = -1;
                }
            }
        }
    } // end of method computeUngroupedFromGroups

    /**
     * Method: accumulatePos / accumulateNeg
     *
     * Increments positive/negative weight change matrices by product of input
     * node states and hidden node states
     *
     */
    @Override
    protected void accumulatePos()
    {
        for (int i=0; i<visibleNodes.length; ++i)
            if (visibleNodes[i].value == 1)
                for (int j=0; j<hiddenNodes.length; ++j)
                    dPos[i][j] += hiddenNodes[j];
    }

    @Override
    protected void accumulateNeg()
    {
        for (int i=0; i<visibleNodes.length; ++i)
            if (visibleNodes[i].value == 1)
                for (int j=0; j<hiddenNodes.length; ++j)
                    dNeg[i][j] += hiddenNodes[j];
    }

    /**
     *
     * @param newInput - the bit array to set as the visible layer
     */
    @Override
    public void setInput(int[] newInput){

        for (int i=0; i < newInput.length; i++)
        {
            //modify value directly (rather than using setValue)
            //in order to reset clamped nodes
            this.visibleNodes[i].value = newInput[i];
        }

        computeUngroupedFromGroups();
    } // end of method setInput

    /**
     *
     * @return - an int[] copy of the state of the visible layer
     */
    @Override
    public int[] getVisible(){
        int[] visints = new int[this.visibleNodes.length];

        for(int i = 0; i < this.visibleNodes.length; i++){
            visints[i] = this.visibleNodes[i].value;
        }

        return visints;
    }

    /**
     *
     * @param numRows - the number of rows in the diagram
     * @param numCols - the number of columns in the diagram
     * @return - all the activation probabilities for the visible nodes,
     * organized into a 2D array
     */
    //@Override
    public float[][] getHintonDiagram(int numRows, int numCols) {
        float[][] probs = new float[numRows][numCols];
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                float currSum = computeVisibleWeightedSum(row * numCols + col);
                probs[row][col] = logsig(currSum, 1);
            }
        }
        return probs;
    }

    /**
     *
     * @return - the current energy state of the machine
     */
    @Override
    public float getEnergy()
    {
        float energy = 0;
        for (int i=0; i<visibleNodes.length; ++i)
            for (int j=0; j<hiddenNodes.length; ++j)
                energy -= visibleNodes[i].value*hiddenNodes[j]*weights[i][j];
        return energy;
    }


    // class for clamped, unclamped inputs
    // (basically a tuple)
    class Node implements Serializable{
        public int value;
        public boolean clamped;

        public Node(int value, boolean clamped){
            this.value = value;
            this.clamped = clamped;
        }

        public void setValue(int v){
            if(this.clamped == false)
                this.value = v;
        }

        @Override
        public String toString(){
            return "Value: "+value+" Clamped: "+clamped;
        }
    }  // end of class Node


} // end of class InputRBM
