package org.openelisglobal.samplemicrobioclassik.valueholder;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.Valid;
import javax.validation.constraints.Pattern;

import org.hl7.fhir.r4.model.Patient;
import org.openelisglobal.common.validator.ValidationHelper;
import org.openelisglobal.common.valueholder.BaseObject;
import org.openelisglobal.person.valueholder.Person;


public class Samplemicrobioclassic extends BaseObject<String>{
  
    
	 private static final long serialVersionUID = 1L;
     private String id;  
    private String epidemiologicalWeek;
    private String education;
    private String patientProfession;
    private String hospitalisation;
    private String bedroomnumber;
 // private Timestamp birthDate;
    private String clinicalInformation;
    private String otherclinicalInformation;
    private String epiMiddleName;
    private String treatmentduration;
    private String traitementEncours;
    private String hospitalisationRecent;
    private String hospitalisationnumber;
    private String invasifgesture;
    private String device;
   
    private String patientId;
   
    private String otherInfoClinical;
    private String  otherOrginalservice;
    
    public String getPatientId() {
        return patientId;
    }
   
	
	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}
	public String getOtherOrginalservice() {
		return otherOrginalservice;
	}
	public void setOtherOrginalservice(String otherOrginalservice) {
		this.otherOrginalservice = otherOrginalservice;
	}
	public String getOtherInfoClinical() {
		return otherInfoClinical;
	}
	public void setOtherInfoClinical(String otherInfoClinical) {
		this.otherInfoClinical = otherInfoClinical;
	}
	
	  @Override
	public String getId() {
		return id;
	}
	  @Override
	public void setId(String id) {
		this.id = id;
	}
	public String getEpidemiologicalWeek() {
		return epidemiologicalWeek;
	}
	public void setEpidemiologicalWeek(String epidemiologicalWeek) {
		this.epidemiologicalWeek = epidemiologicalWeek;
	}
	public String getEducation() {
		return education;
	}
	public void setEducation(String education) {
		this.education = education;
	}
	public String getPatientProfession() {
		return patientProfession;
	}
	public void setPatientProfession(String patientProfession) {
		this.patientProfession = patientProfession;
	}
	public String getHospitalisation() {
		return hospitalisation;
	}
	public void setHospitalisation(String hospitalisation) {
		this.hospitalisation = hospitalisation;
	}
	public String getBedroomnumber() {
		return bedroomnumber;
	}
	public void setBedroomnumber(String bedroomnumber) {
		this.bedroomnumber = bedroomnumber;
	}
	public String getClinicalInformation() {
		return clinicalInformation;
	}
	public void setClinicalInformation(String clinicalInformation) {
		this.clinicalInformation = clinicalInformation;
	}
	public String getOtherclinicalInformation() {
		return otherclinicalInformation;
	}
	public void setOtherclinicalInformation(String otherclinicalInformation) {
		this.otherclinicalInformation = otherclinicalInformation;
	}
	public String getEpiMiddleName() {
		return epiMiddleName;
	}
	public void setEpiMiddleName(String epiMiddleName) {
		this.epiMiddleName = epiMiddleName;
	}
	public String getTreatmentduration() {
		return treatmentduration;
	}
	public void setTreatmentduration(String treatmentduration) {
		this.treatmentduration = treatmentduration;
	}
	public String getTraitementEncours() {
		return traitementEncours;
	}
	public void setTraitementEncours(String traitementEncours) {
		this.traitementEncours = traitementEncours;
	}
	public String getHospitalisationRecent() {
		return hospitalisationRecent;
	}
	public void setHospitalisationRecent(String hospitalisationRecent) {
		this.hospitalisationRecent = hospitalisationRecent;
	}
	public String getHospitalisationnumber() {
		return hospitalisationnumber;
	}
	public void setHospitalisationnumber(String hospitalisationnumber) {
		this.hospitalisationnumber = hospitalisationnumber;
	}
	public String getInvasifgesture() {
		return invasifgesture;
	}
	public void setInvasifgesture(String invasifgesture) {
		this.invasifgesture = invasifgesture;
	}
	public String getDevice() {
		return device;
	}
	public void setDevice(String device) {
		this.device = device;
	}
	
}
   
	    
	    	    
	    
	    



