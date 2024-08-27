package halot.nikitazolin.launcher.localization;

import org.springframework.stereotype.Service;

import halot.nikitazolin.launcher.localization.gui.tab.TabProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class LocalizationService {

  private final TabProvider tabProvider;
  
  public void initializeLocale() {
    log.debug("Start initialize localization");
    String language = "ru";
    
    tabProvider.initializeLanguage(language);
  }
}
