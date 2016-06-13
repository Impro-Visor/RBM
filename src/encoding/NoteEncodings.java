/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package encoding;
import java.util.function.*;

/**
 *
 * @author cssummer16
 */
public enum NoteEncodings {
    ChromaticOneHot, Sequential, CirclesOfThirds;
    
    
    /* Declare/initialize methods and data for getting length of encodings and their groupings here. */
    public int getLeadSheetMelodyColumns()
    {
        switch(this)
        {
            //all encodings have bits for rest and sustain, a number of bits for pitch, and may have 4 octave bits
            case Sequential: return  1+1+24;
            case CirclesOfThirds: return 1+1+7+4;
            case ChromaticOneHot:
            default: return 1+1+12+4;
        }
    }
    
    
    
    public int[] getGroupLengths()
    {
        switch(this)
        {
            case Sequential:        return getGroupLengths(sequentialGroups);
            /*  Circles of thirds involves two groups for pitch, 
                and so we'll separate the two pitch groups from
                sustain and rest bits, accomodating for errors 
                in leadsheet writing. */
            case CirclesOfThirds:   return getGroupLengths(circlesGroups); 
            case ChromaticOneHot:
            default:                return getGroupLengths(chromaticGroups);
        }
    }
    
    private int[] getGroupLengths(Group[] groupArray)
    {
        int[] intArray = new int[groupArray.length];
        for(int i = 0; i < intArray.length; i++)
        {
            intArray[i] = groupArray[i].endIndex - groupArray[i].startIndex;
        }
        return intArray;
    }
    
    public Group[] getGroups()
    {
        switch(this)
        {
            case Sequential:        return sequentialGroups;
            case CirclesOfThirds:   return circlesGroups;
            case ChromaticOneHot:
            default:    return chromaticGroups;
        }
    }
   
    //singleton group configurations contain index positions relative to start of note encoding, used to describe final activation process for input RBM  
    public static final  Group[] sequentialGroups = new Group[]{new Group(0,2,false), new Group(2,24,true)};
    public static final  Group[] circlesGroups = new Group[]{new Group(0,2,false), new Group(2,6,true), new Group(6,9,true), new Group(9,13, true)};
    public static final  Group[] chromaticGroups = new Group[]{new Group(0,2,false), new Group(2,14,true), new Group(14,18,true)};
    
    /*
        Declare/initialize note encoding data here.
    */
    public static int[][] chromaticNotes = {
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

    //one hot encoding for octaves
    public static int[][] octaves = {
        {1, 0, 0, 0},
        {0, 1, 0, 0},
        {0, 0, 1, 0},
        {0, 0, 0, 1}
    };
    
    //sequentialNotes are just chromatic notes for two octaves, meant to be encoded without octave bits on a limited range
    public static int[][] sequentialNotes = {
        {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, //C
        {0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1} //B
    };
    
    //circles of thirds encoding for 12 chromatic notes as described in Judy A. Garland's paper
    //first four bits represent the major third the note is in
    //last 3 bits represent the minor third the note is in
    public static int[][] circlesNotes = {
        {1, 0, 0, 0, 1, 0, 0}, //C
        {0, 1, 0, 0, 0, 1, 0},
        {0, 0, 1, 0, 0, 0, 1},
        {0, 0, 0, 1, 1, 0, 0},
        {1, 0, 0, 0, 0, 1, 0},
        {0, 1, 0, 0, 0, 0, 1},
        {0, 0, 1, 0, 1, 0, 0},
        {0, 0, 0, 1, 0, 1, 0},
        {1, 0, 0, 0, 0, 0, 1},
        {0, 1, 0, 0, 1, 0, 0},
        {0, 0, 1, 0, 0, 1, 0},
        {0, 0, 0, 1, 0, 0, 1}  //B
    };
}
