package com.yasm3.bulbcontrol;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BulbControl {

    protected static final Logger logger = LogManager.getLogger();

    public static void main(String[] argv) {
        logger.info("Starting BulbControl.");
        BulbManager bm = new BulbManager();
    }
}