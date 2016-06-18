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

/**
 * @author Peter Swire
 * class DataVessel
 * 
 */
package rbm;
import java.io.Serializable;
import encoding.Encoding;


public class DataVessel implements Serializable {

    static final long serialVersionUID = 4616631492914999515L;

    private int melody[];
    private int chords[];
    private int numMelodyRows;
    private int numMelodyCols;

    /*
     * Constructor for DataVessel
     */
    public DataVessel(int[] melody, int[] chords, int numRows, int numCols) {
        this.numMelodyRows = numRows;
        this.numMelodyCols = numCols;
        this.melody = melody;
        this.chords = chords;
    }
    
    public int getNumChordRows()
    {
        return chords.length / Encoding.NUM_CHORD_COLUMNS;
    }
    
    public int getNumChordColumns()
    {
        return Encoding.NUM_CHORD_COLUMNS;
    }


    /*
     * Copy constructor
     */
    public DataVessel(DataVessel dv){
        this.numMelodyRows = dv.getNumRows();
        this.numMelodyCols = dv.getNumCols();

        this.melody = new int[dv.getMelodySize()];
        System.arraycopy(dv.melody, 0, this.melody, 0, dv.getMelodySize());

        this.chords = new int[dv.getChordsSize()];
        System.arraycopy(dv.chords, 0, this.chords, 0, dv.getChordsSize());
    }



    @Override
    public String toString(){
        StringBuffer output = new StringBuffer();

        output.append("Number of rows: " + this.numMelodyRows);
        output.append("\nNumber of columns: " + this.numMelodyCols);
        output.append("\nMelody:");

        for(int i = 0; i < melody.length; i++){
            if(i % this.numMelodyCols == 0){
                output.append("\n");
            }
            output.append(this.melody[i] + " ");
        }

        if (chords != null)
        {
            output.append("\nChords:");
            for(int i = 0; i < chords.length; i++){
                if(i % Encoding.NUM_CHORD_COLUMNS == 0){
                    output.append("\n");
                }
                output.append(this.chords[i] + " ");
            }
        }

        return output.toString();

    } // end of method toString


    /**
     *
     * @return - a 1d array of the DataVessel's melody bits
     * followed by its chords bits
     */
    public int[] getData() {
        int[] newArray = new int[getLength()];

        if (melody != null) System.arraycopy(melody, 0, newArray, 0, melody.length);
        if (chords != null) System.arraycopy(chords, 0, newArray, melody.length, chords.length);

        return newArray;
    }

    /**
     *
     * @return - the total length of the DataVessel
     */
    public int getLength() {
        return getMelodySize() + getChordsSize();
    }


    /**
     * Getter function for the field numRows
     */
    public int getNumRows() {
        return numMelodyRows;
    }

    /**
     * Getter function for the field numCols
     */
    public int getNumCols() {
        return numMelodyCols;
    }

    /**
     * Getter function for the field melody
     */
    public int[] getMelody() {
        return melody;
    }

    /**
     * Getter function for the length of the melody field
     */
    public int getMelodySize() {
        if (melody != null) return melody.length;
        else return 0;
    }

    /**
     * Getter function for the field chords
     */
    public int[] getChords() {
        return chords;
    }

    /**
     * Getter function for the length of the chords field
     */
    public int getChordsSize() {
        if (chords != null) return chords.length;
        else return 0;
    }

    /**
     * Setter function for the field numRows
     */
    public void setNumRows(int numRows) {
        this.numMelodyRows = numRows;
    }

    /**
     * Setter function for the field numCols
     */
    public void setNumCols(int numCols) {
        this.numMelodyCols = numCols;
    }

    /**
     * Setter function for the field data
     */
    public void setMelody(int[] melody) {
        this.melody = melody;
    }

    /**
     * Setter function for the field data
     */
    public void setChords(int[] chords) {
        this.chords = chords;
    }
} // end of class DataVessel

