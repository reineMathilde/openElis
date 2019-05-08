package spring.generated.testconfiguration.validator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import spring.generated.testconfiguration.form.SampleTypeOrderForm;
import spring.mine.common.JSONUtils;
import spring.mine.common.validator.ValidationHelper;
import us.mn.state.health.lims.common.log.LogEvent;

@Component
public class SampleTypeOrderFormValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return SampleTypeOrderForm.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		SampleTypeOrderForm form = (SampleTypeOrderForm) target;

		try {
			JSONObject changeList = JSONUtils.getAsObject(form.getJsonChangeList());

			JSONArray sampleTypes = JSONUtils.getAsArray(changeList.get("sampleTypes"));
			for (int i = 0; i < sampleTypes.size(); ++i) {
				JSONObject sampleType = JSONUtils.getAsObject(sampleTypes.get(i));

				ValidationHelper.validateIdField(String.valueOf(sampleType.get("id")), "JsonChangeList",
						"id[" + i + "]", errors, true);

				ValidationHelper.validateFieldAndCharset(String.valueOf(sampleType.get("sortOrder")), "JsonChangeList",
						"sort order[" + i + "]", errors, true, 3, "0-9");
			}
		} catch (ParseException e) {
			LogEvent.logError("SampleTypeOrderFormValidator", "validate()", e.toString());
			errors.rejectValue("jsonChangeList", "error.field.format.json");
		}

	}

}