package halot.nikitazolin.launcher.gui.tab;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.springframework.stereotype.Component;

import halot.nikitazolin.launcher.app.AppService;
import halot.nikitazolin.launcher.localization.gui.tab.TabProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class ApplicationTab {

  private final AppService appService;
  private final TabProvider tabProvider;

  public JPanel makeTab() {
    log.debug("Start making tab, {}", this);

    JPanel appPanel = createApplicationPanel();

    return appPanel;
  }

  private JPanel createApplicationPanel() {
    String tabNameText = tabProvider.getText("app_tab.tab_name");
    String currentAppText = tabProvider.getText("app_tab.current_app");
    String currentAppPathText = appService.currentApp() != null ? appService.currentApp()
        : tabProvider.getText("app_tab.current_app_notset");
    String selectAppText = tabProvider.getText("app_tab.select_app");
    String downloadText = tabProvider.getText("app_tab.download");
    String updateText = tabProvider.getText("app_tab.update");

    JPanel appPanel = new JPanel(new BorderLayout());
    appPanel.setName(tabNameText);

    JPanel pathPanel = new JPanel(new BorderLayout());
    JLabel currentAppLabel = new JLabel(currentAppText + ": ");

    JTextArea currentAppPathArea = new JTextArea(currentAppPathText);
    currentAppPathArea.setEditable(false);
    currentAppPathArea.setLineWrap(true);
    currentAppPathArea.setWrapStyleWord(true);
    JScrollPane scrollPane = new JScrollPane(currentAppPathArea);
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    scrollPane.setPreferredSize(new Dimension(350, 40));

    pathPanel.add(currentAppLabel, BorderLayout.WEST);
    pathPanel.add(scrollPane, BorderLayout.CENTER);

    JPanel controlsPanel = new JPanel(new GridBagLayout());
    JButton selectAppButton = new JButton(selectAppText);
    JButton downloadButton = new JButton(downloadText);
    JButton updateButton = new JButton(updateText);

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(10, 10, 10, 10);

    gbc.gridx = 0;
    gbc.gridy = 0;
    controlsPanel.add(downloadButton, gbc);

    gbc.gridx = 1;
    gbc.gridy = 0;
    controlsPanel.add(updateButton, gbc);

    gbc.gridx = 2;
    gbc.gridy = 0;
    controlsPanel.add(selectAppButton, gbc);

    appPanel.add(pathPanel, BorderLayout.NORTH);
    appPanel.add(controlsPanel, BorderLayout.CENTER);

    selectAppButton.addActionListener(e -> {
      log.debug("Select App button clicked");
      appService.select();
    });

    downloadButton.addActionListener(e -> {
      log.debug("Download button clicked");
      appService.download();
    });

    updateButton.addActionListener(e -> {
      log.debug("Update button clicked");
      appService.update();
    });

    return appPanel;
  }
}
