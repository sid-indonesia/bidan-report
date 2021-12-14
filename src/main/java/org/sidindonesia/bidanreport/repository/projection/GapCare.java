package org.sidindonesia.bidanreport.repository.projection;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GapCare {
	private String ancDate;
	private String gestationalAge;
	private String heightInCm;
	private String weightInKg;
	private String midUpperArmCircumferenceInCm;
	private String vitalSignSystolicBloodPressure;
	private String vitalSignDiastolicBloodPressure;
	private String uterineFundalHeight;
	private String fetalPresentation;
	private String fetalHeartRate;
	private String tetanusToxoidImmunizationStatus;
	private String isGivenTetanusToxoidInjection;
	private String isGivenIronFolicAcidTablet;
	private String hasProteinuria;
	private String hbLevelLabTestResult;
	private String isGlucoseBloodMoreThan140MgDl;
	private String hasThalasemia;
	private String hasSyphilis;
	private String hasHBsAg;
	private String hasHiv;
}
