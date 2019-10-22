package com.techelevator.projects.view;


import java.sql.SQLException;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.techelevator.projects.model.Department;
import com.techelevator.projects.model.Employee;
import com.techelevator.projects.model.jdbc.JDBCDepartmentDAO;
import com.techelevator.projects.model.jdbc.JDBCEmployeeDAO;

public class JDBCDepartmentDAOIntegrationTest {
	private static SingleConnectionDataSource projects;
	private JDBCDepartmentDAO dao;

	@BeforeClass
	public static void setupDataSource() {
		projects = new SingleConnectionDataSource();
		projects.setUrl("jdbc:postgresql://localhost:5432/projects");
		projects.setUsername("postgres");
		projects.setPassword("postgres1");

		projects.setAutoCommit(false);

	}

	@AfterClass
	public static void closeDataSource() {
		projects.destroy();

	}

	@After
	public void walmart() throws SQLException {
		projects.getConnection().rollback();
	}

	@Before
	public void makeDao() {
		dao = new JDBCDepartmentDAO(projects);// object that allows us to access database (like our DBVisualizer)
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
	addedDepartment.setName("Sales");//sales is now in database
	dao.createDepartment(addedDepartment);
	List<Department> oldDepartments = dao.searchDepartmentsByName("Sales");
	
	addedDepartment.setName("Marketing");
	dao.saveDepartment(addedDepartment);
	List<Department> newDepartments = dao.searchDepartmentsByName("Sales");
	Assert.assertEquals(oldDepartments.size(), newDepartments.size() + 1);	
}

@Test
public void get_department_by_id() {
	Department addedDepartment = new Department();
	addedDepartment.setName("Sales");//sales is now in database
	dao.createDepartment(addedDepartment);//when created, it sets the id for us
	
	Department returnedDepartment = dao.getDepartmentById(addedDepartment.getId());
	Assert.assertEquals(addedDepartment.getId(), returnedDepartment.getId());
	Assert.assertEquals(addedDepartment.getName(), returnedDepartment.getName());
}

}
