package com.norg.wrapper;

import com.eviware.soapui.tools.*;

import java.io.File;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws Exception {
        String path = args[args.length-1];

        final String[] arg = Arrays.copyOf(args, args.length-1);
        SoapUIMockServiceRunner runner = new SoapUIMockServiceRunner();
        if (runner.validateCommandLineArgument(arg)) {

            Thread daemon = new Thread(() -> runner.run(arg));
            daemon.setDaemon(true);
            daemon.start();

            File shutdown = new File(path);
            while(!shutdown.exists()) {
                Thread.sleep(1000);
            }

            System.out.println("Terminating...");
            shutdown.delete();
            runner.stopAll();
        }
        System.exit(0);
    }

}
