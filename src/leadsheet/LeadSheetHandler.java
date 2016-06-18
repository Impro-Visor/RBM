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

import encoding.Encoding;
import encoding.NoteEncodings;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import polya.*;
import rbm.DataVessel;
import rbm.Params;

/**
 *
 * @author Peter Swire
 */
public class LeadSheetHandler implements Constants {


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

        int numChordRows = currChords.length / Encoding.NUM_CHORD_COLUMNS;
        int melRows = currMelody.length / Encoding.NUM_NOTE_COLUMNS;

        if (numChordRows != melRows) {
                int[] temp = new int[melRows * Encoding.NUM_CHORD_COLUMNS];
                for (int i = 0; i < temp.length / currChords.length; i++) {
                    System.arraycopy(currChords, 0, temp, i*currChords.length, currChords.length);
                }
                System.arraycopy(currChords, 0, temp,
                        (temp.length / currChords.length)*currChords.length, temp.length % currChords.length);
                currChords = temp;
            }

        return new DataVessel(currMelody, currChords, melRows, Encoding.NUM_NOTE_COLUMNS);
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
                    melody.add(constructNote(duration, midiValue));
                    
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
    /**
     * Constructs a note according to value of the encoding parameter in Params class
     * @param duration Duration of the note in time steps
     * @param midiValue The midi-value of the note
     * @return The given note in array form
     */
    public static int[] constructNote(int duration, int midiValue)
    {
        switch(Params.noteEncoding){
            case Sequential: return constructNote(duration, midiValue, NoteEncodings.sequentialNotes);
            case CirclesOfThirds: return constructNote(duration, midiValue, NoteEncodings.circlesNotes, NoteEncodings.octaves);
            case ChromaticOneHot: //Octave form will be default
            default: return constructNote(duration, midiValue, NoteEncodings.chromaticNotes, NoteEncodings.octaves);
        }
    }
    
    /**
     * Constructs a note without data from a separate octave array
     * @param duration duration of the note in time steps
     * @param midiValue the midi-value of the note
     * @param notesArray the two-dimensional array of note-encodings, starting with c and going up in half-steps
     * @return the given note in array form!
     */
    public static int[] constructNote(int duration, int midiValue, int[][] notesArray)
    {
        return constructNote(duration, midiValue, notesArray, null);
    }
    
    public static int[] constructNote(int duration, int midiValue, int[][] notesArray, int[][] octavesArray)
    {
        //establish sizes of portions of note
        int pitchSize = notesArray[0].length;
        int octaveSize = ((octavesArray != null) ? octavesArray[0].length : 0);
        //constructs notes based on first row's length, constructs octaves based on first octave encoding's length
        int noteSize = (2+pitchSize + octaveSize);
        int length = noteSize * duration;
        int[] newNote = new int[length];
        
        //if we have a rest
        if(midiValue == -1) {
            newNote[1] = 1;
            
            //fill in sustain with rest
            for(int i = noteSize; i < length; i+=noteSize)
            {
                newNote[i] = 1;
                newNote[i+1] = 1; 
            }
        } else { //if we have a real note
            //get our noteValue from input notesArray
            int noteValue = midiValue % notesArray.length;
            //copy note value to first slot
            System.arraycopy(notesArray[noteValue], 0, newNote, 1+1, notesArray[noteValue].length);
            //if we had input an octaves array, we'll fill in octave slots with the given octave (of 4,5,6,7)
            if(octavesArray != null)
            {
                int octaveValue = (midiValue / 12) - 4; // we use octaves 4,5,6,7 (occupying positions 0,1,2,3)
                System.arraycopy(octavesArray[octaveValue], 0, newNote, 1+1+pitchSize, octavesArray[octaveValue].length);
            }
            //fill in sustain bits with same algo, just with i added to indexes
            for(int i = noteSize; i < length; i+=noteSize)
            {
                newNote[i] = 1; 
                System.arraycopy(notesArray[noteValue], 0, newNote, i+1+1, notesArray[noteValue].length);
                if(octavesArray != null)
                {
                    int octaveValue = (midiValue / 12) - 4; // we use octaves 4,5,6,7 (occupying positions 0,1,2,3)
                    System.arraycopy(octavesArray[octaveValue], 0, newNote, i+1+1+pitchSize, octavesArray[octaveValue].length);
                }
            }
        }
        return newNote;
    }
    
/*  
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
        /*
        for (int i = 1; i < duration; i++) {
            newNote[i*(4+1+1+12)] = 1;
            if(midiValue != -1)
            {
                System.arraycopy(Encoding.chromaticNotes[midiValue % 12], 0, newNote, 1+1+i*(1+1+12+4), Encoding.chromaticNotes[midiValue % 12].length);
            }
            else
            {
                newNote[i*(4+1+1+12)+1] = 1;
            }
            
            //System.out.println();
            System.arraycopy(Encoding.octaves[octavePosition], 0, newNote, (1+1+12)+i*(1+1+12+4), Encoding.octaves[octavePosition].length);
        }

        return newNote;
    }
*/
    public static int[] appendArrays(int[] array1, int[] array2){
        int[] newArray = new int[array1.length + array2.length];

        System.arraycopy(array1, 0, newArray, 0, array1.length);
        System.arraycopy(array2, 0, newArray, array1.length, array2.length);

        return newArray;
    }


    public static int[] getChord(String root, String chordType) {
        int[] baseChord = Encoding.CHORD_TYPES.get(chordType);
        return transposeChord(baseChord, Encoding.DISTANCES_FROM_C.get(root));
    }


    public static int[] transposeChord(int[] chord, int dist) {
        int[] newChord = new int[chord.length];
        for (int i = 0; i < chord.length; i++) {
            if (chord[i] == 1) {
                newChord[(i + dist) % Encoding.NUM_PITCHES] = 1;
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
            //System.out.println("Rows: " + numRows + " Columns: " + numCols);

            int duration = 1;
            int[] beat = new int[numCols];
            
            System.arraycopy(melody, 0, beat, 0, numCols);
            if (beat[0] == 1) {
                System.err.println("ERROR: first beat of bit-vector sustained");
                beat[0] = 0;
                beat[1] = 1; //Set error to rest
            }
            String note = decodeNote(beat);

            for (int i=1; i<numRows; ++i)
            {
                System.arraycopy(melody, i*numCols, beat, 0, numCols);
                if (beat[0]==1)
                    duration++;
                else
                {
                    outWriter.write(note+getDuration(duration,rowsPerBeat)+" ");
                    note = decodeNote(beat);
                    duration = 1;
                }
            }
            outWriter.write(note+getDuration(duration,rowsPerBeat)+" ");
            outWriter.newLine();

            // find the chords
            int[] chords = data.getChords();
            int[] temp = new int[Encoding.NUM_CHORD_COLUMNS];


            ArrayList<String> chordHolder = new ArrayList<String>();
            ArrayList<Integer> chordCountHolder = new ArrayList<Integer>();
            String previousSymbol = "";

            for(int row = 0;row < numRows; row++){
                System.arraycopy(chords, row*Encoding.NUM_CHORD_COLUMNS, temp, 0, Encoding.NUM_CHORD_COLUMNS);

                String type = Encoding.CHORD_TYPES.get(temp);
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
                    type = Encoding.CHORD_TYPES.get( transposeChord(temp, offset));
                }


                String symb;
                if (type.equals("NC")) {
                    symb = "NC";
                } else {
                    symb = Encoding.DISTANCES_TO_C.get( Math.abs(offset - 12) % 12  );
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

//DEPRECATED, USE DECODE NOTE
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

        return "r";
    }
    
    private static String getOctaveNote(int[] beat)
    {
        if (beat[1]==1)
            return "r";
        String[] octaveStrings = {"-","", "+", "++"};
        String[] noteStrings = {"c","c#","d","d#","e","f","f#","g","g#","a","a#","b"};
        int[] pitch = Arrays.copyOfRange(beat, 1+1, 1+1+ NoteEncodings.chromaticNotes[0].length);
        int[] octave = Arrays.copyOfRange(beat, 1+1+pitch.length, 1+1+pitch.length+ NoteEncodings.chromaticNotes[0].length);
        int note = 0;
        for(/*^ pre init*/; note < NoteEncodings.chromaticNotes.length; note++)
        {
            if(Arrays.equals(NoteEncodings.chromaticNotes[note], pitch))
                break;
        }
        for(int i = 0; i < NoteEncodings.octaves.length; i++)
        {
            if(Arrays.equals(NoteEncodings.octaves[i], octave))
                return noteStrings[note] + octaveStrings[i];
        }
        System.out.println("WARNING: notes unrecognized, encoding problem");
        return "r";
    }
    
    private static String getSequentialNote(int[] beat)
    {
        if (beat[1]==1)
            return "r";
        String[] octaveString = {"-","", "+", "++"};
        String[] noteString = {"c","c#","d","d#","e","f","f#","g","g#","a","a#","b"};
        int[] pitch = Arrays.copyOfRange(beat, 1+1, 1+1+NoteEncodings.sequentialNotes[0].length);
        for(int note = 0; note < NoteEncodings.sequentialNotes.length; note++)
        {
            if(Arrays.equals(NoteEncodings.sequentialNotes[note], pitch))
                return noteString[note%noteString.length] + octaveString[((note >= noteString.length) ? 1 : 2)]; //overlap second half of sequential notes and use octaves two and three
        }
        System.out.println("WARNING: notes unrecognized, encoding problem");
        return "r";
    }
    
    private static String getCircleNote(int[] beat)
    {
        
        if (beat[1]==1)
            return "r";
        String[] octaveStrings = {"-","", "+", "++"};
        String[] noteStrings = {"c","c#","d","d#","e","f","f#","g","g#","a","a#","b"};
        //System.out.println("Full beat " + Arrays.toString(beat));
        int[] pitch = Arrays.copyOfRange(beat, 1+1, 1+1+NoteEncodings.circlesNotes[0].length);
        //System.out.println("Pitch " + Arrays.toString(pitch));
        int[] octave = Arrays.copyOfRange(beat, 1+1+pitch.length, 1+1+pitch.length+NoteEncodings.octaves[0].length);
        int note = 0;
        for(/*^ pre init*/; note < NoteEncodings.circlesNotes.length; note++)
        {
            
            if(Arrays.equals(NoteEncodings.circlesNotes[note], pitch))
                break;
        }
        if(note >= NoteEncodings.circlesNotes.length)
            System.out.println("WARNING: notes unrecognized, encoding problem");
        for(int i = 0; i < NoteEncodings.octaves.length; i++)
        {
            if(Arrays.equals(NoteEncodings.octaves[i], octave))
                return noteStrings[note] + octaveStrings[i];
        }
        
        return "r";
    }
    
    private static String decodeNote(int[] beat) {
        switch(Params.noteEncoding) {
            case Sequential: return getSequentialNote(beat);
            case CirclesOfThirds: return getCircleNote(beat);
            case ChromaticOneHot: 
            default: return getOctaveNote(beat);
        }
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


