package io.dolphin.libsvm.controller;

import io.dolphin.libsvm.entity.CreditScoreData;
import io.dolphin.libsvm.service.CreditScoreService;
import libsvm.svm_model;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class CreditScoreControllerTest {
    @Mock
    private CreditScoreService creditScoreService;

    @InjectMocks
    private CreditScoreController creditScoreController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(creditScoreController).build();
    }

    @Test
    public void testTrainModel() throws Exception {
        svm_model mockModel = mock(svm_model.class);
        when(creditScoreService.trainModel(any(List.class), anyDouble(), anyDouble())).thenReturn(mockModel);

        List<CreditScoreData> trainingData = Arrays.asList(
                new CreditScoreData(Arrays.asList(1.0, 2.0, 3.0), 75.0),
                new CreditScoreData(Arrays.asList(4.0, 5.0, 6.0), 85.0)
        );

        mockMvc.perform(post("/train")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"trainingData\": [{\"features\": [1.0, 2.0, 3.0], \"label\": 75.0}, {\"features\": [4.0, 5.0, 6.0], \"label\": 85.0}], \"C\": 1.0, \"gamma\": 0.1}")
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(mockModel));

        verify(creditScoreService, times(1)).trainModel(any(List.class), eq(1.0), eq(0.1));
    }

    @Test
    public void testPredictCreditScore() throws Exception {
        svm_model mockModel = mock(svm_model.class);
        when(creditScoreService.predict(any(List.class), any(svm_model.class))).thenReturn(75.0);

        mockMvc.perform(post("/predict")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"features\": [1.0, 2.0, 3.0], \"model\": {}}")
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(75.0));

        verify(creditScoreService, times(1)).predict(any(List.class), eq(mockModel));
    }
}