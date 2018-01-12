package cn.st.utils;

import com.google.common.base.*;
import com.google.common.base.Objects;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table;
import com.google.common.math.IntMath;
import com.google.gson.Gson;
import com.spatial4j.core.io.GeohashUtils;
import jdk.nashorn.internal.ir.annotations.Immutable;
import org.apache.commons.lang3.ArrayUtils;

import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;

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

    public static HashMap<Integer,GpsLen> geoHash2GpsLen=new HashMap<Integer,GpsLen>(){
        {
            put(1,new GpsLen(3,2));
            put(2,new GpsLen(5,5));
            put(3,new GpsLen(8,7));
            put(4,new GpsLen(10,10));
            put(5,new GpsLen(13,12));
            put(6,new GpsLen(15,15));
            put(7,new GpsLen(18,17));
            put(8,new GpsLen(20,20));
            put(9,new GpsLen(23,22));
            put(10,new GpsLen(25,25));
            put(11,new GpsLen(28,27));
            put(12,new GpsLen(30,30));

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
            return pointsTmp;
        }
        return points;
    }

    private static List<Double> adjustBinaryPoint(List<Double> doubles){
        if(doubles.size()==3){
            return doubles;
        }
        else {
            List<Double> adjustDoubles=new ArrayList<>();
            for(int i=0;i<doubles.size();i++){
                if(i==0||i%3!=0){
                    adjustDoubles.add(doubles.get(i));
                }
            }
            return adjustDoubles;
        }
    }

    private static void printBinaryPoint(List<Double> doubles){
        System.out.println(doubles.size());
        for(double d:doubles){
            System.out.println(d);
        }
    }

    private static List<Double> filterPoint(List<Double> doubles,double min,double max){
        List<Double> points=new ArrayList<Double>();
        for(int i=0;i<doubles.size();i++){
            double d=doubles.get(i);
            if(d>=min&&d<=max){
                points.add(d);
            }
        }
        return points;
    }


    private static List<GeoRect> generateGeoRects(int level,Len lngLen,Len latLen, GPS minGps, GPS maxGps){
        List<GeoRect> geoRectList=new ArrayList<>();
        List<Double> lngDoubles=adjustBinaryPoint(getBinaryPoint(lngLen.getMin(),lngLen.getMax(),geoHash2GpsLen.get(level).getLng()));
        List<Double> latDoubles=adjustBinaryPoint(getBinaryPoint(latLen.getMin(),latLen.getMax(),geoHash2GpsLen.get(level).getLat()));
        for(int i=1;i<latDoubles.size();i++){
            for(int j=1;j<lngDoubles.size();j++){
                GeoRect geoRect=new GeoRect();
                geoRect.setMinGps(new GPS(lngDoubles.get(j-1),latDoubles.get(i-1)));
                geoRect.setMaxGps(new GPS(lngDoubles.get(j),latDoubles.get(i)));
                if(isRectIntersect(geoRect.getMinGps(),geoRect.getMaxGps(),minGps,maxGps)){
                    double midLat=(latDoubles.get(i-1)+latDoubles.get(i))/2;
                    double midLng=(lngDoubles.get(j-1)+lngDoubles.get(j))/2;
                    geoRect.setGeo(GeohashUtils.encodeLatLon(midLat,midLng,level));
                    geoRectList.add(geoRect);
                }


            }
        }

        return geoRectList;
    }

    private static List<GeoRect> generateGeoRectsGD(int level,Len lngLen,Len latLen, GPS minGps, GPS maxGps){
        List<GeoRect> geoRectList=new ArrayList<>();
        List<Double> lngDoubles=adjustBinaryPoint(getBinaryPoint(lngLen.getMin(),lngLen.getMax(),geoHash2GpsLen.get(level).getLng()));
        List<Double> latDoubles=adjustBinaryPoint(getBinaryPoint(latLen.getMin(),latLen.getMax(),geoHash2GpsLen.get(level).getLat()));
        for(int i=1;i<latDoubles.size();i++){
            for(int j=1;j<lngDoubles.size();j++){
                GeoRect geoRect=new GeoRect();
                geoRect.setMinGps(new GPS(lngDoubles.get(j-1),latDoubles.get(i-1)));
                geoRect.setMaxGps(new GPS(lngDoubles.get(j),latDoubles.get(i)));
                if(isRectIntersect(geoRect.getMinGps(),geoRect.getMaxGps(),minGps,maxGps)){
                    double midLat=(latDoubles.get(i-1)+latDoubles.get(i))/2;
                    double midLng=(lngDoubles.get(j-1)+lngDoubles.get(j))/2;
                    GPS midGpsGd=GPSUtils.wgs84ToGcj02(midLng,midLat);
                    geoRect.setGeo(GeohashUtils.encodeLatLon(midGpsGd.getLatitude(),midGpsGd.getLongitude(),level));
                    geoRect.setMaxGps(GPSUtils.wgs84ToGcj02(lngDoubles.get(j),latDoubles.get(i)));
                    geoRect.setMinGps(GPSUtils.wgs84ToGcj02(lngDoubles.get(j-1),latDoubles.get(i-1)));
                    geoRectList.add(geoRect);
                }


            }
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
        if(aMax.getLongitude()<=bMin.getLongitude()){
            return false;
        }
        if(aMin.getLongitude()>=bMax.getLongitude()){
            return false;
        }
        if(aMin.getLatitude()>=bMax.getLatitude()){
            return false;
        }
        if (aMax.getLatitude()<=bMin.getLatitude()){
            return false;
        }
        return true;
    }

    public static <T> String toJson(List<T> geoRects){
        Gson gson=new Gson();
        return gson.toJson(geoRects);
    }

    public static <T> List<T> getRandomList(List<T> paramList,int count){
        if(paramList.size()<count){
            return paramList;
        }
        Random random=new Random();
        List<Integer> tempList=new ArrayList<Integer>();
        List<T> newList=new ArrayList<T>();
        int temp=0;
        for(int i=0;i<count;i++){
            temp=random.nextInt(paramList.size());//将产生的随机数作为被抽list的索引
            if(!tempList.contains(temp)){
                tempList.add(temp);
                newList.add(paramList.get(temp));
            }
            else{
                i--;
            }
        }
        return newList;
    }

    public static String toSql(int level,List<GeoRect> geoRectList){
        StringBuffer stringBuffer=new StringBuffer();
        for (GeoRect geoRect:geoRectList
             ) {

            stringBuffer.append(MessageFormat.format("insert into tbl_geohash values (''{0}'',''{1}'',{2},{3},{4},{5},{6},1,now());", UUID.randomUUID().toString().replaceAll("-",""),geoRect.getGeo(),
                    geoRect.getMinGps().getLongitude(),geoRect.getMinGps().getLatitude(),
                    geoRect.getMaxGps().getLongitude(),geoRect.getMaxGps().getLatitude(),level));
            stringBuffer.append("\r\n");
        }
        return stringBuffer.toString();
    }

    public static void main(String[] args) {
       System.out.println(getDistinctBetweenPoints("wt1334","wt1235"));
    }

    private static BiMap<Point,String> GE0HASH_POINT_MAP= HashBiMap.create();

    static {
        GE0HASH_POINT_MAP.put(new Point(1,1),"0");
        GE0HASH_POINT_MAP.put(new Point(2,1),"1");
        GE0HASH_POINT_MAP.put(new Point(3,1),"4");
        GE0HASH_POINT_MAP.put(new Point(4,1),"5");
        GE0HASH_POINT_MAP.put(new Point(5,1),"h");
        GE0HASH_POINT_MAP.put(new Point(6,1),"j");
        GE0HASH_POINT_MAP.put(new Point(7,1),"n");
        GE0HASH_POINT_MAP.put(new Point(8,1),"p");
        GE0HASH_POINT_MAP.put(new Point(1,1),"2");
        GE0HASH_POINT_MAP.put(new Point(2,2),"3");
        GE0HASH_POINT_MAP.put(new Point(3,2),"6");
        GE0HASH_POINT_MAP.put(new Point(4,2),"7");
        GE0HASH_POINT_MAP.put(new Point(5,2),"k");
        GE0HASH_POINT_MAP.put(new Point(6,2),"m");
        GE0HASH_POINT_MAP.put(new Point(7,2),"q");
        GE0HASH_POINT_MAP.put(new Point(8,2),"r");
        GE0HASH_POINT_MAP.put(new Point(1,3),"8");
        GE0HASH_POINT_MAP.put(new Point(2,3),"9");
        GE0HASH_POINT_MAP.put(new Point(3,3),"d");
        GE0HASH_POINT_MAP.put(new Point(4,3),"e");
        GE0HASH_POINT_MAP.put(new Point(5,3),"s");
        GE0HASH_POINT_MAP.put(new Point(6,3),"t");
        GE0HASH_POINT_MAP.put(new Point(7,3),"w");
        GE0HASH_POINT_MAP.put(new Point(8,3),"x");
        GE0HASH_POINT_MAP.put(new Point(1,4),"b");
        GE0HASH_POINT_MAP.put(new Point(2,4),"c");
        GE0HASH_POINT_MAP.put(new Point(3,4),"f");
        GE0HASH_POINT_MAP.put(new Point(4,4),"g");
        GE0HASH_POINT_MAP.put(new Point(5,4),"u");
        GE0HASH_POINT_MAP.put(new Point(6,4),"v");
        GE0HASH_POINT_MAP.put(new Point(7,4),"y");
        GE0HASH_POINT_MAP.put(new Point(8,4),"z");
    }


    /**
     * get distinct of point between two geohash
     * @param geohash1
     * @param geohash2
     * @return
     */
    public static Point getDistinctBetweenPoints(String geohash1,String geohash2){
        Point point1=getPointOfGeohash(geohash1);
        Point point2=getPointOfGeohash(geohash2);
        return new Point(Math.abs(point1.x-point2.x),Math.abs(point1.y-point2.y));
    }

    /**
     * get point of geohash
     * @param geohash
     * @return
     */
    public static Point getPointOfGeohash(String geohash){
        Preconditions.checkArgument(!Strings.isNullOrEmpty(geohash),"geohash must be not empty");
        int x=0;
        int y=0;
        int length=geohash.length();
        BiMap<String, Point> inverse=GE0HASH_POINT_MAP.inverse();
        for(int i=0;i<length;i++){
            String str= String.valueOf(geohash.charAt(i));
            Point point=inverse.get(str);
            if(i==0){
                x=point.x;
                y=point.y;
            }else{
                x=(x-1)*8+point.x;
                y=(y-1)*4+point.y;
            }


        }
        return new Point(x,y);
    }

    static class Point{
        private int x;
        private int y;

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this).add("x",x).add("y",y).toString();
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

    static class GeoRectWithScore extends GeoRect{
        public double getScore() {
            return score;
        }

        public void setScore(double score) {
            this.score = score;
        }

        private double score;

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
