/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package encoding;

import java.util.HashMap;
import leadsheet.BidirectionalHashMap;
import rbm.Params;

/**
 *
 * @author cssummer16
 */
public class Encoding {
    
    public static final int NUM_CHORD_COLUMNS = 12;
    public static final int NUM_NOTE_COLUMNS = Params.noteEncoding.getLeadSheetMelodyColumns();
    
    public final static int[] NO_CHORD          = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    public final static int[] C_MAJOR           = {1, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0};
    public final static int[] C_MAJOR_7         = {1, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 1};
    public final static int[] C_MINOR_7         = {1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 0};
    public final static int[] C_DOM_7           = {1, 0, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0};
    public final static int[] C_MINOR_9         = {1, 0, 1, 1, 0, 0, 0, 1, 0, 0, 0, 0};
    public final static int[] C_13              = {1, 0, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0};
    public final static int[] C_MINOR_7_FLAT_5  = {1, 0, 0, 1, 0, 0, 1, 0, 0, 0, 1, 0};
    public final static int[] C_DOM_7_SHARP_9   = {1, 0, 0, 1, 1, 0, 0, 1, 0, 0, 1, 0};
    public final static int[] C_DOM_7_FLAT_9    = {1, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0};
    public final static int[] C_DIM_7           = {1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0};
    public final static int[] C_9               = {1, 0, 1, 0, 1, 0, 0, 1, 0, 0, 1, 0};
    public final static int[] C_MAJOR_9         = {1, 0, 1, 0, 1, 0, 0, 1, 0, 0, 0, 1};
    public final static int[] C_DOM_7_SHARP_11  = {1, 0, 0, 1, 1, 0, 0, 1, 0, 0, 1, 0};
    public final static int[] C_MAJOR_7_SHARP_11= {1, 0, 0, 1, 1, 0, 0, 1, 0, 0, 0, 1};
    public final static int[] C_6               = {1, 0, 0, 0, 1, 0, 0, 1, 0, 1, 0, 0};
    public final static int[] C_7_ALT           = {1, 0, 0, 1, 1, 0, 0, 0, 1, 0, 1, 0};
    public final static int[] NC               = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    public final static int[] C		= {1, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0};
    public final static int[] CM		= {1, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0};
    public final static int[] Cm_sharp_5	= {1, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0};
    public final static int[] Cm_plus_         = {1, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0};
    public final static int[] Cm		= {1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0};
    public final static int[] Cm11_sharp_5	= {1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 0};
    public final static int[] Cm11		= {1, 0, 1, 1, 0, 1, 0, 1, 0, 0, 1, 0};
    public final static int[] Cm11b5           = {1, 0, 1, 1, 0, 1, 1, 0, 0, 0, 1, 0};
    public final static int[] Cm13             = {1, 0, 1, 1, 0, 1, 0, 1, 0, 1, 1, 0};
    public final static int[] Cm6              = {1, 0, 0, 1, 0, 0, 0, 1, 0, 1, 0, 0};
    public final static int[] Cm69             = {1, 0, 1, 1, 0, 0, 0, 1, 0, 1, 0, 0};
    public final static int[] Cm7_sharp_5	= {1, 0, 0, 1, 0, 0, 0, 0, 1, 0, 1, 0};
    public final static int[] Cm7              = {1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 0};
    public final static int[] Cm7b5            = {1, 0, 0, 1, 0, 0, 1, 0, 0, 0, 1, 0};
    public final static int[] Ch7               = {1, 0, 0, 1, 0, 0, 1, 0, 0, 0, 1, 0};
    public final static int[] Cm9_sharp_5	= {1, 0, 1, 1, 0, 0, 0, 0, 1, 0, 1, 0};
    public final static int[] Cm9              = {1, 0, 1, 1, 0, 0, 0, 1, 0, 0, 1, 0};
    public final static int[] Cm9b5            = {1, 0, 1, 1, 0, 0, 1, 0, 0, 0, 1, 0};
    public final static int[] CmM7             = {1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1};
    public final static int[] CmM7b6           = {1, 0, 0, 1, 0, 0, 0, 1, 1, 0, 0, 1};
    public final static int[] CmM9             = {1, 0, 1, 1, 0, 0, 0, 1, 0, 0, 0, 1};
    public final static int[] Cmadd9           = {1, 0, 1, 1, 0, 0, 0, 1, 0, 0, 0, 0};
    public final static int[] Cmb6             = {1, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0};
    public final static int[] Cmb6M7           = {1, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 1};
    public final static int[] Cmb6b9           = {1, 1, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0};
    public final static int[] CM_sharp_5	= {1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0};
    public final static int[] C_plus_          = {1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0};
    public final static int[] Caug             = {1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0};
    public final static int[] C_plus_7         = {1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 1, 0};
    public final static int[] CM_sharp_5add9	= {1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 0, 0};
    public final static int[] CM7_sharp_5	= {1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1};
    public final static int[] CM7_plus_	= {1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1};
    public final static int[] CM9_sharp_5	= {1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 0, 1};
    public final static int[] C_plus_add9	= {1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 0, 0};
    public final static int[] C7               = {1, 0, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0};
    public final static int[] C7_sharp_5	= {1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 1, 0};
    public final static int[] C7_plus_         = {1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 1, 0};
    public final static int[] Caug7            = {1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 1, 0};
    public final static int[] C7aug            = {1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 1, 0};
    public final static int[] C7_sharp_5_sharp_9	= {1, 0, 0, 1, 1, 0, 0, 0, 1, 0, 1, 0};
    public final static int[] C7alt            = {1, 0, 0, 1, 1, 0, 0, 0, 1, 0, 1, 0};
    public final static int[] C7b13            = {1, 0, 0, 0, 1, 0, 0, 1, 1, 0, 1, 0};
    public final static int[] C7b5_sharp_9	= {1, 0, 0, 1, 1, 0, 0, 0, 1, 0, 1, 0};
    public final static int[] C7b5             = {1, 0, 0, 0, 1, 0, 1, 0, 0, 0, 1, 0};
    public final static int[] C7b5b13          = {1, 0, 0, 0, 1, 0, 1, 1, 1, 0, 1, 0};
    public final static int[] C7b5b9           = {1, 1, 0, 0, 1, 0, 1, 1, 0, 0, 1, 0};
    public final static int[] C7b5b9b13	= {1, 1, 0, 0, 1, 0, 1, 1, 1, 0, 1, 0};
    public final static int[] C7b6             = {1, 0, 0, 0, 1, 0, 0, 1, 1, 0, 1, 0};
    public final static int[] C7b9_sharp_11	= {1, 1, 0, 0, 1, 0, 1, 1, 0, 0, 1, 0};
    public final static int[] C7b9_sharp_11b13	= {1, 1, 0, 0, 1, 0, 1, 1, 1, 0, 1, 0};
    public final static int[] C7b9             = {1, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0};
    public final static int[] C7b9b13_sharp_11	= {1, 1, 0, 0, 1, 0, 1, 1, 1, 0, 1, 0};
    public final static int[] C7b9b13          = {1, 1, 0, 0, 1, 0, 0, 1, 1, 0, 1, 0};
    public final static int[] C7no5            = {1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0};
    public final static int[] C7_sharp_11	= {1, 0, 0, 0, 1, 0, 1, 1, 0, 0, 1, 0};
    public final static int[] C7_sharp_11b13	= {1, 0, 0, 0, 1, 0, 1, 1, 1, 0, 1, 0};
    public final static int[] C7_sharp_5b9_sharp_11	= {1, 1, 0, 0, 1, 0, 1, 0, 1, 0, 1, 0};
    public final static int[] C7_sharp_5b9             = {1, 1, 0, 0, 1, 0, 0, 0, 1, 0, 1, 0};
    public final static int[] C7_sharp_9_sharp_11	= {1, 0, 0, 1, 1, 0, 1, 1, 0, 0, 1, 0};
    public final static int[] C7_sharp_9_sharp_11b13	= {1, 0, 0, 1, 1, 0, 1, 1, 1, 0, 1, 0};
    public final static int[] C7_sharp_9	= {1, 0, 0, 1, 1, 0, 0, 1, 0, 0, 1, 0};
    public final static int[] C7_sharp_9b13	= {1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 1, 0};
    public final static int[] C9               = {1, 0, 1, 0, 1, 0, 0, 1, 0, 0, 1, 0};
    public final static int[] C9_sharp_5	= {1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 1, 0};
    public final static int[] C9_plus_         = {1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 1, 0};
    public final static int[] C9_sharp_11	= {1, 0, 1, 0, 1, 0, 1, 1, 0, 0, 1, 0};
    public final static int[] C9_sharp_11b13	= {1, 0, 1, 0, 1, 0, 1, 1, 1, 0, 1, 0};
    public final static int[] C9_sharp_5_sharp_11	= {1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0};
    public final static int[] C9b13            = {1, 0, 1, 0, 1, 0, 0, 1, 1, 0, 1, 0};
    public final static int[] C9b5             = {1, 0, 1, 0, 1, 0, 1, 0, 0, 0, 1, 0};
    public final static int[] C9b5b13          = {1, 0, 1, 0, 1, 0, 1, 1, 1, 0, 1, 0};
    public final static int[] C9no5            = {1, 0, 1, 0, 1, 0, 0, 0, 0, 0, 1, 0};
    public final static int[] C13_sharp_11	= {1, 0, 1, 0, 1, 0, 1, 1, 0, 1, 1, 0};
    public final static int[] C13_sharp_9_sharp_11	= {1, 0, 0, 1, 1, 0, 1, 1, 0, 1, 1, 0};
    public final static int[] C13_sharp_9	= {1, 0, 0, 1, 1, 0, 0, 1, 0, 1, 1, 0};
    public final static int[] C13              = {1, 0, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0};
    public final static int[] C13b5            = {1, 0, 0, 0, 1, 0, 1, 0, 0, 1, 1, 0};
    public final static int[] C13b9_sharp_11	= {1, 1, 0, 0, 1, 0, 1, 1, 0, 1, 1, 0};
    public final static int[] C13b9            = {1, 1, 0, 0, 1, 0, 0, 1, 0, 1, 1, 0};
    public final static int[] CMsus2           = {1, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0};
    public final static int[] CMsus4           = {1, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0};
    public final static int[] Csus2            = {1, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0};
    public final static int[] Csus4            = {1, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0};
    public final static int[] Csusb9           = {1, 1, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0};
    public final static int[] C7b9b13sus4	= {1, 1, 0, 0, 0, 1, 0, 1, 1, 0, 1, 0};
    public final static int[] C7b9sus          = {1, 1, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0};
    public final static int[] C7b9sus4         = {1, 1, 0, 0, 0, 1, 0, 1, 0, 0, 1, 0};
    public final static int[] C7sus            = {1, 0, 0, 0, 0, 1, 0, 1, 0, 0, 1, 0};
    public final static int[] C7sus4           = {1, 0, 0, 0, 0, 1, 0, 1, 0, 0, 1, 0};
    public final static int[] C7sus4b9         = {1, 1, 0, 0, 0, 1, 0, 1, 0, 0, 1, 0};
    public final static int[] C7sus4b9b13	= {1, 1, 0, 0, 0, 1, 0, 1, 1, 0, 1, 0};
    public final static int[] C7susb9          = {1, 1, 0, 0, 0, 1, 0, 1, 0, 0, 1, 0};
    public final static int[] C9sus4           = {1, 0, 1, 0, 0, 1, 0, 1, 0, 0, 1, 0};
    public final static int[] C9sus            = {1, 0, 1, 0, 0, 1, 0, 1, 0, 0, 1, 0};
    public final static int[] C11              = {1, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0};
    public final static int[] C13sus           = {1, 0, 1, 0, 0, 1, 0, 1, 0, 1, 1, 0};
    public final static int[] C13sus4          = {1, 0, 1, 0, 0, 1, 0, 1, 0, 1, 1, 0};
    public final static int[] CBlues           = {1, 0, 0, 1, 0, 1, 1, 1, 0, 0, 1, 0};
    public final static int[] CBass            = {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};


    public final static int NUM_PITCHES = 12;


    public final static BidirectionalHashMap CHORD_TYPES = new BidirectionalHashMap();
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



    public final static HashMap<String, Integer> DISTANCES_FROM_C = new HashMap<String, Integer>();
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
    public final static HashMap<Integer, String> DISTANCES_TO_C = new HashMap<Integer, String>();
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
}
