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
 * Impro-Visor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * merchantability or fitness for a particular purpose.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Impro-Visor; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package cluster;
import java.util.List;
import java.util.ArrayList;


/**
 *
 * @author Peter Swire
 */

public class Cluster {


/**
 * This class represents a Cluster in a Cluster Analysis Instance. A Cluster is associated
 * with one and only one JCA Instance. A Cluster is related to more than one DataPoints and
 * one centroid.
 * @author Original author: Shyam Sivaraman
 * @version 1.1
 * @see DataPoint
 * @see Centroid
 */
    private String mName;
    private Centroid mCentroid; //will be set by calling setCentroid()
    private double mSumSqr;
    private List<DataPoint> mDataPoints = new ArrayList<DataPoint>();

    public Cluster(String name) {
        mName = name;
    }

    
    // deep copy constructor
    public Cluster(Cluster copy){
        this.mName = new String(copy.getName());
        this.mCentroid = new Centroid(copy.getCentroid());

        List<DataPoint> dps = copy.getDataPoints();

        for(DataPoint dp: dps){
            this.mDataPoints.add(new DataPoint(dp));
        }
    }
    

    public void setCentroid(Centroid c) {
        mCentroid = c;
    }

    public Centroid getCentroid() {
        return mCentroid;
    }

    public void addDataPoint(DataPoint dp) { //called from CAInstance
        dp.setCluster(this); //initiates a inner call to calcEuclideanDistance() in DP.
        mDataPoints.add(dp);
        calcSumOfSquares();
    }

    public void removeDataPoint(DataPoint dp) {
        mDataPoints.remove(dp);
        calcSumOfSquares();
    }

    public int getNumDataPoints() {
        return mDataPoints.size();
    }

    public DataPoint getDataPoint(int pos) {
        return mDataPoints.get(pos);
    }

    public void calcSumOfSquares() { //called from Centroid
        double temp = 0;
        for (DataPoint dp : mDataPoints) {
            temp += dp.getCurrentEuDt();
        }
        mSumSqr = temp;
    }

    public double getSumSqr() {
        return mSumSqr;
    }

    public String getName() {
        return mName;
    }

    public List<DataPoint> getDataPoints() {
        return mDataPoints;
    }

    @Override
    public boolean equals(Object obj){
        Cluster c;
        if( obj instanceof Cluster){
            c = (Cluster)obj;
        }else{
            return false;
        }

        boolean equality = true;

        if( this.getNumDataPoints() != c.getNumDataPoints()){
            equality = false;
        }else{
            for(int i = 0; i < this.getNumDataPoints(); i++){
                if( this.getDataPoint(i).getId() != c.getDataPoint(i).getId()){
                    equality = false;
                    break;
                }
            }
        }

        return equality;
    }



    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();

        sb.append("Cluster: " + this.mName);
        //sb.append("\nCentroid:" + this.mCentroid);
        sb.append("\nDataPoints in Cluster: --\n");

        for(int i = 0; i < this.mDataPoints.size(); i++){
            sb.append(this.mDataPoints.get(i).getId() + " ");
        }

        return sb.toString();
    }
}

