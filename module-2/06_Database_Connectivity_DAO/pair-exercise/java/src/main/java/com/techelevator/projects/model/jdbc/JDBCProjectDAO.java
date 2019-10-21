package com.techelevator.projects.model.jdbc;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.techelevator.projects.model.Project;
import com.techelevator.projects.model.ProjectDAO;

public class JDBCProjectDAO implements ProjectDAO {

	private JdbcTemplate jdbcTemplate;

	public JDBCProjectDAO(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Override
	public List<Project> getAllActiveProjects() {
		ArrayList<Project> projects = new ArrayList<> ();
		String sqlAllProjects = "SELECT project_id, name,from_date, to_date" +
								"FROM project";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlAllProjects);
		while(results.next()) {
			Project project = mapRowToProject(results);
			projects.add (project);
			
			
		}
		return projects;
	}

	@Override
	public void removeEmployeeFromProject(Long projectId, Long employeeId) {
		String sqlDelete = "DELETE from project_employee WHERE project_id = ? AND employee_id = ?";
		jdbcTemplate.update(sqlDelete, projectId, employeeId);
	}

	@Override
	public void addEmployeeToProject(Long projectId, Long employeeId) {
		String sqlInsert = "INSERT INTO project_employee (proeject_id, employee_id) VALUES (?,?)";
		jdbcTemplate.update(sqlInsert, projectId, employeeId);
	}
	
	private Project mapRowToProject(SqlRowSet results) {
		Project projects = new Project();//making a pojo
		projects.setId(results.getLong("project_id"));
		projects.setName(results.getString("name"));
		projects.setStartDate(results.getDate("from_date").toLocalDate());
		projects.setEndDate(results.getDate("to_date").toLocalDate());
		
		return projects;
	}

}
