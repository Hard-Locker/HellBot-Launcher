package halot.nikitazolin.launcher.app.manager;

import java.io.File;
import java.io.IOException;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class AppProcessManager {

  private Process process;

  /**
   * Launches the application using the provided jar file path.
   *
   * @param jarFilePath Absolute path to the jar file
   */
  public void startApp(String jarFilePath) {
    if (process != null && process.isAlive()) {
      log.warn("Application already running.");
      return;
    }

    File jarFile = new File(jarFilePath);

    if (jarFile.exists() && jarFile.isFile()) {
      try {
        process = new ProcessBuilder("javaw", "-Dnogui=true", "-jar", jarFile.getAbsolutePath())
            .directory(jarFile.getParentFile()).start();
        log.info("Application {} running.", jarFile.getName());
      } catch (IOException e) {
        log.error("Error starting application {}: {}", jarFile.getName(), e.getMessage());
      }
    } else {
      log.error("Jar file not found or is not a file: {}", jarFilePath);
    }
  }

  /**
   * Stop the application from running.
   */
  public void stopApp() {
    if (process != null && process.isAlive()) {
      process.destroy();
      log.info("The application has stopped.");
    } else {
      log.warn("The application is not running or has already stopped.");
    }
  }

  /**
   * Checks if the application is running.
   *
   * @return true if the application is running, otherwise false
   */
  public boolean isAppRunning() {
    boolean isRunning = process != null && process.isAlive();
    log.info("Application status: {}", isRunning ? "ON" : "OFF");

    return isRunning;
  }
}
