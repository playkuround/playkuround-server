package com.playkuround.playkuroundserver.global.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import jakarta.annotation.PostConstruct;
import java.awt.geom.Path2D;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LocationUtils {

    private static Path2D.Double polygonKU;

    static {
        polygonKU = new Path2D.Double();
        polygonKU.moveTo(37.539563, 127.072121);
        polygonKU.lineTo(37.541176, 127.072846);
        polygonKU.lineTo(37.541647, 127.072494);
        polygonKU.lineTo(37.541927, 127.071743);
        polygonKU.lineTo(37.544394, 127.072877);
        polygonKU.lineTo(37.544738, 127.073921);
        polygonKU.lineTo(37.544581, 127.074822);
        polygonKU.lineTo(37.544746, 127.075482);
        polygonKU.lineTo(37.544593, 127.075574);
        polygonKU.lineTo(37.544713, 127.076272);
        polygonKU.lineTo(37.544995, 127.076195);
        polygonKU.lineTo(37.545163, 127.076798);
        polygonKU.lineTo(37.544946,127.077041);
        polygonKU.lineTo(37.545000,127.079986);
        polygonKU.lineTo(37.544242,127.080056);
        polygonKU.lineTo(37.543803,127.079626);
        polygonKU.lineTo(37.543248,127.079562);
        polygonKU.lineTo(37.543076,127.081427);
        polygonKU.lineTo(37.542585,127.081403);
        polygonKU.lineTo(37.542581,127.081270);
        polygonKU.lineTo(37.542116,127.081275);
        polygonKU.lineTo(37.542111,127.081420);
        polygonKU.lineTo(37.541997,127.081432);
        polygonKU.lineTo(37.541992,127.081622);
        polygonKU.lineTo(37.541774,127.082035);
        polygonKU.lineTo(37.541512,127.082363);
        polygonKU.lineTo(37.541315,127.082965);
        polygonKU.lineTo(37.540042, 127.082411);
        polygonKU.lineTo(37.540406,127.080927);
        polygonKU.lineTo(37.540235,127.080848);
        polygonKU.lineTo(37.540159,127.081037);
        polygonKU.lineTo(37.538897, 127.080781);
        polygonKU.lineTo(37.539086,127.080362);
        polygonKU.lineTo(37.538966,127.080286);
        polygonKU.lineTo(37.539013,127.080097);
        polygonKU.lineTo(37.538695,127.079976);
        polygonKU.lineTo(37.538804, 127.079230);
        polygonKU.lineTo(37.538759, 127.079198);
        polygonKU.lineTo(37.538949,127.078409);
        polygonKU.lineTo(37.539000,127.076932);
        polygonKU.lineTo(37.539282, 127.075277);
        polygonKU.lineTo(37.538588,127.075051);
        polygonKU.closePath();
    }

    public static boolean isLocatedInKU(double latitude, double longitude) {
        return polygonKU.contains(latitude, longitude);
    }

}
