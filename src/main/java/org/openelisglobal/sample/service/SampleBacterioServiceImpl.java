package org.openelisglobal.sample.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.ObjectUtils;
import org.openelisglobal.address.service.AddressPartService;
import org.openelisglobal.address.service.PersonAddressService;
import org.openelisglobal.address.valueholder.PersonAddress;
import org.openelisglobal.analysis.service.AnalysisService;
import org.openelisglobal.analysis.valueholder.Analysis;
import org.openelisglobal.common.services.IStatusService;
import org.openelisglobal.common.services.StatusService.AnalysisStatus;
import org.openelisglobal.common.services.StatusService.OrderStatus;
import org.openelisglobal.common.services.StatusService.SampleStatus;
import org.openelisglobal.common.util.DateUtil;
import org.openelisglobal.common.util.validator.GenericValidator;
import org.openelisglobal.observationhistory.service.ObservationHistoryService;
import org.openelisglobal.observationhistory.valueholder.ObservationHistory;
import org.openelisglobal.observationhistory.valueholder.ObservationHistory.ValueType;
import org.openelisglobal.observationhistorytype.service.ObservationHistoryTypeService;
import org.openelisglobal.observationhistorytype.valueholder.ObservationHistoryType;
import org.openelisglobal.organization.service.OrganizationService;
import org.openelisglobal.patient.service.PatientService;
import org.openelisglobal.patient.valueholder.Patient;
import org.openelisglobal.patientidentity.service.PatientIdentityService;
import org.openelisglobal.patientidentity.valueholder.PatientIdentity;
import org.openelisglobal.patientidentitytype.util.PatientIdentityTypeMap;
import org.openelisglobal.person.service.PersonService;
import org.openelisglobal.person.valueholder.Person;
import org.openelisglobal.provider.service.ProviderService;
import org.openelisglobal.provider.valueholder.Provider;

import org.openelisglobal.sample.form.SampleTbclassicForm;
import org.openelisglobal.sample.valueholder.OrderPriority;
import org.openelisglobal.sample.valueholder.Sample;
import org.openelisglobal.samplehuman.service.SampleHumanService;
import org.openelisglobal.samplehuman.valueholder.SampleHuman;
import org.openelisglobal.sampleitem.service.SampleItemService;
import org.openelisglobal.sampleitem.valueholder.SampleItem;
import org.openelisglobal.samplemicrobioclassic.dao.MicrobioClassicService;
import org.openelisglobal.samplemicrobioclassik.valueholder.Samplemicrobioclassic;
import org.openelisglobal.sampleorganization.service.SampleOrganizationService;
import org.openelisglobal.sampleorganization.valueholder.SampleOrganization;
import org.openelisglobal.spring.util.SpringContext;
import org.openelisglobal.test.service.TestSectionService;
import org.openelisglobal.test.service.TestService;
import org.openelisglobal.test.valueholder.Test;
import org.openelisglobal.typeofsample.service.TypeOfSampleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@DependsOn({ "springContext" })
public class SampleBacterioServiceImpl implements SampleBacterioService{
	private static final String DEFAULT_ANALYSIS_TYPE = "MANUAL";

	@Autowired
	private TestSectionService testSectionService;
	@Autowired
	private ObservationHistoryService observationHistoryService;
	@Autowired
	private ObservationHistoryTypeService observationHistoryTypeService;
	@Autowired
	private PersonService personService;
	@Autowired
	private PersonAddressService personAddressService;
	@Autowired
	private AddressPartService addressPartService;
	@Autowired
	private PatientIdentityService patientIdentityService;
	@Autowired
	private PatientService patientService;
	@Autowired
	private ProviderService providerService;
	@Autowired
	private SampleService sampleService;
	@Autowired
	private SampleHumanService sampleHumanService;
	@Autowired
	private SampleItemService sampleItemService;
	@Autowired
	private TypeOfSampleService typeOfSampleService;
	@Autowired
	private AnalysisService analysisService;
	@Autowired
	private TestService testService;
	@Autowired
	private OrganizationService organizationService;
	@Autowired
	private SampleOrganizationService sampleOrganizationService;
	@Autowired
	private MicrobioClassicService microbioClassicService;


	private String sampleId;
	private String patientId;
	private String sampleItemId;

	@Transactional
	@Override
	public boolean persistBacterioData(SampleTbclassicForm form, HttpServletRequest request) {
		boolean isOK = false;
		try {
		    persistbacterioInformation(form);
			persistPatientData(form);
			createPatientIdentity(form, patientId);
			sampleId = persistSampleData(form);
			persistSampleHumanData(form);
			sampleItemId = persistSampleItemData(form);
			persistAnalysisData(form);
			persistSampleOrganizationData(form);
			persistObservations(form);
			isOK = true;
		} catch (Exception e) {
			isOK = false;
			throw e;
		}
		return isOK;
	}

	private List<String> persistObservations(SampleTbclassicForm formData) {
	    List<ObservationHistory> observations = new ArrayList<>();

	    // Clinical information
	    ObservationHistory clinicalInfo = new ObservationHistory();
	    clinicalInfo.setSampleId(sampleId);
	    clinicalInfo.setPatientId(patientId);
	    clinicalInfo.setLastupdated(DateUtil.getNowAsTimestamp());
	    clinicalInfo.setSysUserId(formData.getSysUserId());
	    clinicalInfo.setValueType(ValueType.DICTIONARY);
	    clinicalInfo.setValue(formData.getClinicalInformation() != null ? formData.getClinicalInformation() : "");
	    clinicalInfo.setObservationHistoryTypeId(getObservationHistoryTypeId("clinicalInfo"));
	    observations.add(clinicalInfo);

	    // Prescription
	    ObservationHistory prescription = new ObservationHistory();
	    prescription.setSampleId(sampleId);
	    prescription.setPatientId(patientId);
	    prescription.setLastupdated(DateUtil.getNowAsTimestamp());
	    prescription.setSysUserId(formData.getSysUserId());
	    prescription.setValueType(ValueType.DICTIONARY);
	    prescription.setValue(formData.getPrescriptionType() != null ? formData.getPrescriptionType() : "");
	    prescription.setObservationHistoryTypeId(getObservationHistoryTypeId("PrescriptionType"));
	    observations.add(prescription);

	    // Urgent
	    ObservationHistory urgence = new ObservationHistory();
	    urgence.setSampleId(sampleId);
	    urgence.setPatientId(patientId);
	    urgence.setLastupdated(DateUtil.getNowAsTimestamp());
	    urgence.setSysUserId(formData.getSysUserId());
	    urgence.setValueType(ValueType.DICTIONARY);
	    urgence.setValue(formData.getUrgent() != null ? formData.getUrgent() : "");
	    urgence.setObservationHistoryTypeId(getObservationHistoryTypeId("Urgence"));
	    observations.add(urgence);

	    // Payment
	    ObservationHistory payment = new ObservationHistory();
	    payment.setSampleId(sampleId);
	    payment.setPatientId(patientId);
	    payment.setLastupdated(DateUtil.getNowAsTimestamp());
	    payment.setSysUserId(formData.getSysUserId());
	    payment.setValueType(ValueType.DICTIONARY);
	    payment.setValue(formData.getProviderpayment() != null ? formData.getProviderpayment() : "");
	    payment.setObservationHistoryTypeId(getObservationHistoryTypeId("paymentStatus"));
	    observations.add(payment);

	    // Type Examen
	    ObservationHistory typeExamen = new ObservationHistory();
	    typeExamen.setSampleId(sampleId);
	    typeExamen.setPatientId(patientId);
	    typeExamen.setLastupdated(DateUtil.getNowAsTimestamp());
	    typeExamen.setSysUserId(formData.getSysUserId());
	    typeExamen.setValueType(ValueType.DICTIONARY);
	    typeExamen.setValue(formData.getTbTypeExamen() != null ? formData.getTbTypeExamen() : "");
	    typeExamen.setObservationHistoryTypeId(getObservationHistoryTypeId("BacterioTypeExamens"));
	    observations.add(typeExamen);

	    // Study
	    ObservationHistory study = new ObservationHistory();
	    study.setSampleId(sampleId);
	    study.setPatientId(patientId);
	    study.setLastupdated(DateUtil.getNowAsTimestamp());
	    study.setSysUserId(formData.getSysUserId());
	    study.setValueType(ValueType.DICTIONARY);
	    study.setValue(formData.getEducation() != null ? formData.getEducation() : "");
	    study.setObservationHistoryTypeId(getObservationHistoryTypeId("Study"));
	    observations.add(study);

	    // Gestes Invasif
	    ObservationHistory invasif = new ObservationHistory();
	    invasif.setSampleId(sampleId);
	    invasif.setPatientId(patientId);
	    invasif.setLastupdated(DateUtil.getNowAsTimestamp());
	    invasif.setSysUserId(formData.getSysUserId());
	    invasif.setValueType(ValueType.DICTIONARY);
	    invasif.setValue(formData.getGestesInvasif() != null ? formData.getGestesInvasif() : "");
	    invasif.setObservationHistoryTypeId(getObservationHistoryTypeId("Gesteinvasif"));
	    observations.add(invasif);

	    // Conformity
	    ObservationHistory conformity = new ObservationHistory();
	    conformity.setSampleId(sampleId);
	    conformity.setPatientId(patientId);
	    conformity.setLastupdated(DateUtil.getNowAsTimestamp());
	    conformity.setSysUserId(formData.getSysUserId());
	    conformity.setValueType(ValueType.DICTIONARY);
	    conformity.setValue(formData.getTbSpecimenState() != null ? formData.getTbSpecimenState() : "");
	    conformity.setObservationHistoryTypeId(getObservationHistoryTypeId("TbSpecimenState"));
	    observations.add(conformity);

	    // Non-conformity
	    ObservationHistory noconformity = new ObservationHistory();
	    noconformity.setSampleId(sampleId);
	    noconformity.setPatientId(patientId);
	    noconformity.setLastupdated(DateUtil.getNowAsTimestamp());
	    noconformity.setSysUserId(formData.getSysUserId());
	    noconformity.setValueType(ValueType.DICTIONARY);
	    noconformity.setValue(formData.getNonConformityNature() != null ? formData.getNonConformityNature() : "");
	    noconformity.setObservationHistoryTypeId(getObservationHistoryTypeId("NoConformity"));
	    observations.add(noconformity);

	    return observationHistoryService.insertAll(observations);
	}

//Persiste les données de patient. Si le patient existe déjà, ses informations sont mises à jour, sinon un nouveau patient est créé.
	
	
	
	private Patient persistPatientData(SampleTbclassicForm formData) {
		Patient oldPatient = null;
		if (!GenericValidator.isBlankOrNull(formData.getTbSubjectNumber())) {
			oldPatient = patientService.getByExternalId(formData.getTbSubjectNumber());
		}
		if (ObjectUtils.isEmpty(oldPatient)) {
			Patient patient = new Patient();
			patient.setPerson(createPersonAndAddress(formData));
			patient.setExternalId(formData.getTbSubjectNumber());
			patient.setNationalId(formData.getTbSubjectNumber());
			patient.setBirthDateForDisplay(formData.getPatientBirthDate());
			patient.setBirthDate(DateUtil.convertStringDateToTruncatedTimestamp(formData.getPatientBirthDate()));
			patient.setGender(formData.getPatientGender());
			patient.setNationalId(formData.getPatientNationalityId());
			patient.setSysUserId(formData.getSysUserId());
			patientId = patientService.insert(patient);
			patient.setId(patientId);
			  System.out.println("patient: " + patientId);
			return patient;
		} else {
			// update
			oldPatient.setPerson(createPersonAndAddress(formData));
			oldPatient.setBirthDateForDisplay(formData.getPatientBirthDate());
			oldPatient.setBirthDate(DateUtil.convertStringDateToTimestamp(formData.getPatientBirthDate() + " 00:00"));
			oldPatient.setGender(formData.getPatientGender());
			oldPatient.setSysUserId(formData.getSysUserId());
			oldPatient = patientService.update(oldPatient);
			patientId = oldPatient.getId();
			System.out.println("patientId mise a jour : " + patientId);
			return oldPatient;
		}

	}

	// create a new Person
	
	//Crée une nouvelle personne et son adresse associée.
	private Person createPersonAndAddress(SampleTbclassicForm formData) {
		Person person = new Person();
		person.setFirstName(formData.getPatientFirstName());
		person.setLastName(formData.getPatientLastName());	
		person.setLastupdatedFields();
		person.setSysUserId(formData.getSysUserId());
		String personId = personService.insert(person);
		person.setId(personId);
		createPersonAddresses(formData, personId);
		return person;
	}

	// create a new Person
	private String createPatientIdentity(SampleTbclassicForm formData, String patientId) {
		String typeID = PatientIdentityTypeMap.getInstance().getIDForType("SUBJECT");
		PatientIdentity patientIdentity = patientIdentityService.getPatitentIdentityForPatientAndType(patientId,
				typeID);
		if (ObjectUtils.isEmpty(patientIdentity)) {
			patientIdentity = new PatientIdentity();
			patientIdentity.setPatientId(patientId);
			patientIdentity.setIdentityData(formData.getTbSubjectNumber());
			patientIdentity.setLastupdated(DateUtil.getNowAsTimestamp());
			patientIdentity.setIdentityTypeId(typeID);
			return patientIdentityService.insert(patientIdentity);
		} else {
			return patientIdentity.getId();
		}
	}

	private String createPersonAndProvider(SampleTbclassicForm formData) {
		Person person = new Person();
		person.setFirstName(formData.getProviderFirstName());
		person.setLastName(formData.getProviderLastName());
		person.setLastupdatedFields();
		person.setSysUserId(formData.getSysUserId());
		String personId = personService.insert(person);
		person.setId(personId);
		Provider provider = new Provider();
		provider.setPerson(person);
		provider.setLastupdated(DateUtil.getNowAsTimestamp());
		return providerService.insert(provider);
	}

	// create a new Person
	private void createPersonAddresses(SampleTbclassicForm formData, String personId) {
		// define addresses
		List<PersonAddress> existingAddresses = personAddressService.getAddressPartsByPersonId(personId);
		if (ObjectUtils.isEmpty(existingAddresses)) {
			PersonAddress patientPhone = new PersonAddress();
			patientPhone.setPersonId(personId);
			patientPhone.setAddressPartId(addressPartService.getAddresPartByName("phone").getId());
			patientPhone.setType("T");
			patientPhone.setValue(formData.getPatientPhone());
			patientPhone.setSysUserId(formData.getSysUserId());
			patientPhone.setLastupdatedFields();
			personAddressService.insert(patientPhone);
			
			
			PersonAddress patientStreetAddress = new PersonAddress();
			patientStreetAddress.setPersonId(personId);
			patientStreetAddress.setAddressPartId(addressPartService.getAddresPartByName("street").getId());
			patientStreetAddress.setType("T");
			patientStreetAddress.setValue(formData.getPatientAddress());
			patientStreetAddress.setSysUserId(formData.getSysUserId());
			patientStreetAddress.setLastupdatedFields();
			personAddressService.insert(patientStreetAddress);
			
			
			PersonAddress patientCommune = new PersonAddress();
			patientCommune.setPersonId(personId);
			patientCommune.setAddressPartId(addressPartService.getAddresPartByName("commune").getId());
			patientCommune.setType("T");
			patientCommune.setValue(formData.getPersoncommune());
			patientCommune.setSysUserId(formData.getSysUserId());
			patientCommune.setLastupdatedFields();
			personAddressService.insert(patientCommune);
			
			PersonAddress patientVillage = new PersonAddress();
			patientVillage.setPersonId(personId);
			patientVillage.setAddressPartId(addressPartService.getAddresPartByName("village").getId());
			patientVillage.setType("T");
			patientVillage.setValue(formData.getPatientVilleVillage());
			patientVillage.setSysUserId(formData.getSysUserId());
			patientVillage.setLastupdatedFields();
			personAddressService.insert(patientVillage);
					
		} else {
			// update adresses
			existingAddresses.forEach(address -> {
				if (address.getAddressPartId().equals(addressPartService.getAddresPartByName("phone").getId())) {
					address.setValue(formData.getPatientPhone());
				}
				if (address.getAddressPartId().equals(addressPartService.getAddresPartByName("street").getId())) {
					address.setValue(formData.getPatientAddress());
				}
				
				if (address.getAddressPartId().equals(addressPartService.getAddresPartByName("commune").getId())) {
					address.setValue(formData.getPatientAddress());
				}
				
				
				if (address.getAddressPartId().equals(addressPartService.getAddresPartByName("village").getId())) {
					address.setValue(formData.getPatientAddress());
				}
				
				personAddressService.update(address);
			});
		}
	}

	private String persistSampleData(SampleTbclassicForm formData) {
		Sample sample = new Sample();
		if (ObjectUtils.isEmpty(formData.getSampleId())) {
			// create a new Sample
			sample.setAccessionNumber(formData.getLabNo());
			sample.setCollectionDateForDisplay(formData.getRequestDate());
			sample.setCollectionDate(DateUtil.convertStringDateToTruncatedTimestamp(formData.getRequestDate()));
			sample.setReceivedDateForDisplay(formData.getReceivedDate());
			sample.setReceivedDate(DateUtil.convertStringDateToSqlDate(formData.getReceivedDate()));
			sample.setEnteredDate(DateUtil.getNowAsSqlDate());
			sample.setDomain("H");
			sample.setSysUserId(formData.getSysUserId());
			sample.setLastupdated(DateUtil.getNowAsTimestamp());
			sample.setStatusId(SpringContext.getBean(IStatusService.class).getStatusID(OrderStatus.Entered));
			sample.setPriority(OrderPriority.ROUTINE);
			return sampleService.insert(sample);
		} else {
			// update
			sample.setCollectionDateForDisplay(formData.getRequestDate());
			sample.setCollectionDate(DateUtil.convertStringDateToTruncatedTimestamp(formData.getRequestDate()));
			sample.setReceivedDateForDisplay(formData.getReceivedDate());
			sample.setReceivedDate(DateUtil.convertStringDateToSqlDate(formData.getReceivedDate()));
			sample.setEnteredDate(DateUtil.getNowAsSqlDate());
			sample.setSysUserId(formData.getSysUserId());
			sample.setLastupdated(DateUtil.getNowAsTimestamp());
			
			sample.setPriority(OrderPriority.ROUTINE);
			return sampleService.update(sample).getId();
		}
	}
	
	
	private String persistbacterioInformation(SampleTbclassicForm formData) {
	 Samplemicrobioclassic sampleMicrobioClassic = new Samplemicrobioclassic();
	

	 if (ObjectUtils.isEmpty(formData.getSampleId())) {
  
     sampleMicrobioClassic.setEpidemiologicalWeek(formData.getEpidemiologique());
     sampleMicrobioClassic.setEducation(formData.getEducation());
     sampleMicrobioClassic.setPatientProfession(formData.getPatientProfession());
     sampleMicrobioClassic.setHospitalisation(formData.getHospitalisationEnCour());
     sampleMicrobioClassic.setBedroomnumber(formData.getNumeroDeChambre());
     sampleMicrobioClassic.setClinicalInformation(formData.getClinicalInformation());
     sampleMicrobioClassic.setOtherclinicalInformation(formData.getOtherField());
     sampleMicrobioClassic.setEpiMiddleName(formData.getAntibiotherapieRecent());
     sampleMicrobioClassic.setTreatmentduration(formData.getDureeTraitement());
     sampleMicrobioClassic.setTraitementEncours(formData.getTraitementEncours());
     sampleMicrobioClassic.setHospitalisationRecent(formData.getHospitalisationRecent());
     sampleMicrobioClassic.setHospitalisationnumber(formData.getNombreHospitalisations());
     sampleMicrobioClassic.setInvasifgesture(formData.getGestesInvasif());
     sampleMicrobioClassic.setOtherInfoClinical(formData.getOtherInfoClinical());
     sampleMicrobioClassic.setOtherOrginalservice(formData.getOtherOrginalservice());
     sampleMicrobioClassic.setDevice(formData.getDispositif());
    //sampleMicrobioClassic.setSampleId(sampleId);

	    // Assurez-vous que patientId est défini
	   // if (ObjectUtils.isEmpty(formData.getPatientBirthDate())) {
	    //    throw new IllegalArgumentException("Le Patient ID ne peut pas être null ou vide.");
	   // }
	    //sampleMicrobioClassic.setPatientId(formData.getPatientBirthDate());
      // sampleMicrobioClassic.setPatientId(patientId);
    //sampleMicrobioClassic.setPatientId(formData.getPatientId());
    // sampleMicrobioClassic.setPatientId(formData.getPatientId());
     sampleMicrobioClassic.setSysUserId(formData.getSysUserId());
	return microbioClassicService.insert(sampleMicrobioClassic);
	
	}else {
		
	    sampleMicrobioClassic.setEpidemiologicalWeek(formData.getEpidemiologique());
	     sampleMicrobioClassic.setEducation(formData.getEducation());
	     sampleMicrobioClassic.setPatientProfession(formData.getPatientProfession());
	     sampleMicrobioClassic.setHospitalisation(formData.getHospitalisationEnCour());
	     sampleMicrobioClassic.setBedroomnumber(formData.getNumeroDeChambre());
	     sampleMicrobioClassic.setClinicalInformation(formData.getClinicalInformation());
	     sampleMicrobioClassic.setOtherclinicalInformation(formData.getOtherField());
	     sampleMicrobioClassic.setEpiMiddleName(formData.getAntibiotherapieRecent());
	     sampleMicrobioClassic.setTreatmentduration(formData.getDureeTraitement());
	     sampleMicrobioClassic.setTraitementEncours(formData.getTraitementEncours());
	     sampleMicrobioClassic.setHospitalisationRecent(formData.getHospitalisationRecent());
	     sampleMicrobioClassic.setHospitalisationnumber(formData.getNombreHospitalisations());
	     sampleMicrobioClassic.setInvasifgesture(formData.getGestesInvasif());
	     sampleMicrobioClassic.setDevice(formData.getDispositif());
		sampleMicrobioClassic.setSysUserId(formData.getSysUserId());
		 sampleMicrobioClassic.setOtherInfoClinical(formData.getOtherInfoClinical());
	     sampleMicrobioClassic.setOtherOrginalservice(formData.getOtherOrginalservice());
		//sampleMicrobioClassic.setSampleId(sampleId);
		  // sampleMicrobioClassic.setPatientId(patientId);
        return microbioClassicService.update(sampleMicrobioClassic).getId();
		}
	}
	private String persistSampleHumanData(SampleTbclassicForm formData) {
		SampleHuman sampleHuman = new SampleHuman();
		sampleHuman.setSampleId(sampleId);
		sampleHuman.setPatientId(patientId);
		sampleHuman.setLastupdated(DateUtil.getNowAsTimestamp());
		sampleHuman.setSysUserId(formData.getSysUserId());
		sampleHuman.setProviderId(createPersonAndProvider(formData));
		return sampleHumanService.insert(sampleHuman);
	}

	private String persistSampleItemData(SampleTbclassicForm formData) {
		SampleItem item = new SampleItem();
		if (!ObjectUtils.isEmpty(sampleId)) {
			List<SampleItem> oldSampleItems = sampleItemService.getSampleItemsBySampleId(sampleId);
			if (!ObjectUtils.isEmpty(oldSampleItems)) {
				SampleItem oldSampleItem = oldSampleItems.get(0);
				oldSampleItem.setSample(sampleService.get(sampleId));
				oldSampleItem.setLastupdated(DateUtil.getNowAsTimestamp());
				oldSampleItem.setSysUserId(formData.getSysUserId());
				oldSampleItem.setTypeOfSample(typeOfSampleService.get(formData.getTbSpecimen()));
				return sampleItemService.update(oldSampleItem).getId();
			}
		}
		item.setSample(sampleService.get(sampleId));
		item.setLastupdated(DateUtil.getNowAsTimestamp());
		item.setTypeOfSample(typeOfSampleService.get(formData.getTbSpecimen()));
		item.setStatusId(SpringContext.getBean(IStatusService.class).getStatusID(SampleStatus.Entered));
		item.setSortOrder(Integer.toString(1));
		item.setSysUserId(formData.getSysUserId());
		return sampleItemService.insert(item);
	}
	

	private List<String> persistAnalysisData(SampleTbclassicForm formData) {
		List<Analysis> analysisItems = new ArrayList<Analysis>();
		for (String testId : formData.getNewSelectedTests()) {
			Analysis analysis = new Analysis();
			Test test = testService.get(testId);
			SampleItem sampleItem = sampleItemService.get(sampleItemId);
			analysis.setSampleItem(sampleItem);
			analysis.setTest(test);
			analysis.setRevision("0");
			analysis.setTestSection(testSectionService.getTestSectionByName("Bacterio"));
			analysis.setEnteredDate(DateUtil.getNowAsTimestamp());
			analysis.setIsReportable(test.getIsReportable());
			analysis.setAnalysisType(DEFAULT_ANALYSIS_TYPE);
			analysis.setStartedDate(DateUtil.getNowAsSqlDate());
			analysis.setStatusId(SpringContext.getBean(IStatusService.class).getStatusID(AnalysisStatus.NotStarted));
			analysis.setSysUserId(formData.getSysUserId());
			analysis.setFhirUuid(UUID.randomUUID());
			analysis.setSampleTypeName(typeOfSampleService.get(formData.getTbSpecimen()).getDescription());
			analysisItems.add(analysis);
		}
		return analysisService.insertAll(analysisItems);
	}

	private String persistSampleOrganizationData(SampleTbclassicForm formData) {
		SampleOrganization sampleOrganization = new SampleOrganization();
		if (ObjectUtils.isNotEmpty(formData.getSampleId())) {
			sampleOrganization = sampleOrganizationService.getDataBySample(sampleService.get(formData.getSampleId()));
			if (ObjectUtils.isNotEmpty(sampleOrganization)) {
				sampleOrganization.setLastupdated(DateUtil.getNowAsTimestamp());
				sampleOrganization.setSysUserId(formData.getSysUserId());
				sampleOrganization.setSample(sampleService.get(sampleId));
				sampleOrganization.setSysUserId(formData.getSysUserId());
				sampleOrganization.setOrganization(organizationService.get(formData.getReferringSiteCode()));
				return sampleOrganizationService.update(sampleOrganization).getId();
			}
		}
		sampleOrganization.setLastupdated(DateUtil.getNowAsTimestamp());
		sampleOrganization.setSysUserId(formData.getSysUserId());
		sampleOrganization.setSample(sampleService.get(sampleId));
		sampleOrganization.setSysUserId(formData.getSysUserId());
		sampleOrganization.setOrganization(organizationService.get(formData.getReferringSiteCode()));
		return sampleOrganizationService.insert(sampleOrganization);
	}

	private String getObservationHistoryTypeId(String name) {
		ObservationHistoryType oht;
		oht = observationHistoryTypeService.getByName(name);
		if (oht != null) {
			return oht.getId();
		}
		return null;
	}

	@Override
	public void getBacterioFormData(SampleTbclassicForm form) {
		String labnoForSearch = form.getLabnoForSearch();
		if (ObjectUtils.isNotEmpty(labnoForSearch)) {
			Sample searchSample = sampleService.getSampleByAccessionNumber(labnoForSearch);

		}

	}
	
	
	





}
