package halot.nikitazolin.launcher;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import halot.nikitazolin.launcher.app.AppService;
import halot.nikitazolin.launcher.gui.LauncherMainWindow;
import halot.nikitazolin.launcher.init.settings.SettingsService;
import halot.nikitazolin.launcher.init.settings.model.Settings;
import halot.nikitazolin.launcher.localization.LocalizationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Profile("development")
@RequiredArgsConstructor
public class ApplicationRunnerImpl implements ApplicationRunner {

  public static final String LAUNCHER_NAME = "HellBot Launcher";
  public static final String APP_ICON_PATH = "/image/hellbot_256x256.png";
  public static final String APP_TRAY_ON_ICON_PATH = "/image/ico/tray/tray_on_256x256.png";
  public static final String APP_TRAY_OFF_ICON_PATH = "/image/ico/tray/tray_off_256x256.png";
  public static final String APP_DIRECTORY_PATH = "apps";
  public static final String APP_BACKUP_DIRECTORY_PATH = "apps/backup";
  public static final String APP_SECRETS_FILE_NAME = "secrets.yml";
  public static final String APP_SETTINGS_FILE_NAME = "settings.yml";

  public static final String SETTINGS_FILE_PATH = "settings.yml";
  public static final String LANGUAGE_FILE_PATH = "language";

  private final LocalizationService localizationService;
  private final LauncherMainWindow launcherMainWindow;
  private final SettingsService settingsService;
  private final Settings settings;
  private final AppService appService;

  @Override
  public void run(ApplicationArguments args) throws Exception {
    settingsService.validateSettings();
    localizationService.initializeLocale();

    boolean showWindow = !args.containsOption("hidden");
    launcherMainWindow.makeLauncherWindow(showWindow);

    if (settings.isAutostartApp()) {
      appService.start();
    }

    System.out.println("Ready!");
    log.info("Ready!");
  }
}
