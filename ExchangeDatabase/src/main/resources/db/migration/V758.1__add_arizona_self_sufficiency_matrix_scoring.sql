DECLARE @assessmentId BIGINT
SELECT @assessmentId = ID FROM Assessment WHERE code = 'ARIZONA_SSM';

delete from AssessmentScoringValue where assessment_id = @assessmentId

insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Program name', 'Achieving Change Together (ACT)', 0);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Program name', 'Enhanced Case Management and Community Services', 0);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Program name', 'Housing with Dignity', 0);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Program name', 'Saybrook', 0);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Program name', 'Step Up Sacramento (SUS)-Adults', 05);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Program name', 'Stop Plus', 0);

insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Assessment type', 'Initial', 0);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Assessment type', 'Interim', 0);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Assessment type', 'Exit', 0);

insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Survey frequency', 'At intake', 0);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Survey frequency', 'Follow Up - 3 months', 0);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Survey frequency', 'Follow Up - 6 months', 0);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Survey frequency', 'Follow Up - 9 months', 0);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Survey frequency', 'Follow Up - 12 months', 0);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Survey frequency', 'At exit', 0);

insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Income', 'No income', 1);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Income', 'Inadequate income and/or spontaneous or inappropriate spending', 2);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Income', 'Can meet basic needs with subsidy; appropriate spending', 3);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Income', 'Can meet basic needs and manage debt without assistance', 4);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Income', 'Income is sufficient, well managed; has discretionary income and is able to save', 5);

insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Credit Status', 'No Credit History', 1);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Credit Status', 'Needs Improvement (300-579)', 2);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Credit Status', 'Fair (580-669)', 3);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Credit Status', 'Good (670-739)', 4);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Credit Status', 'Excellent (740-850)', 5);

insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Employment', 'No job', 1);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Employment', 'Temporary, part-time or seasonal; inadequate pay, no benefits', 2);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Employment', 'Employed full time; inadequate pay; few or no benefits', 3);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Employment', 'Employed full time with adequate pay and benefits', 4);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Employment', 'Maintains permanent employment with adequate income and benefits', 5);

insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Shelter', 'Homeless or threatened with eviction', 1);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Shelter', 'In transitional, temporary or substandard housing; and/or current rent/mortgage payment is unaffordable (over 30% of income)', 2);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Shelter', 'In stable housing that is safe but only marginally adequate', 3);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Shelter', 'Household is in safe, adequate subsidized housing', 4);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Shelter', 'Household is safe, adequate, unsubsidized housing', 5);

insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Food', 'No food or means to prepare it. Relies to a significant degree on other sources of free or low-cost food', 1);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Food', 'Household is on food stamps', 2);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Food', 'Can meet basic food needs, but requires occasional assistance', 3);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Food', 'Can meet basic food needs without assistance', 4);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Food', 'Can choose to purchase any food household desires', 5);

insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Child Care', 'N/A', 0);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Child Care', 'Needs childcare, but none is available/accessible and/or child is not eligible', 1);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Child Care', 'Childcare is unreliable or unaffordable, inadequate supervision is a problem for childcare that is available', 2);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Child Care', 'Affordable subsidized childcare is available, but limited', 3);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Child Care', 'Reliable, affordable childcare is available, no need for subsidies', 4);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Child Care', 'Able to select quality childcare of choice', 5);

insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'What kind of childcare have you utilized in the past year?', 'Child Action', 0);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'What kind of childcare have you utilized in the past year?', 'Cal Works/ TANF', 0);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'What kind of childcare have you utilized in the past year?', 'Family', 0);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'What kind of childcare have you utilized in the past year?', 'Friends', 0);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'What kind of childcare have you utilized in the past year?', 'None', 0);

insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Children''s Education', 'N/A', 0);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Children''s Education', 'One or more school-aged children not enrolled in school', 0);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Children''s Education', 'One or more school-aged children enrolled in school, but not attending classes', 0);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Children''s Education', 'Enrolled in school, but one or more children only occasionally attending classes', 0);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Children''s Education', 'Enrolled in school and attending classes most of the time', 0);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Children''s Education', 'All school-aged children enrolled and attending on a regular basis', 0);

insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Adult Education', 'Literacy problems and/or no high school diploma/GED are serious barriers to employment', 1);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Adult Education', 'Enrolled in literacy and/or GED program and/or has sufficient command of English to where language is not a barrier to employment', 2);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Adult Education', 'Has high school diploma/GED', 3);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Adult Education', 'Needs additional education/training to improve employment situation and/or to resolve literacy problems to where they are able to function effectively in society', 4);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Adult Education', 'Has completed education/training needed to become employable. No literacy problems', 5);

insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Highest Grade', '8th Grade or Less', 0);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Highest Grade', '9th', 0);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Highest Grade', '10th', 0);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Highest Grade', '11th', 0);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Highest Grade', '12th', 0);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Highest Grade', 'High School Diploma/ GED', 0);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Highest Grade', 'Certificate', 0);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Highest Grade', 'Associate''s', 0);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Highest Grade', 'Bachelor''s', 0);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Highest Grade', 'Advanced Degree', 0);

insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Legal', 'Current outstanding tickets or warrants', 1);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Legal', 'Current charges/trial pending, noncompliance with probation/parole', 2);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Legal', 'Fully compliant with probation/parole terms', 3);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Legal', 'Has successfully completed probation/parole within past 12 months, no new charges filed', 4);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Legal', 'No active criminal justice involvement in more that 12 months and/or no felony criminal history', 5);

insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Ever convicted of the following', 'Felony', 0);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Ever convicted of the following', 'Violent Crime', 0);

insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Ever convicted/charged of the following', 'Domestic Violence', 0);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Ever convicted/charged of the following', 'Harassment', 0);

insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, '290 Registrant?', 'Yes', 0);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, '290 Registrant?', 'No', 0);

insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Health Care Coverage', 'No medical coverage with immediate need', 1);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Health Care Coverage', 'No medical coverage and great difficulty accessing medical care when needed. Some household members may be in poor health', 2);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Health Care Coverage', 'Some members (e.g. Children) have medical coverage', 3);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Health Care Coverage', 'All members can get medical care when needed, but may strain budget', 4);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Health Care Coverage', 'All members are covered by affordable, adequate health insurance ', 5);

insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Life Skills', 'Unable to meet basic needs such as hygiene, food, activities of daily living', 1);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Life Skills', 'Can meet a few but not all needs of daily living without assistance', 2);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Life Skills', 'Can meet most but not all daily living needs without assistance', 3);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Life Skills', 'Able to meet all basic needs of daily living without assistance', 4);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Life Skills', 'Able to provide beyond basic needs of daily living for self and family', 5);

insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Mental Health', 'Danger to self or others; recurring suicidal ideation; experiencing severe difficulty in day-to-day life due to psychological problems', 1);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Mental Health', 'Recurrent mental health symptoms that may affect behavior, but not a danger to self/others; persistent problems with functioning due to mental health symptoms', 2);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Mental Health', 'Mild symptoms may be present but are transient; only moderate difficulty in functioning due to mental health symptoms', 3);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Mental Health', 'Minimal symptoms that are expectable responses to life stressors; only slight impairment in functioning', 4);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Mental Health', 'Symptoms are absent or rare; good or superior functioning in wide range of activities; no more that every day problems or concerns', 5);

insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Substance Abuse', 'Meets criteria for severe abuse/dependence; resulting problems so severe that institutional living or hospitalization may be necessary', 1);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Substance Abuse', 'Meets criteria for dependence; preoccupation with use and/or obtaining drugs/alcohol; withdrawal or withdrawal avoidance behaviors evident; use results in avoidance or neglect of essential life activities', 2);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Substance Abuse', 'Use within last 6 months; evidence of persistent or recurrent social, occupational, emotional or physical problems related to use (such as disruptive behavior or housing problems); problems have persisted for at least one month', 3);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Substance Abuse', 'Client has used during last 6 months, but no evidence of persistent or recurrent social, occupational, emotional, or physical problems related to use; no evidence of recurrent dangerous use', 4);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Substance Abuse', 'No drug use/alcohol abuse in last 6 months', 5);

insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Family/Social Relations', 'Lack of necessary support form family or friends; abuse (DV, child) is present or there is child neglect', 1);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Family/Social Relations', 'Family/friends may be supportive, but lack ability or resources to help; family members do not relate well with one another; potential for abuse or neglect', 2);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Family/Social Relations', 'Some support from family/friends; family members acknowledge and seek to change negative behaviors; are learning to communicate and support', 3);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Family/Social Relations', 'Strong support from family or friends. Household members support each other''s efforts', 4);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Family/Social Relations', 'Has healthy/expanding support network; household is stable and communication is consistently open', 5);

insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Transportation', 'No access to transportation, public or private; may have car that is inoperable', 1);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Transportation', 'Transportation is available, but unreliable, unpredictable, unaffordable; may have care but no insurance, license, etc.', 2);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Transportation', 'Transportation is available and reliable, but limited and/or inconvenient; drivers are licensed and minimally insured', 3);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Transportation', 'Transportation is generally accessible to meet basic travel needs', 4);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Transportation', 'Transportation is readily available and affordable; car is adequately insured', 5);

insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Community Involvement', 'Not applicable due to crisis situation; in “survival” mode', 1);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Community Involvement', 'Socially isolated and/or no social skills and/or lacks motivation to become involved', 2);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Community Involvement', 'Lacks knowledge of ways to become involved', 3);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Community Involvement', 'Some community involvement (advisory group, support group), but has barriers such as transportation, childcare issues', 4);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Community Involvement', 'Actively involved in community', 5);

insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Safety', 'Home or residence is not safe; immediate level of lethality is extremely high; possible CPS involvement', 1);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Safety', 'Safety is threatened/temporary protection is available; level of lethality is high', 2);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Safety', 'Current level of safety is minimally adequate; ongoing safety planning is essential', 3);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Safety', 'Environment is safe, however, future of such is uncertain; safety planning is important', 4);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Safety', 'Environment is apparently safe and stable', 5);

insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Gang Affiliation', 'Yes', 0);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Gang Affiliation', 'No', 0);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Gang Affiliation', 'Data Not Collected', 0);

insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Parenting Skills', 'N/A', 0);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Parenting Skills', 'There are safety concerns regarding parenting skills', 0);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Parenting Skills', 'Parenting skills are minimal', 0);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Parenting Skills', 'Parenting skills are apparent but not adequate', 0);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Parenting Skills', 'Parenting skills are adequate', 0);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Parenting Skills', 'Parenting skills are well developed', 0);

insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Active CPS Case?', 'Yes', 0);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Active CPS Case?', 'No', 0);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Active CPS Case?', 'Data Not Collected', 0);

insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Previous CPS Involvement?', 'Yes', 0);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Previous CPS Involvement?', 'No', 0);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Previous CPS Involvement?', 'Data Not Collected', 0);

insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Disabilities', 'In crisis – acute or chronic symptoms affecting housing, employment, social interactions, etc.', 1);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Disabilities', 'Vulnerable – sometimes or periodically has acute or chronic symptoms affecting housing, employment, social interactions, etc.', 2);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Disabilities', 'Safe – rarely has acute or chronic symptoms affecting housing, employment, social interactions, etc.', 3);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Disabilities', 'Building Capacity – asymptomatic – condition controlled by services or medication', 4);
insert into AssessmentScoringValue(assessment_id, question_name, answer_name, value) values (@assessmentId, 'Disabilities', 'Thriving – no identified disability', 5);

go
