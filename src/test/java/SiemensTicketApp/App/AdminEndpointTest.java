package SiemensTicketApp.App;


import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AdminControllerTest extends BaseIntegrationTest {

    // Credentials from application.properties
    private static final String ADMIN_USER = "admin";
    private static final String ADMIN_PASS = "admin123";

    @Test
    void shouldReturnAllTrainsForAdmin() throws Exception {
        mockMvc.perform(get("/api/admin/trains")
                        .with(httpBasic(ADMIN_USER, ADMIN_PASS)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").exists());
    }

    @Test
    void shouldRejectUnauthorizedAccessToAdminEndpoint() throws Exception {
        mockMvc.perform(get("/api/admin/trains"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReportDelayAndNotifyCustomers() throws Exception {
        mockMvc.perform(post("/api/admin/schedules/1/delay")
                        .with(httpBasic(ADMIN_USER, ADMIN_PASS))
                        .param("minutes", "30"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("notified")));
    }

    @Test
    void shouldGetBookingsForTrain() throws Exception {
        mockMvc.perform(get("/api/admin/trains/1/bookings")
                        .with(httpBasic(ADMIN_USER, ADMIN_PASS)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
