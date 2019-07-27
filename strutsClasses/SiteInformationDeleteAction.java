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
package org.openelisglobal.siteinformation.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.action.DynaActionForm;

import org.openelisglobal.common.action.BaseAction;
import org.openelisglobal.common.exception.LIMSRuntimeException;
import org.openelisglobal.common.util.ConfigurationProperties;
import org.openelisglobal.common.util.validator.ActionError;
import org.openelisglobal.hibernate.HibernateUtil;
import org.openelisglobal.siteinformation.dao.SiteInformationDAO;
import org.openelisglobal.siteinformation.daoimpl.SiteInformationDAOImpl;


public class SiteInformationDeleteAction extends BaseAction {
	static private String FWD_CLOSE = "close";

	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String forward = "success";

		DynaActionForm dynaForm = (DynaActionForm) form;

		String[] selectedIDs = (String[]) dynaForm.get("selectedIDs");
		String currentUserId = getSysUserId(request);

		SiteInformationDAO siteInformationDAO = new SiteInformationDAOImpl();

		org.hibernate.Transaction tx = HibernateUtil.getSession().beginTransaction();
		ActionMessages errors = null;
		try {


			for( String siteInformationId : selectedIDs){
				siteInformationDAO.deleteData(siteInformationId, currentUserId);
			}

			tx.commit();
		} catch (LIMSRuntimeException lre) {
			tx.rollback();

			errors = new ActionMessages();
			ActionError error = null;
			if (lre.getException() instanceof org.hibernate.StaleObjectStateException) {
				error = new ActionError("errors.OptimisticLockException", null,	null);
			} else {
				error = new ActionError("errors.DeleteException", null, null);
			}
			errors.add(ActionMessages.GLOBAL_MESSAGE, error);
			saveErrors(request, errors);
			request.setAttribute(Globals.ERROR_KEY, errors);
			forward = FWD_FAIL;

		}  finally {
            HibernateUtil.closeSession();
        }

		if (forward.equals(FWD_FAIL))
			return mapping.findForward(forward);

		if (TRUE.equalsIgnoreCase(request.getParameter("close"))) {
			forward = FWD_CLOSE;
		}

		ConfigurationProperties.forceReload();

		request.setAttribute("menuDefinition", "SiteInformationMenuDefinition");

		return mapping.findForward(forward);
	}

	protected String getPageTitleKey() {
		return null;
	}

	protected String getPageSubtitleKey() {
		return null;
	}
}