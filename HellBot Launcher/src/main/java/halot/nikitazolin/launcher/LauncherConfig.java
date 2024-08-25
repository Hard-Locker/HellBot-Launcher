package halot.nikitazolin.launcher;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
@ComponentScan(basePackages = "halot.nikitazolin.launcher")
@RequiredArgsConstructor
public class LauncherConfig {

}
