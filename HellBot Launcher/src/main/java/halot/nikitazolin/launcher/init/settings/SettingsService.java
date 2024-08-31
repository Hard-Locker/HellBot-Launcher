package halot.nikitazolin.launcher.init.settings;

import org.springframework.stereotype.Service;

import halot.nikitazolin.launcher.ApplicationRunnerImpl;
import halot.nikitazolin.launcher.init.settings.manager.SettingsFileChecker;
import halot.nikitazolin.launcher.init.settings.manager.SettingsLoader;
import halot.nikitazolin.launcher.init.settings.manager.SettingsSaver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class SettingsService {

  private final SettingsFileChecker settingsFileChecker;
  private final SettingsLoader settingsLoader;
  private final SettingsSaver settingsSaver;
  
  private String filePath = ApplicationRunnerImpl.SETTINGS_FILE_PATH;

  public void validateSettings() {
    settingsFileChecker.ensureFileExists(filePath);
    settingsLoader.load(filePath);
    log.info("Loaded {}", filePath);
  }
  
  public void saveSettings() {
    settingsSaver.saveToFile(filePath);
  }
}
