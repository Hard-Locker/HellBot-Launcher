package halot.nikitazolin.launcher.gui.tab;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.springframework.stereotype.Component;

import halot.nikitazolin.launcher.app.AppService;
import halot.nikitazolin.launcher.localization.gui.tab.TabProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class StatusTab {

  private final AppService appService;
  private final TabProvider tabProvider;

  public JPanel makeTab() {
    log.debug("Start making tab, {}", this);

    String tabNameText = tabProvider.getText("status_tab.tab_name");
    String startText = tabProvider.getText("status_tab.start");
    String stopText = tabProvider.getText("status_tab.stop");
    String startupText = tabProvider.getText("status_tab.startup");

    JPanel statusPanel = new JPanel(new BorderLayout());
    statusPanel.setName(tabNameText);

    JLabel statusLabel = new JLabel();
    statusLabel.setOpaque(true);
    statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
    updateStatusBar(statusLabel);

    JPanel controlsPanel = new JPanel(new GridBagLayout());
    JButton startButton = new JButton(startText);
    JButton stopButton = new JButton(stopText);
    JCheckBox startupCheckBox = new JCheckBox(startupText);

    Dimension buttonSize = new Dimension(100, 30);
    startButton.setPreferredSize(buttonSize);
    stopButton.setPreferredSize(buttonSize);

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(10, 10, 10, 10);

    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.anchor = GridBagConstraints.WEST;
    controlsPanel.add(startButton, gbc);

    gbc.gridx = 1;
    gbc.gridy = 0;
    controlsPanel.add(stopButton, gbc);

    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.gridwidth = 2;
    gbc.anchor = GridBagConstraints.WEST;
    controlsPanel.add(startupCheckBox, gbc);

    statusPanel.add(statusLabel, BorderLayout.NORTH);
    statusPanel.add(controlsPanel, BorderLayout.CENTER);

    startButton.addActionListener(e -> {
      appService.start();
      updateStatusBar(statusLabel);
    });

    stopButton.addActionListener(e -> {
      appService.stop();
      updateStatusBar(statusLabel);
    });
    
    startupCheckBox.addActionListener(e -> {
      appService.changeStartup();
    });

    return statusPanel;
  }

  private void updateStatusBar(JLabel statusLabel) {
    if (appService.isRunning()) {
      String statusOnText = tabProvider.getText("status_tab.status_on");
      statusLabel.setText(statusOnText);
      statusLabel.setBackground(Color.GREEN);
    } else {
      String statusOffText = tabProvider.getText("status_tab.status_off");
      statusLabel.setText(statusOffText);
      statusLabel.setBackground(Color.RED);
    }
  }
}
