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
import java.io.*;
/**
 *
 * @author Sam Bosley
 */
public class MusicBrain implements Serializable {

    static final long serialVersionUID = 2811772098825291649L;

    private LayeredRBM lrbm;
    private int numMelodyRows;
    private int numMelodyCols;
    private int inputLength;
    private File[] trainingDataFiles;
    private DataVessel[] trainingDataVessels;


    private boolean learnRhythms;
    private boolean transposeInputs;
    private boolean useWindowing;
    private int stepSize;

    private int[] layerSizes;
    private int numEpochs;
    private float learningRate;

    private transient MainFrame owner;

    public MusicBrain(MainFrame owner) {
        this.owner = owner;

        layerSizes = owner.getLayerSizes();
        numEpochs = owner.getNumEpochs();
        learningRate = owner.getLearningRate();

        transposeInputs = owner.transposeInputs();
        useWindowing = owner.useWindowing();
        stepSize = owner.getStepSize();

        trainingDataFiles = owner.getTrainingData();

        setupLRBM();

    }

    //Used for loading a brain
    public void setOwner(MainFrame owner) {
        this.owner = owner;
    }

    public void setupLRBM() {

        trainingDataVessels = FileParser.parseAllFiles(trainingDataFiles);
        if (transposeInputs) {
            trainingDataVessels = FileParser.transposeDataVessels(trainingDataVessels);
        }
        if (useWindowing) {
            trainingDataVessels = FileParser.parseDataVesselsToWindows(trainingDataVessels, owner.getWindowLength(), stepSize);
        }
        
        numMelodyRows = trainingDataVessels[0].getNumRows();
        numMelodyCols = trainingDataVessels[0].getNumCols();
        inputLength = trainingDataVessels[0].getLength();
        lrbm = new LayeredRBM(inputLength, numMelodyRows, numMelodyCols, layerSizes);

    }

    public void addLayerToBrain(int newLayerSize) {
        RBM[] currLayers = lrbm.layers;
        int[] currLayerSizes = lrbm.layerSizes;

        RBM[] newLayers = new RBM[currLayers.length + 1];
        int[] newLayerSizes = new int[currLayerSizes.length + 1];

        System.arraycopy(currLayers, 0, newLayers, 0, currLayers.length);
        System.arraycopy(currLayerSizes, 0, newLayerSizes, 0, currLayerSizes.length);

        newLayers[newLayers.length - 1] = new RBM(currLayers[currLayers.length - 1].getHidden(), newLayerSize);
        newLayerSizes[newLayerSizes.length - 1] = newLayerSize;

        lrbm.numLayers++;
        lrbm.layerSizes = newLayerSizes;
        this.layerSizes = newLayerSizes;
        lrbm.layers = newLayers;

        lrbm.layeredLearn(trainingDataVessels, numEpochs, lrbm.numLayers-1, owner.getTrainingProgressBars(), owner.getEnergyDisplay());

    }

    public void train() {
        lrbm.layeredLearn(trainingDataVessels, numEpochs, 0, owner.getTrainingProgressBars(), owner.getEnergyDisplay());
    }

    public int getNumMelodyRows() {
        return numMelodyRows;
    }

    public int getNumMelodyCols() {
        return numMelodyCols;
    }

    public int getInputLength() {
        return inputLength;
    }

    public int getStepSize() {
        return stepSize;
    }

    public int[] getLayerSizes() {
        return layerSizes;
    }

    public int getNumEpochs() {
        return numEpochs;
    }

    public float getLearningRate() {
        return learningRate;
    }

    public boolean getTransposeInputs() {
        return transposeInputs;
    }

    public boolean getUseWindowing() {
        return useWindowing;
    }

    public boolean getLearnRhythms() {
        return learnRhythms;
    }

    public File[] getTrainingDataFiles() {
        return trainingDataFiles;
    }

    public DataVessel generate(DataVessel generationSeed, int numGenerationCycles) {
        lrbm.clamp(numMelodyRows*numMelodyCols, lrbm.getInputLength());
        DataVessel toReturn = lrbm.layeredGenerate(generationSeed, numGenerationCycles);
        lrbm.unclamp(numMelodyRows*numMelodyCols, lrbm.getInputLength());
        return toReturn;
    }
    //somewhat sure this method creates sections of a long melody from the base size
    private DataVessel windowedGenerate(DataVessel chordSeed, int numCycles, int stepSize) {
        int[][] output = new int[(chordSeed.getChordsSize()/Params.NUM_CHORD_COLUMNS)/stepSize][stepSize*numMelodyCols];
        lrbm.clamp(numMelodyRows*numMelodyCols, lrbm.getInputLength()); //Clamp chord bits


        int numFirstGenerations = (numMelodyRows/stepSize) - 1;
        int[] chordWindow = new int[numMelodyRows*Params.NUM_CHORD_COLUMNS];
        System.arraycopy(chordSeed.getChords(), 0, chordWindow, 0, chordWindow.length);

        DataVessel seed = DataGenerator.chordData(numMelodyRows*numMelodyCols, chordWindow);

        for (int i = 0; i < numFirstGenerations; i++) {
            seed = lrbm.layeredGenerate(seed, numCycles);
            lrbm.clamp(i*numMelodyCols*stepSize, (i+1)*numMelodyCols*stepSize);
            System.arraycopy(seed.getMelody(), i*numMelodyCols*stepSize, output[i], 0, output[i].length);
        }

        seed = lrbm.layeredGenerate(seed, numCycles);
        System.arraycopy(seed.getMelody(), numFirstGenerations*numMelodyCols*stepSize,
                output[numFirstGenerations], 0, output[numFirstGenerations].length);

        for (int i = numFirstGenerations + 1; i < output.length; i++) {
            System.arraycopy(seed.getMelody(), numMelodyCols*stepSize,
                    seed.getMelody(), 0, (numMelodyRows-stepSize)*numMelodyCols); //Shift melody over
            int[] randData = DataGenerator.randomData(numMelodyCols*stepSize);
            System.arraycopy(randData, 0, seed.getMelody(),
                    (numMelodyRows-stepSize)*numMelodyCols, randData.length); //Get random part of melody seed

            System.arraycopy(chordSeed.getChords(), (i-numFirstGenerations)*Params.NUM_CHORD_COLUMNS*stepSize,
                    seed.getChords(), 0, numMelodyRows*Params.NUM_CHORD_COLUMNS); //Shifting chords

            seed = lrbm.layeredGenerate(seed, numCycles);
            System.arraycopy(seed.getMelody(), numFirstGenerations*numMelodyCols*stepSize,
                    output[i], 0, output[i].length);
        }


        lrbm.unclamp(0, (numMelodyRows-stepSize)*numMelodyCols);

        for (int i = 0; i < output.length; i++) {
            System.arraycopy(output[i], 0, chordSeed.getMelody(), i*output[0].length, output[i].length);
        }

        return chordSeed;

    }

    public DataVessel loopingWindowedGenerate(DataVessel chordSeed, int numCycles, int stepSize, int outputRows)
    {
        int[] chords = chordSeed.getChords();
        int chordLength = chords.length;

        int[] loopedChord = new int[outputRows*Params.NUM_CHORD_COLUMNS];
        for (int i=0; i<loopedChord.length/chordLength; ++i)
            System.arraycopy(chords, 0, loopedChord, i*chordLength, chordLength);
        System.arraycopy(chords, 0, loopedChord, (loopedChord.length/chordLength)*chordLength, loopedChord.length%chordLength);

        //this data vessel WILL have chords filled to represent up until the desired length specified in new melody size
        DataVessel loopedChordSeed = DataGenerator.chordData(outputRows*numMelodyCols, loopedChord);

        return windowedGenerate(loopedChordSeed, numCycles, stepSize);
    }

    public void resetVisFrame() {
        lrbm.resetVisFrame();
    }

    public void writeReceptiveFieldImage(boolean clustering, boolean thresholding, String filename) {
        lrbm.writeReceptiveFieldImage(clustering, thresholding, this.numMelodyRows, this.numMelodyCols, filename);
    }

}
