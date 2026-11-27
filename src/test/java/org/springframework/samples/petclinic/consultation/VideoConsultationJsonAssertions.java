package org.springframework.samples.petclinic.consultation;

import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public final class VideoConsultationJsonAssertions {

    private VideoConsultationJsonAssertions() {
    }

    public static ResultActions assertProblemDetail(ResultActions actions, int status, String title) throws Exception {
        return actions.andExpect(jsonPath("$.status").value(status))
                .andExpect(jsonPath("$.title").value(title));
    }
}
