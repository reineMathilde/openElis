package spring.generated.testconfiguration.controller;

import java.lang.String;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.math.NumberUtils;
import org.hibernate.HibernateException;
import org.hibernate.Transaction;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import spring.generated.forms.TestActivationForm;
import spring.mine.common.controller.BaseController;
import spring.mine.common.form.BaseForm;
import spring.mine.common.validator.BaseErrors;
import us.mn.state.health.lims.common.services.DisplayListService;
import us.mn.state.health.lims.common.services.TestService;
import us.mn.state.health.lims.common.services.TypeOfSampleService;
import us.mn.state.health.lims.common.util.IdValuePair;
import us.mn.state.health.lims.hibernate.HibernateUtil;
import us.mn.state.health.lims.test.valueholder.Test;
import us.mn.state.health.lims.typeofsample.dao.TypeOfSampleDAO;
import us.mn.state.health.lims.typeofsample.daoimpl.TypeOfSampleDAOImpl;
import us.mn.state.health.lims.typeofsample.valueholder.TypeOfSample;
import us.mn.state.health.lims.test.beanItems.TestActivationBean;
import us.mn.state.health.lims.test.dao.TestDAO;
import us.mn.state.health.lims.test.daoimpl.TestDAOImpl;

@Controller
public class TestActivationController extends BaseController {
  @RequestMapping(
      value = "/TestActivation",
      method = RequestMethod.GET
  )
  public ModelAndView showTestActivation(HttpServletRequest request,
      @ModelAttribute("form") TestActivationForm form) {
    String forward = FWD_SUCCESS;
    if (form == null) {
    	form = new TestActivationForm();
    }
        form.setFormAction("");
    BaseErrors errors = new BaseErrors();
    if (form.getErrors() != null) {
    	errors = (BaseErrors) form.getErrors();
    }
    ModelAndView mv = checkUserAndSetup(form, errors, request);

    if (errors.hasErrors()) {
    	return mv;
    }
    
    List<TestActivationBean> activeTestList = createTestList(true, false);
    List<TestActivationBean> inactiveTestList = createTestList(false, false);
    form.setActiveTestList(activeTestList);
    form.setInactiveTestList(inactiveTestList);
    
    return findForward(forward, form);
  }
  
  private List<TestActivationBean> createTestList(boolean active, boolean refresh) {
      ArrayList<TestActivationBean> testList = new ArrayList<TestActivationBean>();

      if (refresh) 
    	  DisplayListService.refreshList(active ? DisplayListService.ListType.SAMPLE_TYPE_ACTIVE : DisplayListService.ListType.SAMPLE_TYPE_INACTIVE);
      	  
      List<IdValuePair> sampleTypeList = DisplayListService.getList(active ? DisplayListService.ListType.SAMPLE_TYPE_ACTIVE : DisplayListService.ListType.SAMPLE_TYPE_INACTIVE);
      
      //if not active we use alphabetical ordering, the default is display order
      if( !active){
        IdValuePair.sortByValue( sampleTypeList );
      }

      for( IdValuePair pair : sampleTypeList){
          TestActivationBean bean = new TestActivationBean();

          List<Test> tests = TypeOfSampleService.getAllTestsBySampleTypeId(pair.getId());
          List<IdValuePair> activeTests = new ArrayList<IdValuePair>();
          List<IdValuePair> inactiveTests = new ArrayList<IdValuePair>();

          //initial ordering will be by display order.  Inactive tests will then be re-ordered alphabetically
          Collections.sort(tests, new Comparator<Test>() {
              @Override
              public int compare(Test o1, Test o2) {
              	//compare sort order
              	if (NumberUtils.isNumber(o1.getSortOrder()) && NumberUtils.isNumber(o2.getSortOrder())) {
              		return Integer.parseInt(o1.getSortOrder()) - Integer.parseInt(o2.getSortOrder());
                  //if o2 has no sort order o1 does, o2 is assumed to be higher
              	} else if (NumberUtils.isNumber(o1.getSortOrder())){
                  	return -1;
                  //if o1 has no sort order o2 does, o1 is assumed to be higher
                  } else if (NumberUtils.isNumber(o2.getSortOrder())) {
                  	return 1;
                  //else they are considered equal
                  } else {
                  	return 0;
                  }
              }
          });

          for( Test test : tests) {
              if( test.isActive()) {
                  activeTests.add(new IdValuePair(test.getId(), TestService.getUserLocalizedTestName(test)));
              }else{
                  inactiveTests.add(new IdValuePair(test.getId(), TestService.getUserLocalizedTestName(test)));
              }
          }

          IdValuePair.sortByValue( inactiveTests);

          bean.setActiveTests(activeTests);
          bean.setInactiveTests(inactiveTests);
          if( !activeTests.isEmpty() || !inactiveTests.isEmpty()) {
              bean.setSampleType(pair);
              testList.add(bean);
          }
      }

      return testList;
  }
  
  @RequestMapping(
	      value = "/TestActivation",
	      method = RequestMethod.POST
	  )
	  public ModelAndView postTestActivation(HttpServletRequest request,
	      @ModelAttribute("form") TestActivationForm form) throws Exception {
	  
	    String forward = FWD_SUCCESS;

	    BaseErrors errors = new BaseErrors();
	    if (form.getErrors() != null) {
	    	errors = (BaseErrors) form.getErrors();
	    }
	    ModelAndView mv = checkUserAndSetup(form, errors, request);

	    if (errors.hasErrors()) {
	    	return mv;
	    }

	    String changeList = form.getJsonChangeList();
        
	    JSONParser parser=new JSONParser();

        JSONObject obj = (JSONObject)parser.parse(changeList);

        List<ActivateSet> activateSampleSets = getActivateSetForActions("activateSample", obj, parser);
        List<String> deactivateSampleIds = getIdsForActions("deactivateSample", obj, parser);
        List<ActivateSet> activateTestSets = getActivateSetForActions("activateTest", obj, parser);
        List<String> deactivateTestIds = getIdsForActions("deactivateTest", obj, parser);

        List<Test> deactivateTests = getDeactivatedTests(deactivateTestIds);
        List<Test> activateTests = getActivatedTests(activateTestSets);
        List<TypeOfSample> deactivateSampleTypes = getDeactivatedSampleTypes(deactivateSampleIds );
        List<TypeOfSample> activateSampleTypes = getActivatedSampleTypes(activateSampleSets);

        Transaction tx = HibernateUtil.getSession().beginTransaction();

        TestDAO testDAO = new TestDAOImpl();
        TypeOfSampleDAO typeOfSampleDAO = new TypeOfSampleDAOImpl();

        try{
            for(Test test : deactivateTests){
                testDAO.updateData(test);
            }

            for(Test test : activateTests){
                testDAO.updateData(test);
            }

            for(TypeOfSample typeOfSample : deactivateSampleTypes){
                typeOfSampleDAO.updateData(typeOfSample);
            }

            for(TypeOfSample typeOfSample : activateSampleTypes){
                typeOfSampleDAO.updateData(typeOfSample);
            }

            if( !deactivateSampleTypes.isEmpty() || !activateSampleTypes.isEmpty()){
                TypeOfSampleService.clearCache();
            }

            tx.commit();
        }catch( HibernateException e ){
            tx.rollback();
        }finally{
            HibernateUtil.closeSession();
        }

	    List<TestActivationBean> activeTestList = createTestList(true, true);
	    List<TestActivationBean> inactiveTestList = createTestList(false, true);
	    form.setActiveTestList(activeTestList);
	    form.setInactiveTestList(inactiveTestList);
	  
	  return findForward(forward, form);
  }
  
  private List<Test> getDeactivatedTests(List<String> testIds) {
      List<Test> tests = new ArrayList<Test>();

      for( String testId : testIds){
          Test test = new TestService(testId).getTest();
          test.setIsActive( "N");
          test.setSysUserId(currentUserId);
          tests.add(test);
      }

      return tests;
  }

  private List<Test> getActivatedTests(List<ActivateSet> testIds) {
      List<Test> tests = new ArrayList<Test>();

      for( ActivateSet set : testIds){
          Test test = new TestService(set.id).getTest();
          test.setIsActive( "Y");
          test.setSortOrder( String.valueOf(set.sortOrder * 10));
          test.setSysUserId(currentUserId);
          tests.add(test);
      }

      return tests;
  }

  private List<TypeOfSample> getDeactivatedSampleTypes(List<String> sampleTypeIds) {
      List<TypeOfSample> sampleTypes = new ArrayList<TypeOfSample>();

      for( String id : sampleTypeIds){
          TypeOfSample typeOfSample = TypeOfSampleService.getTransientTypeOfSampleById(id);
          typeOfSample.setActive( false );
          typeOfSample.setSysUserId(currentUserId);
          sampleTypes.add(typeOfSample);
      }

      return sampleTypes;
  }

  private List<TypeOfSample> getActivatedSampleTypes(List<ActivateSet> sampleTypeSets) {
      List<TypeOfSample> sampleTypes = new ArrayList<TypeOfSample>();

      for( ActivateSet set : sampleTypeSets){
          TypeOfSample typeOfSample = TypeOfSampleService.getTransientTypeOfSampleById(set.id);
          typeOfSample.setActive( true );
          typeOfSample.setSortOrder(set.sortOrder * 10);
          typeOfSample.setSysUserId(currentUserId);
          sampleTypes.add(typeOfSample);
      }

      return sampleTypes;
  }

  private List<String> getIdsForActions(String key, JSONObject root, JSONParser parser){
      List<String> list = new ArrayList<String>();

      String action = (String)root.get(key);

      try {
          JSONArray actionArray = (JSONArray)parser.parse(action);

          for(int i = 0 ; i < actionArray.size(); i++   ){
              list.add((String) ((JSONObject) actionArray.get(i)).get("id"));
          }
      } catch (ParseException e) {
          e.printStackTrace();
      }

      return list;
  }

  private List<ActivateSet> getActivateSetForActions(String key, JSONObject root, JSONParser parser) {
      List<ActivateSet> list = new ArrayList<ActivateSet>();

      String action = (String)root.get(key);

      try {
          JSONArray actionArray = (JSONArray)parser.parse(action);

          for(int i = 0 ; i < actionArray.size(); i++   ){
              ActivateSet set = new ActivateSet();
              set.id = String.valueOf(((JSONObject) actionArray.get(i)).get("id"));
              Long longSort = (Long)((JSONObject) actionArray.get(i)).get("sortOrder");
              set.sortOrder = longSort.intValue();
              list.add(set);
          }
      } catch (ParseException e) {
          e.printStackTrace();
      }
      
      return list;
  }
  protected ModelAndView findLocalForward(String forward, BaseForm form) {
    if ("success".equals(forward)) {
      return new ModelAndView("testActivationDefinition", "form", form);
    } else {
      return new ModelAndView("PageNotFound");
    }
  }
  
  private class ActivateSet{
      public String id;
      public Integer sortOrder;
  }

  protected String getPageTitleKey() {
    return null;
  }

  protected String getPageSubtitleKey() {
    return null;
  }
}