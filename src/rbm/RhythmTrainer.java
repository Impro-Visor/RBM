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
public class RhythmTrainer {

    private static final int WHOLE            = 48;
    private static final int HALF             = 24;
    private static final int QUARTER          = 12;
    private static final int QUARTER_TRIP     = 8;
    private static final int EIGHTH           = 6;
    private static final int EIGHTH_TRIP      = 4;
    private static final int SIXTEENTH        = 3;
    private static final int SIXTEENTH_TRIP   = 2;
    private static final int[] DURATIONS = {WHOLE, HALF, QUARTER, QUARTER_TRIP, EIGHTH, EIGHTH_TRIP, SIXTEENTH, SIXTEENTH_TRIP};

    public static int[] addRhythmTrack(int[] data)
    {
        ArrayList<int[]> tracks = new ArrayList<int[]>();
        int[] currTrack;
        for (int duration: DURATIONS)
        {
            int numBits = 1+((data.length-1)/duration);
            currTrack = new int[numBits];
            currTrack[0] = data[0];
            for (int i=1; i<numBits; i++) {
                currTrack[i] = data[i*duration];
            }
            tracks.add(currTrack);
        }

        for(int[] temp : tracks){
            data = appendArrays(data, temp);
        }
        return data;
    }

    public static int[] convertToRhythm(DataVessel d) {
       int numRows = d.getNumRows();
       int numCols = d.getNumCols();

       int[] oldMelody = d.getMelody();
       int[] rhythm = new int[numRows];

       for (int i = 0; i < numRows; i++) {
           if (oldMelody[i*numCols] == 1) {
               rhythm[i] = 1;
           }
       }
       return rhythm;
    }

    private static int[] appendArrays(int[] a, int[] b)
    {
        int[]out = new int[a.length+b.length];
        System.arraycopy(a, 0, out, 0, a.length);
        System.arraycopy(b, 0, out, a.length, b.length);
        return out;
    }

}
