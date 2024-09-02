package halot.nikitazolin.launcher.gui.tab;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.springframework.stereotype.Component;

import halot.nikitazolin.launcher.app.AppService;
import halot.nikitazolin.launcher.app.manager.AppStatusObserver;
import halot.nikitazolin.launcher.localization.gui.tab.TabProvider;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class StatusTab implements AppStatusObserver {

  private final AppService appService;
  private final TabProvider tabProvider;

  JLabel statusLabel = new JLabel();

  @PostConstruct
  public void init() {
    appService.addObserver(this);
  }

  @PreDestroy
  public void destroy() {
    appService.removeObserver(this);
  }

  @Override
  public void onAppStatusChanged() {
    updateStatusBar();
  }

  public JPanel makeTab() {
    log.debug("Start making tab, {}", this);

    JPanel statusPanel = createStatusPanel();
    JLabel statusLabel = createStatusLabel();
    JPanel controlsPanel = createControlsPanel();

    statusPanel.add(statusLabel, BorderLayout.NORTH);
    statusPanel.add(controlsPanel, BorderLayout.CENTER);

    return statusPanel;
  }

  private JPanel createStatusPanel() {
    String tabNameText = tabProvider.getText("status_tab.tab_name");
    JPanel statusPanel = new JPanel(new BorderLayout());
    statusPanel.setName(tabNameText);
    return statusPanel;
  }

  private JLabel createStatusLabel() {
    statusLabel.setOpaque(true);
    statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
    updateStatusBar();
    return statusLabel;
  }

  private JPanel createControlsPanel() {
    String startText = tabProvider.getText("status_tab.start");
    String stopText = tabProvider.getText("status_tab.stop");

    JPanel controlsPanel = new JPanel(new GridBagLayout());
    JButton startButton = new JButton(startText);
    JButton stopButton = new JButton(stopText);

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

    startButton.addActionListener(e -> {
      appService.start();
      updateStatusBar();
    });

    stopButton.addActionListener(e -> {
      appService.stop();
      updateStatusBar();
    });

    return controlsPanel;
  }

  private void updateStatusBar() {
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
