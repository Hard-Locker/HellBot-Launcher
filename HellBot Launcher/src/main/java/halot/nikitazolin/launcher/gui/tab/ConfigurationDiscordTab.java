package halot.nikitazolin.launcher.gui.tab;

import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class ConfigurationDiscordTab {

  public JPanel makeTab() {
    JPanel configPanel = new JPanel();
    configPanel.setLayout(new FlowLayout());
    JLabel configLabel = new JLabel("Настройки Discord API");
    JTextArea configTextArea = new JTextArea(20, 50);
    JButton saveConfigButton = new JButton("Сохранить");

    configPanel.add(configLabel);
    configPanel.add(new JScrollPane(configTextArea));
    configPanel.add(saveConfigButton);

    return configPanel;
  }
}
