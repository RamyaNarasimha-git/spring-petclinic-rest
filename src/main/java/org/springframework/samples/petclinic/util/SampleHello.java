package org.springframework.samples.petclinic.util;

/**
 * Simple sample class added for demonstration purposes.
 */
public final class SampleHello {

    private SampleHello() {
        // Utility class
    }

    public static String message() {
        return "Hello from SampleHello";
    }
}
