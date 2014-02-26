package com.ledoyen.sql.querybuilder;

import com.ledoyen.sql.querybuilder.clause.DefaultWhereClause;
import com.ledoyen.sql.querybuilder.clause.DefaultWhereClause.FormattedDefaultWhereClauseBinder;
import com.ledoyen.sql.querybuilder.clause.DoubleBindedWhereClause.DoubleBindedWhereClauseBuilder;
import com.ledoyen.sql.querybuilder.clause.OperatorSimpleBindedWhereClauseBuilder;
import com.ledoyen.sql.querybuilder.clause.OperatorSimpleBindedWhereClauseBuilder.Operator;
import com.ledoyen.sql.querybuilder.clause.SimpleBindedWhereClause.CollectionSimpleBindedWhereClauseBuilder;
import com.ledoyen.sql.querybuilder.clause.SimpleBindedWhereClause.SimpleBindedWhereClauseBuilder;

public interface UserClauses {

	String INITIAL_SELECT = new StringBuilder("select count(pv.user_id), pv.var_name  from (((profile_value pv ")
									.append("join users u) join civility c) join business_unit bu) ").toString();

	DefaultWhereClause JOIN_USER_CIVILITY = new DefaultWhereClause("c.id = u.civility_id");
    DefaultWhereClause JOIN_USER_PROFILE_VALUE = new DefaultWhereClause("pv.user_id = u.id");
    DefaultWhereClause JOIN_USER_BUSINESS_UNIT = new DefaultWhereClause("u.business_id = bu.id");
    FormattedDefaultWhereClauseBinder PROVILE_VALUE_HISTO = new FormattedDefaultWhereClauseBinder("pv.is_histo is %s");
    DoubleBindedWhereClauseBuilder USER_AGE = new DoubleBindedWhereClauseBuilder(
	    "timestampdiff(YEAR, u.birth_date, sysdate()) between :ageMin and :ageMax", "ageMin", "ageMax");
    OperatorSimpleBindedWhereClauseBuilder PROFILE_VALUE_SCORE_MIN = new OperatorSimpleBindedWhereClauseBuilder("pv.double_value %s :scoreMin",
	    "scoreMin", Operator.MORE);
    OperatorSimpleBindedWhereClauseBuilder PROFILE_VALUE_SCORE_MAX = new OperatorSimpleBindedWhereClauseBuilder("pv.double_value %s :scoreMax",
	    "scoreMax", Operator.LESS);
    DefaultWhereClause CIVILITY_MR = new DefaultWhereClause("c.name = 'M.'");
    DefaultWhereClause CIVILITY_NOT_MR = new DefaultWhereClause("c.name != 'M.'");
    CollectionSimpleBindedWhereClauseBuilder PROFILE_VALUE_VARIABLE_NAMES = new CollectionSimpleBindedWhereClauseBuilder(
	    "pv.var_name in (:variableNames) ", "variableNames");
    DoubleBindedWhereClauseBuilder CREATION_DATE = new DoubleBindedWhereClauseBuilder("u.creation_date between :startDate and :endDate", "startDate",
	    "endDate");
    SimpleBindedWhereClauseBuilder BUSINESS_UNIT_CODE = new SimpleBindedWhereClauseBuilder("bu.code = :codeCaisse", "codeCaisse");
    SimpleBindedWhereClauseBuilder BUSINESS_UNIT_CATEGORY = new SimpleBindedWhereClauseBuilder("bu.category = :codeCatCaisse", "codeCatCaisse");
}
