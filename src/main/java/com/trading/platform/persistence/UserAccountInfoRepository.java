package com.trading.platform.persistence;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.trading.platform.persistence.entity.UserAccount;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

@Repository
public class UserAccountInfoRepository implements UserAccountReadOnlyRepository {

	private static final String CLIENT_ID_COLUMN = "clientId";

	private static final String IS_MASTER_COLUMN = "isMaster";

	private static final String IS_TRADE_ALLOWED_COLUMN = "isTradeAllowed";

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public UserAccount findByClientId(String clientId) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<UserAccount> limitQuery = cb.createQuery(UserAccount.class);
		Root<UserAccount> from = limitQuery.from(UserAccount.class);
		limitQuery.select(from);
		limitQuery.where(cb.equal(from.get(CLIENT_ID_COLUMN), clientId));

		return entityManager.createQuery(limitQuery).setMaxResults(1).getSingleResult();
	}

	@Override
	public UserAccount getMasterAccount() {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<UserAccount> limitQuery = cb.createQuery(UserAccount.class);
		Root<UserAccount> from = limitQuery.from(UserAccount.class);
		limitQuery.select(from);
		limitQuery.where(cb.equal(from.get(IS_MASTER_COLUMN), true));

		return entityManager.createQuery(limitQuery).setMaxResults(1).getSingleResult();
	}

	@Override
	public List<UserAccount> getAllUserAccount() {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<UserAccount> limitQuery = cb.createQuery(UserAccount.class);
		Root<UserAccount> from = limitQuery.from(UserAccount.class);
		limitQuery.select(from);

		return entityManager.createQuery(limitQuery).getResultList();
	}

	@Override
	public List<UserAccount> getAllTradableUserAccount() {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<UserAccount> limitQuery = cb.createQuery(UserAccount.class);
		Root<UserAccount> from = limitQuery.from(UserAccount.class);
		limitQuery.select(from);
		limitQuery.where(cb.equal(from.get(IS_TRADE_ALLOWED_COLUMN), true));

		return entityManager.createQuery(limitQuery).getResultList();
	}

}
