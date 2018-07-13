package com.exemplo.rest;

import com.exemplo.WatchService;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


class WatchServiceTest {


    @Test
    void testReadCSVFile() {

        final CountDownLatch serviceInvoked = new CountDownLatch(1);

        WatchService watchService = new WatchService();

        Thread watchServicethread = new Thread(watchService::readCSVFile);

        watchServicethread.start();


        String file = "test_file2.csv";

        String sourcePath = "/home/tania/" + file;
        File source = new File(sourcePath);

        String destPath = "/home/tania/input/" + file;
        File dest = new File(destPath);


        try {
            FileUtils.copyFile(source, dest);

        } catch (IOException e) {
            e.printStackTrace();
        }

        //serviceInvoked.countDown();

        try {
            //serviceInvoked.await();
            serviceInvoked.await(500, TimeUnit.MILLISECONDS);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
