update ResidentAssessmentResult
set json_result =
    replace(
        json_result,
        '"Have you had a chance to see or talk with family or friends since my last visit":',
        '"Have you had a chance to see or talk with family or friends since my last visit?":'
    )

update ResidentAssessmentResult
set json_result =
    replace(
        json_result,
        '"In the last week have you often felt sad or depressed.":',
        '"In the last week have you often felt sad or depressed?":'
    )

update ResidentAssessmentResult
set json_result =
    replace(
        json_result,
        '"Do you have enough testing supplies for the next 7 days?":',
        '"Do you have enough diabetic testing supplies for the next 7 days?":'
    )
