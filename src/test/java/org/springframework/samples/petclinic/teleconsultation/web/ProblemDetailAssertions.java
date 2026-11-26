package org.springframework.samples.petclinic.teleconsultation.web;

import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public final class ProblemDetailAssertions {

    private ProblemDetailAssertions() {
    }

    public static ResultActions assertProblemDetail(ResultActions actions) throws Exception {
        return actions
                .andExpect(content().contentType("application/problem+json"))
                .andExpect(jsonPath("$.title").exists())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.detail").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.path").exists());
    }
}
