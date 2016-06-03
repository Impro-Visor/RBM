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

import java.util.Random;

public class DataGenerator
{

    /**
     *
     * @param numBits - the number of bits of data to generate
     * @return an array of numBits integers assigned the value 0
     */
    public static int[] zeros(int numBits)
    {
        int[] data = new int[numBits];
        for (int i=0; i<numBits; ++i)
            data[i] = 0;
        return data;
    }

    /**
     *
     * @param numBits - the number of bits of data to generate
     * @return an array of numBits integers assigned the value 1
     */
    public static int[] ones(int numBits)
    {
        int[] data = new int[numBits];
        for (int i=0; i<numBits; ++i)
            data[i] = 1;
        return data;
    }

    /**
     *
     * @param numBits - the number of bits of data to generate
     * @return an array of numBits integers randomly assigned the value 0 or 1
     */
    public static int[] randomData(int numBits)
    {
        int[] data = new int[numBits];
        for (int i=0; i<numBits; ++i)
            data[i] = getBit();
        return data;
    }

    /**
     *
     * @param numBits - the number of bits of melody data to generate
     * @param chord - an array of chord bits for the generated data to use
     * @return - a new data vessel with the given chord data and random melody data.
     */
    public static DataVessel chordData(int numBits, int[] chord)
    {
        int[] melodyArray = randomData(numBits);
        int numRows = chord.length/Params.NUM_CHORD_COLUMNS;
        int numCols = numBits/numRows;
        return new DataVessel(melodyArray, chord, numRows, numCols);
    }

    /**
     *
     * @param numChordBits - number of chord bits to generated
     * @param melody - an array of melody bits for the generated data to use
     * @return - a new DataVessel with the given melody data and random chord data.
     */
    public static DataVessel melodyData(int numChordBits, int[] melody) {
        int[] chordArray = randomData(numChordBits);
        int numRows = chordArray.length/Params.NUM_CHORD_COLUMNS;
        int numCols = melody.length/numRows;
        return new DataVessel(melody, chordArray, numRows, numCols);
    }

    /**
     *
     * @return an integer randomly assigned the value 0 or 1
     */
    private static int getBit()
    {
        Random rand = new Random();
        if (rand.nextBoolean())
            return 1;
        else
            return 0;
    }
}
