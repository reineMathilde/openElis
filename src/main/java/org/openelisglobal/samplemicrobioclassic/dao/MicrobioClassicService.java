package org.openelisglobal.samplemicrobioclassic.dao;

import java.util.List;

import org.openelisglobal.common.service.BaseObjectService;
import org.openelisglobal.patient.valueholder.Patient;
import org.openelisglobal.person.valueholder.Person;
import org.openelisglobal.sample.valueholder.Sample;
import org.openelisglobal.samplemicrobioclassik.valueholder.Samplemicrobioclassic;

public interface MicrobioClassicService extends BaseObjectService<Samplemicrobioclassic, String> {
	 List<Samplemicrobioclassic> getAllSamples();

	    Samplemicrobioclassic getSampleById(String id);

	    Samplemicrobioclassic getSampleByPatientId(String patientId);

	    void saveSample(Samplemicrobioclassic sample);

	    void updateSample(Samplemicrobioclassic sample);
	    
	   
	    List<Patient> getAllPatientsWithSampleEntered();
	    List<Patient> getAllPatientsWithSampleEnteredMissingFhirUuid();

}
