package com.driveflow.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("בדיקות SearchController")
class SearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("TC-006a: GET /search ללא פרמטרים — מחזיר עמוד חיפוש 200")
    void getSearchPage_NoParams_Returns200WithForm() throws Exception {
        mockMvc.perform(get("/search"))
                .andExpect(status().isOk())
                .andExpect(view().name("search/results"))
                .andExpect(model().attributeExists("branches"))
                .andExpect(model().attributeExists("categories"))
                .andExpect(model().attributeExists("vehicles"))
                .andExpect(model().attributeExists("numDays"));
    }

    @Test
    @DisplayName("TC-006b: GET /search עם תאריכים תקינים — מחזיר vehicles ב-model")
    void getSearchPage_WithValidParams_ReturnsVehiclesInModel() throws Exception {
        mockMvc.perform(get("/search")
                .param("pickupBranchId", "1")
                .param("pickupDate", "2026-06-10T10:00:00")
                .param("returnDate", "2026-06-15T10:00:00"))
                .andExpect(status().isOk())
                .andExpect(view().name("search/results"))
                .andExpect(model().attributeExists("vehicles"))
                .andExpect(model().attributeExists("numDays"));
    }

    @Test
    @DisplayName("TC-006c: GET /search — model מכיל branches ו-categories תמיד")
    void getSearchPage_AlwaysContainsBranchesAndCategories() throws Exception {
        mockMvc.perform(get("/search"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("branches"))
                .andExpect(model().attributeExists("categories"));
    }
}
