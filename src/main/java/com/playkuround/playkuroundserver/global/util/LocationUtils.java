package com.playkuround.playkuroundserver.global.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.awt.geom.Path2D;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LocationUtils {

    public static boolean isLocatedInKU(double latitude, double longitude) {
        Path2D.Double polygon = new Path2D.Double();
        polygon.moveTo(37.539563, 127.072121);
        polygon.lineTo(37.541176, 127.072846);
        polygon.lineTo(37.541647, 127.072494);
        polygon.lineTo(37.541927, 127.071743);
        polygon.lineTo(37.544394, 127.072877);
        polygon.lineTo(37.544738, 127.073921);
        polygon.lineTo(37.544581, 127.074822);
        polygon.lineTo(37.544746, 127.075482);
        polygon.lineTo(37.544593, 127.075574);
        polygon.lineTo(37.544713, 127.076272);
        polygon.lineTo(37.544995, 127.076195);
        polygon.lineTo(37.545163, 127.076798);
        polygon.lineTo(37.544946,127.077041);
        polygon.lineTo(37.545000,127.079986);
        polygon.lineTo(37.544242,127.080056);
        polygon.lineTo(37.543803,127.079626);
        polygon.lineTo(37.543248,127.079562);
        polygon.lineTo(37.543076,127.081427);
        polygon.lineTo(37.542585,127.081403);
        polygon.lineTo(37.542581,127.081270);
        polygon.lineTo(37.542116,127.081275);
        polygon.lineTo(37.542111,127.081420);
        polygon.lineTo(37.541997,127.081432);
        polygon.lineTo(37.541992,127.081622);
        polygon.lineTo(37.541774,127.082035);
        polygon.lineTo(37.541512,127.082363);
        polygon.lineTo(37.541315,127.082965);
        polygon.lineTo(37.540042, 127.082411);
        polygon.lineTo(37.540406,127.080927);
        polygon.lineTo(37.540235,127.080848);
        polygon.lineTo(37.540159,127.081037);
        polygon.lineTo(37.538897, 127.080781);
        polygon.lineTo(37.539086,127.080362);
        polygon.lineTo(37.538966,127.080286);
        polygon.lineTo(37.539013,127.080097);
        polygon.lineTo(37.538695,127.079976);
        polygon.lineTo(37.538804, 127.079230);
        polygon.lineTo(37.538759, 127.079198);
        polygon.lineTo(37.538949,127.078409);
        polygon.lineTo(37.539000,127.076932);
        polygon.lineTo(37.539282, 127.075277);
        polygon.lineTo(37.538588,127.075051);
        polygon.closePath();

        return polygon.contains(latitude, longitude);
    }
}
