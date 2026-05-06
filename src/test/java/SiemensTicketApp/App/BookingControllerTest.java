package SiemensTicketApp.App;

import SiemensTicketApp.App.DTO.BookingRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class BookingControllerTest extends BaseIntegrationTest {

    @Test
    void shouldBookTicketSuccessfully() throws Exception {
        BookingRequest request = new BookingRequest(1L, "John Doe", "john@example.com", 2);

        mockMvc.perform(post("/api/public/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerName").value("John Doe"))
                .andExpect(jsonPath("$.customerEmail").value("john@example.com"))
                .andExpect(jsonPath("$.seatsBooked").value(2));
    }

    @Test
    void shouldRejectBookingWhenSeatsExceedCapacity() throws Exception {
        // Train IC-101 has capacity 100, try to book 999
        BookingRequest request = new BookingRequest(1L, "John Doe", "john@example.com", 999);

        mockMvc.perform(post("/api/public/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void shouldRejectBookingWithInvalidEmail() throws Exception {
        BookingRequest request = new BookingRequest(1L, "John Doe", "not-an-email", 2);

        mockMvc.perform(post("/api/public/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.customerEmail").exists());
    }

    @Test
    void shouldRejectBookingWithZeroSeats() throws Exception {
        BookingRequest request = new BookingRequest(1L, "John Doe", "john@example.com", 0);

        mockMvc.perform(post("/api/public/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldPreventOverbooking() throws Exception {
        // Fill up all 100 seats
        BookingRequest first = new BookingRequest(1L, "John Doe", "john@example.com", 99);
        mockMvc.perform(post("/api/public/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(first)))
                .andExpect(status().isOk());

        // Try to book 5 more — only 1 seat left
        BookingRequest second = new BookingRequest(1L, "Jane Doe", "jane@example.com", 5);
        mockMvc.perform(post("/api/public/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(second)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").exists());
    }
}
