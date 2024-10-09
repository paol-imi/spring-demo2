package org.example.library.actuator;

import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Custom info contributor for the library application.
 */
@Component
public class AppInfoContributor implements InfoContributor {
    /**
     * Contribute information about the library application.
     *
     * @param builder the builder to contribute information to
     */
    @Override
    public void contribute(Info.Builder builder) {
        // TODO: Read those from pom.xml
        builder.withDetail("custom-info", Map.of(
                "app-name", "Library App",
                "version", "1.0.0"));
    }
}
