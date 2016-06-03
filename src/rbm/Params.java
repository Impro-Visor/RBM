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

public class Params
{

    /*THINGS THAT ARE GLOBAL:

     * GENERATIONAL_ANNEALING
     * FINAL_ACTIVATION
     * DISPLAY_MODE
     * ALPHA (learning rate)
     *
     */
    //////////////////
    //RBM PARAMETERS//
    //////////////////

    //number of training cycles per input (should probably remain at 1)
    public static final int NUM_TRAINING_CYCLES = 1;

    //minimum annealing rate for training (and generating if generational annealing is on)
    public static final float MIN_ANNEALING_RATE = 1f;

    //ues annealing during melody generation (should probably stay off)
    public static boolean GENERATIONAL_ANNEALING = false; //GLOBAL VARIABLE//

    //learning rate for weight changes in the RBM class
    public static float ALPHA = 0.2f; //GLOBAL VARIABLE//

    //final activation settings
    public static final int DEFAULT = 0;
    public static final int MAX_PROB = 1;
    public static final int PROB_DIST = 2;
    public static int FINAL_ACTIVATION_MODE = MAX_PROB; //GLOBAL VARIABLE//


    //////////////////
    //GUI PARAMETERS//
    //////////////////
    public static final int NUM_LAYERS = 3;
    public static final int DEFAULT_LAYER_SIZE = 100;
    public static final int MIN_LAYER_SIZE = 10;
    public static final int MAX_LAYER_SIZE = 5000;
    public static final int MIN_LAYER_NUM = 1;
    public static final int MAX_LAYER_NUM = 100;
    public static final int DEFAULT_NUM_EPOCHS = 250;
    public static final int DEFAULT_NUM_GENERATION_CYCLES = 20;


    /////////////////////
    //VISUAL PARAMETERS//
    /////////////////////

    //selects what to display during generation
    public static final int NONE = 0;
    public static final int STATE = 1;
    public static final int HINTON = 2;
    public static int VISUAL_DISPLAY_MODE = HINTON; //GLOBAL VARIABLE//

    //size of boxes in display
    public static final int BOX_WIDTH = 50;

    //number of milliseconds to sleep for visualizer
    public static final int SLEEP_TIME = 75;


    ///////////////////
    //FILE PARAMETERS//
    ///////////////////
    public static final int NUM_OUTPUTS = 5;
    public static final String OUT_DIRECTORY = "./";
    public static final String OUT_FILENAME = "new_melody";
    public static final int NUM_CHORD_COLUMNS = 12;
    public static final int NUM_LEADSHEET_MELODY_COLUMNS = 18;


    ////////////////////////////////
    // RECEPTIVE FIELD PARAMETERS //
    ////////////////////////////////
    public static final double THRESHOLD = 0.5;

}
