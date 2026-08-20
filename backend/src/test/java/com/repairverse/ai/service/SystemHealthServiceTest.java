package com.repairverse.ai.service;

import com.repairverse.ai.config.AppProperties;
import com.repairverse.ai.dto.HealthDto.SystemHealthResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SystemHealthServiceTest {

    @Mock
    private DataSource dataSource;

    @Mock
    private Connection connection;

    @Mock
    private AppProperties appProperties;

    @Mock
    private Environment environment;

    @InjectMocks
    private SystemHealthService systemHealthService;

    @BeforeEach
    void setUp() {
        when(environment.getActiveProfiles()).thenReturn(new String[]{"test"});
    }

    @Test
    @DisplayName("Should return UP status when database connection is valid")
    void getSystemHealth_Success() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.isValid(anyInt())).thenReturn(true);

        SystemHealthResponse response = systemHealthService.getSystemHealth();

        assertThat(response.success()).isTrue();
        assertThat(response.data().status()).isEqualTo("UP");
        assertThat(response.data().services().get("database")).isEqualTo("UP");
    }

    @Test
    @DisplayName("Should return DEGRADED status when database check fails")
    void getSystemHealth_DatabaseDown() throws SQLException {
        when(dataSource.getConnection()).thenThrow(new SQLException("Connection refused"));

        SystemHealthResponse response = systemHealthService.getSystemHealth();

        assertThat(response.success()).isTrue();
        assertThat(response.data().status()).isEqualTo("DEGRADED");
        assertThat(response.data().services().get("database")).isEqualTo("DOWN");
    }
}
