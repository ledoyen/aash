# SQL-Query-Builder

SQL-Query-Builder is a tool to help you compose native sql queries and make DAO code more readable.

Assuming the definition of a library of clauses, your DAO code will look like :

```java
QueryBuilder nqb = QueryBuilder
	.select(INITIAL_SELECT)
	.where(JOIN_USER_CIVILITY,
			JOIN_USER_SCORE,
			JOIN_USER_REGION,
			SCORE_HISTO.with(false),
			USER_AGE.with(ageMin, ageMax),
			SCORE_MIN.with(scoreMin, scoreMinInclusive),
			SCORE_MAX.with(scoreMax, scoreMaxInclusive),
			conditional(civility != null, "M.".equals(civility) ? CIVILITY_MR : CIVILITY_NOT_MR),
			SCORE_NAMES.with(names),
			CREATION_DATE.betweenDates(startDate, endDate),
			or(REGION_CODE.with(code),
			REGION_CATEGORY.withString(category)))
	.groupOrOrder("group by s.name");

List<?> results = nqb.nativeQuery(getEntityManager()).getResultList();
```

## JPA
SQL-Query-Builder supports JPA as you can see above by building a **javax.persistence.Query**, given an **javax.persistence.EntityManager** :
```java
QueryBuilder nqb = QueryBuilder
	.select(INITIAL_SELECT)
...
Query query = nqb.nativeQuery(getEntityManager());
List<?> results = query.getResultList();
```

## JDBC
SQL-Query-Builder supports also JDBC by building a **java.sql.PreparedStatement**, given a **java.sql.Connection** :
```java
PreparedStatementBuilder psb = PreparedStatementBuilder
	.select(INITIAL_SELECT)
...
PreparedStatement preparedStatement = psb.preparedStatement(getConnection());
ResultSet results = preparedStatement.executeQuery();
```
*As __PreparedStatement__ does not support named parameters and __CallableStatement__ is not supported by all SGBD, a conversion is made from names to positions.* 

## Clauses
Each clause type or builder is designed to fit most needs, and can be extended for exotic specifications.
Depending on the type of clause, you will benefit from implicit parameter check.

### From the simpler :
```java
if (code != null) {
	request.append(" and r.code = :code ");
}
...
if (code != null) {
    query.setParameter("code", code);
}
```
is done by the equivalent clause :
```java
REGION_CODE.with(code)
```

### To complex assertions :
```java
if (startDate != null) {
	request.append(" and u.creation_date between :startDate and :endDate ");
}
...
if (startDate != null) {
    query.setParameter("startDate", Dates.floor(startDate));
    query.setParameter("endDate", endDate != null ? Dates.ceiling(endDate) : Dates.ceiling(startDate));
}
```
is done by the equivalent clause :
```java
CREATION_DATE.betweenDates(startDate, endDate)
```

### Via String length check
```java
if (category != null && category.length() > 0) {
	request.append(" and r.category = :category ");
}
...
if (category != null && category.length() > 0) {
    query.setParameter("category", category);
}
```
is done by the equivalent clause :
```java
REGION_CATEGORY.withString(category)
```

### And collections and maps handling
```java
if(names != null && !names.isEmpty()) {
	sql.append(" and s.name in (:names) ");
}
...
if(names != null && !names.isEmpty()) {
	query.setParameter("names", names);
}
```
is done by the equivalent clause :
```java
SCORE_NAMES.with(names)
```
