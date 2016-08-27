package com.bhencke.chariot;

import com.heroicrobot.dropbit.registry.*;
import com.heroicrobot.dropbit.devices.pixelpusher.Pixel;
import com.heroicrobot.dropbit.devices.pixelpusher.Strip;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by benh on 8/26/16.
 */
public class Chariot {

    DeviceRegistry registry;

    int lastPosition;
    int canvasW = 1024;
    int canvasH = 600;
    int stride = 240;

    class TestObserver implements Observer {
        public boolean hasStrips = false;
        public void update(Observable registry, Object updatedDevice) {
            System.out.println("Registry changed!");
            if (updatedDevice != null) {
                System.out.println("Device change: " + updatedDevice);
            }
            this.hasStrips = true;
        }
    };

    TestObserver testObserver;


    void setup() {
        registry = new DeviceRegistry();
        testObserver = new TestObserver();
        registry.addObserver(testObserver);
    }


    void draw() {
        // scrape for the strips
        if (testObserver.hasStrips) {
            registry.setExtraDelay(0);
            registry.startPushing();

            int stripy = 0;
            List<Strip> strips = registry.getStrips();

            // for every strip:
            for(Strip strip : strips) {
                int strides_per_strip = strip.getLength() / stride;

                // for every pixel in the physical strip
                for (int stripx = 0; stripx < strip.getLength(); stripx++) {

                    int c = Color.HSBtoRGB((float) ((System.currentTimeMillis() % 10000 / 10000.0) + stripx / 10.0), 1f, 1f);
                    strip.setPixel(c, stripx);

                }
                stripy++;
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Chariot c = new Chariot();
        c.setup();
        while(true) {
            c.draw();
            Thread.sleep(5);
        }
    }

    int color(int r, int g, int b) {
        //bgr
        return b | g<<8 | r<<16;
    }


}


