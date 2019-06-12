package spring.service.resultlimit;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.validator.GenericValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import spring.mine.internationalization.MessageUtil;
import spring.service.common.BaseObjectServiceImpl;
import spring.service.dictionary.DictionaryService;
import spring.service.siteinformation.SiteInformationService;
import spring.service.typeoftestresult.TypeOfTestResultService;
import spring.util.SpringContext;
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.util.DateUtil;
import us.mn.state.health.lims.common.util.IdValuePair;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.patient.valueholder.Patient;
import us.mn.state.health.lims.resultlimits.dao.ResultLimitDAO;
import us.mn.state.health.lims.resultlimits.valueholder.ResultLimit;
import us.mn.state.health.lims.siteinformation.valueholder.SiteInformation;
import us.mn.state.health.lims.test.valueholder.Test;

@Service
@DependsOn({ "springContext" })
public class ResultLimitServiceImpl extends BaseObjectServiceImpl<ResultLimit, String> implements ResultLimitService {

	private static ResultLimitServiceImpl INSTANCE;

	private final double INVALID_PATIENT_AGE = Double.MIN_VALUE;
	private String NUMERIC_RESULT_TYPE_ID;
	private String SELECT_LIST_RESULT_TYPE_IDS;

	@Autowired
	protected ResultLimitDAO baseObjectDAO = SpringContext.getBean(ResultLimitDAO.class);

	@Autowired
	private DictionaryService dictionaryService = SpringContext.getBean(DictionaryService.class);
	@Autowired
	private SiteInformationService siteInformationService = SpringContext.getBean(SiteInformationService.class);
	@Autowired
	private TypeOfTestResultService typeOfTestResultService = SpringContext.getBean(TypeOfTestResultService.class);

	private double currPatientAge;

	public synchronized void initializeGlobalVariables() {
		NUMERIC_RESULT_TYPE_ID = typeOfTestResultService.getTypeOfTestResultByType("N").getId();
		SELECT_LIST_RESULT_TYPE_IDS = typeOfTestResultService.getTypeOfTestResultByType("D").getId()
				+ typeOfTestResultService.getTypeOfTestResultByType("M").getId();

	}

	public ResultLimitServiceImpl() {
		super(ResultLimit.class);
		initializeGlobalVariables();
	}

	@PostConstruct
	private void registerInstance() {
		INSTANCE = this;
	}

	public static ResultLimitService getInstance() {
		return INSTANCE;
	}

	@Override
	protected ResultLimitDAO getBaseObjectDAO() {
		return baseObjectDAO;
	}
	
	@Override
	public ResultLimit getResultLimitForTestAndPatient(Test test, Patient patient) {
		return getResultLimitForTestAndPatient(test.getId(), patient);
	}

	@Override
	public ResultLimit getResultLimitForTestAndPatient(String testId, Patient patient) {
		currPatientAge = INVALID_PATIENT_AGE;

		List<ResultLimit> resultLimits = getResultLimits(testId);

		if (resultLimits.isEmpty()) {
			return null;
		} else if (patient == null
				|| patient.getBirthDate() == null && GenericValidator.isBlankOrNull(patient.getGender())) {
			return defaultResultLimit(resultLimits);
		} else if (GenericValidator.isBlankOrNull(patient.getGender())) {
			return ageBasedResultLimit(resultLimits, patient);
		} else if (patient.getBirthDate() == null) {
			return genderBasedResultLimit(resultLimits, patient);
		} else {
			return ageAndGenderBasedResultLimit(resultLimits, patient);
		}
	}

	private ResultLimit defaultResultLimit(List<ResultLimit> resultLimits) {

		for (ResultLimit limit : resultLimits) {
			if (GenericValidator.isBlankOrNull(limit.getGender()) && limit.ageLimitsAreDefault()) {
				return limit;
			}
		}
		return new ResultLimit();
	}

	private ResultLimit ageBasedResultLimit(List<ResultLimit> resultLimits, Patient patient) {

		ResultLimit resultLimit = null;

		// First we look for a limit with no gender
		for (ResultLimit limit : resultLimits) {
			if (GenericValidator.isBlankOrNull(limit.getGender()) && !limit.ageLimitsAreDefault()
					&& getCurrPatientAge(patient) >= limit.getMinAge()
					&& getCurrPatientAge(patient) <= limit.getMaxAge()) {

				resultLimit = limit;
				break;
			}
		}

		// if none is found then drop the no gender requirement
		if (resultLimit == null) {
			for (ResultLimit limit : resultLimits) {
				if (!limit.ageLimitsAreDefault() && getCurrPatientAge(patient) >= limit.getMinAge()
						&& getCurrPatientAge(patient) <= limit.getMaxAge()) {

					resultLimit = limit;
					break;
				}
			}
		}

		return resultLimit == null ? defaultResultLimit(resultLimits) : resultLimit;
	}

	private ResultLimit genderBasedResultLimit(List<ResultLimit> resultLimits, Patient patient) {

		ResultLimit resultLimit = null;

		// First we look for a limit with no age
		for (ResultLimit limit : resultLimits) {
			if (limit.ageLimitsAreDefault() && patient.getGender().equals(limit.getGender())) {

				resultLimit = limit;
				break;
			}
		}

		// drop the age limit
		if (resultLimit == null) {
			for (ResultLimit limit : resultLimits) {
				if (patient.getGender().equals(limit.getGender())) {
					resultLimit = limit;
					break;
				}
			}
		}
		return resultLimit == null ? defaultResultLimit(resultLimits) : resultLimit;
	}

	/*
	 * We only get here if patient has age and gender
	 */
	private ResultLimit ageAndGenderBasedResultLimit(List<ResultLimit> resultLimits, Patient patient) {

		ResultLimit resultLimit = null;

		List<ResultLimit> fullySpecifiedLimits = new ArrayList<>();
		// first age and gender matter
		for (ResultLimit limit : resultLimits) {
			if (patient.getGender().equals(limit.getGender()) && !limit.ageLimitsAreDefault()) {

				// if fully qualified don't retest for only part of the
				// qualification
				fullySpecifiedLimits.add(limit);

				if (patientInAgeRange(patient, limit)) {

					resultLimit = limit;
					break;
				}
			}
		}

		resultLimits.removeAll(fullySpecifiedLimits);

		// second only age matters
		if (resultLimit == null) {
			for (ResultLimit limit : resultLimits) {
				if (!limit.ageLimitsAreDefault() && patientInAgeRange(patient, limit)) {

					resultLimit = limit;
					break;
				}
			}
		}

		// third only gender matters
		return resultLimit == null ? genderBasedResultLimit(resultLimits, patient) : resultLimit;
	}

	private boolean patientInAgeRange(Patient patient, ResultLimit limit) {
		return getCurrPatientAge(patient) >= limit.getMinAge() && getCurrPatientAge(patient) <= limit.getMaxAge();
	}

	private double getCurrPatientAge(Patient patient) {
		if (currPatientAge == INVALID_PATIENT_AGE && patient.getBirthDate() != null) {

			Calendar dob = Calendar.getInstance();
			dob.setTime(patient.getBirthDate());

			currPatientAge = DateUtil.getAgeInMonths(patient.getBirthDate(), new Date());
		}

		return currPatientAge;
	}

	@Override
	public String getDisplayNormalRange(double low, double high, String significantDigits, String separator) {

		if (low == Float.NEGATIVE_INFINITY && high == Float.POSITIVE_INFINITY) {
			return MessageUtil.getMessage("result.anyValue");
		}

		if (low == high) {
			return "";
		}

		if (high == Float.POSITIVE_INFINITY) {
			return "> " + StringUtil.doubleWithSignificantDigits(low, significantDigits);
		}

		if (low == Float.NEGATIVE_INFINITY) {
			return "< " + StringUtil.doubleWithSignificantDigits(high, significantDigits);
		}

		return StringUtil.doubleWithSignificantDigits(low, significantDigits) + separator
				+ StringUtil.doubleWithSignificantDigits(high, significantDigits);
	}

	@Override
	public String getDisplayReferenceRange(ResultLimit resultLimit, String significantDigits, String separator) {
		String range = "";
		if (resultLimit != null && !GenericValidator.isBlankOrNull(resultLimit.getResultTypeId())) {
			if (NUMERIC_RESULT_TYPE_ID.equals(resultLimit.getResultTypeId())) {
				range = getDisplayNormalRange(resultLimit.getLowNormal(), resultLimit.getHighNormal(),
						significantDigits, separator);
			} else if (SELECT_LIST_RESULT_TYPE_IDS.contains(resultLimit.getResultTypeId())
					&& !GenericValidator.isBlankOrNull(resultLimit.getDictionaryNormalId())) {
				return dictionaryService.getDataForId(resultLimit.getDictionaryNormalId()).getLocalizedName();
			}
		}
		return range;
	}

	/**
	 * Get the valid range for numeric result limits. For other result types an
	 * empty string will be returned
	 *
	 * @param resultLimit       The limit from which we will get the valid range
	 * @param significantDigits The numbe of significant digit to display
	 * @param separator         -- how to separate the numbers
	 * @return The range
	 */
	@Override
	public String getDisplayValidRange(ResultLimit resultLimit, String significantDigits, String separator) {
		String range = "";
		if (resultLimit != null && !GenericValidator.isBlankOrNull(resultLimit.getResultTypeId())) {
			if (NUMERIC_RESULT_TYPE_ID.equals(resultLimit.getResultTypeId())) {
				range = getDisplayNormalRange(resultLimit.getLowValid(), resultLimit.getHighValid(), significantDigits,
						separator);
			}
		}
		return range;
	}

	@Override
	public String getDisplayAgeRange(ResultLimit resultLimit, String separator) {
		if (resultLimit.getMinAge() == 0 && resultLimit.getMaxAge() == Float.POSITIVE_INFINITY) {
			return MessageUtil.getMessage("age.anyAge");
		}

		if (resultLimit.getMaxAge() == Float.POSITIVE_INFINITY) {
			return ">" + resultLimit.getMinAge();
		}

		return resultLimit.getMinAge() + separator + resultLimit.getMaxAge();
	}

	@Override
	public List<ResultLimit> getResultLimits(Test test) {
		return getResultLimits(test.getId());
	}

	@Override
	public List<ResultLimit> getResultLimits(String testId) {
		return baseObjectDAO.getAllResultLimitsForTest(testId);
	}

	/**
	 * The id in the returned set of IdValuePair refers to the upper end of the age
	 * range in months It will be either a number or "Infinity"
	 *
	 * @return A list of pairs
	 */
	@Override
	public List<IdValuePair> getPredefinedAgeRanges() {
		List<IdValuePair> ages = new ArrayList<>();

		List<SiteInformation> siteInformationList = siteInformationService
				.getSiteInformationByDomainName("resultAgeRange");

		for (SiteInformation info : siteInformationList) {
			String localizedName = null;
			if ("new born".equals(info.getName())) {
				localizedName = info.getLocalizedName();
			} else if ("infant".equals(info.getName())) {
				localizedName = info.getLocalizedName();
			} else if ("young child".equals(info.getName())) {
				localizedName = info.getLocalizedName();
			} else if ("child".equals(info.getName())) {
				localizedName = info.getLocalizedName();
			} else if ("adult".equals(info.getName())) {
				localizedName = info.getLocalizedName();
			}

			ages.add(new IdValuePair(info.getValue(), localizedName));
		}

		Collections.sort(ages, new Comparator<IdValuePair>() {
			@Override
			public int compare(IdValuePair o1, IdValuePair o2) {
				if ("Infinity".equals(o1.getId())) {
					return 1;
				}

				if ("Infinity".equals(o2.getId())) {
					return -1;
				}

				return Integer.parseInt(o1.getId()) - Integer.parseInt(o2.getId());
			}
		});

		return ages;
	}

	@Override
	public List getAllResultLimits() throws LIMSRuntimeException {
		return getBaseObjectDAO().getAllResultLimits();
	}

	@Override
	public List getPageOfResultLimits(int startingRecNo) throws LIMSRuntimeException {
		return getBaseObjectDAO().getPageOfResultLimits(startingRecNo);
	}

	@Override
	public void getData(ResultLimit resultLimit) throws LIMSRuntimeException {
		getBaseObjectDAO().getData(resultLimit);
	}

	@Override
	public List getNextResultLimitRecord(String id) throws LIMSRuntimeException {
		return getBaseObjectDAO().getNextResultLimitRecord(id);
	}

	@Override
	public List getPreviousResultLimitRecord(String id) throws LIMSRuntimeException {
		return getBaseObjectDAO().getPreviousResultLimitRecord(id);
	}

	@Override
	public List<ResultLimit> getAllResultLimitsForTest(String testId) throws LIMSRuntimeException {
		return getBaseObjectDAO().getAllResultLimitsForTest(testId);
	}
	
	

	@Override
	public ResultLimit getResultLimitById(String resultLimitId) throws LIMSRuntimeException {
		return getBaseObjectDAO().getResultLimitById(resultLimitId);
	}
}