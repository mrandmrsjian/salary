package com.csjian.servlet;

import java.io.*;
import java.util.*;

import com.csjian.model.bean.CompanyBean;
import com.csjian.model.dao.*;
import com.csjian.form.CreateRemoveForm;
import com.csjian.util.*;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * Servlet implementation class for Servlet: ShowFile
 *
 */
public class RemoveFormServlet extends javax.servlet.http.HttpServlet implements
		javax.servlet.Servlet {
	/*
	 * (non-Java-doc)
	 * 
	 * @see javax.servlet.http.HttpServlet#HttpServlet()
	 */
	public RemoveFormServlet() {
		super();
	}

	/*
	 * (non-Java-doc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request,
	 * HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			response.setHeader("Content-Transfer-Encoding", "binary");
			response.setHeader("Content-Disposition", "attachment;filename=\"removeform.pdf" + "\"");
			response.setContentType("application/pdf");
			String regcode = request.getParameter("regcode") != null ? request.getParameter("regcode") : "";
			String year = request.getParameter("year") != null ? request.getParameter("year") : "";
			Vector people = new Vector();
			int count = request.getParameter("count") != null ? Integer.parseInt(request.getParameter("count")) : 0;
			for (int i = 0; i < count; i++) {
				if (request.getParameter("employeeno" + i) != null && !request.getParameter("employeeno" + i).equals("")) {
					String[] person = { "", "", "", "", "", "", "", "", "", "" };
					person[0] = request.getParameter("employeeno" + i);
					person[1] = request.getParameter("isdependant" + i);
					person[2] = request.getParameter("name" + i);
					person[3] = request.getParameter("unicode" + i);
					person[4] = request.getParameter("ename" + i);
					person[5] = request.getParameter("eunicode" + i);
					person[6] = StringUtils.twToAd(request.getParameter("ebirth" + i));
					person[7] = request.getParameter("reason" + i);
					person[8] = request.getParameter("code" + i);
					person[9] = StringUtils.twToAd(request.getParameter("adate" + i));
					people.add(person);
				}
			}

			ServletContext servletContext = request.getSession().getServletContext();
			WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
			DaoFactory daoFactory = (DaoFactory) wac.getBean("daoFactory");
			InsuranceDao dao = (InsuranceDao) daoFactory.createDao(DaoFactory.INSURANCE);
			dao.removeInsurance(regcode, year, people);
			daoFactory.returnDao(DaoFactory.INSURANCE, dao);
			CompanyDao cdao = (CompanyDao) daoFactory.createDao(DaoFactory.COMPANY);
			CompanyBean company = cdao.getCompany(regcode);
			daoFactory.returnDao(DaoFactory.COMPANY, cdao);

			OutputStream os = response.getOutputStream();
			(new CreateRemoveForm()).renderForm(
					regcode,
					people,
					this.getServletContext().getResourceAsStream("/WEB-INF/blankform/removeform.pdf"), company, os);
			os.flush();
			os.close();
		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}

	/*
	 * (non-Java-doc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request,
	 * HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}