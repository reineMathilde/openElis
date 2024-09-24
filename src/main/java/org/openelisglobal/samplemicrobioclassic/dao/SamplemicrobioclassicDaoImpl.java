package org.openelisglobal.samplemicrobioclassic.dao;

import java.util.Vector;


import org.openelisglobal.common.log.LogEvent;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.openelisglobal.common.dao.BaseDAO;
import org.openelisglobal.common.exception.LIMSRuntimeException;
import org.openelisglobal.patient.valueholder.Patient;
import org.openelisglobal.patient.valueholder.PatientContact;
import org.openelisglobal.person.valueholder.Person;
import org.openelisglobal.sample.valueholder.Sample;
import org.openelisglobal.samplemicrobioclassik.valueholder.Samplemicrobioclassic;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Vector;

import org.apache.commons.beanutils.PropertyUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.openelisglobal.common.daoimpl.BaseDAOImpl;
import org.openelisglobal.common.exception.LIMSRuntimeException;
import org.openelisglobal.common.log.LogEvent;
import org.openelisglobal.common.util.SystemConfiguration;

import org.springframework.stereotype.Component;

import org.springframework.transaction.annotation.Transactional;


@Component
@Transactional
public class SamplemicrobioclassicDaoImpl extends BaseDAOImpl<Samplemicrobioclassic, String> implements SamplemicrobioclassicDao {

	   public SamplemicrobioclassicDaoImpl() {
	        super(Samplemicrobioclassic.class);
	   }
	    @Override
	    @Transactional(readOnly = true)
	    public List<Samplemicrobioclassic> getAllSamples() throws LIMSRuntimeException {
	        try {
	            String sql = "from Samplemicrobioclassic";
	            Query<Samplemicrobioclassic> query = entityManager.unwrap(Session.class).createQuery(sql, Samplemicrobioclassic.class);
	            return query.list();
	        } catch (HibernateException e) {
	            handleException(e, "getAllSamples");
	            return null;
	        }
	    }
	    

	    @Override
	    @Transactional(readOnly = true)
	    public Samplemicrobioclassic getSampleByPatientId(String patientId) throws LIMSRuntimeException {
	        try {
	            String sql = "from Samplemicrobioclassic s where s.patient.id = :patientId";
	            Query<Samplemicrobioclassic> query = entityManager.unwrap(Session.class).createQuery(sql, Samplemicrobioclassic.class);
	            query.setParameter("patientId", patientId);
	            return query.uniqueResult();
	        } catch (HibernateException e) {
	            handleException(e, "getSampleByPatientId");
	            return null;
	        }
	    }

	    @Override
	    @Transactional
	    public void saveSample(Samplemicrobioclassic sample) throws LIMSRuntimeException {
	        try {
	            entityManager.unwrap(Session.class).save(sample);
	        } catch (HibernateException e) {
	            handleException(e, "saveSample");
	        }
	    }

	    @Override
	    @Transactional
	    public void updateSample(Samplemicrobioclassic sample) throws LIMSRuntimeException {
	        try {
	            entityManager.unwrap(Session.class).update(sample);
	        } catch (HibernateException e) {
	            handleException(e, "updateSample");
	        }
	    }



	    private void handleException(HibernateException e, String methodName) {
	        LogEvent.logError(e.toString(), e);
	        throw new LIMSRuntimeException("Error in SamplemicrobioclassicDaoImpl " + methodName, e);
	    }
	    
	    
	    
	    
	    

	    
	    
	    @Override
	    public List<Patient> getAllPatientsWithSampleEntered() {
	        List<Patient> patients = new ArrayList<>();
	        try {
	            String sql = "select distinct patient from Patient as patient, Samplemicrobioclassic as samplemicrobioclassic where samplemicrobioclassic.patientId = patient.id";
	            Query<Patient> query = entityManager.unwrap(Session.class).createQuery(sql, Patient.class);
	            patients = query.getResultList();
	        } catch (HibernateException e) {
	            LogEvent.logError(e.toString(), e);
	            throw new LIMSRuntimeException("Error in Samplemicrobioclassic getAllPatientsWithSampleEntered()", e);
	        }

	        return patients;
	    }
	    
	    @Override
	    public List<Patient> getAllPatientsWithSampleEnteredMissingFhirUuid() {
	        List<Patient> patients = new ArrayList<>();
	        try {
	            String sql = "select distinct patient from Patient as patient, Samplemicrobioclassic as samplemicrobioclassic where samplemicrobioclassic.patientId = patient.id AND patient.fhirUuid is null";
	            Query<Patient> query = entityManager.unwrap(Session.class).createQuery(sql, Patient.class);
	            patients = query.getResultList();
	        } catch (HibernateException e) {
	            LogEvent.logError(e.toString(), e);
	            throw new LIMSRuntimeException("Error in Samplemicrobioclassic getAllPatientsWithSampleEntered()", e);
	        }

	        return patients;
	    }
	    
	    
	    
}
