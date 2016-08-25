/**
 * Created by pavel.krizhanovskiy on 24.08.2016.
 */
import com.eviware.soapui.tools.*;

public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println(System.getProperty("java.classpath") );
        SoapUIMockServiceRunner runner = new SoapUIMockServiceRunner();
        if (runner.validateCommandLineArgument(args)) {
            Thread daemon = new Thread(() -> runner.run(args));
            daemon.setDaemon(true);
            daemon.start();

            Thread.sleep(20000);

            System.out.println("Terminating...");
            runner.stopAll();
        }
        System.exit(0);
    }

}
