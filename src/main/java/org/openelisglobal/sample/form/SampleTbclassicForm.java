package org.openelisglobal.sample.form;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.openelisglobal.common.form.BaseForm;
import org.openelisglobal.common.util.IdValuePair;
import org.openelisglobal.common.util.validator.CustomDateValidator.DateRelation;
import org.openelisglobal.common.validator.ValidationHelper;
import org.openelisglobal.validation.annotations.SafeHtml;
import org.openelisglobal.validation.annotations.ValidAccessionNumber;
import org.openelisglobal.validation.annotations.ValidDate;
import org.openelisglobal.validation.annotations.ValidName;
import org.openelisglobal.validation.annotations.ValidTime;
import org.openelisglobal.validation.annotations.SafeHtml.SafeListLevel;
import org.openelisglobal.validation.constraintvalidator.NameValidator.NameType;


public class SampleTbclassicForm extends BaseForm {
    

    private static final long serialVersionUID = 1L;

	private Boolean rememberSiteAndRequester;
	
	private String labnoForSearch;
	private String providerFaxNumber;
	
	private String providerEmail; 
	private String  patientMonth; 
	

	private String  providerWorkPhone;

    @ValidDate(relative = DateRelation.TODAY)
    private String currentDate = "";

    // for display
    private List<IdValuePair> referralOrganizations;
    
    private List<IdValuePair> genders;
    
    @Pattern(regexp = ValidationHelper.ID_REGEX)
    private String tbOrderReason;
    
    private List<IdValuePair> tbOrderReasons;
    
    @Pattern(regexp = ValidationHelper.ID_REGEX)
    private String tbDiagnosticReason;
    
    private List<IdValuePair> tbDiagnosticReasons;
    
    @Pattern(regexp = ValidationHelper.ID_REGEX)
    private String tbFollowupReason;
    
    private List<IdValuePair> tbFollowupReasons;
    
    
    
    
    @Pattern(regexp = ValidationHelper.ID_REGEX)
    private String microbioSpecimenNature;
    
    private List<IdValuePair> microbioSpecimenNatures;
    
    @SafeHtml(level = SafeListLevel.NONE)
    private String tbFollowupPeriodLine1;
    
    @SafeHtml(level = SafeListLevel.NONE)
    private String tbFollowupPeriodLine2;
    
    private List<IdValuePair> tbFollowupPeriodsLine1;
    
    private List<IdValuePair> tbFollowupPeriodsLine2;
    
    private List<IdValuePair> tbDiagnosticMethods;
    
    @Pattern(regexp = ValidationHelper.ID_REGEX)
    private String tbAspect;
    
    private List<IdValuePair> tbAspects;
    
    // for display
    private List<IdValuePair> sampleTypes;

    @Pattern(regexp = ValidationHelper.PATIENT_ID_REGEX)
    private String patientNationalityId;

    @Pattern(regexp = ValidationHelper.PATIENT_ID_REGEX)
    private String guid;
//    private UUID fhirUuid;


    
    
    @NotBlank()
    @Pattern(regexp = ValidationHelper.GENDER_REGEX)
    private String patientGender;
    
    
    
    
    
    
    
    
    
    // ages are display only
    private String patientAge;
    private String patientDay;
    
    @ValidDate(relative = DateRelation.PAST)
    private String patientBirthDate = "";

    @SafeHtml(level = SafeHtml.SafeListLevel.NONE)
    private String requesterName;

    @SafeHtml(level = SafeHtml.SafeListLevel.NONE)
    private String externalOrderNumber;

    @NotBlank()
    @ValidAccessionNumber()
    private String labNo;

    @ValidDate(relative = DateRelation.PAST)
    private String requestDate;
    
    @NotEmpty()
    private List<String> newSelectedTests;

   
    
    //for updates
    private List<String> selectedTestToRemove;
    
    //for updates
    private String selectedMethodToRemove;

    @NotBlank()
    @ValidDate(relative = DateRelation.PAST)
    private String receivedDate;

    @ValidTime()
    private String receivedTime;

    @SafeHtml(level = SafeHtml.SafeListLevel.NONE)
    private String referringPatientNumber;

    @SafeHtml(level = SafeHtml.SafeListLevel.NONE)
    private String referringSiteCode;

    @SafeHtml()
    private String referringSiteName;

    @Pattern(regexp = ValidationHelper.ID_REGEX)
    private String providerPersonId;

    @ValidName(nameType = NameType.FIRST_NAME)
    private String providerFirstName;

    
    
    
    private String sitereferent;
    @ValidName(nameType = NameType.LAST_NAME)
    private String providerLastName;
    
    @ValidName(nameType = NameType.FIRST_NAME)
    private String patientFirstName;
    
    @ValidName(nameType = NameType.LAST_NAME)
    private String patientLastName;
    
    @Pattern(regexp = ValidationHelper.PHONE_REGEX)
    private String patientPhone;
    
    @SafeHtml(level = SafeHtml.SafeListLevel.NONE)
    private String patientAddress;
    
    @Pattern(regexp = ValidationHelper.PATIENT_ID_REGEX)
    private String tbSubjectNumber;
    
    @NotNull()
    private Boolean modified = false;

    @Pattern(regexp = ValidationHelper.ID_REGEX)
    private String sampleId;
    
    @Pattern(regexp = ValidationHelper.ID_REGEX)
    private String tbSpecimenNature;
    
    private List<IdValuePair> tbSpecimenNatures;
    private List<IdValuePair> referringSiteCodes;
    @Pattern(regexp = ValidationHelper.ID_REGEX)
    private String rejectReason;
    
    private String tbSpecimen;
   

    private boolean readOnly = false;

    // for display
    private List<IdValuePair> testSectionList;
    
    private List<IdValuePair>prescriptionTypes;

    // for display
    private List<IdValuePair> rejectReasonList;
	
	private String providerpayment;
	
	private String epidemiologique;
	
	private String providerprelevement;
	private String prescriptionType;
	private String urgent;
	
	
	
	
	
	private String personndomicile;
	private String personcommune;
	private String patientVilleVillage ;
	
	
	//new
    @Pattern(regexp = ValidationHelper.PHONE_REGEX)
    private String personPhoneNumber;
	
    
 
  
    
 

    private String clinicalInformation;
    
    private List<IdValuePair> microbioclinicalinfos;
    
    
	
    
    //@Pattern(regexp = ValidationHelper.ID_REGEX)
    private String orginalservice;
    
    private List<IdValuePair> orginalservices;
    
    //@Pattern(regexp = ValidationHelper.ID_REGEX)
    private String gestesInvasif;
    
    private List<IdValuePair> gestesInvasifs;
	
    @Pattern(regexp = ValidationHelper.ID_REGEX)
	private String patientRegion;
    private List<IdValuePair> patientRegions;
  
    
    
	private String  personDistrict;  
	private String patientNiveauEtude;
	private String patientProfession;

	
	private String hospitalisationEnCour;
	private String numeroDeChambre;
	
	private String otherOrginalservice;

	
	
	private String antibiotherapieRecent;
	private String antibiotique1;
	private String antibiotique2;
	private String antibiotique3;
	private String dureeTraitement;
	private String traitementEncours;
	private String hospitalisationRecent;
	private String nombreHospitalisations;
	private String otherField;
	private String otherInfoClinical;
	
	private String autresGestesInvasifs;
	private String tbSpecimenState;
	private String tbCollectionDate;
	private String tbCollectionTime;
	private String collecteurname;
	private String tbSubjectNumberthree;
	private String tbSubjectNumbertwo;
	private String tbSubjectNumberone;
	private String nonConformityNature;
	
	private String pregnancyStatus;
	
	
	  @Pattern(regexp = ValidationHelper.ID_REGEX)
	private String  education;
    private List<IdValuePair> educations; 
    @Pattern(regexp = ValidationHelper.ID_REGEX)
  	private String  tbTypeExamen;
     private List<IdValuePair>tbTypeExamens;
                   
    private List<IdValuePair> providerpayments;
    
    private List<IdValuePair>  tbSpecimenStates;
    private List<IdValuePair>  epidemiologiques;
    private List<IdValuePair>  urgents;
    
    private  List<IdValuePair>  prescriptionTypas;
 
    
    private List<IdValuePair> noConformitynatures;
    
    
    @Pattern(regexp = ValidationHelper.ID_REGEX)
  	private String  dispositif;
      private List<IdValuePair>dispositifs;
    
    
    private boolean useReferral;
	
    public List<IdValuePair> getUrgents() {
		return urgents;
	}

	public void setUrgents(List<IdValuePair> urgents) {
		this.urgents = urgents;
	}

	public List<IdValuePair> getEpidemiologiques() {
		return epidemiologiques;
	}

	public void setEpidemiologiques(List<IdValuePair> epidemiologiques) {
		this.epidemiologiques = epidemiologiques;
	}

	public List<IdValuePair> getPatientDistricts() {
		return patientDistricts;
	}
	
	

	public List<IdValuePair> getPrescriptionTypas() {
		return prescriptionTypas;
	}

	public void setPrescriptionTypas(List<IdValuePair> prescriptionTypas) {
		this.prescriptionTypas = prescriptionTypas;
	}

	public String getTbSpecimen() {
		return tbSpecimen;
	}

	public void setTbSpecimen(String tbSpecimen) {
		this.tbSpecimen = tbSpecimen;
	}

	public void setPatientDistricts(List<IdValuePair> patientDistricts) {
		this.patientDistricts = patientDistricts;
	}

	public String getDispositif() {
		return dispositif;
	}

	public void setDispositif(String dispositif) {
		this.dispositif = dispositif;
	}

	public String getOtherInfoClinical() {
		return otherInfoClinical;
	}

	public void setOtherInfoClinical(String otherInfoClinical) {
		this.otherInfoClinical = otherInfoClinical;
	}

	public List<IdValuePair> getDispositifs() {
		return dispositifs;
	}

	public void setDispositifs(List<IdValuePair> dispositifs) {
		this.dispositifs = dispositifs;
	}

	private List<IdValuePair> patientDistricts; 
	   
	public List<IdValuePair> getEducations() {
		return educations;
	}

	public void setEducations(List<IdValuePair> educations) {
		this.educations = educations;
	}

	public String getOtherOrginalservice() {
		return otherOrginalservice;
	}

	public void setOtherOrginalservice(String otherOrginalservice) {
		this.otherOrginalservice = otherOrginalservice;
	}

	

	public String getEducation() {
		return education;
	}

	public void setEducation(String education) {
		this.education = education;
	}

	public String getOrginalservice() {
		return orginalservice;
	}

	public void setOrginalservice(String orginalservice) {
		this.orginalservice = orginalservice;
	}

	public List<IdValuePair> getOrginalservices() {
		return orginalservices;
	}

	public void setOrginalservices(List<IdValuePair> orginalservices) {
		this.orginalservices = orginalservices;
	}

	public List<IdValuePair> getNoConformitynatures() {
		return noConformitynatures;
	}

	public void setNoConformitynatures(List<IdValuePair> noConformitynatures) {
		this.noConformitynatures = noConformitynatures;
	}

	public String getGestesInvasif() {
		return gestesInvasif;
	}

	public void setGestesInvasif(String gestesInvasif) {
		this.gestesInvasif = gestesInvasif;
	}

	public List<IdValuePair> getGestesInvasifs() {
		return gestesInvasifs;
	}

	public void setGestesInvasifs(List<IdValuePair> gestesInvasifs) {
		this.gestesInvasifs = gestesInvasifs;
	}

	public List<IdValuePair> getMicrobioclinicalinfos() {
		return microbioclinicalinfos;
	}

	public String getTbTypeExamen() {
		return tbTypeExamen;
	}

	public void setTbTypeExamen(String tbTypeExamen) {
		this.tbTypeExamen = tbTypeExamen;
	}

	public List<IdValuePair> getTbTypeExamens() {
		return tbTypeExamens;
	}

	public void setTbTypeExamens(List<IdValuePair> tbTypeExamens) {
		this.tbTypeExamens = tbTypeExamens;
	}

	public void setMicrobioclinicalinfos(List<IdValuePair> microbioclinicalinfos) {
		this.microbioclinicalinfos = microbioclinicalinfos;
	}

	public String getTbSubjectNumberthree() {
		return tbSubjectNumberthree;
	}

	public void setTbSubjectNumberthree(String tbSubjectNumberthree) {
		this.tbSubjectNumberthree = tbSubjectNumberthree;
	}

	public String getOtherField() {
		return otherField;
	}

	public void setOtherField(String otherField) {
		this.otherField = otherField;
	}

	public String getTbSubjectNumbertwo() {
		return tbSubjectNumbertwo;
	}

	public List<IdValuePair> getPrescriptionTypes() {
		return prescriptionTypes;
	}

	public void setPrescriptionTypes(List<IdValuePair> prescriptionTypes) {
		this.prescriptionTypes = prescriptionTypes;
	}

	public String getNonConformityNature() {
		return nonConformityNature;
	}

	public void setNonConformityNature(String nonConformityNature) {
		this.nonConformityNature = nonConformityNature;
	}

	public void setTbSubjectNumbertwo(String tbSubjectNumbertwo) {
		this.tbSubjectNumbertwo = tbSubjectNumbertwo;
	}

	public String getPatientMonth() {
		return patientMonth;
	}

	public void setPatientMonth(String patientMonth) {
		this.patientMonth = patientMonth;
	}

	public String getPatientDay() {
		return patientDay;
	}

	public void setPatientDay(String patientDay) {
		this.patientDay = patientDay;
	}

	public String getTbSubjectNumberone() {
		return tbSubjectNumberone;
	}

	public void setTbSubjectNumberone(String tbSubjectNumberone) {
		this.tbSubjectNumberone = tbSubjectNumberone;
	}

	

	public String getSitereferent() {
		return sitereferent;
	}

	public void setSitereferent(String sitereferent) {
		this.sitereferent = sitereferent;
	}

	public String getCollecteurname() {
		return collecteurname;
	}

	public void setCollecteurname(String collecteurname) {
		this.collecteurname = collecteurname;
	}

	public String getTbSpecimenState() {
		return tbSpecimenState;
	}

	public void setTbSpecimenState(String tbSpecimenState) {
		this.tbSpecimenState = tbSpecimenState;
	}

	public String getPregnancyStatus() {
		return pregnancyStatus;
	}

	public void setPregnancyStatus(String pregnancyStatus) {
		this.pregnancyStatus = pregnancyStatus;
	}

	public boolean isUseReferral() {
		return useReferral;
	}

	public void setUseReferral(boolean useReferral) {
		this.useReferral = useReferral;
	}

	public String getTbCollectionDate() {
		return tbCollectionDate;
	}

	public void setTbCollectionDate(String tbCollectionDate) {
		this.tbCollectionDate = tbCollectionDate;
	}

	public List<IdValuePair> getTbSpecimenStates() {
		return tbSpecimenStates;
	}

	public void setTbSpecimenStates(List<IdValuePair> tbSpecimenStates) {
		this.tbSpecimenStates = tbSpecimenStates;
	}

	public String getMicrobioSpecimenNature() {
		return microbioSpecimenNature;
	}

	public void setMicrobioSpecimenNature(String microbioSpecimenNature) {
		this.microbioSpecimenNature = microbioSpecimenNature;
	}

	public List<IdValuePair> getMicrobioSpecimenNatures() {
		return microbioSpecimenNatures;
	}

	public void setMicrobioSpecimenNatures(List<IdValuePair> microbioSpecimenNatures) {
		this.microbioSpecimenNatures = microbioSpecimenNatures;
	}

	public List<IdValuePair> getProviderpayments() {
		return providerpayments;
	}

	public void setProviderpayments(List<IdValuePair> providerpayments) {
		this.providerpayments = providerpayments;
	}

	public String getTbCollectionTime() {
		return tbCollectionTime;
	}

	public void setTbCollectionTime(String tbCollectionTime) {
		this.tbCollectionTime = tbCollectionTime;
	}

	public String getHospitalisationRecent() {
		return hospitalisationRecent;
	}

	public void setHospitalisationRecent(String hospitalisationRecent) {
		this.hospitalisationRecent = hospitalisationRecent;
	}

	public String getNombreHospitalisations() {
		return nombreHospitalisations;
	}

	public void setNombreHospitalisations(String nombreHospitalisations) {
		this.nombreHospitalisations = nombreHospitalisations;
	}

	

	public String getAutresGestesInvasifs() {
		return autresGestesInvasifs;
	}

	public void setAutresGestesInvasifs(String autresGestesInvasifs) {
		this.autresGestesInvasifs = autresGestesInvasifs;
	}

	public String getDureeTraitement() {
		return dureeTraitement;
	}

	public void setDureeTraitement(String dureeTraitement) {
		this.dureeTraitement = dureeTraitement;
	}

	public String getTraitementEncours() {
		return traitementEncours;
	}

	public void setTraitementEncours(String traitementEncours) {
		this.traitementEncours = traitementEncours;
	}

	public String getAntibiotique1() {
		return antibiotique1;
	}

	public void setAntibiotique1(String antibiotique1) {
		this.antibiotique1 = antibiotique1;
	}

	public String getAntibiotique2() {
		return antibiotique2;
	}

	public void setAntibiotique2(String antibiotique2) {
		this.antibiotique2 = antibiotique2;
	}

	public String getAntibiotique3() {
		return antibiotique3;
	}

	public void setAntibiotique3(String antibiotique3) {
		this.antibiotique3 = antibiotique3;
	}

	public String getAntibiotherapieRecent() {
		return antibiotherapieRecent;
	}

	public void setAntibiotherapieRecent(String antibiotherapieRecent) {
		this.antibiotherapieRecent = antibiotherapieRecent;
	}

	public String getHospitalisationEnCour() {
		return hospitalisationEnCour;
	}

	public void setHospitalisationEnCour(String hospitalisationEnCour) {
		this.hospitalisationEnCour = hospitalisationEnCour;
	}

	public String getNumeroDeChambre() {
		return numeroDeChambre;
	}

	public void setNumeroDeChambre(String numeroDeChambre) {
		this.numeroDeChambre = numeroDeChambre;
	}

	public String getClinicalInformation() {
		return clinicalInformation;
	}

	public void setClinicalInformation(String clinicalInformation) {
		this.clinicalInformation = clinicalInformation;
	}

	public String getPatientProfession() {
		return patientProfession;
	}

	public void setPatientProfession(String patientProfession) {
		this.patientProfession = patientProfession;
	}

	public String getPatientNiveauEtude() {
		return patientNiveauEtude;
	}

	public List<IdValuePair> getPatientRegions() {
		return patientRegions;
	}

	public void setPatientRegions(List<IdValuePair> patientRegions) {
		this.patientRegions = patientRegions;
	}

	public void setPatientNiveauEtude(String patientNiveauEtude) {
		this.patientNiveauEtude = patientNiveauEtude;
	}

	public String getPersonDistrict() {
		return personDistrict;
	}

	public void setPersonDistrict(String personDistrict) {
		this.personDistrict = personDistrict;
	}

	public String getPatientRegion() {
		return patientRegion;
	}

	public void setPatientRegion(String patientRegion) {
		this.patientRegion = patientRegion;
	}

	public String getPersonPhoneNumber() {
		return personPhoneNumber;
	}

	public void setPersonPhoneNumber(String personPhoneNumber) {
		this.personPhoneNumber = personPhoneNumber;
	}

	public String getPatientVilleVillage() {
		return patientVilleVillage;
	}

	public void setPatientVilleVillage(String patientVilleVillage) {
		this.patientVilleVillage = patientVilleVillage;
	}

	public String getPersonndomicile() {
		return personndomicile;
	}

	public void setPersonndomicile(String personndomicile) {
		this.personndomicile = personndomicile;
	}

	public String getPersoncommune() {
		return personcommune;
	}

	public void setPersoncommune(String personcommune) {
		this.personcommune = personcommune;
	}

	public String getPatientNationalityId() {
		return patientNationalityId;
	}

	public void setPatientNationalityId(String patientNationalityId) {
		this.patientNationalityId = patientNationalityId;
	}

	public String getUrgent() {
		return urgent;
	}

	public void setUrgent(String urgent) {
		this.urgent = urgent;
	}

	public String getPrescriptionType() {
		return prescriptionType;
	}

	public void setPrescriptionType(String prescriptionType) {
		this.prescriptionType = prescriptionType;
	}

	public String getProviderprelevement() {
		return providerprelevement;
	}

	public void setProviderprelevement(String providerprelevement) {
		this.providerprelevement = providerprelevement;
	}

	public String getEpidemiologique() {
		return epidemiologique;
	}

	public void setEpidemiologique(String epidemiologique) {
		this.epidemiologique = epidemiologique;
	}

	public String getProviderpayment() {
		return providerpayment;
	}

	public void setProviderpayment(String providerpayment) {
		this.providerpayment = providerpayment;
	}

	public String getProviderEmail() {
		return providerEmail;
	}

	public void setProviderEmail(String providerEmail) {
		this.providerEmail = providerEmail;
	}

	public String getProviderFaxNumber() {
		return providerFaxNumber;
	}

	public void setProviderFaxNumber(String providerFaxNumber) {
		this.providerFaxNumber = providerFaxNumber;
	}

	private String sysUserId;
	public String getProviderWorkPhone() {
		return providerWorkPhone;
	}

	public void setProviderWorkPhone(String providerWorkPhone) {
		this.providerWorkPhone = providerWorkPhone;
	}




	public Boolean getRememberSiteAndRequester() {
		return rememberSiteAndRequester;
	}

	public void setRememberSiteAndRequester(Boolean rememberSiteAndRequester) {
		this.rememberSiteAndRequester = rememberSiteAndRequester;
	}

	public String getCurrentDate() {
		return currentDate;
	}

	public void setCurrentDate(String currentDate) {
		this.currentDate = currentDate;
	}

	public List<IdValuePair> getReferralOrganizations() {
		return referralOrganizations;
	}

	public void setReferralOrganizations(List<IdValuePair> referralOrganizations) {
		this.referralOrganizations = referralOrganizations;
	}

	public List<IdValuePair> getSampleTypes() {
		return sampleTypes;
	}

	public void setSampleTypes(List<IdValuePair> sampleTypes) {
		this.sampleTypes = sampleTypes;
	}



	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}


	public String getExternalOrderNumber() {
		return externalOrderNumber;
	}

	public void setExternalOrderNumber(String externalOrderNumber) {
		this.externalOrderNumber = externalOrderNumber;
	}

	public String getLabNo() {
		return labNo;
	}

	public void setLabNo(String labNo) {
		this.labNo = labNo;
	}

	public String getRequestDate() {
		return requestDate;
	}

	public void setRequestDate(String requestDate) {
		this.requestDate = requestDate;
	}

	public String getReceivedTime() {
		return receivedTime;
	}

	public void setReceivedTime(String receivedTime) {
		this.receivedTime = receivedTime;
	}

	public String getReferringPatientNumber() {
		return referringPatientNumber;
	}

	public void setReferringPatientNumber(String referringPatientNumber) {
		this.referringPatientNumber = referringPatientNumber;
	}

	public String getReferringSiteCode() {
		return referringSiteCode;
	}

	public void setReferringSiteCode(String referringSiteCode) {
		this.referringSiteCode = referringSiteCode;
	}

	public String getReferringSiteName() {
		return referringSiteName;
	}

	public void setReferringSiteName(String referringSiteName) {
		this.referringSiteName = referringSiteName;
	}

	public String getProviderPersonId() {
		return providerPersonId;
	}

	public void setProviderPersonId(String providerPersonId) {
		this.providerPersonId = providerPersonId;
	}

	public String getProviderFirstName() {
		return providerFirstName;
	}

	public void setProviderFirstName(String providerFirstName) {
		this.providerFirstName = providerFirstName;
	}

	public String getProviderLastName() {
		return providerLastName;
	}

	public void setProviderLastName(String providerLastName) {
		this.providerLastName = providerLastName;
	}

	public Boolean getModified() {
		return modified;
	}

	public void setModified(Boolean modified) {
		this.modified = modified;
	}

	public String getSampleId() {
		return sampleId;
	}

	public void setSampleId(String sampleId) {
		this.sampleId = sampleId;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public List<IdValuePair> getTestSectionList() {
		return testSectionList;
	}

	public void setTestSectionList(List<IdValuePair> testSectionList) {
		this.testSectionList = testSectionList;
	}

	public List<IdValuePair> getRejectReasonList() {
		return rejectReasonList;
	}

	public void setRejectReasonList(List<IdValuePair> rejectReasonList) {
		this.rejectReasonList = rejectReasonList;
	}

	public List<IdValuePair> getGenders() {
		return genders;
	}

	public void setGenders(List<IdValuePair> genders) {
		this.genders = genders;
	}

	public String getTbOrderReason() {
		return tbOrderReason;
	}

	public void setTbOrderReason(String tbOrderReason) {
		this.tbOrderReason = tbOrderReason;
	}

	public List<IdValuePair> getTbOrderReasons() {
		return tbOrderReasons;
	}

	public void setTbOrderReasons(List<IdValuePair> tbOrderReasons) {
		this.tbOrderReasons = tbOrderReasons;
	}

	public String getTbDiagnosticReason() {
		return tbDiagnosticReason;
	}

	public void setTbDiagnosticReason(String tbDiagnosticReason) {
		this.tbDiagnosticReason = tbDiagnosticReason;
	}

	public List<IdValuePair> getTbDiagnosticReasons() {
		return tbDiagnosticReasons;
	}

	public void setTbDiagnosticReasons(List<IdValuePair> tbDiagnosticReasons) {
		this.tbDiagnosticReasons = tbDiagnosticReasons;
	}

	public String getTbFollowupReason() {
		return tbFollowupReason;
	}

	public void setTbFollowupReason(String tbFollowupReason) {
		this.tbFollowupReason = tbFollowupReason;
	}

	public List<IdValuePair> getTbFollowupReasons() {
		return tbFollowupReasons;
	}

	public void setTbFollowupReasons(List<IdValuePair> tbFollowupReasons) {
		this.tbFollowupReasons = tbFollowupReasons;
	}

	public List<IdValuePair> getTbDiagnosticMethods() {
		return tbDiagnosticMethods;
	}

	public void setTbDiagnosticMethods(List<IdValuePair> tbDiagnosticMethods) {
		this.tbDiagnosticMethods = tbDiagnosticMethods;
	}

	public String getTbAspect() {
		return tbAspect;
	}

	public void setTbAspect(String tbAspect) {
		this.tbAspect = tbAspect;
	}

	public List<IdValuePair> getTbAspects() {
		return tbAspects;
	}

	public void setTbAspects(List<IdValuePair> tbAspects) {
		this.tbAspects = tbAspects;
	}

	public String getPatientGender() {
		return patientGender;
	}

	public void setPatientGender(String patientGender) {
		this.patientGender = patientGender;
	}

	public String getPatientAge() {
		return patientAge;
	}

	public void setPatientAge(String patientAge) {
		this.patientAge = patientAge;
	}

	public String getPatientBirthDate() {
		return patientBirthDate;
	}

	public void setPatientBirthDate(String patientBirthDate) {
		this.patientBirthDate = patientBirthDate;
	}

	public String getRequesterName() {
		return requesterName;
	}

	public void setRequesterName(String requesterName) {
		this.requesterName = requesterName;
	}

	public String getReceivedDate() {
		return receivedDate;
	}

	public void setReceivedDate(String receivedDate) {
		this.receivedDate = receivedDate;
	}

	public String getPatientFirstName() {
		return patientFirstName;
	}

	public void setPatientFirstName(String patientFirstName) {
		this.patientFirstName = patientFirstName;
	}

	public String getPatientLastName() {
		return patientLastName;
	}

	public void setPatientLastName(String patientLastName) {
		this.patientLastName = patientLastName;
	}

	public String getPatientPhone() {
		return patientPhone;
	}

	public void setPatientPhone(String patientPhone) {
		this.patientPhone = patientPhone;
	}

	public String getPatientAddress() {
		return patientAddress;
	}

	public void setPatientAddress(String patientAddress) {
		this.patientAddress = patientAddress;
	}

	public String getTbSubjectNumber() {
		return tbSubjectNumber;
	}

	public void setTbSubjectNumber(String tbSubjectNumber) {
		this.tbSubjectNumber = tbSubjectNumber;
	}

	public String getTbSpecimenNature() {
		return tbSpecimenNature;
	}

	public void setTbSpecimenNature(String tbSpecimenNature) {
		this.tbSpecimenNature = tbSpecimenNature;
	}

	public List<IdValuePair> getTbSpecimenNatures() {
		return tbSpecimenNatures;
	}

	public void setTbSpecimenNatures(List<IdValuePair> tbSpecimenNatures) {
		this.tbSpecimenNatures = tbSpecimenNatures;
	}

	public String getRejectReason() {
		return rejectReason;
	}

	public void setRejectReason(String rejectReason) {
		this.rejectReason = rejectReason;
	}

	public String getTbFollowupPeriodLine1() {
		return tbFollowupPeriodLine1;
	}

	public void setTbFollowupPeriodLine1(String tbFollowupPeriodLine1) {
		this.tbFollowupPeriodLine1 = tbFollowupPeriodLine1;
	}

	public String getTbFollowupPeriodLine2() {
		return tbFollowupPeriodLine2;
	}

	public void setTbFollowupPeriodLine2(String tbFollowupPeriodLine2) {
		this.tbFollowupPeriodLine2 = tbFollowupPeriodLine2;
	}

	public List<IdValuePair> getTbFollowupPeriodsLine1() {
		return tbFollowupPeriodsLine1;
	}

	public void setTbFollowupPeriodsLine1(List<IdValuePair> tbFollowupPeriodsLine1) {
		this.tbFollowupPeriodsLine1 = tbFollowupPeriodsLine1;
	}

	public List<IdValuePair> getTbFollowupPeriodsLine2() {
		return tbFollowupPeriodsLine2;
	}

	public void setTbFollowupPeriodsLine2(List<IdValuePair> tbFollowupPeriodsLine2) {
		this.tbFollowupPeriodsLine2 = tbFollowupPeriodsLine2;
	}

	public List<IdValuePair> getReferringSiteCodes() {
		return referringSiteCodes;
	}

	public void setReferringSiteCodes(List<IdValuePair> referringSiteCodes) {
		this.referringSiteCodes = referringSiteCodes;
	}

	public List<String> getNewSelectedTests() {
		return newSelectedTests;
	}

	public void setNewSelectedTests(List<String> newSelectedTests) {
		this.newSelectedTests = newSelectedTests;
	}

	public List<String> getSelectedTestToRemove() {
		return selectedTestToRemove;
	}

	public void setSelectedTestToRemove(List<String> selectedTestToRemove) {
		this.selectedTestToRemove = selectedTestToRemove;
	}

	public String getSysUserId() {
		return sysUserId;
	}

	public void setSysUserId(String sysUserId) {
		this.sysUserId = sysUserId;
	}


	public String getSelectedMethodToRemove() {
		return selectedMethodToRemove;
	}

	public void setSelectedMethodToRemove(String selectedMethodToRemove) {
		this.selectedMethodToRemove = selectedMethodToRemove;
	}

	public String getLabnoForSearch() {
		return labnoForSearch;
	}

	public void setLabnoForSearch(String labnoForSearch) {
		this.labnoForSearch = labnoForSearch;
	}
	  
}
