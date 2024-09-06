package halot.nikitazolin.launcher.app.manager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.springframework.stereotype.Component;

import halot.nikitazolin.launcher.util.UtilWindowsOS;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class StartupManager {

  private static final String LAUNCHER_NAME = "HellBotLauncher";
  private static final String ARGS = " --hidden";
  private static final String STARTUP_FOLDER_PATH = System.getenv("APPDATA")
      + "\\Microsoft\\Windows\\Start Menu\\Programs\\Startup";

  private final UtilWindowsOS utilWindowsOS;

  public boolean addLauncherToStartup() {
    try {
      String launcherPath = getLauncherPath();

      if (launcherPath.isEmpty()) {
        log.error("Failed to get launcher path. Launcher cannot be added to startup.");
        return false;
      }

      if (launcherPath.endsWith(".exe")) {
        return addExeToStartup(launcherPath);
      }

      if (launcherPath.endsWith(".jar")) {
        return addJarToStartup(launcherPath);
      }

      // Default action
      return false;
    } catch (Exception e) {
      log.error("Failed to add launcher to startup", e);
      return false;
    }
  }

  public boolean removeLauncherFromStartup() {
    try {
      String launcherPath = getLauncherPath();

      if (launcherPath.isEmpty()) {
        log.error("Failed to get launcher path. Launcher cannot be added to startup.");
        return false;
      }

      if (launcherPath.endsWith(".exe")) {
        String command = utilWindowsOS.buildCommandRemoveFromRegistry(UtilWindowsOS.USER_STARTUP_REGISTRY_KEY,
            LAUNCHER_NAME);
        boolean success = utilWindowsOS.executePowerShellCommand(command);

        return success;
      }

      if (launcherPath.endsWith(".jar")) {
        String shortcutPath = STARTUP_FOLDER_PATH + "\\\\" + LAUNCHER_NAME + ".lnk";
        boolean success = utilWindowsOS.deleteShortcut(shortcutPath);

        return success;
      }

      // Default action
      return false;
    } catch (Exception e) {
      log.error("Failed to remove launcher from startup", e);
      return false;
    }
  }

  /**
   * Adds an EXE file to Windows startup by adding a registry entry.
   */
  private boolean addExeToStartup(String appPath) {
    String command = utilWindowsOS.buildCommandAddToRegistry(UtilWindowsOS.USER_STARTUP_REGISTRY_KEY, LAUNCHER_NAME,
        appPath, ARGS);
    boolean success = utilWindowsOS.executePowerShellCommand(command);

    return success;
  }

  /**
   * Adds a JAR file to Windows startup by creating a bat file and placing a
   * shortcut in the startup folder.
   */
  private boolean addJarToStartup(String jarPath) {
    String batPath = createBatAppStarter(jarPath);
    String shortcutPath = utilWindowsOS.createShortcut(batPath, LAUNCHER_NAME).orElse(null);
    boolean success = utilWindowsOS.moveFile(shortcutPath, STARTUP_FOLDER_PATH);

    return success;
  }

  /**
   * Determines the path to the launcher, either as an executable or JAR file.
   */
  private String getLauncherPath() {
    String userDir = System.getProperty("user.dir");
    log.debug("Using working directory (user.dir): {}", userDir);
    String classPath = System.getProperty("java.class.path");
    log.debug("Using classpath (java.class.path): {}", classPath);

    File classPathFile = new File(classPath);

    if (classPathFile.isAbsolute()) {
      log.debug("Launcher path (absolute): {}", classPathFile.getAbsolutePath());
      return classPathFile.getAbsolutePath();
    }

    File file = new File(userDir, classPath);
    log.debug("Launcher path (relative combined): {}", file.getAbsolutePath());

    return file.getAbsolutePath();
  }

  /**
   * Creates a bat file to launch the JAR file in the application folder.
   *
   * @param jarPath the path to the JAR file
   * @return the path to the created bat file, or null if an error occurs
   */
  private String createBatAppStarter(String jarPath) {
    String batFilePath = null;

    try {
      String appFolder = new File(jarPath).getParent();
      String batFileName = LAUNCHER_NAME + ".bat";
      batFilePath = appFolder + "\\" + batFileName;
      String batContent = "@echo off\n" + "start javaw -jar \"" + jarPath + "\"" + ARGS;

      try (FileWriter writer = new FileWriter(batFilePath)) {
        writer.write(batContent);
      }

      log.debug("Created bat file at {}", batFilePath);
    } catch (IOException e) {
      log.error("Failed to create bat file for JAR at {}: {}", jarPath, e.getMessage());
    }

    return batFilePath;
  }
}
