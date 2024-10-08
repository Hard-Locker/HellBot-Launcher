package halot.nikitazolin.launcher.gui;

import java.awt.GraphicsEnvironment;
import java.net.URL;

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
import halot.nikitazolin.launcher.init.settings.model.Settings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class LauncherMainWindow {

  private static final String icoPath = ApplicationRunnerImpl.APP_ICON_PATH;

  private final StatusTab statusTab;
  private final ApplicationTab applicationTab;
  private final ConfigurationTab configurationTab;
  private final ConfigurationDiscordTab configurationDiscordTab;
  private final ConfigurationYoutubeTab configurationYoutubeTab;
  private final ConfigurationDbTab configurationDbTab;
  private final SettingTab settingTab;
  private final SystemTrayManager systemTrayManager;
  private final Settings settings;

  private short width = 700;
  private short height = 500;

  public void makeLauncherWindow(boolean windowVisible) {
    checkHeadless();

    String appName = ApplicationRunnerImpl.LAUNCHER_NAME;

    if (settings.isShowCustomAppName()) {
      if (settings.getCustomAppName() != null && !settings.getCustomAppName().isEmpty()) {
        appName = settings.getCustomAppName();
      }
    }

    JFrame frame = makeWindow(appName);

    if (settings.isShowInTray()) {
      systemTrayManager.makeTray(frame, appName);
    }

    frame.setVisible(windowVisible);
  }

  private JFrame makeWindow(String appName) {
    JFrame frame = new JFrame(appName);
    frame.setSize(width, height);
    frame.setLocationRelativeTo(null);

    if (settings.isShowInTray() && settings.isHideToTrayOnClose()) {
      frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    } else {
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    URL iconUrl = getClass().getResource(icoPath);
    if (iconUrl != null) {
      ImageIcon ico = new ImageIcon(iconUrl);
      frame.setIconImage(ico.getImage());
    } else {
      log.error("Icon not found");
    }

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

    return frame;
  }

  private void checkHeadless() {
    if (GraphicsEnvironment.isHeadless()) {
      log.error("Headless environment detected, cannot run GUI");
      return;
    }
  }
}
