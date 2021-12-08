package org.sidindonesia.bidanreport.repository.projection;

public interface HealthEducationProjection extends MotherIdentityWhatsAppProjection {
	String getPregnancyTrimester();

	String getCalculatedGestationalAge();
}
