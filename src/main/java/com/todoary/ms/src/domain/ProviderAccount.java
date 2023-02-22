package com.todoary.ms.src.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;

import static com.todoary.ms.src.domain.Provider.APPLE;

@Getter
@NoArgsConstructor
@EqualsAndHashCode(of = {"provider", "providerId"})
@Embeddable
public class ProviderAccount {
    @Enumerated(EnumType.STRING)
    @NotNull
    private Provider provider;

    @NotNull
    private String providerId;

    private ProviderAccount(Provider provider, String providerId) {
        this.provider = provider;
        this.providerId = providerId;
    }

    public static ProviderAccount none() {
        return new ProviderAccount(Provider.NONE, "none");
    }

    public static ProviderAccount appleFrom(String providerId) {
        return new ProviderAccount(APPLE, providerId);
    }

    public static ProviderAccount googleFrom(String providerId) {
        return new ProviderAccount(Provider.GOOGLE, providerId);
    }

    public static ProviderAccount from(String provider, String provider_id) {
        return new ProviderAccount(Provider.findByProviderName(provider), provider_id);
    }
}
