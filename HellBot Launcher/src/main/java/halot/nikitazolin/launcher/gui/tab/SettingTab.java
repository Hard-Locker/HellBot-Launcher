package halot.nikitazolin.launcher.gui.tab;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.springframework.stereotype.Component;

import halot.nikitazolin.launcher.app.AppService;
import halot.nikitazolin.launcher.init.settings.model.Settings;
import halot.nikitazolin.launcher.localization.LocalizationService;
import halot.nikitazolin.launcher.localization.gui.tab.TabProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class SettingTab {

  private final AppService appService;
  private final TabProvider tabProvider;
  private final Settings settings;
  private final LocalizationService localizationService;

  private Map<String, List<String>> lang = LocalizationService.LANGUAGES;

  public JPanel makeTab() {
    log.debug("Start making tab, {}", this);

    JPanel settingsPanel = createSettingsPanel();
    JPanel controlsPanel = createControlsPanel();

    settingsPanel.add(createTitleLabel(), BorderLayout.NORTH);
    settingsPanel.add(controlsPanel, BorderLayout.CENTER);

    return settingsPanel;
  }

  private JPanel createSettingsPanel() {
    JPanel panel = new JPanel(new BorderLayout());
    String tabNameText = tabProvider.getText("setting_tab.tab_name");
    panel.setName(tabNameText);

    return panel;
  }

  private JLabel createTitleLabel() {
    String tabNameText = tabProvider.getText("setting_tab.tab_name");
    JLabel label = new JLabel(tabNameText, SwingConstants.CENTER);
    label.setOpaque(true);

    return label;
  }

  private JPanel createControlsPanel() {
    JPanel panel = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = createGridBagConstraints();

    JCheckBox showInTrayCheckBox = createShowInTrayCheckBox();
    JCheckBox hideOnCloseCheckBox = createHideOnCloseCheckBox(showInTrayCheckBox);
    JCheckBox autostartLauncherCheckBox = createAutostartLauncherCheckBox();
    JCheckBox autostartAppCheckBox = createAutostartAppCheckBox();
    JComboBox<String> languageComboBox = createLanguageComboBox();
    JLabel restartHintLabel = createRestartHintLabel();

    panel.add(showInTrayCheckBox, positionComponent(gbc, 0, 0, 2));
    panel.add(hideOnCloseCheckBox, positionComponent(gbc, 0, 1, 2));
    panel.add(autostartLauncherCheckBox, positionComponent(gbc, 0, 2, 2));
    panel.add(autostartAppCheckBox, positionComponent(gbc, 0, 3, 2));
    panel.add(new JLabel(tabProvider.getText("setting_tab.language")), positionComponent(gbc, 0, 4, 1));
    panel.add(languageComboBox, positionComponent(gbc, 1, 4, 1));
    panel.add(restartHintLabel, positionComponent(gbc, 0, 5, 2));

    return panel;
  }

  private JCheckBox createShowInTrayCheckBox() {
    String showInTrayText = tabProvider.getText("setting_tab.show_in_tray");
    JCheckBox checkBox = new JCheckBox(showInTrayText, settings.isShowInTray());

    checkBox.addActionListener(e -> {
      boolean selected = checkBox.isSelected();
      log.debug("Clicked checkbox: {}", checkBox.getName());
      appService.changeShowInTray(selected);
    });

    return checkBox;
  }

  private JCheckBox createHideOnCloseCheckBox(JCheckBox showInTrayCheckBox) {
    String hideOnCloseText = tabProvider.getText("setting_tab.hide_on_close");
    JCheckBox checkBox = new JCheckBox(hideOnCloseText, settings.isHideToTrayOnClose());
    checkBox.setEnabled(showInTrayCheckBox.isSelected());

    showInTrayCheckBox.addActionListener(e -> {
      boolean selected = showInTrayCheckBox.isSelected();
      checkBox.setEnabled(selected);
      log.debug("Hide on close checkbox enabled: {}", selected);
    });

    checkBox.addActionListener(e -> {
      boolean selected = checkBox.isSelected();
      log.debug("Clicked checkbox: {}", checkBox.getName());
      appService.changeHideOnClose(selected);
    });

    return checkBox;
  }

  private JCheckBox createAutostartLauncherCheckBox() {
    String aotustartLauncherText = tabProvider.getText("setting_tab.autostart_launcher");
    JCheckBox checkBox = new JCheckBox(aotustartLauncherText, settings.isAutostartLauncher());

    checkBox.addActionListener(e -> {
      boolean selected = checkBox.isSelected();
      log.debug("Clicked checkbox: {}", checkBox.getName());
      appService.changeAutostartLauncher(selected);
    });

    return checkBox;
  }

  private JCheckBox createAutostartAppCheckBox() {
    String autostartAppText = tabProvider.getText("setting_tab.autostart_app");
    JCheckBox checkBox = new JCheckBox(autostartAppText, settings.isAutostartApp());

    checkBox.addActionListener(e -> {
      boolean selected = checkBox.isSelected();
      log.debug("Clicked checkbox: {}", checkBox.getName());
      appService.changeAutostartApp(selected);
    });

    return checkBox;
  }

  private JComboBox<String> createLanguageComboBox() {
    JComboBox<String> comboBox = new JComboBox<>(lang.keySet().toArray(new String[0]));
    comboBox.setSelectedItem(localizationService.getCurrentLanguage());

    comboBox.addActionListener(e -> {
      log.debug("Language selected");
      String selectedLanguage = (String) comboBox.getSelectedItem();
      appService.changeLanguage(lang.get(selectedLanguage).get(0));
    });

    return comboBox;
  }

  private JLabel createRestartHintLabel() {
    String restartHintText = tabProvider.getText("setting_tab.restart_hint");
    JLabel label = new JLabel(restartHintText);
    label.setForeground(Color.RED);

    return label;
  }

  private GridBagConstraints createGridBagConstraints() {
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(10, 10, 10, 10);

    return gbc;
  }

  private GridBagConstraints positionComponent(GridBagConstraints gbc, int x, int y, int width) {
    gbc.gridx = x;
    gbc.gridy = y;
    gbc.gridwidth = width;
    return gbc;
  }
}
