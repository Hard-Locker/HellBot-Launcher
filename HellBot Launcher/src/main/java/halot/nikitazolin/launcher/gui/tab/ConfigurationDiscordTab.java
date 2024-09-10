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
public class ConfigurationDiscordTab {

  private final TabProvider tabProvider;

  public JPanel makeTab() {
    log.debug("Start making tab, {}", this);

    JPanel discordPanel = createDiscordPanel();

    return discordPanel;
  }

  private JPanel createDiscordPanel() {
    String tabNameText = tabProvider.getText("config_tab_discord.tab_name");
    JPanel discordPanel = new JPanel(new GridBagLayout());
    discordPanel.setName(tabNameText);

    return discordPanel;
  }
}
