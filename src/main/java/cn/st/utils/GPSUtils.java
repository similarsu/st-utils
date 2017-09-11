package cn.st.utils;

/**
 * Created by coolearth on 17-9-7.
 */
public class GPSUtils {
    private final static double MIN_LNG=73.66;
    private final static double MAX_LNG=135.05;
    private final static double MIN_LAT=3.86;
    private final static double MAX_LAT=53.55;
    private final static double PI=Math.PI;
    private final static double X_PI=PI * 3000.0 / 180.0;
    private final static double a=6378245.0;
    private final static double ee=0.00669342162296594323;
    private GPSUtils(){

    }

    /**
     * 判断是否在国内
     * @param lng
     * @param lat
     * @return
     */
    private static boolean outOfChina(double lng,double lat){
        if(lng<MIN_LNG||lng>MAX_LNG){
            return true;
        }

        if (lat<MIN_LAT||lat>MAX_LAT){
            return true;
        }
        return false;
    }

    /**
     * 转换经度
     * @param lng
     * @param lat
     * @return
     */
    private static double transformLng(double lng,double lat){
        double ret = 300.0 + lng + 2.0 * lat + 0.1 * lng * lng + 0.1 * lng * lat + 0.1 * Math.sqrt(Math.abs(lng));
        ret += (20.0 * Math.sin(6.0 * lng * PI) + 20.0 * Math.sin(2.0 * lng * PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(lng * PI) + 40.0 * Math.sin(lng / 3.0 * PI)) * 2.0 / 3.0;
        ret += (150.0 * Math.sin(lng / 12.0 * PI) + 300.0 * Math.sin(lng / 30.0 * PI)) * 2.0 / 3.0;
        return ret;
    }

    /**
     * 转换纬度
     * @param lng
     * @param lat
     * @return
     */
    private static double transformLat(double lng, double lat) {
        double ret = -100.0 + 2.0 * lng + 3.0 * lat + 0.2 * lat * lat + 0.1 * lng * lat + 0.2 * Math.sqrt(Math.abs(lng));
        ret += (20.0 * Math.sin(6.0 * lng * PI) + 20.0 * Math.sin(2.0 * lng * PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(lat * PI) + 40.0 * Math.sin(lat / 3.0 * PI)) * 2.0 / 3.0;
        ret += (160.0 * Math.sin(lat / 12.0 * PI) + 320 * Math.sin(lat * PI / 30.0)) * 2.0 / 3.0;
        return ret;
    };

    /**
     * 国际坐标转为国内（火星）坐标
     * @param lng
     * @param lat
     * @return
     */
    public static GPS wgs84ToGcj02(double lng,double lat) {
        if (outOfChina(lng, lat)) {
            return new GPS(lng,lat);
        }
            double dLat = transformLat(lng - 105.0, lat - 35.0);
            double dLng = transformLng(lng - 105.0, lat - 35.0);
            double radLat = lat / 180.0 * PI;
            double magic = Math.sin(radLat);
            magic = 1 - ee * magic * magic;
            double sqrtMagic = Math.sqrt(magic);
            dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * PI);
            dLng = (dLng * 180.0) / (a / sqrtMagic * Math.cos(radLat) * PI);
            double mgLat = lat + dLat;
            double mgLng = lng + dLng;
            return new GPS(mgLng,mgLat);
    }

    /**
     * 国内（火星）坐标转为国际坐标
     * @param lng
     * @param lat
     * @return
     */
    public static GPS gcj02ToWgs84(double lng,double lat) {
        if (outOfChina(lng, lat)) {
            return new GPS(lng,lat);
        } else {
            double dLat = transformLat(lng - 105.0, lat - 35.0);
            double dLng = transformLng(lng - 105.0, lat - 35.0);
            double radLat = lat / 180.0 * PI;
            double magic = Math.sin(radLat);
            magic = 1 - ee * magic * magic;
            double sqrtMagic = Math.sqrt(magic);
            dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * PI);
            dLng = (dLng * 180.0) / (a / sqrtMagic * Math.cos(radLat) * PI);
            double mgLat = lat + dLat;
            double mgLng = lng + dLng;
            return new GPS(lng * 2 - mgLng, lat * 2 - mgLat);
        }
    };

    /**
     * 百度坐标转为国内（火星）坐标
     * @param lng
     * @param lat
     * @return
     */
    public static GPS bd09ToGcj02(double lng, double lat) {
        double x = lng - 0.0065;
        double y = lat - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * X_PI);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * X_PI);
        double ggLng = z * Math.cos(theta);
        double ggLat = z * Math.sin(theta);
        return new GPS(ggLng, ggLat);
    };

    /**
     * 国内（火星）坐标转为百度坐标
     * @param lng
     * @param lat
     * @return
     */
    public static GPS gcj02ToBd09(double lng,double lat) {
        double z = Math.sqrt(lng * lng + lat * lat) + 0.00002 * Math.sin(lat * X_PI);
        double theta = Math.atan2(lat, lng) + 0.000003 * Math.cos(lng * X_PI);
        double bdLng = z * Math.cos(theta) + 0.0065;
        double bdLat = z * Math.sin(theta) + 0.006;
        return new GPS(bdLng, bdLat);
    };

    /**
     * 百度坐标转为国际坐标
     * @param lng
     * @param lat
     * @return
     */
    public static GPS bd09ToWgs84(double lng,double lat){
        GPS ggGps=bd09ToGcj02(lng, lat);
        return gcj02ToWgs84(ggGps.getLongitude(),ggGps.getLatitude());
    }

    /**
     * 国际坐标转为百度坐标
     * @param lng
     * @param lat
     * @return
     */
    public static GPS wgs84ToBd09(double lng,double lat){
        GPS ggGps=wgs84ToGcj02(lng, lat);
        return gcj02ToBd09(ggGps.getLongitude(),ggGps.getLatitude());
    }

    /**
     * 根据点和距离获取正方形
     * @param lat
     * @param lng
     * @param radius 米
     * @return
     */
    public static GPS[] getAround(double lat, double lng, double radius) {

        double degree = (24901 * 1609) / 360.0;

        double dpmLat = 1 / degree;
        double radiusLat = dpmLat * radius;
        double minLat = lat - radiusLat;
        double maxLat = lat + radiusLat;

        double mpdLng = degree * Math.cos(lat * (PI / 180.0));
        double dpmLng = 1 / mpdLng;
        double radiusLng = dpmLng * radius;
        double minLng = lng - radiusLng;
        double maxLng = lng + radiusLng;
        return new GPS[]{new GPS(minLng,minLat),new GPS(maxLng,maxLat)};
    }

    public static void main(String[] args) {
        GPS gps=GPSUtils.wgs84ToGcj02(120.56927132,27.57587845     );
        System.out.println(gps.toString());
        gps=GPSUtils.wgs84ToBd09(120.69065943,27.98429014     );
        System.out.println(gps.toString());
    }
}

class GPS{
    private double longitude;
    private double latitude;

    public GPS(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    @Override
    public String toString() {
        return longitude+","+latitude;
    }
}
