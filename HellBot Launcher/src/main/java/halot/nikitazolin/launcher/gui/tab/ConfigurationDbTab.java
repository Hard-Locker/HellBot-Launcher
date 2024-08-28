package halot.nikitazolin.launcher.gui.tab;

import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.springframework.stereotype.Component;

import halot.nikitazolin.launcher.localization.gui.tab.TabProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class ConfigurationDbTab {

  private final TabProvider tabProvider;

  public JPanel makeTab() {
    JPanel configPanel = new JPanel();
    configPanel.setLayout(new FlowLayout());
    JLabel configLabel = new JLabel("Настройки БД");
    JTextArea configTextArea = new JTextArea(20, 50);
    JButton saveConfigButton = new JButton("Сохранить");

    configPanel.add(configLabel);
    configPanel.add(new JScrollPane(configTextArea));
    configPanel.add(saveConfigButton);

    return configPanel;
  }
}
