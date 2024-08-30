package halot.nikitazolin.launcher.app;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.springframework.stereotype.Service;

import halot.nikitazolin.launcher.ApplicationRunnerImpl;
import halot.nikitazolin.launcher.app.manager.AppFileManager;
import halot.nikitazolin.launcher.app.manager.AppProcessManager;
import halot.nikitazolin.launcher.init.settings.model.Settings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AppService {

  private final AppFileManager appFileManager;
  private final AppProcessManager appProcessManager;
  private final Settings settings;

  private final String directoryPath = ApplicationRunnerImpl.APP_DIRECTORY_PATH;
  private final String jarAppName = "hell-bot";

  public void start() {
    if (isRunning()) {
      log.info("Application already running");
      return;
    }

    Optional<String> appPath = appFileManager.findJarFileAbsolutePath(jarAppName, Paths.get(directoryPath));

    if (appPath.isPresent()) {
      appProcessManager.startApp(appPath.get());
      log.info("Application was running");
    } else {
      log.error("No application selected");
      return;
    }
  }

  public void stop() {
    if (isRunning()) {
      appProcessManager.stopApp();
      log.info("Application was stopped");
    } else {
      log.info("Application already stopped");
    }
  }

  public boolean isRunning() {
    return appProcessManager.isAppRunning();
  }

  public void update() {
    if (isRunning()) {
      appProcessManager.stopApp();
    }

    Path appDirectoryPath = Paths.get(directoryPath);
    String appPath = appFileManager.findJarFileAbsolutePath(jarAppName, appDirectoryPath).get();
    appFileManager.deleteAppJarFile(Paths.get(appPath));

    appFileManager.loadAppJar(true, appDirectoryPath);
  }

  public void download() {
    appFileManager.downloadJarFromGithub(Paths.get(directoryPath));
  }

  public void select() {
    appFileManager.selectLocalJarFile(Paths.get(directoryPath));
  }

  public String currentApp() {
    Optional<String> appPath = appFileManager.findJarFileAbsolutePath(jarAppName, Paths.get(directoryPath));

    if (appPath.isPresent()) {
      log.debug("Application was selected");
      return appPath.get();
    } else {
      log.error("No application selected");
      return null;
    }
  }

  // TODO
  public void changeStartup() {

  }

  // TODO
  public void changeHideToTray() {
    if (settings.isHideOnClose() == false) {
      settings.setHideOnClose(true);
    } else {
      settings.setHideOnClose(false);
    }
  }
}
