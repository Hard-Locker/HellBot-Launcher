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

  /**
   * This method is designed to automatically set or fix startup. This logic is
   * needed to prevent double startup when loading the system. That is, so that
   * the user does not add JAR and EXE to startup at the same time.
   * 
   * @param startup means true to enable startup or false to disable startup
   * @return true if operation success, false otherwise
   */
  public boolean autoFixStartup(boolean startup) {
    if (startup == true) {
      boolean shortcutExists = utilWindowsOS.shortcutExists(STARTUP_FOLDER_PATH, LAUNCHER_NAME);
      boolean registryEntryExists = utilWindowsOS.registryEntryExists(UtilWindowsOS.USER_STARTUP_REGISTRY_KEY,
          LAUNCHER_NAME);

      if (shortcutExists || registryEntryExists) {
        log.debug("Found entry in startup. Trying to remove and add launcher to startup.");
        return removeLauncherFromStartup() && addLauncherToStartup();
      } else {
        log.debug("Trying to add launcher to startup.");
        return addLauncherToStartup();
      }
    }

    if (startup == false) {
      log.debug("Trying to remove launcher from startup.");
      return removeLauncherFromStartup();
    }

    return false;
  }

  private boolean addLauncherToStartup() {
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

  private boolean removeLauncherFromStartup() {
    if (getLauncherPath().isEmpty()) {
      log.error("Failed to get launcher path. Launcher cannot be removed from startup.");
      return false;
    }

    boolean shortcutExists = utilWindowsOS.shortcutExists(STARTUP_FOLDER_PATH, LAUNCHER_NAME);
    boolean registryEntryExists = utilWindowsOS.registryEntryExists(UtilWindowsOS.USER_STARTUP_REGISTRY_KEY,
        LAUNCHER_NAME);

    boolean shortcutRemoved = false;
    boolean registryEntryRemoved = false;

    // Remove shortcut if exists
    if (shortcutExists) {
      String shortcutPath = STARTUP_FOLDER_PATH + "\\\\" + LAUNCHER_NAME + ".lnk";
      shortcutRemoved = utilWindowsOS.deleteShortcut(shortcutPath);

      if (shortcutRemoved) {
        log.debug("Launcher shortcut removed from startup.");
      } else {
        log.error("Failed to remove launcher shortcut from startup.");
      }
    }

    // Remove registry entry if exists
    if (registryEntryExists) {
      String command = utilWindowsOS.buildCommandRemoveFromRegistry(UtilWindowsOS.USER_STARTUP_REGISTRY_KEY,
          LAUNCHER_NAME);
      registryEntryRemoved = utilWindowsOS.executePowerShellCommand(command);

      if (registryEntryRemoved) {
        log.debug("Launcher registry entry removed from startup.");
      } else {
        log.error("Failed to remove launcher registry entry from startup.");
      }
    }

    return shortcutRemoved || registryEntryRemoved;
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
