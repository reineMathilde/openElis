package org.openelisglobal.sample.service;


import javax.servlet.http.HttpServletRequest;
import org.openelisglobal.sample.form.SampleTbclassicForm;

public interface SampleBacterioService {
     boolean persistBacterioData(SampleTbclassicForm form, HttpServletRequest request);
		
  	void getBacterioFormData(SampleTbclassicForm form);
	
	
}

