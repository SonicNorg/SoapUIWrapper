package com.norg.wrapper;

import com.eviware.soapui.model.mock.MockRunner;
import com.eviware.soapui.tools.*;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

public class Main {
    private static final long TIMEOUT = 60_000; //1 minute

    public static void main(String[] args) throws Exception {
        String path = args[args.length-1];

        final String[] arg = Arrays.copyOf(args, args.length-1);
        SoapUIMockServiceRunner runner = new SoapUIMockServiceRunner();
        if (runner.validateCommandLineArgument(arg)) {

            Thread daemon = new Thread(() -> runner.run(arg));
            daemon.setDaemon(true);
            daemon.start();

            File shutdown = new File(path);
            long startTime = System.currentTimeMillis();
            while(!shutdown.exists() || isEnabled(runner) || (System.currentTimeMillis()-startTime < TIMEOUT)) {
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
        Field runnersField = runner.getClass().getField("runners");
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
