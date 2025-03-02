package com.sleektiv.mes.advancedGenealogy.criteriaModifier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import com.sleektiv.localization.api.TranslationService;
import com.sleektiv.mes.basic.constants.NumberPatternFields;
import com.sleektiv.model.api.search.SearchCriteriaBuilder;
import com.sleektiv.model.api.search.SearchRestrictions;

@Service
public class NumberPatternCriteriaModifiersAG {

	@Autowired
	private TranslationService translationService;

	public void restrictNumberPatternForUnused(final SearchCriteriaBuilder searchCriteriaBuilder) {
		searchCriteriaBuilder.add(SearchRestrictions.or(SearchRestrictions.isNull(NumberPatternFields.USED_IN),
				SearchRestrictions.eq(NumberPatternFields.USED_IN, translationService
						.translate("basic.parameter.numberPattern.usedIn.value", LocaleContextHolder.getLocale()))));
	}

	public void restrictNumberPatternForUnusedDelivery(final SearchCriteriaBuilder searchCriteriaBuilder) {
		searchCriteriaBuilder.add(SearchRestrictions.or(SearchRestrictions.isNull(NumberPatternFields.USED_IN),
				SearchRestrictions.eq(NumberPatternFields.USED_IN, translationService
						.translate("basic.parameter.numberPattern.usedInDeliveryProductBatch.value", LocaleContextHolder.getLocale()))));
	}
}
