package com.optifi.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_preferences")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserPreference {
    @Id
    @EqualsAndHashCode.Include
    Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(length = 3, nullable = false)
    private String baseCurrency;

    @Column(length = 10)
    @Builder.Default
    private String locale = "en_US";
}
