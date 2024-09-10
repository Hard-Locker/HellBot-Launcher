package halot.nikitazolin.launcher.app;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import halot.nikitazolin.launcher.ApplicationRunnerImpl;
import halot.nikitazolin.launcher.app.manager.AppFileManager;
import halot.nikitazolin.launcher.app.manager.AppProcessManager;
import halot.nikitazolin.launcher.app.manager.AppStatusObserver;
import halot.nikitazolin.launcher.app.manager.StartupManager;
import halot.nikitazolin.launcher.init.settings.SettingsService;
import halot.nikitazolin.launcher.init.settings.model.Settings;
import halot.nikitazolin.launcher.localization.LocalizationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AppService {

  private final AppFileManager appFileManager;
  private final AppProcessManager appProcessManager;
  private final Settings settings;
  private final SettingsService settingsService;
  private final LocalizationService localizationService;
  private final StartupManager startupManager;

  private final String jarAppName = "hell-bot";
  private final String directoryPath = ApplicationRunnerImpl.APP_DIRECTORY_PATH;
  private final String backupPath = ApplicationRunnerImpl.APP_BACKUP_DIRECTORY_PATH;
  private final String appSecretsName = ApplicationRunnerImpl.APP_SECRETS_FILE_NAME;
  private final String appSettingsName = ApplicationRunnerImpl.APP_SETTINGS_FILE_NAME;
  private final List<AppStatusObserver> observers = new ArrayList<>();

  public void start() {
    if (isRunning()) {
      log.info("Application already running");
      return;
    }

    log.debug("Attempting to find application jar file in directory: {}", directoryPath);
    Optional<String> appPath = appFileManager.findJarFileAbsolutePath(jarAppName, Paths.get(directoryPath));

    if (appPath.isPresent()) {
      log.info("Starting application with path: {}", appPath.get());
      appProcessManager.startApp(appPath.get());
      log.info("Application started successfully");
    } else {
      log.error("No application jar file found in directory: {}", directoryPath);
    }

    notifyObservers();
  }

  public void stop() {
    if (isRunning()) {
      log.info("Stopping application...");
      appProcessManager.stopApp();
      log.info("Application stopped successfully");
    } else {
      log.info("Application is not running; no action taken");
    }

    notifyObservers();
  }

  public boolean isRunning() {
    boolean running = appProcessManager.isAppRunning();
    log.debug("Checking if application is running: {}", running);
    return running;
  }

  public void update() {
    log.info("Updating application...");
    if (isRunning()) {
      log.info("Stopping running application before update...");
      stop();
    }

    Path appDirectoryPath = Paths.get(directoryPath);
    Optional<String> appPathOptional = appFileManager.findJarFileAbsolutePath(jarAppName, appDirectoryPath);

    if (appPathOptional.isPresent()) {
      String appPath = appPathOptional.get();
      log.info("Deleting old application jar file: {}", appPath);
      appFileManager.deleteAppJarFile(Paths.get(appPath));

      log.info("Loading new application jar file...");
      appFileManager.loadAppJar(true, appDirectoryPath);
      log.info("Application updated successfully");
    } else {
      log.error("Failed to update: no application jar file found in directory: {}", directoryPath);
    }
  }

  public void download() {
    log.info("Downloading application jar file from GitHub...");
    appFileManager.downloadJarFromGithub(Paths.get(directoryPath));
    log.info("Download completed");
  }

  public void select() {
    log.info("Selecting local jar file...");
    appFileManager.selectLocalJarFile(Paths.get(directoryPath));
    log.info("Local jar file selected");
  }

  public String currentApp() {
    log.debug("Attempting to retrieve the current application jar file path");
    Optional<String> appPath = appFileManager.findJarFileAbsolutePath(jarAppName, Paths.get(directoryPath));

    if (appPath.isPresent()) {
      log.debug("Current application path: {}", appPath.get());
      return appPath.get();
    } else {
      log.error("No application jar file found in directory: {}", directoryPath);
      return null;
    }
  }

  public void backupAppFiles() {
    appFileManager.copyFile((directoryPath + "/" + appSecretsName), Paths.get(backupPath));
    appFileManager.copyFile((directoryPath + "/" + appSettingsName), Paths.get(backupPath));
  }
  
  public void restoreAppFiles() {
    appFileManager.copyFile((backupPath + "/" + appSecretsName), Paths.get(directoryPath));
    appFileManager.copyFile((backupPath + "/" + appSettingsName), Paths.get(directoryPath));
  }

  public void changeShowInTray(boolean showInTray) {
    log.info("Changing setting 'Show In Tray' to {}", showInTray);
    settings.setShowInTray(showInTray);
    settingsService.saveSettings();
    log.debug("'Show In Tray' setting updated");
  }

  public void changeHideOnClose(boolean hideOnClose) {
    log.info("Changing setting 'Hide On Close' to {}", hideOnClose);
    settings.setHideToTrayOnClose(hideOnClose);
    settingsService.saveSettings();
    log.debug("'Hide On Close' setting updated");
  }

  public void changeAutostartLauncher(boolean autostartLauncher) {
    log.info("Changing setting 'Autostart Launcher' to {}", autostartLauncher);
    boolean operationSuccessful = false;

    operationSuccessful = startupManager.autoFixStartup(autostartLauncher);

    if (operationSuccessful) {
      settings.setAutostartLauncher(autostartLauncher);
      settingsService.saveSettings();
      log.debug("'Autostart Launcher' setting updated successfully.");
    }
  }

  public void changeAutostartApp(boolean autostartApp) {
    log.info("Changing setting 'Autostart App' to {}", autostartApp);
    settings.setAutostartApp(autostartApp);
    settingsService.saveSettings();
    log.debug("'Autostart App' setting updated");
  }

  public void changeLanguage(String language) {
    log.info("Changing application language to {}", language);
    localizationService.changeLanguage(language);
    log.debug("Language changed to {}", language);
  }

  public void addObserver(AppStatusObserver observer) {
    observers.add(observer);
  }

  public void removeObserver(AppStatusObserver observer) {
    observers.remove(observer);
  }

  private void notifyObservers() {
    for (AppStatusObserver observer : observers) {
      observer.onAppStatusChanged();
    }
  }
}
