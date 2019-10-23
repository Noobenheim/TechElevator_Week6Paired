package com.techelevator.projects.view;


import java.sql.SQLException;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import com.techelevator.projects.model.Department;
import com.techelevator.projects.model.jdbc.JDBCDepartmentDAO;

public class JDBCDepartmentDAOIntegrationTest {
	private static SingleConnectionDataSource projects;
	private static JDBCDepartmentDAO dao;

	@BeforeClass
	public static void setupDataSource() {
		projects = DBTestHelper.getConnection();
		dao = DBTestHelper.getDepartmentDAO();
	}

	@AfterClass
	public static void closeDataSource() {
		projects.destroy();
	}

	@After
	public void walmart() throws SQLException {
		projects.getConnection().rollback();
	}
	
	@Test
	public void get_all_departments() {
		List<Department> oldDepartments = dao.getAllDepartments();
		//don't need to do own sql, don't have to do insert statement directly because what the insert would have done is already done in the create department
		Department addDepartment = new Department ();
		addDepartment.setName ("Operations");
		dao.createDepartment(addDepartment);
		List<Department> newDepartments = dao.getAllDepartments();
		Assert.assertEquals(oldDepartments.size() + 1, newDepartments.size());//compare the two
	}
	
	@Test
	public void search_departments_by_name() {
		List<Department> oldDepartments = dao.searchDepartmentsByName("Operations");
		//don't need to do own sql, don't have to do insert statement directly because what the insert would have done is already done in the create department
		Department addDepartment = new Department ();
		addDepartment.setName ("Operations");
		dao.createDepartment(addDepartment);
		List<Department> newDepartments = dao.searchDepartmentsByName("Operations");
		Assert.assertEquals(oldDepartments.size() + 1, newDepartments.size());
	}	
	@Test
	//create department object
	public void save_department() {
		Department addedDepartment = new Department();
		addedDepartment.setName("Sales");
		dao.createDepartment(addedDepartment);//sales is now in database
		List<Department> oldDepartments = dao.searchDepartmentsByName("Sales");
		
		addedDepartment.setName("Marketing");
		dao.saveDepartment(addedDepartment);
		List<Department> newDepartments = dao.searchDepartmentsByName("Sales");
		Assert.assertEquals(oldDepartments.size(), newDepartments.size() + 1);	
	}
	
	@Test
	public void get_department_by_id() {
		Department addedDepartment = new Department();
		addedDepartment.setName("Sales");
		dao.createDepartment(addedDepartment);//sales is now in database, when created, it sets the id for us
		
		Department returnedDepartment = dao.getDepartmentById(addedDepartment.getId());
		
		Assert.assertEquals(addedDepartment.getId(), returnedDepartment.getId());
		Assert.assertEquals(addedDepartment.getName(), returnedDepartment.getName());
	}

}
