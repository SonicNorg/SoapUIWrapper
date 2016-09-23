package com.norg.wrapper;

import com.eviware.soapui.model.mock.MockRunner;
import com.eviware.soapui.tools.*;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

public class Main {
    private static final long DEFAULT_TIMEOUT = 60_000; //1 minute
    public static final int WAIT_FOR_MOCKS = 15_000;


    public static void main(String[] args) throws Exception {
        String path = args[args.length-2];
        int timeout = Integer.parseInt(args[args.length-1]) * 1000;
        System.out.println("Timeout: " + timeout + " ms");

        assert (timeout < WAIT_FOR_MOCKS);

        final String[] arg = Arrays.copyOf(args, args.length-2);
        SoapUIMockServiceRunner runner = new SoapUIMockServiceRunner();
        if (runner.validateCommandLineArgument(arg)) {

            Thread daemon = new Thread(() -> runner.run(arg));
            daemon.setDaemon(true);
            daemon.start();

            File shutdown = new File(path);
            long startTime = System.currentTimeMillis();
            //ждем поднятия моков
            while((System.currentTimeMillis()-startTime < WAIT_FOR_MOCKS) && !isEnabled(runner)) {
                Thread.sleep(1000);
            }
            while(!shutdown.exists() && isEnabled(runner) && (System.currentTimeMillis()-startTime < timeout)) {
                Thread.sleep(1000);
            }

            System.out.println("Terminating...");
            if(shutdown.exists()) {
                shutdown.delete();
            }

            if(isEnabled(runner)) {
                runner.stopAll();
            }
        }
        System.exit(0);
    }

    public static boolean isEnabled(SoapUIMockServiceRunner runner) throws NoSuchFieldException, IllegalAccessException {
        Field runnersField = runner.getClass().getDeclaredField("runners");
        runnersField.setAccessible(true);
        List<MockRunner> runners = (List<MockRunner>) runnersField.get(runner);
        for (MockRunner run :
                runners) {
            if (run.isRunning()) {
                return true;
            }
        }
        return false;
    }

}
