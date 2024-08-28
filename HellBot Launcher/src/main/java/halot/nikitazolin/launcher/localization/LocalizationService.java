package halot.nikitazolin.launcher.localization;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.springframework.stereotype.Service;

import halot.nikitazolin.launcher.localization.app.manager.ManagerProvider;
import halot.nikitazolin.launcher.localization.gui.tab.TabProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class LocalizationService {

  private final TabProvider tabProvider;
  private final ManagerProvider managerProvider;

  private static final String LANGUAGE_FILE_PATH = "language";

  public void initializeLocale() {
    log.debug("Start initialize localization");
    String language = readLanguageFromFile();

    if (language != null) {
      setLanguage(language);
      log.info("Use localization, {}", language);
    } else {
      setLanguage("en");
      log.warn("Language file not found or empty, using default language");
    }
  }

  public void changeLanguage(String language) {
    log.debug("Change localization to {}", language);
    setLanguage(language);
    saveLanguageToFile(language);
  }

  private void setLanguage(String language) {
    tabProvider.initializeLanguage(language);
    managerProvider.initializeLanguage(language);
  }

  private String readLanguageFromFile() {
    try (BufferedReader reader = new BufferedReader(new FileReader(LANGUAGE_FILE_PATH))) {
      return reader.readLine().trim();
    } catch (IOException e) {
      log.error("Failed to read language file", e);
      return null;
    }
  }

  private void saveLanguageToFile(String language) {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(LANGUAGE_FILE_PATH))) {
      writer.write(language);
    } catch (IOException e) {
      log.error("Failed to save language to file", e);
    }
  }
}
