package halot.nikitazolin.launcher.gui.tab;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.springframework.stereotype.Component;

import halot.nikitazolin.launcher.app.AppService;
import halot.nikitazolin.launcher.localization.gui.tab.TabProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class ConfigurationTab {

  private final AppService appService;
  private final TabProvider tabProvider;

  public JPanel makeTab() {
    log.debug("Start making tab, {}", this);

    JPanel configPanel = createConfigurationPanel();

    return configPanel;
  }

  private JPanel createConfigurationPanel() {
    String tabNameText = tabProvider.getText("config_tab.tab_name");
    String backupText = tabProvider.getText("config_tab.backup");
    String restoreText = tabProvider.getText("config_tab.restore");

    JPanel configPanel = new JPanel(new GridBagLayout());
    configPanel.setName(tabNameText);
    JButton backupButton = new JButton(backupText);
    JButton restoreButton = new JButton(restoreText);

    Dimension buttonSize = new Dimension(200, 30);
    backupButton.setPreferredSize(buttonSize);
    restoreButton.setPreferredSize(buttonSize);

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(10, 10, 10, 10);

    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.anchor = GridBagConstraints.WEST;
    configPanel.add(backupButton, gbc);

    gbc.gridx = 1;
    gbc.gridy = 0;
    configPanel.add(restoreButton, gbc);

    backupButton.addActionListener(e -> {
      appService.backupAppFiles();
    });

    restoreButton.addActionListener(e -> {
      appService.restoreAppFiles();
    });

    return configPanel;
  }
}
