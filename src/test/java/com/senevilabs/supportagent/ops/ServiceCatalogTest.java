package com.senevilabs.supportagent.ops;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ServiceCatalogTest {

    @Test
    void allowsOnlyConfiguredContainers() {
        ServiceCatalog catalog = new ServiceCatalog();
        catalog.setDockerContainers(List.of("vpn-db"));

        assertThat(catalog.allowsDockerContainer("vpn-db")).isTrue();
        assertThat(catalog.allowsDockerContainer("prod-db")).isFalse();
    }

    @Test
    void refusesEverythingWhenNoneConfigured() {
        ServiceCatalog catalog = new ServiceCatalog();

        assertThat(catalog.getDockerContainers()).isEmpty();
        assertThat(catalog.allowsDockerContainer("vpn-db")).isFalse();
        assertThat(catalog.describeDockerContainers()).isEqualTo("(none configured)");
    }

    @Test
    void describesConfiguredContainers() {
        ServiceCatalog catalog = new ServiceCatalog();
        catalog.setDockerContainers(List.of("vpn-db", "crm-app"));

        assertThat(catalog.describeDockerContainers()).isEqualTo("vpn-db, crm-app");
    }
}