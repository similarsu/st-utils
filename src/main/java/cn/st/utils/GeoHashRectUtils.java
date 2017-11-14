package cn.st.utils;

import com.google.gson.Gson;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by coolearth on 17-11-13.
 */
public class GeoHashRectUtils {
    public static HashMap<Integer,String> geoHash2Bash32=new HashMap<Integer,String>(){
        {
            put(0,"B");
            put(1,"C");
            put(2,"F");
            put(3,"G");
            put(4,"U");
            put(5,"V");
            put(6,"Y");
            put(7,"Z");

            put(8,"8");
            put(9,"9");
            put(10,"D");
            put(11,"E");
            put(12,"S");
            put(13,"T");
            put(14,"W");
            put(15,"X");

            put(16,"2");
            put(17,"3");
            put(18,"6");
            put(19,"7");
            put(20,"K");
            put(21,"M");
            put(22,"Q");
            put(23,"R");

            put(24,"0");
            put(25,"1");
            put(26,"4");
            put(27,"5");
            put(28,"H");
            put(29,"J");
            put(30,"N");
            put(31,"P");
        }
    };

    public static HashMap<Integer,String> geoHash2GpsLen=new HashMap<Integer,String>(){
        {
            put(1,"W");
            put(2,"W,ST");
            put(3,"W,ST");
        }
    };

    private static List<Double> getBinaryPoint(double min,double max,int level){

        List<Double> points=new ArrayList<Double>();
        double mid=(max+min)/2;
        points.add(min);
        points.add(mid);
        points.add(max);

        if(level>1){
            List<Double> pointsTmp=new ArrayList<Double>();
            for(int i=1;i<points.size();i++){
                pointsTmp.addAll(getBinaryPoint(points.get(i-1),points.get(i),level-1));
            }
            pointsTmp.remove(level*2-1);
            return pointsTmp;
        }
        return points;
    }

    private static void printBinaryPoint(List<Double> doubles){
        for(double d:doubles){
            System.out.println(d);
        }
    }


    private static List<GeoRect> generateGeoRects(int level,GeoRect parent,Len lngLen,Len latLen, GPS minGps, GPS maxGps){
        List<GeoRect> geoRectList=new ArrayList<>();
        List<Double> lngDoubles=getBinaryPoint(lngLen.getMin(),lngLen.getMax(),3);

        List<Double> latDoubles=getBinaryPoint(latLen.getMin(),latLen.getMax(),2);
        int k=0;
        for(int i=latDoubles.size()-1;i>0;i--){
            for(int j=1;j<lngDoubles.size();j++){
                GeoRect geoRect=new GeoRect();
                geoRect.setMinGps(new GPS(lngDoubles.get(j-1),latDoubles.get(i-1)));
                geoRect.setMaxGps(new GPS(lngDoubles.get(j),latDoubles.get(i)));
                StringBuilder stringBuilder=new StringBuilder();
                if(parent!=null){
                    stringBuilder.append(parent.getGeo());
                }
                stringBuilder.append(geoHash2Bash32.get(k));
                geoRect.setGeo(stringBuilder.toString());

                if(isRectIntersect(geoRect.getMinGps(),geoRect.getMaxGps(),minGps,maxGps))
                    geoRectList.add(geoRect);
                k++;
            }
        }
        if(level>1){
            List<GeoRect> geoRectListTmp=new ArrayList<>();
            for(int i=0;i<geoRectList.size();i++){
                GeoRect geoRect=geoRectList.get(i);
                GPS minGpsTmp=geoRect.getMinGps();
                GPS maxGpsTmp=geoRect.getMaxGps();
                geoRectListTmp.addAll(generateGeoRects(level-1,geoRect,new Len(minGpsTmp.getLongitude(),maxGpsTmp.getLongitude()),new Len(minGpsTmp.getLatitude(),maxGpsTmp.getLatitude()),minGps,maxGps));
            }
            return geoRectListTmp;
        }
        return geoRectList;
    }

    private static void printGeoRect(List<GeoRect> geoRectList){
        System.out.println("rect's num ="+geoRectList.size());
        int i=0;
        for (GeoRect geoRect:geoRectList) {
            System.out.print(geoRect+",");
            i++;
            if(i%8==0){
                System.out.println();
            }
        }
    }

    /**
     * 判断矩形是否相交
     * @param aMin
     * @param aMax
     * @param bMin
     * @param bMax
     * @return
     */
    private static boolean isRectIntersect(GPS aMin,GPS aMax,GPS bMin,GPS bMax){
        if(aMax.getLongitude()<bMin.getLongitude()){
            return false;
        }
        if(aMin.getLongitude()>bMax.getLongitude()){
            return false;
        }
        if(aMin.getLatitude()>bMax.getLatitude()){
            return false;
        }
        if (aMax.getLatitude()<bMax.getLatitude()){
            return false;
        }

        return true;
    }

    public static String toJson(List<GeoRect> geoRects){
        Gson gson=new Gson();
        return gson.toJson(geoRects);
    }

    public static void main(String[] args) {
        //printBinaryPoint(getBinaryPoint(-90,90,2));
        //getGeoHash(3);
        List<GeoRect> geoRectList=generateGeoRects(6,null,new Len(-180,180),new Len(-90,90),new GPS(119,27),new GPS(122,29));
        String gson=toJson(geoRectList);
        System.out.println(gson);

        try {
            FileWriter fileWriter=new FileWriter("6.json");
            fileWriter.write(gson);
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class Len{
        private double min;
        private double max;

        public double getMin() {
            return min;
        }

        public void setMin(double min) {
            this.min = min;
        }

        public double getMax() {
            return max;
        }

        public void setMax(double max) {
            this.max = max;
        }

        public Len(double min, double max) {
            this.min = min;
            this.max = max;
        }
    }

    static class GeoRect{
        private String geo;
        private GPS maxGps;
        private GPS minGps;

        public String getGeo() {
            return geo;
        }

        public void setGeo(String geo) {
            this.geo = geo;
        }

        public GPS getMaxGps() {
            return maxGps;
        }

        public void setMaxGps(GPS maxGps) {
            this.maxGps = maxGps;
        }

        public GPS getMinGps() {
            return minGps;
        }

        public void setMinGps(GPS minGps) {
            this.minGps = minGps;
        }

        @Override
        public String toString() {
            return "GeoRect{" +
                    "geo='" + geo + '\'' +
                    ", maxGps=" + maxGps +
                    ", minGps=" + minGps +
                    '}';
        }
    }

    static class Gps{
        private double lng;
        private double lat;

        public Gps(double lng, double lat) {
            this.lng = lng;
            this.lat = lat;
        }

        public double getLng() {
            return lng;
        }

        public void setLng(double lng) {
            this.lng = lng;
        }

        public double getLat() {
            return lat;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

        @Override
        public String toString() {
            return "Gps{" +
                    "lng=" + lng +
                    ", lat=" + lat +
                    '}';
        }
    }

    static class GpsLen{
        private int lng;
        private int lat;

        public GpsLen(int lng, int lat) {
            this.lng = lng;
            this.lat = lat;
        }

        public int getLng() {
            return lng;
        }

        public void setLng(int lng) {
            this.lng = lng;
        }

        public int getLat() {
            return lat;
        }

        public void setLat(int lat) {
            this.lat = lat;
        }
    }
}
