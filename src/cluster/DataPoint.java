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


package cluster;

/**
 *
 * @author Peter Swire
 */
/**
    This class represents a candidate for Cluster analysis. A candidate must have
    a name and two independent variables on the basis of which it is to be clustered.
    A Data Point must have two variables and a name. A List of  Data Point object
    is fed into the constructor of the JCA class. JCA and DataPoint are the only
    classes which may be available from other packages.
    @author original author: Shyam Sivaraman
    @version 1.0
    @see JCA
    @see Cluster
*/

public class DataPoint {
    private float[] vector;
    private int id;
    private Cluster mCluster;
    private double mEuDt;

    public DataPoint(float[] vector, int id) {
        this.vector = vector;
        this.id = id;
    }


    // copy constructor
    public DataPoint(DataPoint copy){
        int length = copy.getVector().length;
        this.vector = new float[length];
        System.arraycopy(copy.getVector(), 0, this.vector, 0, length);

        this.id = copy.getId();
    }

    public void setCluster(Cluster cluster) {
        mCluster = cluster;
        calcEuclideanDistance();
    }

    public void calcEuclideanDistance() {

        //called when DP is added to a cluster or when a Centroid is recalculated.
        double dist = 0;
        float[] mvector = mCluster.getCentroid().getMVector();

        for(int i = 0; i < mvector.length; i++){
            dist += Math.pow(this.vector[i] - mvector[i], 2);
        }

        this.mEuDt = Math.sqrt(dist);
    }

    public double testEuclideanDistance(Centroid c) {
        float[] cenVec = c.getMVector();
        double dist = 0;

        for(int i = 0; i < cenVec.length; i++){
            dist += Math.pow(this.vector[i] - cenVec[i], 2);
        }
        
        return Math.sqrt(dist);
    }

    public float[] getVector(){
        return this.vector;
    }

    public Cluster getCluster() {
        return mCluster;
    }

    public double getCurrentEuDt() {
        return mEuDt;
    }

    public int getId(){
        return this.id;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();

        sb.append("---------\nDataPoint\n");
        sb.append("Id: " + this.id);
        sb.append("\nValue:\n");

        for(int i = 0; i < this.vector.length; i++){
            sb.append(this.vector[i] + " ");
        }

        sb.append("\n---------\n");

        return sb.toString();
    }

}
