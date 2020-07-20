/**
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations under
 * the License.
 *
 * The Original Code is OpenELIS code.
 *
 * Copyright (C) The Minnesota Department of Health.  All Rights Reserved.
 *
 * Contributor(s): CIRG, University of Washington, Seattle WA.
 */
package org.openelisglobal.sample.daoimpl;

import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.validator.GenericValidator;
import org.hibernate.Session;
import org.openelisglobal.common.exception.LIMSRuntimeException;
import org.openelisglobal.common.log.LogEvent;
import org.openelisglobal.common.provider.query.PatientSearchResults;
import org.openelisglobal.patientidentitytype.util.PatientIdentityTypeMap;
import org.openelisglobal.sample.dao.SearchResultsDAO;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class SearchResultsDAOImp implements SearchResultsDAO {

    @PersistenceContext
    EntityManager entityManager;

    private static final String FIRST_NAME_PARAM = "firstName";
    private static final String LAST_NAME_PARAM = "lastName";
    private static final String NATIONAL_ID_PARAM = "nationalID";
    private static final String EXTERNAL_ID_PARAM = "externalID";
    private static final String ST_NUMBER_PARAM = "stNumber";
    private static final String SUBJECT_NUMBER_PARAM = "subjectNumber";
    private static final String ID_PARAM = "id";
    private static final String GUID = "guid";
    private static final String DATE_OF_BIRTH = "dateOfBirth";
    private static final String GENDER = "gender";

    private static final String ID_TYPE_FOR_ST = "stNumberId";
    private static final String ID_TYPE_FOR_SUBJECT_NUMBER = "subjectNumberId";
    private static final String ID_TYPE_FOR_GUID = "guidId";

    private static final Charset UTF_8 = Charset.forName("UTF-8");

    @Override
    @SuppressWarnings("rawtypes")
    @Transactional
    public List<PatientSearchResults> getSearchResults(String lastName, String firstName, String STNumber,
            String subjectNumber, String nationalID, String externalID, String patientID, String guid, String dateOfBirth, String gender)
            throws LIMSRuntimeException {

        List queryResults;

        try {
            boolean queryFirstName = !GenericValidator.isBlankOrNull(firstName);
            boolean queryLastName = !GenericValidator.isBlankOrNull(lastName);
            boolean queryNationalId = !GenericValidator.isBlankOrNull(nationalID);
            boolean querySTNumber = !GenericValidator.isBlankOrNull(STNumber);
            boolean querySubjectNumber = !GenericValidator.isBlankOrNull(subjectNumber);
            boolean queryExternalId = !GenericValidator.isBlankOrNull(externalID);
            boolean queryAnyID = queryExternalId && queryNationalId;
            boolean queryPatientID = !GenericValidator.isBlankOrNull(patientID);
            boolean queryGuid = !GenericValidator.isBlankOrNull(guid);
            boolean queryDateOfBirth = !GenericValidator.isBlankOrNull(dateOfBirth);
            boolean queryGender = !GenericValidator.isBlankOrNull(gender);

            String sql = buildQueryString(queryLastName, queryFirstName, querySTNumber, querySubjectNumber,
                    queryNationalId, queryExternalId, queryAnyID, queryPatientID, queryGuid, queryDateOfBirth, queryGender);

            org.hibernate.Query query = entityManager.unwrap(Session.class).createSQLQuery(sql);

            query.setInteger(ID_TYPE_FOR_ST, Integer.valueOf(PatientIdentityTypeMap.getInstance().getIDForType("ST")));
            query.setInteger(ID_TYPE_FOR_SUBJECT_NUMBER,
                    Integer.valueOf(PatientIdentityTypeMap.getInstance().getIDForType("SUBJECT")));
            query.setInteger(ID_TYPE_FOR_GUID,
                    Integer.valueOf(PatientIdentityTypeMap.getInstance().getIDForType("GUID")));

            lastName = '%' + lastName + '%';
            firstName = '%' + firstName + '%';
            STNumber = '%' + STNumber + '%';
            subjectNumber = '%' + subjectNumber + '%';
            nationalID = '%' + nationalID + '%';
            externalID = '%' + externalID + '%';
            patientID = '%' + patientID + '%';
            guid = '%' + guid + '%';
            dateOfBirth = '%' + dateOfBirth + '%';
//            gender = '%' + gender + '%';
            
            if (queryFirstName) {
                query.setString(FIRST_NAME_PARAM, firstName);
            }
            if (queryLastName) {
                query.setText(LAST_NAME_PARAM, lastName);
            }
            if (queryNationalId) {
                query.setString(NATIONAL_ID_PARAM, nationalID);
            }
            if (queryExternalId) {
                query.setString(EXTERNAL_ID_PARAM, nationalID);
            }
            if (querySTNumber) {
                query.setString(ST_NUMBER_PARAM, STNumber);
            }
            if (querySubjectNumber) {
                query.setString(SUBJECT_NUMBER_PARAM, subjectNumber);
            }
            if (queryPatientID) {
                query.setInteger(ID_PARAM, Integer.valueOf(patientID));
            }
            if (queryGuid) {
                query.setString(GUID, guid);
            }
            if (queryDateOfBirth) {
                query.setString(DATE_OF_BIRTH, dateOfBirth);
            }
            if (queryGender) {
                query.setString(GENDER, gender);
            }
            System.out.println(">>>: " + query.getQueryString());
//            String[] dArray = { " ", " ", subjectNumber, nationalID, gender, " ", " ", " "};
//            String[] sArray = query.getNamedParameters();
//            for (int i = 0; i < sArray.length; i++) {
//                System.out.println(">>>: " + sArray[i] + ":" + dArray[i] );
//            }
//            System.out.println(">>>: " +
//            "lastName" + lastName + ':' +
//            "firstName " +            firstName + ':' +
//            "STNumber " +            STNumber + ':' +
//            "subjectNumber " +            subjectNumber + ':' +
//            "nationalID " +            nationalID + ':' +
//            "externalID " +            externalID + ':' +
//            "patientID " +            patientID + ':' +
//            "guid " +            guid + ':' +
//            "dateOfBirth " +            dateOfBirth + ':' +
//            "gender " +            gender );
            
           
            
            
            queryResults = query.list();
        } catch (RuntimeException e) {
            LogEvent.logDebug(e);
            throw new LIMSRuntimeException("Error in SearchResultsDAOImpl getSearchResults()", e);
        }

        List<PatientSearchResults> results = new ArrayList<>();

        for (Object resultLine : queryResults) {

            Object[] line = (Object[]) resultLine;

            results.add(new PatientSearchResults((BigDecimal) line[0], (String) line[1], (String) line[2],
                    (String) line[3], (String) line[4], (String) line[5], (String) line[6], (String) line[7],
                    (String) line[8], (String) line[9], null));
        }

        return results;
    }

    /**
     * @param lastName
     * @param firstName
     * @param STNumber
     * @param subjectNumber
     * @param nationalID
     * @param externalID
     * @param patientID     - if a previous query has already found a candidate
     *                      patient
     */
    private String buildQueryString(boolean lastName, boolean firstName, boolean STNumber, boolean subjectNumber,
            boolean nationalID, boolean externalID, boolean anyID, boolean patientID, boolean guid, boolean dateOfBirth, boolean gender) {

        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append(
                "select p.id, pr.first_name, pr.last_name, p.gender, p.entered_birth_date, p.national_id, p.external_id, pi.identity_data as st, piSN.identity_data as subject, piGUID.identity_data as guid from patient p join person pr on p.person_id = pr.id ");
        queryBuilder.append("left join patient_identity  pi on pi.patient_id = p.id and pi.identity_type_id = :");
        queryBuilder.append(ID_TYPE_FOR_ST);
        queryBuilder.append(" ");

        queryBuilder.append("left join patient_identity  piSN on piSN.patient_id = p.id and piSN.identity_type_id = :");
        queryBuilder.append(ID_TYPE_FOR_SUBJECT_NUMBER);
        queryBuilder.append(" ");

        queryBuilder.append(
                "left join patient_identity  piGUID on piGUID.patient_id = p.id and piGUID.identity_type_id = :");
        queryBuilder.append(ID_TYPE_FOR_GUID);
        queryBuilder.append(" where ");

        if (anyID) {
            queryBuilder.append(" ( false or ");
            if (subjectNumber) {
                queryBuilder.append(" piSN.identity_data ilike :");
                queryBuilder.append(SUBJECT_NUMBER_PARAM);
                queryBuilder.append(" or");
            }

            if (nationalID) {
                queryBuilder.append(" p.national_id ilike :");
                queryBuilder.append(NATIONAL_ID_PARAM);
                queryBuilder.append(" or");
            }

            if (externalID) {
                queryBuilder.append(" p.external_id ilike :");
                queryBuilder.append(EXTERNAL_ID_PARAM);
                queryBuilder.append(" or");
            }
            
            if (STNumber) {
                queryBuilder.append(" pi.identity_data ilike :");
                queryBuilder.append(ST_NUMBER_PARAM);
                queryBuilder.append(" and");
            }
            
        } else {
            queryBuilder.append(" ( false or ");
            if (subjectNumber) {
                queryBuilder.append(" piSN.identity_data ilike :");
                queryBuilder.append(SUBJECT_NUMBER_PARAM);
                queryBuilder.append(" or");
            }

            if (nationalID) {
                queryBuilder.append(" p.national_id ilike :");
                queryBuilder.append(NATIONAL_ID_PARAM);
                queryBuilder.append(" or");
            }

            if (externalID) {
                queryBuilder.append(" p.external_id ilike :");
                queryBuilder.append(EXTERNAL_ID_PARAM);
                queryBuilder.append(" or");
            }
            
            if (STNumber) {
                queryBuilder.append(" pi.identity_data ilike :");
                queryBuilder.append(ST_NUMBER_PARAM);
                queryBuilder.append(" and");
            }
        }
        
        // Need to close paren before dangling AND/OR.
        int lastAndIndex = queryBuilder.lastIndexOf("and");
        int lastOrIndex = queryBuilder.lastIndexOf("or");

        if (lastAndIndex > lastOrIndex) {
            queryBuilder.delete(lastAndIndex, queryBuilder.length());
            queryBuilder.append(") and");
        } else if (lastOrIndex > lastAndIndex) {
            queryBuilder.delete(lastOrIndex, queryBuilder.length());
            queryBuilder.append(") or");
        }

        if (patientID) {
            queryBuilder.append(" p.id = :");
            queryBuilder.append(ID_PARAM);
            queryBuilder.append(" and");
        }

        if (guid) {
            queryBuilder.append(" piGUID.identity_data = :");
            queryBuilder.append(GUID);
            queryBuilder.append(" and");
        }
        
        if (lastName) {
            queryBuilder.append(" pr.last_name ilike :");
            queryBuilder.append(LAST_NAME_PARAM);
            queryBuilder.append(" and");
        }

        if (firstName) {
            queryBuilder.append(" pr.first_name ilike :");
            queryBuilder.append(FIRST_NAME_PARAM);
            queryBuilder.append(" and");
        }
        
        if (dateOfBirth) {
            queryBuilder.append(" p.entered_birth_date ilike :");
            queryBuilder.append(DATE_OF_BIRTH);
            queryBuilder.append(" and");
        }
        
        if (gender) {
            queryBuilder.append(" p.gender = :");
            queryBuilder.append(GENDER);
            queryBuilder.append(" and");
        }
     
        // No matter which was added last there is one dangling AND to remove.
        lastAndIndex = queryBuilder.lastIndexOf("and");
        lastOrIndex = queryBuilder.lastIndexOf("or");

        if (lastAndIndex > lastOrIndex) {
            queryBuilder.delete(lastAndIndex, queryBuilder.length());
        } else if (lastOrIndex > lastAndIndex) {
            queryBuilder.delete(lastOrIndex, queryBuilder.length());
        }

        return queryBuilder.toString();
    }

    public List getNextRecord(String id, String table, Class clazz) throws LIMSRuntimeException {
        // TODO Auto-generated method stub
        return null;
    }

    public List getPreviousRecord(String id, String table, Class clazz) throws LIMSRuntimeException {
        // TODO Auto-generated method stub
        return null;
    }

    public Integer getTotalCount(String table, Class clazz) throws LIMSRuntimeException {
        // TODO Auto-generated method stub
        return null;
    }

}
