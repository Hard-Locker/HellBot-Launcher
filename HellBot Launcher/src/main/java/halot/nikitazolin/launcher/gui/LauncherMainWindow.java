package halot.nikitazolin.launcher.gui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class LauncherMainWindow {

  private String appName = "HellBot Launcher";
  private String icoPath = "src/main/resources/image/hellbot_1024x1024.png";
  private short width = 1200;
  private short height = 700;

  public void showLauncherWindow() {
    if (GraphicsEnvironment.isHeadless()) {
      log.error("Headless environment detected, cannot run GUI");
      return;
    }

    JFrame frame = new JFrame(appName);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(width, height);
    frame.setResizable(false);
    frame.setLayout(new FlowLayout());

    ImageIcon ico = new ImageIcon(icoPath);
    frame.setIconImage(ico.getImage());

    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    frame.setLocation(((screenSize.width / 2) - (width / 2)), ((screenSize.height / 2) - (height / 2)));

    JButton selectBotJarButton = new JButton("Выбрать/Скачать JAR");
    JButton configureBotButton = new JButton("Настройка Бота");
    JButton updateBotButton = new JButton("Обновить Бота");
    JButton startBotButton = new JButton("Запуск Бота");
    JButton stopBotButton = new JButton("Остановка Бота");

    frame.add(selectBotJarButton);
    frame.add(configureBotButton);
    frame.add(updateBotButton);
    frame.add(startBotButton);
    frame.add(stopBotButton);

    selectBotJarButton.addActionListener(e -> JOptionPane.showMessageDialog(null, "Выбрать/Скачать JAR"));
    configureBotButton.addActionListener(e -> JOptionPane.showMessageDialog(null, "Настройка Бота"));
    updateBotButton.addActionListener(e -> JOptionPane.showMessageDialog(null, "Обновить Бота"));
    startBotButton.addActionListener(e -> JOptionPane.showMessageDialog(null, "Запуск Бота"));
    stopBotButton.addActionListener(e -> JOptionPane.showMessageDialog(null, "Остановка Бота"));

    frame.setVisible(true);
  }
}
