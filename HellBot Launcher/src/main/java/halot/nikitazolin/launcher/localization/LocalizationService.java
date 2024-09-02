package halot.nikitazolin.launcher.localization;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import halot.nikitazolin.launcher.ApplicationRunnerImpl;
import halot.nikitazolin.launcher.localization.app.manager.ManagerProvider;
import halot.nikitazolin.launcher.localization.gui.tab.TabProvider;
import halot.nikitazolin.launcher.localization.gui.tray.TrayProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class LocalizationService {

  private static final String FILE_PATH = ApplicationRunnerImpl.LANGUAGE_FILE_PATH;
  public static final Map<String, List<String>> LANGUAGES;

  private final TabProvider tabProvider;
  private final ManagerProvider managerProvider;
  private final TrayProvider trayProvider;

  private String currentLanguage;

  static {
    Map<String, List<String>> languages = new HashMap<>();
    languages.put("English", List.of("en", "english"));
    languages.put("Russian", List.of("ru", "russian"));
    LANGUAGES = Collections.unmodifiableMap(languages);
  }

  public void initializeLocale() {
    log.debug("Start initializing localization");
    String language = readLanguageFromFile();

    if (language != null && isLanguageSupported(language)) {
      applyLanguage(language);
      log.info("Using localization: {}", language);
    } else {
      applyLanguage("en");
      log.warn("Language file not found or unsupported, using default language");
    }
  }

  public void changeLanguage(String language) {
    if (language != null && isLanguageSupported(language)) {
      log.debug("Changing localization to {}", language);
      applyLanguage(language);
      saveLanguageToFile(language);
    } else {
      log.warn("Unsupported language: {}, keeping the current language", language);
    }
  }

  public String getCurrentLanguage() {
    return currentLanguage;
  }

  private void applyLanguage(String language) {
    String fullLanguageName = getFullLanguageName(language);
    currentLanguage = fullLanguageName;

    tabProvider.initializeLanguage(language);
    managerProvider.initializeLanguage(language);
    trayProvider.initializeLanguage(language);
  }

  private String readLanguageFromFile() {
    try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
      return reader.readLine().trim();
    } catch (IOException e) {
      log.error("Failed to read language file at {}", FILE_PATH, e);
      return null;
    }
  }

  private void saveLanguageToFile(String language) {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
      writer.write(language);
    } catch (IOException e) {
      log.error("Failed to save language '{}' to file at {}", language, FILE_PATH, e);
    }
  }

  private boolean isLanguageSupported(String language) {
    return LANGUAGES.values().stream().flatMap(List::stream).anyMatch(l -> l.equalsIgnoreCase(language));
  }

  private String getFullLanguageName(String language) {
    return LANGUAGES.entrySet().stream().filter(entry -> entry.getValue().contains(language.toLowerCase()))
        .map(Map.Entry::getKey).findFirst().orElse(null);
  }
}
