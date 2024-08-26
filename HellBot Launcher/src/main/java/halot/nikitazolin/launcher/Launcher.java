package halot.nikitazolin.launcher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Launcher {

  public static void main(String[] args) {
    System.setProperty("java.awt.headless", "false");
    SpringApplication.run(Launcher.class, args);
  }
}
