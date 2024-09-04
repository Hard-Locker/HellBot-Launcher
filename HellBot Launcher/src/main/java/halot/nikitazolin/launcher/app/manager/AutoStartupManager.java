package halot.nikitazolin.launcher.app.manager;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class AutoStartupManager {

  private static final String REGISTRY_KEY_PATH = "HKCU:\\Software\\Microsoft\\Windows\\CurrentVersion\\Run";
  private static final String LAUNCHER_NAME = "HellBotLauncher";

  /**
   * Adds the launcher to the Windows startup by updating the registry key.
   * 
   * @return true if the operation was successful, false otherwise
   */
  public boolean addLauncherToStartup() {
    try {
      String launcherPath = getLauncherPath();

      if (launcherPath.isEmpty()) {
        log.error("Failed to get launcher path. Launcher cannot be added to startup.");
        return false;
      }

      String command = buildAddCommand(launcherPath);

      return executePowerShellCommand(command);
    } catch (Exception e) {
      log.error("Failed to add launcher to startup", e);
      return false;
    }
  }

  /**
   * Removes the launcher from the Windows startup by removing the registry key.
   * 
   * @return true if the operation was successful, false otherwise
   */
  public boolean removeLauncherFromStartup() {
    try {
      String command = buildRemoveCommand();

      return executePowerShellCommand(command);
    } catch (Exception e) {
      log.error("Failed to remove launcher from startup", e);
      return false;
    }
  }

  /**
   * Builds the PowerShell command to add the launcher to startup.
   * 
   * @param launcherPath the path to the launcher executable or JAR file
   * @return the PowerShell command string
   */
  private String buildAddCommand(String launcherPath) {
    if (launcherPath.endsWith(".jar")) {
      launcherPath = "javaw -jar \"" + launcherPath + "\" --hidden";
    }
    return "powershell.exe Set-ItemProperty -Path '" + REGISTRY_KEY_PATH + "' -Name '" + LAUNCHER_NAME + "' -Value '"
        + launcherPath + "'";
  }

  /**
   * Builds the PowerShell command to remove the launcher from startup.
   * 
   * @return the PowerShell command string
   */
  private String buildRemoveCommand() {
    return "powershell.exe Remove-ItemProperty -Path '" + REGISTRY_KEY_PATH + "' -Name '" + LAUNCHER_NAME + "'";
  }

  /**
   * Executes a PowerShell command and logs the output.
   * 
   * @param command the PowerShell command to execute
   * @return true if the command executed successfully, false otherwise
   */
  private boolean executePowerShellCommand(String command) {
    log.info("Executing command: {}", command);

    try {
      ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));
      processBuilder.redirectErrorStream(true);
      Process process = processBuilder.start();

      try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
        String line;
        while ((line = reader.readLine()) != null) {
          log.info(line);
        }
      }

      int exitCode = process.waitFor();

      if (exitCode == 0) {
        log.info("Command executed successfully.");
        return true;
      } else {
        log.error("Command execution failed. Exit code: {}", exitCode);
        return false;
      }
    } catch (IOException e) {
      log.error("Failed to execute command due to IO error", e);
      return false;
    } catch (InterruptedException e) {
      log.error("Command execution was interrupted", e);
      Thread.currentThread().interrupt();
      return false;
    }
  }

  /**
   * Determines the path to the launcher, either as an executable or JAR file.
   * 
   * @return the absolute path to the launcher
   */
  private String getLauncherPath() {
    String userDir = System.getProperty("user.dir");
    log.info("Using working directory (user.dir): {}", userDir);

    String classPath = System.getProperty("java.class.path");
    log.info("Using classpath (java.class.path): {}", classPath);

    if (classPath != null && classPath.endsWith(".jar")) {
      File jarFile = new File(userDir, classPath);
      log.info("Launcher JAR path: {}", jarFile.getAbsolutePath());

      return jarFile.getAbsolutePath();
    }

    log.info("Returning working directory as launcher path: {}", userDir);
    return Paths.get(userDir).toAbsolutePath().toString();
  }
}
