package com.techelevator.projects.model.jdbc;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.techelevator.projects.model.Department;
import com.techelevator.projects.model.Employee;
import com.techelevator.projects.model.EmployeeDAO;
import com.techelevator.projects.model.Project;

public class JDBCEmployeeDAO implements EmployeeDAO {

	private JdbcTemplate jdbcTemplate;

	public JDBCEmployeeDAO(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public List<Employee> getAllEmployees() {
		ArrayList<Employee> employees = new ArrayList<>();
		String sqlAllEmployees = "SELECT employee_id, department_id, first_name,last_name,birth_date,gender,hire_date FROM employee";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlAllEmployees);
		while (results.next()) {
			Employee employee = mapRowToEmployee(results);
			employees.add(employee);

		}
		return employees;

	}

	@Override
	public List<Employee> searchEmployeesByName(String firstNameSearch, String lastNameSearch) {
		ArrayList<Employee> employees = new ArrayList<> ();
		String sqlAllEmployees = "SELECT department_id, first_name,last_name,birth_date,gender,hire_date FROM employee WHERE first_name ILIKE '%'|| ? ||'%' AND last_name ILIKE '%'|| ? ||'%'";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlAllEmployees, firstNameSearch, lastNameSearch);
		while(results.next()) {
			Employee employee = mapRowToEmployee(results);
			employees.add(employee);
		}
		
		return employees;
		
		
		
	}

	@Override
	public List<Employee> getEmployeesByDepartmentId(long id) {
		ArrayList<Employee> employees = new ArrayList<> ();
		String sqlAllEmployee = "SELECT employee_id,department_id, first_name,last_name,birth_date,gender,hire_date FROM department WHERE department_id = ?";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlAllEmployee, id);
		while(results.next()) {
			Employee employee = mapRowToEmployee(results);
			employees.add(employee);
		}
		
		return employees;
		
	}

	@Override
	public List<Employee> getEmployeesWithoutProjects() {
		return new ArrayList<>();
	}

	@Override
	public List<Employee> getEmployeesByProjectId(Long projectId) {
		return new ArrayList<>();
	}

	@Override
	public void changeEmployeeDepartment(Long employeeId, Long departmentId) {

	}
	
	private Employee mapRowToEmployee(SqlRowSet results) {
		Employee employees = new Employee();//making a pojo
		employees.setId(results.getLong("employee_id"));
		employees.setDepartmentId(results.getLong("department_id"));
		employees.setFirstName(results.getString("first_name"));
		employees.setLastName(results.getString("last_name"));
		employees.setBirthDay(results.getDate ("birth_date").toLocalDate());
		employees.setGender(results.getString ("gender").charAt(0));
		employees.setHireDate(results.getDate("hire_date").toLocalDate());
		
		return employees;
	}
}
