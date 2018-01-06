import java.awt.geom.Point2D;
public class CoordinateUtil {

    /**
     * 平面直角座標系と緯度経度を相互変換する <br>
     * 国土地理院の「測量計算サイト」の式を使用 <br>
     * SpecialThanks http://kobiwa.net/blog/2017/03/22/post-286/ <br>
     * @param lon 経度[度分秒]
     * @param lat 緯度[度分秒]
     * @param lon0 原点:経度[度分秒]
     * @param lat0 原点:緯度[度分秒]
     * @return
     */
    public static Point2D.Double convertLonLat2XY(double lon, double lat, double lon0, double lat0) {

        double daa = 6378137; //長半径
        double dF = 298.257222101d; //逆扁平率
        double dM0 = 0.9999; //平面直角座標系のY軸上における縮尺係数(UTM座標系の場合→0.9996)

        double dn = 1d / (2 * dF - 1);

        //角度をラジアン単位に
        lon = Math.toRadians(lon);
        lat = Math.toRadians(lat);
        lon0 = Math.toRadians(lon0);
        lat0 = Math.toRadians(lat0);

        double dt = Math.sinh(atanh(Math.sin(lat)) - (2 * Math.sqrt(dn)) / (1 + dn) * atanh(2 * Math.sqrt(dn) / (1 + dn) * Math.sin(lat)));
        double dtb = Math.sqrt(1 + Math.pow(dt, 2));
        double dLmc = Math.cos(lon - lon0);
        double dLms = Math.sin(lon - lon0);
        double dXi = Math.atan(dt / dLmc);
        double dEt = atanh(dLms / dtb);

        //α1→0～α5→4
        double[] dal = new double[6];
        dal[0] = 0;
        dal[1] = 1d / 2d * dn - 2d / 3d * Math.pow(dn, 2) + 5d / 16d * Math.pow(dn, 3) + 41d / 180d * Math.pow(dn, 4) - 127d / 288d * Math.pow(dn, 5);
        dal[2] = 13d / 48d * Math.pow(dn, 2) - 3d / 5d * Math.pow(dn, 3) + 557d / 1440d * Math.pow(dn, 4) + 281d / 630d * Math.pow(dn, 5);
        dal[3] = 61d / 240d * Math.pow(dn, 3) - 103d / 140d * Math.pow(dn, 4) + 15061d / 26880d * Math.pow(dn, 5);
        dal[4] = 49561d / 161280d * Math.pow(dn, 4) - 179d / 168d * Math.pow(dn, 5);
        dal[5] = 34729d / 80640d * Math.pow(dn, 5);
        double dSg = 0;
        double dTu = 0;
        for (int j = 1; j <= 5; j++) {
            dSg = dSg + 2 * j * dal[j] * Math.cos(2 * j * dXi) * Math.cosh(2 * j * dEt);
            dTu = dTu + 2 * j * dal[j] * Math.sin(2 * j * dXi) * Math.sinh(2 * j * dEt);
        }
        dSg = 1 + dSg;

        //A0-A5
        double[] dA = new double[6];
        dA[0] = 1 + Math.pow(dn, 2) / 4 + Math.pow(dn, 4) / 64;
        dA[1] = -3d / 2d * (dn - Math.pow(dn, 3) / 8 - Math.pow(dn, 5) / 64);
        dA[2] = 15d / 16d * (Math.pow(dn, 2) - Math.pow(dn, 4) / 4);
        dA[3] = -35d / 48d * (Math.pow(dn, 3) - 5d / 16d * Math.pow(dn, 5));
        dA[4] = 315d / 512d * Math.pow(dn, 4);
        dA[5] = -693d / 1280d * Math.pow(dn, 5);
        double dAb = dM0 * daa / (1 + dn) * dA[0];
        double dSb = 0;
        for (int j = 1; j <= 5; j++) {
            dSb = dSb + dA[j] * Math.sin(2 * j * lat0);
        }
        dSb = dM0 * daa / (1 + dn) * (dA[0] * lat0 + dSb);

        double y = 0;
        double x = 0;
        for (int j = 1; j <= 5; j++) {
            y = y + dal[j] * Math.sin(2 * j * dXi) * Math.cosh(2 * j * dEt);
            x = x + dal[j] * Math.cos(2 * j * dXi) * Math.sinh(2 * j * dEt);
        }
        y = dAb * (dXi + y) - dSb;
        x = dAb * (dEt + x);

        Point2D.Double point = new Point2D.Double(x,y);

        return point;

    }

    /**
     * Pointのlonとlatを元にXYを設定する
     * @param point 変換したいPointインスタンス
     * @param lon0
     * @param lat0
     */
    // public static Point2D.Double convertLonLat2XY(double osmLat,double osmLon, double lon0, double lat0) {
    //
    //     Point2D.Double xyPoint = convertLonLat2XY(osmLon, osmLat, lon0, lat0);
    //
    //     return xyPoint;
    //
    // }

    /**
     * 双曲線正接関数の逆関数
     * @param x
     * @return
     */
    private static double atanh(double x) {
        return (1d / 2d * Math.log((1 + x) / (1 - x)));
    }

}
