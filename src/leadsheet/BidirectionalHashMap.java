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

import java.util.HashMap;


/**
 * This class is used for going from chord arrays to their symbols and back.
 *
 * Chord arrays are converted to strings when used as keys.
 *
 * @author Peter Swire
 */

public class BidirectionalHashMap{
    private HashMap<String, String> SSMap;
    private HashMap<String, int[]> SIMap;


    // constructor
    public BidirectionalHashMap(){
        this.SSMap = new HashMap<String, String>();
        this.SIMap = new HashMap<String, int[]>();
    }

    
    public void put(String key, int[] value){
        String joined = intArrayToString(value);
        SIMap.put(key, value);
        SSMap.put(joined, key);
    }

    public int[] getIntArray(String key){
        return SIMap.get(key);
    }

    public String getSymbol(int[] array){
        String joined = intArrayToString(array);
        return SSMap.get(joined);
    }


    public int[] get(String key){
        return SIMap.get(key);
    }

    public String get(int[] array){
        String joined = intArrayToString(array);
        return SSMap.get(joined);
    }


    /**
     * join a 1d array to a string for use as a key
     *
     * @param array
     * @return
     */
    private String intArrayToString(int[] array){
        StringBuilder output = new StringBuilder();

        for(int token: array){
            output.append("" + token + " ");  // todo: no space at end
        }

        return output.toString().trim();
    }

} // end of class hashmap
