package com.todoary.ms.src.domain;

import java.util.Arrays;

public enum Provider {
    GOOGLE,
    APPLE,
    NONE;

    public static Provider findByProviderName(String providerName) {
        return Arrays.stream(Provider.values())
                .filter(provider -> provider.toString().equals(providerName.toUpperCase()))
                .findAny()
                .orElse(NONE);
    }
}
