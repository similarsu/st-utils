package cn.st.utils;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * a utils for divide
 * @author Similar Su
 * @since 2018-1-6
 */
public class DivideUtils {
    /**
     * line divide
     * @param min
     * @param max
     * @param divide
     * @return
     */
    public static List<Double> line(double min, double max, int divide){
        Preconditions.checkArgument(min<max,"min must be less than max");
        Preconditions.checkArgument(divide>0,"divide must be greater than zero");

        List<Double> cutList=Lists.newArrayList();
        cutList.add(min);
        double cur=min;
        double step=(max-min)/divide;
        for(int i=2;i<=divide;i++){
            cur=cur+step;
            cutList.add(cur);
        }
        cutList.add(max);
        return cutList;
    }

    /**
     * rect divide
     * @param minX
     * @param minY
     * @param maxX
     * @param maxY
     * @param divideX
     * @param divideY
     * @return
     */
    public static List<Rect> rect(double minX,double minY,double maxX,double maxY,int divideX,int divideY){
        List<Double> xList=line(minX,maxX,divideX);
        List<Double> yList=line(minY,maxY,divideY);
        List<Rect> rectList=Lists.newArrayList();
        for(int y=0;y<yList.size()-1;y++){
            for(int x=0;x<xList.size()-1;x++){
                rectList.add(new Rect(xList.get(x),yList.get(y),xList.get(x+1),yList.get(y+1)));
            }
        }
        return rectList;
    }

    public static void main(String[] args) {
        System.out.print(rect(1,1,2,2,4,2));
    }

    public static class Point{
        private double x;
        private double y;

        public double getX() {
            return x;
        }

        public void setX(double x) {
            this.x = x;
        }

        public double getY() {
            return y;
        }

        public void setY(double y) {
            this.y = y;
        }

        public Point(double x, double y) {
            this.x = x;
            this.y = y;
        }

        public Point(){

        }

        @Override
        public String toString() {
            return "Point{" +
                    "x=" + x +
                    ", y=" + y +
                    '}';
        }
    }

    public static class Rect{
        private Point min;
        private Point max;

        public Point getMin() {
            return min;
        }

        public void setMin(Point min) {
            this.min = min;
        }

        public Point getMax() {
            return max;
        }

        public void setMax(Point max) {
            this.max = max;
        }

        public Rect(Point min, Point max) {
            this.min = min;
            this.max = max;
        }

        public Rect(double minX,double minY,double maxX,double maxY){
            this.min=new Point(minX,minY);
            this.max=new Point(maxX,maxY);
        }

        @Override
        public String toString() {
            return "Rect{" +
                    "min=" + min +
                    ", max=" + max +
                    '}';
        }
    }
}
