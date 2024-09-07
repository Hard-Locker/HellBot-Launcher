package halot.nikitazolin.launcher.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class UtilWindowsOS {

  public static final String USER_STARTUP_REGISTRY_KEY = "HKCU:\\Software\\Microsoft\\Windows\\CurrentVersion\\Run";
  public static final String MACHINE_STARTUP_REGISTRY_KEY = "HKLM:\\Software\\Microsoft\\Windows\\CurrentVersion\\Run";

  /**
   * Moves a file from one location to another.
   *
   * @param sourceFilePath     the source file path
   * @param destinationDirPath the destination directory path
   * @return true if the file was moved successfully, false otherwise
   */
  public boolean moveFile(String sourceFilePath, String destinationDirPath) {
    if (sourceFilePath == null || sourceFilePath.isEmpty()) {
      log.error("Source file path is invalid (null or empty)");
      return false;
    }

    if (destinationDirPath == null || destinationDirPath.isEmpty()) {
      log.error("Destination path is invalid (null or empty)");
      return false;
    }

    File sourceFile = new File(sourceFilePath);
    File destinationDir = new File(destinationDirPath);

    if (destinationDir.isDirectory()) {
      File destinationFile = new File(destinationDir, sourceFile.getName());
      log.debug("Attempting to move file from '{}' to directory '{}'", sourceFilePath, destinationDirPath);

      try {
        Files.move(sourceFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        log.info("File successfully moved from '{}' to '{}'", sourceFilePath, destinationFile.getPath());
        return true;
      } catch (IOException e) {
        log.error("Failed to move file from '{}' to '{}'", sourceFilePath, destinationFile.getPath(), e);
        return false;
      }
    } else {
      log.error("'{}' is not a valid directory", destinationDirPath);
      return false;
    }
  }

  /**
   * Creates a shortcut to any executable file in the same directory.
   *
   * @param executableFilePath the path to the executable file (e.g., .exe, .bat)
   * @return an Optional containing the shortcut path if created successfully, or
   *         an empty Optional otherwise
   */
  public Optional<String> createShortcut(String executableFilePath, String shortcutName) {
    if (executableFilePath == null || executableFilePath.isEmpty()) {
      log.error("File path is invalid (null or empty)");
      return Optional.empty();
    }

    File executableFile = new File(executableFilePath);

    if (!executableFile.exists()) {
      log.error("Executable file '{}' does not exist", executableFilePath);
      return Optional.empty();
    }

    String shortcutFileName = shortcutName + ".lnk";
    String shortcutPath = executableFile.getParent() + "\\" + shortcutFileName;
    String escapedShortcutPath = shortcutPath.replace("\\", "\\\\");
    String escapedExecutablePath = executableFilePath.replace("\\", "\\\\");

    String command = String.format(
        "$WScriptShell = New-Object -ComObject WScript.Shell; " +
        "$Shortcut = $WScriptShell.CreateShortcut('%s'); "+
        "$Shortcut.TargetPath = '%s'; " +
        "$Shortcut.WorkingDirectory = '%s'; " +
        "$Shortcut.IconLocation = '%s'; " +
        "$Shortcut.Save()",
        escapedShortcutPath,
        escapedExecutablePath,
        executableFile.getParent(),
        escapedExecutablePath);

    log.debug("Creating shortcut for '{}' at '{}'", executableFilePath, shortcutPath);
    boolean commandSuccess = executePowerShellCommand(command);

    if (commandSuccess) {
      log.info("Shortcut successfully created at '{}'", shortcutPath);
      return Optional.of(shortcutPath);
    } else {
      log.error("Failed to create shortcut for '{}'", executableFilePath);
      return Optional.empty();
    }
  }

  /**
   * Checks if a shortcut exists in a specified folder.
   *
   * @param folderPath   the path to the folder
   * @param shortcutName the name of the shortcut (without .lnk extension)
   * @return true if the shortcut exists, false otherwise
   */
  public boolean shortcutExists(String folderPath, String shortcutName) {
    if (folderPath == null || folderPath.isEmpty()) {
      log.error("Folder path is invalid (null or empty)");
      return false;
    }

    File folder = new File(folderPath);

    if (!folder.isDirectory()) {
      log.error("'{}' is not a valid directory", folderPath);
      return false;
    }

    String shortcutFileName = shortcutName.endsWith(".lnk") ? shortcutName : shortcutName + ".lnk";
    File shortcutFile = new File(folder, shortcutFileName);
    log.debug("Checking if shortcut '{}' exists in folder '{}'", shortcutFileName, folderPath);

    if (shortcutFile.exists() && shortcutFile.isFile()) {
      log.debug("Shortcut '{}' exists in folder '{}'", shortcutFileName, folderPath);
      return true;
    } else {
      log.debug("Shortcut '{}' does not exist in folder '{}'", shortcutFileName, folderPath);
      return false;
    }
  }

  /**
   * Deletes the shortcut file.
   *
   * @param shortcutFilePath the path to the shortcut file (e.g., .lnk)
   * @return true if the shortcut was deleted successfully, false otherwise
   */
  public boolean deleteShortcut(String shortcutFilePath) {
    if (shortcutFilePath == null || shortcutFilePath.isEmpty()) {
      log.error("Shortcut file path is invalid (null or empty)");
      return false;
    }

    File shortcutFile = new File(shortcutFilePath);
    log.debug("Attempting to delete shortcut at '{}'", shortcutFilePath);

    if (!shortcutFile.exists()) {
      log.warn("Shortcut file not found: {}", shortcutFilePath);
      return false;
    }

    try {
      boolean deleted = shortcutFile.delete();

      if (deleted) {
        log.info("Shortcut file deleted successfully: {}", shortcutFilePath);
      } else {
        log.error("Failed to delete shortcut file: {}", shortcutFilePath);
      }

      return deleted;
    } catch (SecurityException e) {
      log.error("Permission denied to delete shortcut file: {}. Error: {}", shortcutFilePath, e.getMessage());
      return false;
    }
  }

  /**
   * Builds the PowerShell command to add an entry to startup.
   */
  public String buildCommandAddToRegistry(String registryKey, String name, String value) {
    String command = "Set-ItemProperty -Path '" + registryKey + "' -Name '" + name + "' -Value '\"" + value + "\"'";
    log.debug("Building command to add to registry: '{}'", command);
    return command;
  }

  /**
   * Builds the PowerShell command to add an entry with arguments to startup.
   */
  public String buildCommandAddToRegistry(String registryKey, String name, String value, String valueArgs) {
    String command = "Set-ItemProperty -Path '" + registryKey + "' -Name '" + name + "' -Value '\"" + value + "\""
        + valueArgs + "'";
    log.debug("Building command to add to registry with args: '{}'", command);
    return command;
  }

  /**
   * Builds the PowerShell command to remove an entry from startup.
   */
  public String buildCommandRemoveFromRegistry(String registryKey, String name) {
    String command = "Remove-ItemProperty -Path '" + registryKey + "' -Name '" + name + "'";
    log.debug("Building command to remove from registry: '{}'", command);
    return command;
  }

  /**
   * Checks if a registry entry exists.
   *
   * @param registryKey the registry key path (e.g.,
   *                    "HKCU:\\Software\\Microsoft\\Windows\\CurrentVersion\\Run")
   * @param name        the name of the registry entry
   * @return true if the registry entry exists, false otherwise
   */
  public boolean registryEntryExists(String registryKey, String name) {
    String command = "Get-ItemProperty -Path '" + registryKey + "' -Name '" + name + "'";
    log.debug("Checking if registry entry exists: '{}, {}'", registryKey, name);
    boolean commandSuccess = executePowerShellCommand(command);

    if (commandSuccess) {
      log.debug("Registry entry '{}' in '{}' exists", name, registryKey);
      return true;
    } else {
      log.debug("Registry entry '{}' in '{}' does not exist", name, registryKey);
      return false;
    }
  }

  /**
   * Executes a PowerShell command.
   */
  public boolean executePowerShellCommand(String command) {
    if (command == null || command.isEmpty()) {
      log.error("Command is invalid (null or empty)");
      return false;
    }

    log.info("Executing PowerShell command: {}", command);

    try {
      String executeCommand = "powershell.exe " + command;
      ProcessBuilder processBuilder = new ProcessBuilder(executeCommand.split(" "));
      processBuilder.redirectErrorStream(true);
      Process process = processBuilder.start();

      try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
        String line;

        while ((line = reader.readLine()) != null) {
          log.debug("PowerShell output: {}", line);
        }
      }

      int exitCode = process.waitFor();
      log.debug("PowerShell command exited with code: {}", exitCode);

      return exitCode == 0;
    } catch (IOException | InterruptedException e) {
      log.error("Failed to execute PowerShell command", e);
      Thread.currentThread().interrupt();

      return false;
    }
  }
}
