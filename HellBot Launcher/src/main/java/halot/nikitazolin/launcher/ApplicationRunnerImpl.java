package halot.nikitazolin.launcher;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import halot.nikitazolin.launcher.gui.LauncherMainWindow;
import halot.nikitazolin.launcher.localization.LocalizationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Profile("development")
@RequiredArgsConstructor
public class ApplicationRunnerImpl implements ApplicationRunner {

  private final LocalizationService localizationService;
  private final LauncherMainWindow launcherMainWindow;
  
  public static final String APP_DIRECTORY_PATH = "apps";
  public static final String AUTHORIZATION_FILE_PATH = "secrets.yml";
  public static final String SETTINGS_FILE_PATH = "settings.yml";

  @Override
  public void run(ApplicationArguments args) throws Exception {
    localizationService.initializeLocale();
    launcherMainWindow.showLauncherWindow();
    
    System.out.println("Ready!");
    log.info("Ready!");
  }
}
