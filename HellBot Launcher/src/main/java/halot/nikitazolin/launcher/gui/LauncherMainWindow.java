package halot.nikitazolin.launcher.gui;

import java.awt.GraphicsEnvironment;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.springframework.stereotype.Component;

import halot.nikitazolin.launcher.ApplicationRunnerImpl;
import halot.nikitazolin.launcher.gui.tab.ApplicationTab;
import halot.nikitazolin.launcher.gui.tab.ConfigurationDbTab;
import halot.nikitazolin.launcher.gui.tab.ConfigurationDiscordTab;
import halot.nikitazolin.launcher.gui.tab.ConfigurationTab;
import halot.nikitazolin.launcher.gui.tab.ConfigurationYoutubeTab;
import halot.nikitazolin.launcher.gui.tab.SettingTab;
import halot.nikitazolin.launcher.gui.tab.StatusTab;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class LauncherMainWindow {

  private final StatusTab statusTab;
  private final ApplicationTab applicationTab;
  private final ConfigurationTab configurationTab;
  private final ConfigurationDiscordTab configurationDiscordTab;
  private final ConfigurationYoutubeTab configurationYoutubeTab;
  private final ConfigurationDbTab configurationDbTab;
  private final SettingTab settingTab;
  private final SystemTrayManager systemTrayManager;

  private String appName = ApplicationRunnerImpl.APP_NAME;
  private String icoPath = ApplicationRunnerImpl.APP_ICON_PATH;
  private short width = 700;
  private short height = 500;

  public void showLauncherWindow(boolean visible) {
    checkHeadless();

    JFrame frame = new JFrame(appName);
    frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    frame.setSize(width, height);
    frame.setLocationRelativeTo(null);

    ImageIcon ico = new ImageIcon(icoPath);
    frame.setIconImage(ico.getImage());

    JTabbedPane tabbedPane = new JTabbedPane();
    JPanel statusPanel = statusTab.makeTab();
    JPanel appPanel = applicationTab.makeTab();
    JPanel configPanel = configurationTab.makeTab();
    JPanel configDiscordPanel = configurationDiscordTab.makeTab();
    JPanel configYoutubePanel = configurationYoutubeTab.makeTab();
    JPanel configDbPanel = configurationDbTab.makeTab();
    JPanel settingPanel = settingTab.makeTab();
    tabbedPane.addTab(statusPanel.getName(), statusPanel);
    tabbedPane.addTab(appPanel.getName(), appPanel);
    tabbedPane.addTab(configPanel.getName(), configPanel);
    tabbedPane.addTab(configDiscordPanel.getName(), configDiscordPanel);
    tabbedPane.addTab(configYoutubePanel.getName(), configYoutubePanel);
    tabbedPane.addTab(configDbPanel.getName(), configDbPanel);
    tabbedPane.addTab(settingPanel.getName(), settingPanel);
    frame.add(tabbedPane);
    
    systemTrayManager.hideToTray(frame);

    frame.setVisible(visible);
  }

  private void checkHeadless() {
    if (GraphicsEnvironment.isHeadless()) {
      log.error("Headless environment detected, cannot run GUI");
      return;
    }
  }
}
