package com.vgarcia.marketplace.domain.models;


import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public record ClientDomain(
        Long id,
        String email,
        String name,
        String lastName,
        String phoneNumber,
        String address,
        LocalDate birthDate,
        LocalDateTime registrationDate,
        LocalDateTime lastActivityDate,
        LocalDate lastRenewalDate,
        Date legacyRegistrationDate

) {

    public long getYearsOfAntiquity() {
        if (this.registrationDate == null) {
            return 0;
        }
        return ChronoUnit.YEARS.between(this.registrationDate.toLocalDate(), LocalDate.now());
    }

    public long getDaysSinceLegacyRegistration() {
        if (this.legacyRegistrationDate == null) {
            return 0;
        }
        long diffInMillis = new Date().getTime() - this.legacyRegistrationDate.getTime();
        return diffInMillis / (1000 * 60 * 60 * 24);
    }

}
