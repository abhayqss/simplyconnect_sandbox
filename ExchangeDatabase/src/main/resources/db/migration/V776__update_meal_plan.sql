update DocumentSignatureTemplateField
set related_field_id = null
where name = 'threeMealsPerDayPlusSnack'
   or name = 'wouldLikeToParticipateInMealPlan'
   or name = 'doNotWantToParticipateInMealPlan'
go
