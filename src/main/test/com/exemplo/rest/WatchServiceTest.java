package com.exemplo.rest;

import com.exemplo.WatchService;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;


//@RunWith(EasyMockRunner.class)
class WatchServiceTest {

    //Create de mock
    /*@Mock
    private Files file = EasyMock.createMock(Files.class);
    */

    //Set the tested class
    /*@TestSubject
    private WatchService watchService = new WatchService();*/

    
    
    /*private static void createFile()
    {
        String sourcePath = "/home/tania/test_file.csv";
        File source = new File(sourcePath);

        String destPath = "/home/tania/input/test_file.csv";
        File dest = new File(destPath);


        try {
            FileUtils.copyFile(source,dest);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }*/

    @Test
    void testReadCSVFile()
    {

        WatchService watchService = new WatchService();

        //Thread watchServicethread = new Thread(() -> watchService.readCSVFile());

        Thread watchServicethread = new Thread(watchService::readCSVFile);
        watchServicethread.start();

        try {

            Thread.sleep(1000);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        //Thread createFilethread = new Thread(WatchServiceTest::createFile);

        Thread createFilethread = new Thread(() ->
        {
            String sourcePath = "/home/tania/test_file.csv";
            File source = new File(sourcePath);

            String destPath = "/home/tania/input/test_file.csv";
            File dest = new File(destPath);


            try {
                FileUtils.copyFile(source,dest);

            } catch (IOException e) {
                e.printStackTrace();
            }

        });

        createFilethread.start();


        try {

            Thread.sleep(1000);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }
}
