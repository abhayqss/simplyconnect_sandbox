package com.scnsoft.eldermark.h2;

import com.scnsoft.eldermark.service.WebServiceClientFactory;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.test.context.ActiveProfiles;

import javax.annotation.PostConstruct;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Supplier;

/**
 * @author phomal
 * Created on 2/13/17.
 * <p>
 * P.S moved and adjusted from old portal by
 * @author sparuchnik
 */
@Configuration
@ComponentScan(basePackages = {"com.scnsoft.eldermark"})
@PropertySource({"classpath:application-h2.properties"})
@ActiveProfiles("h2")
public class TestApplicationH2Config {

    //mock for faster context startup
    @MockBean
    WebServiceClientFactory webServiceClientFactory;

    @SpyBean
    private JdbcTemplate jdbcTemplate;

    //'remember' session which opens key instead of actual execution
    @PostConstruct
    public void initJdbcTemplate() {
        Mockito
                .lenient()
                .doAnswer((Answer<Void>) invocationOnMock -> {
                    OpenKeySessionTestSupport.addSession(physicalSessionIdFetcher.get());
                    return null;
                })
                .when(jdbcTemplate)
                .execute("OPEN SYMMETRIC KEY SymmetricKey1 DECRYPTION BY CERTIFICATE Certificate1");

    }

    public interface PhysicalSessionIdFetcher extends Supplier<Integer> {
    }

    //additional Bean to fetch current physical session id
    @Bean
    public PhysicalSessionIdFetcher physicalSessionIdFetcher(JdbcTemplate jdbcTemplate) {
        return () -> {
            final Integer[] sessionId = new Integer[1];
            jdbcTemplate.query("CALL SESSION_ID()", new RowCallbackHandler() {
                @Override
                public void processRow(ResultSet resultSet) throws SQLException {
                    sessionId[0] = resultSet.getInt(1);
                }
            });

            return sessionId[0];
        };
    }

    @Autowired
    private PhysicalSessionIdFetcher physicalSessionIdFetcher;
}
