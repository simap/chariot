package com.bhencke.chariot;

import com.heroicrobot.dropbit.registry.*;
import com.heroicrobot.dropbit.devices.pixelpusher.Pixel;
import com.heroicrobot.dropbit.devices.pixelpusher.Strip;

import java.awt.*;
import java.util.*;
import java.util.List;

import static java.lang.Math.*;

/**
 * Created by benh on 8/26/16.
 */
public class Chariot {

    public static void main(String[] args) throws Exception {
        final boolean[] keepRunning = {true};
        Chariot c = new Chariot();
        c.setup();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                System.out.println("Shutdown hook called");
                keepRunning[0] = false;
                c.stop();
            }
        });

        while(keepRunning[0]) {
            c.draw();
            Thread.sleep(5);
        }
    }

    DeviceRegistry registry;
    TestObserver testObserver;
    Random rnd = new Random();

    long nextFlashTime = 0;
    boolean isFlashing = false;

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

    void setup() {
        registry = new DeviceRegistry();
        testObserver = new TestObserver();
        registry.addObserver(testObserver);
    }


    void draw() {

        if (System.currentTimeMillis() > nextFlashTime) {
            isFlashing = true;
        }

        if (isFlashing && System.currentTimeMillis() > nextFlashTime + 600) {
            isFlashing = false;
            nextFlashTime = System.currentTimeMillis() + 5000 + rnd.nextInt(5000);
        }




        // scrape for the strips
        if (testObserver.hasStrips) {
            registry.setExtraDelay(0);
            registry.startPushing();

            int stripy = 0;
            List<Strip> strips = registry.getStrips();

            // for every strip:
            for(Strip strip : strips) {

                // for every pixel in the physical strip
                for (int stripx = 0; stripx < strip.getLength(); stripx++) {

                    //fade green to red, leave blue on

                    int r, g, b;

                    double t = System.currentTimeMillis()/1000.0;


                    double c = sin(t + stripx/5.0 + stripy * PI);
//                    c = pow(c, 1.5);
                    if (c > 0) {
                        r = b = (int) (255 * (c));
                        g = 0;
                    } else {
                        g = b = (int) (255 * abs(c));
                        r = 0;
                    }

                    if (isFlashing) {
                        if (cos(System.currentTimeMillis() / 100.0 - stripx / 10.0) > 0.95) {
                            r = g = b = 255;
                        }
                    }


                    strip.setPixel(color(r, g, b), stripx);

                }
                stripy++;
            }
        }
    }

    private void stop() {
        registry.stopPushing();
    }

    int color(int r, int g, int b) {
        return b | g<<8 | r<<16;
    }


}


