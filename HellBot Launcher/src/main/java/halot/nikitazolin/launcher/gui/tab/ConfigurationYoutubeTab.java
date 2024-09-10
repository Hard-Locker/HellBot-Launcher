package halot.nikitazolin.launcher.gui.tab;

import java.awt.GridBagLayout;

import javax.swing.JPanel;

import org.springframework.stereotype.Component;

import halot.nikitazolin.launcher.localization.gui.tab.TabProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class ConfigurationYoutubeTab {

  private final TabProvider tabProvider;

  public JPanel makeTab() {
    log.debug("Start making tab, {}", this);

    JPanel youtubePanel = createYoutubePanel();

    return youtubePanel;
  }

  private JPanel createYoutubePanel() {
    String tabNameText = tabProvider.getText("config_tab_youtube.tab_name");
    JPanel youtubePanel = new JPanel(new GridBagLayout());
    youtubePanel.setName(tabNameText);

    return youtubePanel;
  }
}
