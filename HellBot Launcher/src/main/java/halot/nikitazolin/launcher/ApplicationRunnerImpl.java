package halot.nikitazolin.launcher;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import halot.nikitazolin.launcher.gui.LauncherMainWindow;
import halot.nikitazolin.launcher.init.settings.SettingsService;
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
  private final SettingsService settingsService;

  public static final String APP_NAME = "HellBot Launcher";
  public static final String APP_ICON_PATH = "src/main/resources/image/hellbot_256x256.png";
  public static final String APP_DIRECTORY_PATH = "apps";
  public static final String SETTINGS_FILE_PATH = "settings.yml";
  public static final String LANGUAGE_FILE_PATH = "language";
  public static final String AUTHORIZATION_FILE_PATH = "apps/secrets.yml";

  @Override
  public void run(ApplicationArguments args) throws Exception {
    settingsService.validateSettings();
    localizationService.initializeLocale();
    launcherMainWindow.showLauncherWindow(true);

    System.out.println("Ready!");
    log.info("Ready!");
  }
}
