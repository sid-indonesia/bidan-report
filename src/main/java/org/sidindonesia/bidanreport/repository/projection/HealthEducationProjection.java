package org.sidindonesia.bidanreport.repository.projection;

import java.time.LocalDate;

public interface HealthEducationProjection extends MotherIdentityWhatsAppProjection {
	String getPregnancyTrimester();
	LocalDate getLastMenstrualPeriodDate();
}
