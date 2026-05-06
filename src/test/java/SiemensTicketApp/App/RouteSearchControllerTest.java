package SiemensTicketApp.App;
import SiemensTicketApp.App.DTO.TripSearchRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class RouteSearchControllerTest extends BaseIntegrationTest {

    @Test
    void shouldFindDirectConnection() throws Exception {
        TripSearchRequest request = new TripSearchRequest("Budapest", "Vienna");

        mockMvc.perform(post("/api/public/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].requiresChangeover").value(false))
                .andExpect(jsonPath("$[0].stations[0]").value("Budapest"));
    }

    @Test
    void shouldFindConnectionWithChangeover() throws Exception {
        // Budapest -> Prague requires a changeover (Vienna or Bratislava)
        TripSearchRequest request = new TripSearchRequest("Budapest", "Prague");

        mockMvc.perform(post("/api/public/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].requiresChangeover").value(true));
    }

    @Test
    void shouldReturnErrorForUnknownStation() throws Exception {
        TripSearchRequest request = new TripSearchRequest("Budapest", "Tokyo");

        mockMvc.perform(post("/api/public/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void shouldReturnErrorForNoConnection() throws Exception {
        // Warsaw has no route connecting it to anything in our seed data
        TripSearchRequest request = new TripSearchRequest("Budapest", "Warsaw");

        mockMvc.perform(post("/api/public/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }
}
