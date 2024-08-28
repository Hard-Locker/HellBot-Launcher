package halot.nikitazolin.launcher.app;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.springframework.stereotype.Service;

import halot.nikitazolin.launcher.ApplicationRunnerImpl;
import halot.nikitazolin.launcher.app.manager.AppFileManager;
import halot.nikitazolin.launcher.app.manager.AppProcessManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AppService {

  private final AppFileManager appFileManager;
  private final AppProcessManager appProcessManager;

  public void start() {
    if (appProcessManager.isAppRunning()) {
      log.info("Application already running");
      return;
    } else {
      Path appDirectoryPath = Paths.get(ApplicationRunnerImpl.APP_DIRECTORY_PATH);
      Optional<String> appPath = appFileManager.findJarFileAbsolutePath("hell-bot", appDirectoryPath);

      if (appPath.isPresent()) {
        appProcessManager.startApp(appPath.get());
        log.info("Application was running");
      } else {
        log.error("No application selected");
        return;
      }
    }
  }

  public void stop() {
    if (appProcessManager.isAppRunning()) {
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
    if (appProcessManager.isAppRunning()) {
      appProcessManager.stopApp();
    }

    Path appDirectoryPath = Paths.get(ApplicationRunnerImpl.APP_DIRECTORY_PATH);
    String appPath = appFileManager.findJarFileAbsolutePath("hell-bot", appDirectoryPath).get();
    appFileManager.deleteAppJarFile(Paths.get(appPath));

    appFileManager.loadAppJar(true, appDirectoryPath);
  }

  public void download() {
    Path appDirectoryPath = Paths.get(ApplicationRunnerImpl.APP_DIRECTORY_PATH);
    appFileManager.downloadJarFromGithub(appDirectoryPath);
  }

  public void select() {
    Path appDirectoryPath = Paths.get(ApplicationRunnerImpl.APP_DIRECTORY_PATH);
    appFileManager.selectLocalJarFile(appDirectoryPath);
  }

  public String currentApp() {
    Path appDirectoryPath = Paths.get(ApplicationRunnerImpl.APP_DIRECTORY_PATH);
    Optional<String> appPath = appFileManager.findJarFileAbsolutePath("hell-bot", appDirectoryPath);
    
    if (appPath.isPresent()) {
      log.info("Application was running");
      return appPath.get();
    } else {
      log.error("No application selected");
      return null;
    }
  }

  // TODO
  public void changeStartup() {

  }
}
