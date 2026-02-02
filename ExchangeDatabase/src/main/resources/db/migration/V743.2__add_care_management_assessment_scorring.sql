DECLARE @assessmentId BIGINT
SELECT @assessmentId = ID FROM Assessment WHERE code = 'CARE_MGMT';

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'Have you had a chance to see or talk with family or friends since my last visit?', 'Yes', 0);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'Have you had a chance to see or talk with family or friends since my last visit?', 'No', 0);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'Did you have enough to eat yesterday?', 'Yes', 0);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'Did you have enough to eat yesterday?', 'No', 0);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'How have you slept for the last couple of nights?', 'Excellent', 0);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'How have you slept for the last couple of nights?', 'Very Good', 0);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'How have you slept for the last couple of nights?', 'Good', 0);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'How have you slept for the last couple of nights?', 'Fair', 0);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'How have you slept for the last couple of nights?', 'Poor', 0);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'How would you rate your overall health today?', 'Excellent', 0);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'How would you rate your overall health today?', 'Very Good', 0);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'How would you rate your overall health today?', 'Good', 0);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'How would you rate your overall health today?', 'Fair', 0);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'How would you rate your overall health today?', 'Poor', 0);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'How would you rate your pain today?', '0: No pain', 0);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'How would you rate your pain today?', '0: 1-3: Mild pain', 0);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'How would you rate your pain today?', '4-6: Moderate pain', 0);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'How would you rate your pain today?', '7-9: Severe pain', 0);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'How would you rate your pain today?', '10: Worst pain you can imagine', 0);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'Do you have any new aches or pains since my last visit?', 'Yes', 1);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'Do you have any new aches or pains since my last visit?', 'No', 0);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'Have you tripped or fallen since my last visit?', 'Yes', 1);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'Have you tripped or fallen since my last visit?', 'No', 0);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'In the last week have you often felt sad or depressed.', 'Yes', 0);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'In the last week have you often felt sad or depressed.', 'No', 0);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'Are you drinking water regularly?', 'Yes', 0);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'Are you drinking water regularly?', 'No', 0);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'How would you rate your ability to take a deep breath right now? ', 'Excellent', 0);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'How would you rate your ability to take a deep breath right now? ', 'Very Good', 0);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'How would you rate your ability to take a deep breath right now? ', 'Good', 0);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'How would you rate your ability to take a deep breath right now? ', 'Fair', 1);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'How would you rate your ability to take a deep breath right now? ', 'Poor', 1);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'Have you done some exercise or physical activity since my last visit?', 'Yes', 0);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'Have you done some exercise or physical activity since my last visit?', 'No', 0);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'Have you seen any changes in your ankles since my last visit?', 'Yes', 0);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'Have you seen any changes in your ankles since my last visit?', 'No', 0);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'Has the swelling in your ankles increased or decreased since my last visit?', 'Increased', 0);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'Has the swelling in your ankles increased or decreased since my last visit?', 'Decreased', 0);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'Is your blood sugar being tested regularly?', 'Yes', 0);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'Is your blood sugar being tested regularly?', 'No', 1);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'Is your blood sugar being tested regularly?', 'N/A', 0);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'Do you have enough testing supplies for the next 7 days?', 'Yes', 0);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'Do you have enough testing supplies for the next 7 days?', 'No', 1);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'Do you have enough testing supplies for the next 7 days?', 'N/A', 0);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'Do you have enough of your medication for the next 7 days?', 'Yes', 0);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'Do you have enough of your medication for the next 7 days?', 'No', 1);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'Do you have enough of your medication for the next 7 days?', 'N/A', 0);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'May I help you contact the pharmacy or doctor for refills? ', 'Yes', 0);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'May I help you contact the pharmacy or doctor for refills? ', 'No', 0);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'Do you have a doctor''s appointment coming up or can I help you schedule one today?', 'Yes', 0);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'Do you have a doctor''s appointment coming up or can I help you schedule one today?', 'No', 0);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'Do you need transportation to your doctor''s appointment?', 'Yes', 1);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'Do you need transportation to your doctor''s appointment?', 'No', 0);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'Is there anything we can help you with for clothing or personal care items?', 'Yes', 1);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'Is there anything we can help you with for clothing or personal care items?', 'No', 0);

INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'Do you want your care coordinator to reach out to you?', 'Yes', 1);
INSERT INTO AssessmentScoringValue(assessment_id, question_name, answer_name, value) VALUES (@assessmentId, 'Do you want your care coordinator to reach out to you?', 'No', 0);

INSERT INTO AssessmentScoringGroup(assessment_id, score_low, score_high, severity, severity_short, highlighting, comments, passed_high_school, is_risk_identified) VALUES (@assessmentId, 0, 0,NULL,NULL, NULL, NULL, NULL, 0);
INSERT INTO AssessmentScoringGroup(assessment_id, score_low, score_high, severity, severity_short, highlighting, comments, passed_high_school, is_risk_identified) VALUES (@assessmentId, 1, 9,NULL,NULL, NULL, NULL, NULL, 1);

GO

