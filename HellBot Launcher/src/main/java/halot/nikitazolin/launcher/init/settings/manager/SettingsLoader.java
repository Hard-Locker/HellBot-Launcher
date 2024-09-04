package halot.nikitazolin.launcher.init.settings.manager;

import java.io.FileInputStream;
import java.io.InputStream;

import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import halot.nikitazolin.launcher.init.settings.model.Settings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class SettingsLoader {

  private final Settings settings;

  public void load(String filePath) {
    Yaml yaml = new Yaml(new Constructor(Settings.class, new LoaderOptions()));

    try (InputStream inputStream = new FileInputStream(filePath)) {
      log.info("Loading configuration file from {}", filePath);
      Settings loadedConfig = yaml.load(inputStream);

      if (loadedConfig != null) {
        safelyAssignSettings(loadedConfig);
      }
    } catch (Exception e) {
      log.error("Failed to load configuration from file: {}", e.getMessage());
    }
  }

  private void safelyAssignSettings(Settings loadedConfig) {
    try {
      settings.setShowInTray(defaultIfNull(loadedConfig.isShowInTray(), true));
      settings.setHideToTrayOnClose(defaultIfNull(loadedConfig.isHideToTrayOnClose(), true));
      settings.setAutostartLauncher(defaultIfNull(loadedConfig.isAutostartLauncher(), true));
      settings.setAutostartApp(defaultIfNull(loadedConfig.isAutostartApp(), true));
      log.debug("Successfully applied settings");
    } catch (Exception e) {
      log.error("Error applying settings: {}", e.getMessage());
    }
  }

  private <T> T defaultIfNull(T value, T defaultValue) {
    return value == null ? defaultValue : value;
  }
}
