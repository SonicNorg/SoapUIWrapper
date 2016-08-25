/**
 * Created by pavel.krizhanovskiy on 24.08.2016.
 */
import com.eviware.soapui.tools.*;

public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println(System.getProperty("java.classpath") );
        SoapUIMockServiceRunner runner = new SoapUIMockServiceRunner();
        Thread daemon = new Thread(new Runnable() {
            @Override
            public void run() {
                runner.runFromCommandLine(args);
            }
        });
        daemon.setDaemon(true);
        daemon.start();

        Thread.sleep(17000);

        System.out.println("Terminating...");
        runner.stopAll();
        System.exit(0);
    }

}
