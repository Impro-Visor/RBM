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

package leadsheet;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import polya.*;
import rbm.DataVessel;
import rbm.Params;

/**
 *
 * @author Peter Swire
 */
public class LeadSheetHandler implements Constants {


    final static int[] NO_CHORD          = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    final static int[] C_MAJOR           = {1, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0};
    final static int[] C_MAJOR_7         = {1, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 1};
    final static int[] C_MINOR_7         = {1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 0};
    final static int[] C_DOM_7           = {1, 0, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0};
    final static int[] C_MINOR_9         = {1, 0, 1, 1, 0, 0, 0, 1, 0, 0, 0, 0};
    final static int[] C_13              = {1, 0, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0};
    final static int[] C_MINOR_7_FLAT_5  = {1, 0, 0, 1, 0, 0, 1, 0, 0, 0, 1, 0};
    final static int[] C_DOM_7_SHARP_9   = {1, 0, 0, 1, 1, 0, 0, 1, 0, 0, 1, 0};
    final static int[] C_DOM_7_FLAT_9    = {1, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0};
    final static int[] C_DIM_7           = {1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0};
    final static int[] C_9               = {1, 0, 1, 0, 1, 0, 0, 1, 0, 0, 1, 0};
    final static int[] C_MAJOR_9         = {1, 0, 1, 0, 1, 0, 0, 1, 0, 0, 0, 1};
    final static int[] C_DOM_7_SHARP_11  = {1, 0, 0, 1, 1, 0, 0, 1, 0, 0, 1, 0};
    final static int[] C_MAJOR_7_SHARP_11= {1, 0, 0, 1, 1, 0, 0, 1, 0, 0, 0, 1};
    final static int[] C_6               = {1, 0, 0, 0, 1, 0, 0, 1, 0, 1, 0, 0};
    final static int[] C_7_ALT           = {1, 0, 0, 1, 1, 0, 0, 0, 1, 0, 1, 0};
    final static int[] NC               = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    final static int[] C		= {1, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0};
    final static int[] CM		= {1, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0};
    final static int[] Cm_sharp_5	= {1, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0};
    final static int[] Cm_plus_         = {1, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0};
    final static int[] Cm		= {1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0};
    final static int[] Cm11_sharp_5	= {1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 0};
    final static int[] Cm11		= {1, 0, 1, 1, 0, 1, 0, 1, 0, 0, 1, 0};
    final static int[] Cm11b5           = {1, 0, 1, 1, 0, 1, 1, 0, 0, 0, 1, 0};
    final static int[] Cm13             = {1, 0, 1, 1, 0, 1, 0, 1, 0, 1, 1, 0};
    final static int[] Cm6              = {1, 0, 0, 1, 0, 0, 0, 1, 0, 1, 0, 0};
    final static int[] Cm69             = {1, 0, 1, 1, 0, 0, 0, 1, 0, 1, 0, 0};
    final static int[] Cm7_sharp_5	= {1, 0, 0, 1, 0, 0, 0, 0, 1, 0, 1, 0};
    final static int[] Cm7              = {1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 0};
    final static int[] Cm7b5            = {1, 0, 0, 1, 0, 0, 1, 0, 0, 0, 1, 0};
    final static int[] Ch7               = {1, 0, 0, 1, 0, 0, 1, 0, 0, 0, 1, 0};
    final static int[] Cm9_sharp_5	= {1, 0, 1, 1, 0, 0, 0, 0, 1, 0, 1, 0};
    final static int[] Cm9              = {1, 0, 1, 1, 0, 0, 0, 1, 0, 0, 1, 0};
    final static int[] Cm9b5            = {1, 0, 1, 1, 0, 0, 1, 0, 0, 0, 1, 0};
    final static int[] CmM7             = {1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1};
    final static int[] CmM7b6           = {1, 0, 0, 1, 0, 0, 0, 1, 1, 0, 0, 1};
    final static int[] CmM9             = {1, 0, 1, 1, 0, 0, 0, 1, 0, 0, 0, 1};
    final static int[] Cmadd9           = {1, 0, 1, 1, 0, 0, 0, 1, 0, 0, 0, 0};
    final static int[] Cmb6             = {1, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0};
    final static int[] Cmb6M7           = {1, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 1};
    final static int[] Cmb6b9           = {1, 1, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0};
    final static int[] CM_sharp_5	= {1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0};
    final static int[] C_plus_          = {1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0};
    final static int[] Caug             = {1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0};
    final static int[] C_plus_7         = {1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 1, 0};
    final static int[] CM_sharp_5add9	= {1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 0, 0};
    final static int[] CM7_sharp_5	= {1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1};
    final static int[] CM7_plus_	= {1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1};
    final static int[] CM9_sharp_5	= {1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 0, 1};
    final static int[] C_plus_add9	= {1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 0, 0};
    final static int[] C7               = {1, 0, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0};
    final static int[] C7_sharp_5	= {1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 1, 0};
    final static int[] C7_plus_         = {1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 1, 0};
    final static int[] Caug7            = {1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 1, 0};
    final static int[] C7aug            = {1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 1, 0};
    final static int[] C7_sharp_5_sharp_9	= {1, 0, 0, 1, 1, 0, 0, 0, 1, 0, 1, 0};
    final static int[] C7alt            = {1, 0, 0, 1, 1, 0, 0, 0, 1, 0, 1, 0};
    final static int[] C7b13            = {1, 0, 0, 0, 1, 0, 0, 1, 1, 0, 1, 0};
    final static int[] C7b5_sharp_9	= {1, 0, 0, 1, 1, 0, 0, 0, 1, 0, 1, 0};
    final static int[] C7b5             = {1, 0, 0, 0, 1, 0, 1, 0, 0, 0, 1, 0};
    final static int[] C7b5b13          = {1, 0, 0, 0, 1, 0, 1, 1, 1, 0, 1, 0};
    final static int[] C7b5b9           = {1, 1, 0, 0, 1, 0, 1, 1, 0, 0, 1, 0};
    final static int[] C7b5b9b13	= {1, 1, 0, 0, 1, 0, 1, 1, 1, 0, 1, 0};
    final static int[] C7b6             = {1, 0, 0, 0, 1, 0, 0, 1, 1, 0, 1, 0};
    final static int[] C7b9_sharp_11	= {1, 1, 0, 0, 1, 0, 1, 1, 0, 0, 1, 0};
    final static int[] C7b9_sharp_11b13	= {1, 1, 0, 0, 1, 0, 1, 1, 1, 0, 1, 0};
    final static int[] C7b9             = {1, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0};
    final static int[] C7b9b13_sharp_11	= {1, 1, 0, 0, 1, 0, 1, 1, 1, 0, 1, 0};
    final static int[] C7b9b13          = {1, 1, 0, 0, 1, 0, 0, 1, 1, 0, 1, 0};
    final static int[] C7no5            = {1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0};
    final static int[] C7_sharp_11	= {1, 0, 0, 0, 1, 0, 1, 1, 0, 0, 1, 0};
    final static int[] C7_sharp_11b13	= {1, 0, 0, 0, 1, 0, 1, 1, 1, 0, 1, 0};
    final static int[] C7_sharp_5b9_sharp_11	= {1, 1, 0, 0, 1, 0, 1, 0, 1, 0, 1, 0};
    final static int[] C7_sharp_5b9             = {1, 1, 0, 0, 1, 0, 0, 0, 1, 0, 1, 0};
    final static int[] C7_sharp_9_sharp_11	= {1, 0, 0, 1, 1, 0, 1, 1, 0, 0, 1, 0};
    final static int[] C7_sharp_9_sharp_11b13	= {1, 0, 0, 1, 1, 0, 1, 1, 1, 0, 1, 0};
    final static int[] C7_sharp_9	= {1, 0, 0, 1, 1, 0, 0, 1, 0, 0, 1, 0};
    final static int[] C7_sharp_9b13	= {1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 1, 0};
    final static int[] C9               = {1, 0, 1, 0, 1, 0, 0, 1, 0, 0, 1, 0};
    final static int[] C9_sharp_5	= {1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 1, 0};
    final static int[] C9_plus_         = {1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 1, 0};
    final static int[] C9_sharp_11	= {1, 0, 1, 0, 1, 0, 1, 1, 0, 0, 1, 0};
    final static int[] C9_sharp_11b13	= {1, 0, 1, 0, 1, 0, 1, 1, 1, 0, 1, 0};
    final static int[] C9_sharp_5_sharp_11	= {1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0};
    final static int[] C9b13            = {1, 0, 1, 0, 1, 0, 0, 1, 1, 0, 1, 0};
    final static int[] C9b5             = {1, 0, 1, 0, 1, 0, 1, 0, 0, 0, 1, 0};
    final static int[] C9b5b13          = {1, 0, 1, 0, 1, 0, 1, 1, 1, 0, 1, 0};
    final static int[] C9no5            = {1, 0, 1, 0, 1, 0, 0, 0, 0, 0, 1, 0};
    final static int[] C13_sharp_11	= {1, 0, 1, 0, 1, 0, 1, 1, 0, 1, 1, 0};
    final static int[] C13_sharp_9_sharp_11	= {1, 0, 0, 1, 1, 0, 1, 1, 0, 1, 1, 0};
    final static int[] C13_sharp_9	= {1, 0, 0, 1, 1, 0, 0, 1, 0, 1, 1, 0};
    final static int[] C13              = {1, 0, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0};
    final static int[] C13b5            = {1, 0, 0, 0, 1, 0, 1, 0, 0, 1, 1, 0};
    final static int[] C13b9_sharp_11	= {1, 1, 0, 0, 1, 0, 1, 1, 0, 1, 1, 0};
    final static int[] C13b9            = {1, 1, 0, 0, 1, 0, 0, 1, 0, 1, 1, 0};
    final static int[] CMsus2           = {1, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0};
    final static int[] CMsus4           = {1, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0};
    final static int[] Csus2            = {1, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0};
    final static int[] Csus4            = {1, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0};
    final static int[] Csusb9           = {1, 1, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0};
    final static int[] C7b9b13sus4	= {1, 1, 0, 0, 0, 1, 0, 1, 1, 0, 1, 0};
    final static int[] C7b9sus          = {1, 1, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0};
    final static int[] C7b9sus4         = {1, 1, 0, 0, 0, 1, 0, 1, 0, 0, 1, 0};
    final static int[] C7sus            = {1, 0, 0, 0, 0, 1, 0, 1, 0, 0, 1, 0};
    final static int[] C7sus4           = {1, 0, 0, 0, 0, 1, 0, 1, 0, 0, 1, 0};
    final static int[] C7sus4b9         = {1, 1, 0, 0, 0, 1, 0, 1, 0, 0, 1, 0};
    final static int[] C7sus4b9b13	= {1, 1, 0, 0, 0, 1, 0, 1, 1, 0, 1, 0};
    final static int[] C7susb9          = {1, 1, 0, 0, 0, 1, 0, 1, 0, 0, 1, 0};
    final static int[] C9sus4           = {1, 0, 1, 0, 0, 1, 0, 1, 0, 0, 1, 0};
    final static int[] C9sus            = {1, 0, 1, 0, 0, 1, 0, 1, 0, 0, 1, 0};
    final static int[] C11              = {1, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0};
    final static int[] C13sus           = {1, 0, 1, 0, 0, 1, 0, 1, 0, 1, 1, 0};
    final static int[] C13sus4          = {1, 0, 1, 0, 0, 1, 0, 1, 0, 1, 1, 0};
    final static int[] CBlues           = {1, 0, 0, 1, 0, 1, 1, 1, 0, 0, 1, 0};
    final static int[] CBass            = {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};


    final static int NUM_PITCHES = 12;


    private final static BidirectionalHashMap CHORD_TYPES = new BidirectionalHashMap();
    static {
        CHORD_TYPES.put("NC", NO_CHORD);
        CHORD_TYPES.put("", C_MAJOR);
        CHORD_TYPES.put("M", CM);
        CHORD_TYPES.put("M7", C_MAJOR_7);
        CHORD_TYPES.put("m7", C_MINOR_7);
        CHORD_TYPES.put("7", C_DOM_7);
        CHORD_TYPES.put("m9", C_MINOR_9);
        CHORD_TYPES.put("13", C_13);
        CHORD_TYPES.put("m7b5", C_MINOR_7_FLAT_5);
        CHORD_TYPES.put("7#9", C_DOM_7_SHARP_9);
        CHORD_TYPES.put("7b9", C_DOM_7_FLAT_9);
        CHORD_TYPES.put("o7", C_DIM_7);
        CHORD_TYPES.put("9", C_9);
        CHORD_TYPES.put("M9", C_MAJOR_9);
        CHORD_TYPES.put("7#11", C_DOM_7_SHARP_11);
        CHORD_TYPES.put("M7#11", C_MAJOR_7_SHARP_11);
        CHORD_TYPES.put("6", C_6);
        CHORD_TYPES.put("7alt", C_7_ALT);
        CHORD_TYPES.put("", C);
        CHORD_TYPES.put("m#5", Cm_sharp_5);
        CHORD_TYPES.put("m+", Cm_plus_);
        CHORD_TYPES.put("m", Cm);
        CHORD_TYPES.put("m11#5", Cm11_sharp_5);
        CHORD_TYPES.put("m11", Cm11);
        CHORD_TYPES.put("m11b5", Cm11b5);
        CHORD_TYPES.put("m13", Cm13);
        CHORD_TYPES.put("m6", Cm6);
        CHORD_TYPES.put("m69", Cm69);
        CHORD_TYPES.put("m7#5", Cm7_sharp_5);
        CHORD_TYPES.put("m7", Cm7);
        CHORD_TYPES.put("m7b5", Cm7b5);
        CHORD_TYPES.put("h7", Ch7);
        CHORD_TYPES.put("m9#5", Cm9_sharp_5);
        CHORD_TYPES.put("m9", Cm9);
        CHORD_TYPES.put("m9b5", Cm9b5);
        CHORD_TYPES.put("mM7", CmM7);
        CHORD_TYPES.put("mM7b6", CmM7b6);
        CHORD_TYPES.put("mM9", CmM9);
        CHORD_TYPES.put("madd9", Cmadd9);
        CHORD_TYPES.put("mb6", Cmb6);
        CHORD_TYPES.put("mb6M7", Cmb6M7);
        CHORD_TYPES.put("mb6b9", Cmb6b9);
        CHORD_TYPES.put("M#5", CM_sharp_5);
        CHORD_TYPES.put("+", C_plus_);
        CHORD_TYPES.put("aug", Caug);
        CHORD_TYPES.put("+7", C_plus_7);
        CHORD_TYPES.put("M#5add9", CM_sharp_5add9);
        CHORD_TYPES.put("M7#5", CM7_sharp_5);
        CHORD_TYPES.put("M7+", CM7_plus_);
        CHORD_TYPES.put("M9#5", CM9_sharp_5);
        CHORD_TYPES.put("+add9", C_plus_add9);
        CHORD_TYPES.put("7", C7);
        CHORD_TYPES.put("7#5", C7_sharp_5);
        CHORD_TYPES.put("7+", C7_plus_);
        CHORD_TYPES.put("aug7", Caug7);
        CHORD_TYPES.put("7aug", C7aug);
        CHORD_TYPES.put("7#5#9", C7_sharp_5_sharp_9);
        CHORD_TYPES.put("7alt", C7alt);
        CHORD_TYPES.put("7b13", C7b13);
        CHORD_TYPES.put("7b5#9", C7b5_sharp_9);
        CHORD_TYPES.put("7b5", C7b5);
        CHORD_TYPES.put("7b5b13", C7b5b13);
        CHORD_TYPES.put("7b5b9", C7b5b9);
        CHORD_TYPES.put("7b5b9b13", C7b5b9b13);
        CHORD_TYPES.put("7b6", C7b6);
        CHORD_TYPES.put("7b9#11", C7b9_sharp_11);
        CHORD_TYPES.put("7b9#11b13", C7b9_sharp_11b13);
        CHORD_TYPES.put("7b9", C7b9);
        CHORD_TYPES.put("7b9b13#11", C7b9b13_sharp_11);
        CHORD_TYPES.put("7b9b13", C7b9b13);
        CHORD_TYPES.put("7no5", C7no5);
        CHORD_TYPES.put("7#11", C7_sharp_11);
        CHORD_TYPES.put("7#11b13", C7_sharp_11b13);
        CHORD_TYPES.put("7#5b9#11", C7_sharp_5b9_sharp_11);
        CHORD_TYPES.put("7#5b9", C7_sharp_5b9);
        CHORD_TYPES.put("7#9#11", C7_sharp_9_sharp_11);
        CHORD_TYPES.put("7#9#11b13", C7_sharp_9_sharp_11b13);
        CHORD_TYPES.put("7#9", C7_sharp_9);
        CHORD_TYPES.put("7#9b13", C7_sharp_9b13);
        CHORD_TYPES.put("9", C9);
        CHORD_TYPES.put("9#5", C9_sharp_5);
        CHORD_TYPES.put("9+", C9_plus_);
        CHORD_TYPES.put("9#11", C9_sharp_11);
        CHORD_TYPES.put("9#11b13", C9_sharp_11b13);
        CHORD_TYPES.put("9#5#11", C9_sharp_5_sharp_11);
        CHORD_TYPES.put("9b13", C9b13);
        CHORD_TYPES.put("9b5", C9b5);
        CHORD_TYPES.put("9b5b13", C9b5b13);
        CHORD_TYPES.put("9no5", C9no5);
        CHORD_TYPES.put("13#11", C13_sharp_11);
        CHORD_TYPES.put("13#9#11", C13_sharp_9_sharp_11);
        CHORD_TYPES.put("13#9", C13_sharp_9);
        CHORD_TYPES.put("13", C13);
        CHORD_TYPES.put("13b5", C13b5);
        CHORD_TYPES.put("13b9#11", C13b9_sharp_11);
        CHORD_TYPES.put("13b9", C13b9);
        CHORD_TYPES.put("Msus2", CMsus2);
        CHORD_TYPES.put("Msus4", CMsus4);
        CHORD_TYPES.put("sus2", Csus2);
        CHORD_TYPES.put("sus4", Csus4);
        CHORD_TYPES.put("susb9", Csusb9);
        CHORD_TYPES.put("7b9b13sus4", C7b9b13sus4);
        CHORD_TYPES.put("7b9sus", C7b9sus);
        CHORD_TYPES.put("7b9sus4", C7b9sus4);
        CHORD_TYPES.put("7sus", C7sus);
        CHORD_TYPES.put("7sus4", C7sus4);
        CHORD_TYPES.put("7sus4b9", C7sus4b9);
        CHORD_TYPES.put("7sus4b9b13", C7sus4b9b13);
        CHORD_TYPES.put("7susb9", C7susb9);
        CHORD_TYPES.put("9sus4", C9sus4);
        CHORD_TYPES.put("9sus", C9sus);
        CHORD_TYPES.put("11", C11);
        CHORD_TYPES.put("13sus", C13sus);
        CHORD_TYPES.put("13sus4", C13sus4);
        CHORD_TYPES.put("Blues", CBlues);
        CHORD_TYPES.put("Bass", CBass);
    }



    private final static HashMap<String, Integer> DISTANCES_FROM_C = new HashMap<String, Integer>();
    static {
        DISTANCES_FROM_C.put("C", 0);
        DISTANCES_FROM_C.put("C#", 1);
        DISTANCES_FROM_C.put("Db", 1);
        DISTANCES_FROM_C.put("D", 2);
        DISTANCES_FROM_C.put("D#", 3);
        DISTANCES_FROM_C.put("Eb", 3);
        DISTANCES_FROM_C.put("E", 4);
        DISTANCES_FROM_C.put("F", 5);
        DISTANCES_FROM_C.put("F#", 6);
        DISTANCES_FROM_C.put("Gb", 6);
        DISTANCES_FROM_C.put("G", 7);
        DISTANCES_FROM_C.put("G#", 8);
        DISTANCES_FROM_C.put("Ab", 8);
        DISTANCES_FROM_C.put("A", 9);
        DISTANCES_FROM_C.put("A#", 10);
        DISTANCES_FROM_C.put("Bb", 10);
        DISTANCES_FROM_C.put("B", 11);

        // no chord: no transposition
        DISTANCES_FROM_C.put("NC", 0);
    }

    // for going from bit vectors to chords
    private final static HashMap<Integer, String> DISTANCES_TO_C = new HashMap<Integer, String>();
    static {
        DISTANCES_TO_C.put( 0, "C");
        DISTANCES_TO_C.put( 1, "C#");
        DISTANCES_TO_C.put( 2, "D");
        DISTANCES_TO_C.put( 3, "D#");
        DISTANCES_TO_C.put( 4, "E");
        DISTANCES_TO_C.put( 5, "F");
        DISTANCES_TO_C.put( 6, "F#");
        DISTANCES_TO_C.put( 7, "G");
        DISTANCES_TO_C.put( 8, "G#");
        DISTANCES_TO_C.put( 9, "A");
        DISTANCES_TO_C.put( 10, "A#");
        DISTANCES_TO_C.put( 11, "B");
    }


    // notes as bit vectors
    // we will be working with 4 octaves
    static int[][] notes = {
        {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},   // C
        {0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1}    // B
    };

    static int[][] octaves = {
        {1, 0, 0, 0},
        {0, 1, 0, 0},
        {0, 0, 1, 0},
        {0, 0, 0, 1}
    };


    public static DataVessel[] parseAllLeadSheetLicks(File[] files) {
        DataVessel[] outputs = new DataVessel[files.length];
        for (int i = 0; i < files.length; i++) {
            outputs[i] = parseLeadSheetLick(files[i]);
        }
        return outputs;
    }


    public static DataVessel parseLeadSheetLick(File filename) {
        return parseLeadSheetLick(filename.getAbsolutePath());
    }

    public static DataVessel parseLeadSheetLick(String filename) {
        int[] currMelody = parseLeadSheetMelody(filename);
        int[] currChords = parseLeadSheetChords(filename);

        int numChordRows = currChords.length / Params.NUM_CHORD_COLUMNS;
        int melRows = currMelody.length / Params.NUM_LEADSHEET_MELODY_COLUMNS;

        if (numChordRows != melRows) {
                int[] temp = new int[melRows * Params.NUM_CHORD_COLUMNS];
                for (int i = 0; i < temp.length / currChords.length; i++) {
                    System.arraycopy(currChords, 0, temp, i*currChords.length, currChords.length);
                }
                System.arraycopy(currChords, 0, temp,
                        (temp.length / currChords.length)*currChords.length, temp.length % currChords.length);
                currChords = temp;
            }

        return new DataVessel(currMelody, currChords, melRows, rbm.Params.NUM_LEADSHEET_MELODY_COLUMNS);
    }


    //TODO: make this support more than 8th note resolution files
    public static int[] parseLeadSheetMelody(String filename) {

        ArrayList<int[]> melody = new ArrayList<int[]>();

        try {
            
            Tokenizer tokenizer = new Tokenizer(new FileInputStream(filename));
            Object temp;
            
            while( (temp = tokenizer.nextSexp()) != Tokenizer.eof ){

                if(temp instanceof String){ 
                    String noteString = (String)temp;

                    char firstChar = noteString.charAt(0);

                    if(!Character.isLowerCase(firstChar)){
                        continue;
                    }

                    //System.out.println(noteString);
                    //NoteSymbol note = NoteSymbol.makeNoteSymbol(noteString);
                    NoteSymbol noteSymbol = NoteSymbol.makeNoteSymbol(noteString);
                    // if it was an invalid note, there is no reason to add it to the melody
                    if(noteSymbol == null){
                        continue;
                    }
                    //how does changing the resolution scalar affect file processing
                    int duration = noteSymbol.getDuration()/Constants.RESOLUTION_SCALAR;
                    int midiValue = noteSymbol.getMIDI();

                    //melody.add(constructNote(duration, midiValue));
                    melody.add(constructNoteOctaveForm(duration, midiValue));
                    
                }
                
            } // end of EOF while

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }

        int[] output = new int[0];

        while(melody.size() > 0){
            output = appendArrays(output, melody.remove(0));
        }

        return output;
    } // end of method parseLeadSheet

    public static int[] parseLeadSheetMelody(File file) {
        return parseLeadSheetMelody(file.getAbsolutePath());
    }


    //TODO: make this support more than 8th note resolution files
    public static int[] parseLeadSheetChords(String filename) {

        ArrayList<int[]> chords = new ArrayList<int[]>();
        ArrayList<int[]> partialChordList = new ArrayList<int[]>();
        int[] lastChord = null;

        try {

            Tokenizer tokenizer = new Tokenizer(new FileInputStream(filename));



            Object temp = tokenizer.nextSexp();
            while( !(temp instanceof EOF) ){

                if(temp instanceof String){
                    String strToken = (String)temp;
                    char firstChar = strToken.charAt(0);
                    if (Character.isUpperCase(firstChar)) { //Check for chord symbols
                        
                        char secondChar = '_';  // a dummy value
                        String root = "";
                        String type = "";

                        if(strToken.length() > 1){
                            secondChar = strToken.charAt(1);
                        }

                        if (firstChar == 'N' && secondChar == 'C'){
                            root = "NC";
                            type = "NC";
                        }else if (secondChar == '#' || secondChar == 'b') {
                            root = strToken.substring(0, 2);
                            type = strToken.substring(2);
                        } else  {
                            root = strToken.substring(0, 1);

                            if(strToken.length() > 1){
                                type = strToken.substring(1);
                            }
                        }

                        int[] chord = getChord(root, type);
                        lastChord = chord;
                        partialChordList.add(chord);

                    }else if(strToken.equals("/")){
                        partialChordList.add(lastChord);
                    }


                    if(strToken.equals("|")){
                        int numChords = partialChordList.size();

                        //Only supports certain even chord divisions, i.e.  Constants.WHOLE/(numChords*Constants.RESOLUTION_SCALAR)
                        //must be a whole number
                        for (int j = 0; j < numChords; j++) {
                            for (int i = 0; i < Constants.WHOLE/(numChords*Constants.RESOLUTION_SCALAR); i++) {
                                chords.add(partialChordList.get(j));
                            }
                        }
                        partialChordList = new ArrayList<int[]>();

                    }


                }


                temp = tokenizer.nextSexp();
            } // end of EOF while

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }

        int[] output = new int[0];

        while(chords.size() > 0){
            output = appendArrays(output, chords.remove(0));
        }

        return output;
    } // end of method parseLeadSheetChords


    public static int[] parseLeadSheetChords(File file) {
        return parseLeadSheetChords(file.getAbsolutePath());
    }

    /*
    public static int[] constructNote(int duration, int midiValue){

        // (notes in an octave + attack & rest) * number of octaves * length of the note
        int[] newNote = new int[((12)*4+2)*duration];
        newNote[0] = 1; // set the attack bit

        if(midiValue == -1){  // if it is a rest
            newNote[1] = 1;  //TODO: set ALL THE RESTS

            for(int i = 0; i < duration; i++){
                newNote[i * ((12)*4 + 2) + 1] = 1;
            }

        }else{


            int octave = midiValue / 12;
            int noteValue = midiValue % 12;
            int octavePosition = octave - 4; // we use octaves 4,5,6,7 (occupying positions 0,1,2,3)

            for(int i = 0; i < duration; i++){
                System.arraycopy(notes[noteValue], 0, newNote, (12*octavePosition+2)+i*(12*4 +2), notes[noteValue].length);
            }

        }
        return newNote;
    } // end of method constructNote
*/

    public static int[] constructNoteOctaveForm(int duration, int midiValue) {

        int[] newNote = new int[(12+2+4)*duration];
        int octavePosition;

        if(midiValue == -1) { //if rest
            newNote[1] = 1;
            octavePosition = 1;
            System.arraycopy(octaves[1], 0, newNote, (1+1+12), octaves[1].length); //use octave 1 as default

        } else {

            int octave = midiValue / 12;
            int noteValue = midiValue % 12;
            octavePosition = octave - 4; // we use octaves 4,5,6,7 (occupying positions 0,1,2,3)

            System.arraycopy(notes[noteValue], 0, newNote, 1+1, notes[noteValue].length);
            System.arraycopy(octaves[octavePosition], 0, newNote, (1+1+12), octaves[octavePosition].length);
        }
        /*
            Code to add sustain notes, uses one-hot encoding
        */
        /*
        for (int i = 1; i < duration; i++) {
            newNote[i*(4+1+1+12)] = 1; //set sustained bit
            System.arraycopy(octaves[octavePosition], 0, newNote, (1+1+12)+i*(1+1+12+4), octaves[octavePosition].length);
        }
        */
        /*
            Code to add sustain notes without one-hot encoding (pass in note values for sustain notes)
        */
        
        for (int i = 1; i < duration; i++) {
            newNote[i*(4+1+1+12)] = 1;
            if(midiValue != -1)
            {
                System.arraycopy(notes[midiValue % 12], 0, newNote, 1+1+i*(1+1+12+4), notes[midiValue % 12].length);
            }
            else
            {
                newNote[i*(4+1+1+12)+1] = 1;
            }
            /*
            //print out sustain note data
            for(int x = i*(1+1+12+4); x < (i+1)*(1+1+12+4); x ++)
            {
                System.out.print(newNote[x]);
            }
            */
            System.out.println();
            System.arraycopy(octaves[octavePosition], 0, newNote, (1+1+12)+i*(1+1+12+4), octaves[octavePosition].length);
        }

        return newNote;
    }

    public static int[] appendArrays(int[] array1, int[] array2){
        int[] newArray = new int[array1.length + array2.length];

        System.arraycopy(array1, 0, newArray, 0, array1.length);
        System.arraycopy(array2, 0, newArray, array1.length, array2.length);

        return newArray;
    }


    public static int[] getChord(String root, String chordType) {
        int[] baseChord = CHORD_TYPES.get(chordType);
        return transposeChord(baseChord, DISTANCES_FROM_C.get(root));
    }


    public static int[] transposeChord(int[] chord, int dist) {
        int[] newChord = new int[chord.length];
        for (int i = 0; i < chord.length; i++) {
            if (chord[i] == 1) {
                newChord[(i + dist) % NUM_PITCHES] = 1;
            }
        }
        return newChord;
    }

   public static void writeLeadSheet(DataVessel data, int rowsPerBeat, String filename) {
       try
       {
            BufferedWriter outWriter = new BufferedWriter(new FileWriter(new File(filename)));

            //set style
            outWriter.write("(section (style no-style-but-swing))");
            outWriter.newLine();

            int[] melody = data.getMelody();
            int numRows = data.getNumRows();
            int numCols = data.getNumCols();

            int duration = 1;
            int[] beat = new int[numCols];
            System.arraycopy(melody, 0, beat, 0, numCols);
            if (beat[0] == 1) {
                System.err.println("ERROR: first beat of bit-vector sustained");
                beat[0] = 0;
                beat[1] = 1; //Set error to rest
            }
            String note = getNote(beat);

            for (int i=1; i<numRows; ++i)
            {
                System.arraycopy(melody, i*numCols, beat, 0, numCols);
                if (beat[0]==1)
                    duration++;
                else
                {
                    outWriter.write(note+getDuration(duration,rowsPerBeat)+" ");
                    note = getNote(beat);
                    duration = 1;
                }
            }
            outWriter.write(note+getDuration(duration,rowsPerBeat)+" ");
            outWriter.newLine();

            // find the chords
            int[] chords = data.getChords();
            int[] temp = new int[Params.NUM_CHORD_COLUMNS];


            ArrayList<String> chordHolder = new ArrayList<String>();
            ArrayList<Integer> chordCountHolder = new ArrayList<Integer>();
            String previousSymbol = "";

            for(int row = 0;row < numRows; row++){
                System.arraycopy(chords, row*Params.NUM_CHORD_COLUMNS, temp, 0, Params.NUM_CHORD_COLUMNS);

                String type = CHORD_TYPES.get(temp);
                int offset = 0;

                // transpose until we hit the C chord, giving us the type and the distance to the symbol we need
                while(type == null)
                {
                    offset += 1;
                    if (offset >= 12) {
                        System.err.println("Invalid chord, using NC");
                        type = "NC";
                        break;
                    }
                    type = CHORD_TYPES.get( transposeChord(temp, offset));
                }


                String symb;
                if (type.equals("NC")) {
                    symb = "NC";
                } else {
                    symb = DISTANCES_TO_C.get( Math.abs(offset - 12) % 12  );
                    symb = symb + type;
                }

                // if we have seen this one previously, increment its count
                if(symb.equals(previousSymbol)){
                    int lastIndex = chordCountHolder.size() - 1;
                    int prevcount = chordCountHolder.get( lastIndex );
                    prevcount += 1;
                    chordCountHolder.set( lastIndex , prevcount);
                }else{
                    previousSymbol = symb;
                    chordHolder.add(symb);
                    chordCountHolder.add(1);
                }



                if(row != 0 && ( (row + 1) % (rowsPerBeat * 4) ) == 0 ){

                    // find the minimum count
                    int min = chordCountHolder.get(0);
                    for(int testmin: chordCountHolder){
                        if(testmin < min){
                            min = testmin;
                        }
                    }

                    // divide all the counts by the minimum count, giving us the
                    // number of times we need to play each chord
                    for(int i = 0; i < chordCountHolder.size(); i++){
                        chordCountHolder.set(i, chordCountHolder.get(i) / min);
                    }

                    // play each chord for the right number of times
                    for(int i = 0; i < chordHolder.size(); i++){
                        for(int j = 0; j < chordCountHolder.get(i); j++){
                            outWriter.write( chordHolder.get(i) + " "  );
                        }
                    }


                    // close the measure and reset all variables
                    outWriter.write(" | ");
                    outWriter.newLine();
                    previousSymbol = "";
                    chordHolder = new ArrayList<String>();
                    chordCountHolder = new ArrayList<Integer>();
                }

            }

            outWriter.close();
       }catch(Exception e){
           e.printStackTrace();
       }

    } // end of method writeLeadSheet


    private static String getNote(int[] beat) {
        //account for rests
        if (beat[1]==1)
            return "r";

        String[] octaveString = {"-","", "+", "++"};
        String[] noteString = {"c","c#","d","d#","e","f","f#","g","g#","a","a#","b"};
        int octave;
        for (octave = 0; octave < 4; octave++) {
            if (beat[(1+1+12)+octave] == 1) break;
        }
        for (int note = 0; note < 12; ++note)
            if (beat[(1+1) + note]==1)
                return noteString[note]+octaveString[octave];

        return null;
    }

    private static String getDuration(int duration, int divisionsPerBeat)
    {
        //For simple durations shorter than one beat
        if (divisionsPerBeat % duration == 0)
        {
            switch (divisionsPerBeat/duration)
            {
                case 12:
                    return "32/3";
                case 6:
                    return "16/3";
                case 4:
                    return "16";
                case 3:
                    return "8/3";
                case 2:
                    return "8";
                case 1:
                    return "4";
                default:
                    System.err.println("something's wrong in getDuration...");
            }

        //For simple durations longer than one beat
        } else if (duration % divisionsPerBeat == 0) {
            switch (duration/divisionsPerBeat)
            {
                case 1:
                    return "4";
                case 2:
                    return "2";
                case 3:
                    return "2.";
                case 4:
                    return "1";
                default:
                    return "1"+"+"+getDuration(duration-4*divisionsPerBeat, divisionsPerBeat);
            }

        //For unplesent durations: recurse!
        } else return getDuration(1, divisionsPerBeat)+"+"+getDuration(duration-1, divisionsPerBeat);
        return null;
    }

    public static void main(String[] args){
        DataVessel data = rbm.FileParser.parseFile("0001.ls");
        //System.out.println(data);
        writeLeadSheet(data, 12, "temp.ls");

    }


}  // end of class LeadSheetHandler


