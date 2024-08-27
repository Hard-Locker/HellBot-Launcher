package halot.nikitazolin.launcher.gui;

import java.awt.GraphicsEnvironment;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.springframework.stereotype.Component;

import halot.nikitazolin.launcher.gui.tab.ApplicationTab;
import halot.nikitazolin.launcher.gui.tab.ConfigurationDbTab;
import halot.nikitazolin.launcher.gui.tab.ConfigurationDiscordTab;
import halot.nikitazolin.launcher.gui.tab.ConfigurationTab;
import halot.nikitazolin.launcher.gui.tab.ConfigurationYoutubeTab;
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

  private String appName = "HellBot Launcher";
  private String icoPath = "src/main/resources/image/hellbot_1024x1024.png";
  private short width = 1000;
  private short height = 600;

  public void showLauncherWindow() {
    checkHeadless();

    JFrame frame = new JFrame(appName);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
    tabbedPane.addTab("Статус", statusPanel);
    tabbedPane.addTab("Приложение", appPanel);
    tabbedPane.addTab("Настройки конфигурации", configPanel);
    tabbedPane.addTab("Настройки Discord API", configDiscordPanel);
    tabbedPane.addTab("Настройки YouTube", configYoutubePanel);
    tabbedPane.addTab("Настройки БД", configDbPanel);
    frame.add(tabbedPane);

    frame.setVisible(true);
  }

  private void checkHeadless() {
    if (GraphicsEnvironment.isHeadless()) {
      log.error("Headless environment detected, cannot run GUI");
      return;
    }
  }
}
