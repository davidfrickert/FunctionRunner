
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(proxyBeanMethods = false)
public class App {

    public static final long START_TIME = System.nanoTime();

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

}
