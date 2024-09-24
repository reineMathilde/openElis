package org.openelisglobal.samplemicrobioclassic.dao;

import java.util.List;

import org.openelisglobal.common.dao.BaseDAO;
import org.openelisglobal.common.exception.LIMSRuntimeException;
import org.openelisglobal.patient.valueholder.Patient;
import org.openelisglobal.sample.valueholder.Sample;
import org.openelisglobal.samplemicrobioclassik.valueholder.Samplemicrobioclassic;

public interface SamplemicrobioclassicDao  extends BaseDAO<Samplemicrobioclassic, String>{
	

List<Samplemicrobioclassic> getAllSamples() throws LIMSRuntimeException;



Samplemicrobioclassic getSampleByPatientId(String patientId) throws LIMSRuntimeException;

void saveSample(Samplemicrobioclassic sample) throws LIMSRuntimeException;

void updateSample(Samplemicrobioclassic sample) throws LIMSRuntimeException;



//List<Samplemicrobioclassic> getSamplesByClinicalInformation(String clinicalInformation) throws LIMSRuntimeException;






//Patient getPatientForSample(Samplemicrobioclassic sample) throws LIMSRuntimeException;

public List<Patient> getAllPatientsWithSampleEntered();

public List<Patient> getAllPatientsWithSampleEnteredMissingFhirUuid();





}





