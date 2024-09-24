package org.openelisglobal.samplemicrobioclassic.dao;

import java.util.List;

import org.openelisglobal.common.service.BaseObjectServiceImpl;
import org.openelisglobal.patient.valueholder.Patient;
import org.openelisglobal.sample.valueholder.Sample;
import org.openelisglobal.samplehuman.dao.SampleHumanDAO;
import org.openelisglobal.samplehuman.valueholder.SampleHuman;
import org.openelisglobal.samplemicrobioclassik.valueholder.Samplemicrobioclassic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MicrobioClassicServiceImpl extends BaseObjectServiceImpl<Samplemicrobioclassic, String> implements MicrobioClassicService {
	

   @Autowired
   private SamplemicrobioclassicDao samplemicrobioclassicDao;
   
  

  public MicrobioClassicServiceImpl() {
      super(Samplemicrobioclassic.class);
   }

@Override
protected SamplemicrobioclassicDao getBaseObjectDAO() {
return samplemicrobioclassicDao;
}

@Override
public List<Samplemicrobioclassic> getAllSamples() {
return samplemicrobioclassicDao.getAllSamples();
}


@Override
public Samplemicrobioclassic getSampleByPatientId(String patientId) {
    return samplemicrobioclassicDao.getSampleByPatientId(patientId);
}

@Override
public void saveSample(Samplemicrobioclassic sample) {
    samplemicrobioclassicDao.saveSample(sample);
}

@Override
public void updateSample(Samplemicrobioclassic sample) {
    samplemicrobioclassicDao.updateSample(sample);
}





@Override
public List<Patient> getAllPatientsWithSampleEntered() {
    return samplemicrobioclassicDao.getAllPatientsWithSampleEntered();
}

@Override
public List<Patient> getAllPatientsWithSampleEnteredMissingFhirUuid() {
    return samplemicrobioclassicDao.getAllPatientsWithSampleEnteredMissingFhirUuid();
}

@Override
public Samplemicrobioclassic getSampleById(String id) {
	// TODO Auto-generated method stub
	return null;
}
}