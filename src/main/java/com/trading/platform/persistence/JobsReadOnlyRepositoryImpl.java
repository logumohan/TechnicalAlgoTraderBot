package com.trading.platform.persistence;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.trading.platform.persistence.entity.Job;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

@Repository
public class JobsReadOnlyRepositoryImpl implements JobsReadOnlyRepository {

	private static final String NAME_COLUMN = "name";

	private static final String USER_NAME_COLUMN = "userName";

	private static final String JOB_TYPE_COLUMN = "jobType";

	private static final String STRATEGY_COLUMN = "strategy";

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public Job findById(String name, String userName) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Job> limitQuery = cb.createQuery(Job.class);
		Root<Job> from = limitQuery.from(Job.class);
		limitQuery.where(cb.equal(from.get(NAME_COLUMN), name), cb.equal(from.get(USER_NAME_COLUMN), userName));
		limitQuery.select(from);

		return entityManager.createQuery(limitQuery).getSingleResult();
	}

	@Override
	public List<Job> findByName(String name) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Job> limitQuery = cb.createQuery(Job.class);
		Root<Job> from = limitQuery.from(Job.class);
		limitQuery.where(cb.equal(from.get(NAME_COLUMN), name));
		limitQuery.select(from);

		return entityManager.createQuery(limitQuery).getResultList();
	}

	@Override
	public List<Job> findJobs() {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Job> limitQuery = cb.createQuery(Job.class);
		Root<Job> from = limitQuery.from(Job.class);
		limitQuery.select(from);

		return entityManager.createQuery(limitQuery).getResultList();
	}

	@Override
	public List<Job> findJobsByType(String jobType) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Job> limitQuery = cb.createQuery(Job.class);
		Root<Job> from = limitQuery.from(Job.class);
		limitQuery.where(cb.equal(from.get(JOB_TYPE_COLUMN), jobType));
		limitQuery.select(from);

		return entityManager.createQuery(limitQuery).getResultList();
	}

	@Override
	public List<Job> findJobsByStrategy(String strategy) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Job> limitQuery = cb.createQuery(Job.class);
		Root<Job> from = limitQuery.from(Job.class);
		limitQuery.where(cb.equal(from.get(STRATEGY_COLUMN), strategy));
		limitQuery.select(from);

		return entityManager.createQuery(limitQuery).getResultList();
	}

	@Override
	public List<Job> findJobsBy(String jobType, String strategy) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Job> limitQuery = cb.createQuery(Job.class);
		Root<Job> from = limitQuery.from(Job.class);
		limitQuery.where(cb.equal(from.get(JOB_TYPE_COLUMN), jobType), cb.equal(from.get(STRATEGY_COLUMN), strategy));
		limitQuery.select(from);

		return entityManager.createQuery(limitQuery).getResultList();
	}

}
