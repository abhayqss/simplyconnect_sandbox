declare @assessment_id bigint;
select @assessment_id = id from Assessment where code = 'HOUSING_ASSESSMENT';

UPDATE [dbo].[Assessment]
SET [json_content] = '{
                    "logoPosition": "right",
                    "pages": [
                        {
                            "name": "page1",
                            "elements": [
                                {
                                    "type": "text",
                                    "name": "Program",
                                    "title": "Program",
                                    "hideNumber": true,
                                    "isRequired": true,
                                    "maxLength": 256
                                },
                                {
                                    "type": "text",
                                    "name": "Case_manager",
                                    "title": "Case manager",
                                    "hideNumber": true,
                                    "isRequired": true,
                                    "maxLength": 256
                                },
                                {
                                    "name": "Assessment_Type",
                                    "title": "Assessment Type",
                                    "type": "radiogroup",
                                    "renderAs": "prettycheckbox",
                                    "choices": [
                                        "Intake",
                                        "Quarterly",
                                        "Exit"
                                    ]
                                },
                                {
                                    "type": "html",
                                    "name": "Collapse/Expand All",
                                    "html": "<a class=\"sv-expand-all-sections-btn\">Expand All</a>\n<a class=\"sv-collapse-all-sections-btn\">Collapse All</a>"
                                },
                                {
                                    "type": "panel",
                                    "name": "Member Information",
                                    "assessmentSectionAnchor": "Member Information",
                                    "title": "Member Information",
                                    "isExpandable": true,
                                    "state": "expanded",
                                    "elements": [
                                        {
                                            "type": "text",
                                            "name": "First_name",
                                            "title": "First name",
                                            "hideNumber": true,
                                            "isRequired": true,
                                            "readOnly": true,
                                            "maxLength": 256
                                        },
                                        {
                                            "type": "text",
                                            "name": "Last_name",
                                            "title": "Last name",
                                            "hideNumber": true,
                                            "isRequired": true,
                                            "readOnly": true,
                                            "maxLength": 256
                                        },
                                        {
                                            "type": "radiogroup",
                                            "name": "Address_type",
                                            "title": "Address type",
                                            "hideNumber": true,
                                            "isRequired": true,
                                            "choices": [
                                                {
                                                    "value": "item1",
                                                    "text": "Home "
                                                },
                                                {
                                                    "value": "item2",
                                                    "text": "Emergency"
                                                },
                                                {
                                                    "value": "item3",
                                                    "text": "Mailing"
                                                },
                                                {
                                                    "value": "item4",
                                                    "text": "Parents"
                                                },
                                                {
                                                    "value": "item5",
                                                    "text": "Other "
                                                }
                                            ],
                                            "renderAs": "prettycheckbox"
                                        },
                                        {
                                            "type": "text",
                                            "name": "Other",
                                            "visibleIf": "{Address_type} = ''item5''",
                                            "isRequired": true,
                                            "title": "Other",
                                            "hideNumber": true,
                                            "maxLength": 256
                                        },
                                        {
                                            "type": "text",
                                            "name": "Street",
                                            "title": "Street",
                                            "hideNumber": true,
                                            "isRequired": true,
                                            "maxLength": 256
                                        },
                                        {
                                            "type": "text",
                                            "name": "City",
                                            "title": "City",
                                            "hideNumber": true,
                                            "isRequired": true,
                                            "maxLength": 256
                                        },
                                        {
                                            "type": "text",
                                            "name": "Zip",
                                            "title": "Zip",
                                            "hideNumber": true,
                                            "isRequired": true,
                                            "maxLength": 5
                                        },
                                        {
                                            "type": "text",
                                            "name": "Phone",
                                            "title": "Phone",
                                            "hideNumber": true,
                                            "isRequired": true,
                                            "maxLength": 16
                                        },
                                        {
                                            "type": "text",
                                            "name": "Email_Address",
                                            "title": "Email Address",
                                            "hideNumber": true,
                                            "isRequired": true,
                                            "maxLength": 256
                                        }
                                    ]
                                },
                                {
                                    "type": "panel",
                                    "name": "Housing Background",
                                    "assessmentSectionAnchor": "Housing Background",
                                    "isExpandable": true,
                                    "state": "expanded",
                                    "title": "Housing Background",
                                    "elements": [
                                        {
                                            "type": "text",
                                            "name": "Place_where_you_lived_that_worked_well",
                                            "title": "What was the last place where you lived that worked well? What made it work well?",
                                            "hideNumber": true,
                                            "maxLength": 512
                                        },
                                        {
                                            "type": "radiogroup",
                                            "name": "Have_you_ever_been_on_lease",
                                            "title": "Have you ever been on a lease before or recently applied for one?  ",
                                            "hideNumber": true,
                                            "isRequired": true,
                                            "choices": [
                                                {
                                                    "value": "item1",
                                                    "text": "Yes"
                                                },
                                                {
                                                    "value": "item2",
                                                    "text": "No"
                                                }
                                            ],
                                            "renderAs": "prettycheckbox"
                                        },
                                        {
                                            "type": "text",
                                            "name": "provide_appx_date_and_name_of_property",
                                            "visibleIf": "{Have_you_ever_been_on_lease} = ''item1''",
                                            "isRequired": true,
                                            "title": "If yes, please provide appx date and name of property. What was the result?",
                                            "hideNumber": true,
                                            "maxLength": 512
                                        },
                                        {
                                            "type": "radiogroup",
                                            "name": "have_you_lived_in_subsidized_housing_before",
                                            "title": "As an adult have you lived in subsidized housing before? (Section 8, voucher, etc)?",
                                            "hideNumber": true,
                                            "isRequired": true,
                                            "choices": [
                                                "Yes",
                                                "No"
                                            ],
                                            "renderAs": "prettycheckbox"
                                        },
                                        {
                                            "type": "comment",
                                            "name": "when_where_and_what_was_the_result",
                                            "visibleIf": "{have_you_lived_in_subsidized_housing_before} = ''Yes''",
                                            "isRequired": true,
                                            "title": "If yes, please provide when, where, and what was the result?",
                                            "hideNumber": true,
                                            "maxLength": 512
                                        },
                                        {
                                            "type": "radiogroup",
                                            "name": "have_Housing_Voucher",
                                            "title": "Do you currently have a Housing Voucher? ",
                                            "hideNumber": true,
                                            "isRequired": true,
                                            "choices": [
                                                {
                                                    "value": "item1",
                                                    "text": "Yes"
                                                },
                                                {
                                                    "value": "item2",
                                                    "text": "No"
                                                }
                                            ],
                                            "renderAs": "prettycheckbox"
                                        },
                                        {
                                            "type": "text",
                                            "name": "what_type_and_when",
                                            "visibleIf": "{have_Housing_Voucher} = ''item1''",
                                            "isRequired": true,
                                            "title": "If yes, what type and when is the expiration date? ",
                                            "hideNumber": true,
                                            "maxLength": 256
                                        },
                                        {
                                            "type": "radiogroup",
                                            "name": "do_have_any_evictions",
                                            "title": "Do you have any evictions in the last ten years?",
                                            "hideNumber": true,
                                            "isRequired": true,
                                            "choices": [
                                                {
                                                    "value": "item1",
                                                    "text": "Yes"
                                                },
                                                {
                                                    "value": "item2",
                                                    "text": "No"
                                                }
                                            ],
                                            "renderAs": "prettycheckbox"
                                        },
                                        {
                                            "type": "text",
                                            "name": "when_property_name_amount_owed",
                                            "visibleIf": "{do_have_any_evictions} = ''item1''",
                                            "isRequired": true,
                                            "title": "If yes, when, property name, and is there an amount owed to the landlord? ",
                                            "hideNumber": true,
                                            "maxLength": 256
                                        },
                                        {
                                            "type": "radiogroup",
                                            "name": "Do_have_any_concerns_about_moving",
                                            "title": "Do you have any concerns about moving into your own place?",
                                            "hideNumber": true,
                                            "isRequired": true,
                                            "choices": [
                                                {
                                                    "value": "item1",
                                                    "text": "Yes"
                                                },
                                                {
                                                    "value": "item2",
                                                    "text": "No"
                                                }
                                            ],
                                            "renderAs": "prettycheckbox"
                                        }
                                    ]
                                },
                                {
                                    "type": "panel",
                                    "name": "Housing Preferences",
                                    "assessmentSectionAnchor": "Housing Preferences",
                                    "isExpandable": true,
                                    "state": "expanded",
                                    "title": "Housing Preferences",
                                    "elements": [
                                        {
                                            "type": "radiogroup",
                                            "name": "Do_you_other_family_member_attend_school",
                                            "title": "Do you or other family member attend school and/or work?",
                                            "hideNumber": true,
                                            "isRequired": true,
                                            "choices": [
                                                {
                                                    "value": "item1",
                                                    "text": "Yes"
                                                },
                                                {
                                                    "value": "item2",
                                                    "text": "No"
                                                }
                                            ],
                                            "renderAs": "prettycheckbox"
                                        },
                                        {
                                            "type": "text",
                                            "name": "where_they_go_to_school_or_work",
                                            "visibleIf": "{Do_you_other_family_member_attend_school} = ''item1''",
                                            "isRequired": true,
                                            "title": "If yes, please list who and where they go to school or work",
                                            "hideNumber": true,
                                            "maxLength": 256
                                        },
                                        {
                                            "type": "radiogroup",
                                            "name": "neighborhood_you_would_like_to_avoid",
                                            "title": "Is there a neighborhood you would like to avoid due to domestic violence or recovery needs?",
                                            "hideNumber": true,
                                            "isRequired": true,
                                            "choices": [
                                                {
                                                    "value": "item1",
                                                    "text": "Yes"
                                                },
                                                {
                                                    "value": "item2",
                                                    "text": "No"
                                                }
                                            ],
                                            "renderAs": "prettycheckbox"
                                        },
                                        {
                                            "type": "text",
                                            "name": "please_list_and_why",
                                            "visibleIf": "{neighborhood_you_would_like_to_avoid} = ''item1''",
                                            "isRequired": true,
                                            "title": "If yes, please list and why",
                                            "hideNumber": true,
                                            "maxLength": 256
                                        },
                                        {
                                            "type": "radiogroup",
                                            "name": "Do_you_have_any_accessibility_needs",
                                            "title": "Do you have any accessibility needs that need to be considered?  Inability to walk, climb stairs or any other mobility considerations?",
                                            "hideNumber": true,
                                            "isRequired": true,
                                            "choices": [
                                                {
                                                    "value": "item1",
                                                    "text": "Yes"
                                                },
                                                {
                                                    "value": "item2",
                                                    "text": "No"
                                                }
                                            ],
                                            "renderAs": "prettycheckbox"
                                        },
                                        {
                                            "type": "text",
                                            "name": "accessibility_needs_clarify",
                                            "visibleIf": "{Do_you_have_any_accessibility_needs} = ''item1''",
                                            "isRequired": true,
                                            "title": "If yes, please clarify",
                                            "hideNumber": true,
                                            "maxLength": 256
                                        },
                                        {
                                            "type": "radiogroup",
                                            "name": "have_any_pets?",
                                            "title": "Do you have any pets? ",
                                            "hideNumber": true,
                                            "isRequired": true,
                                            "choices": [
                                                {
                                                    "value": "item1",
                                                    "text": "Yes"
                                                },
                                                {
                                                    "value": "item2",
                                                    "text": "No"
                                                }
                                            ],
                                            "renderAs": "prettycheckbox"
                                        },
                                        {
                                            "type": "text",
                                            "name": "pets_how_many",
                                            "visibleIf": "{have_any_pets?} = ''item1''",
                                            "isRequired": true,
                                            "title": "If so, how many, and what kind?",
                                            "hideNumber": true,
                                            "maxLength": 256
                                        },
                                        {
                                            "type": "matrix",
                                            "name": "question1",
                                            "visibleIf": "{have_any_pets?} = ''item1''",
                                            "isRequired": true,
                                            "title": "Do you have any of the following documentation for your pet?",
                                            "hideNumber": true,
                                            "columns": [
                                                "Yes",
                                                "No"
                                            ],
                                            "rows": [
                                                {
                                                    "value": "Service_Animal_Certification",
                                                    "text": "Service Animal Certification"
                                                },
                                                {
                                                    "value": "Companion_Animal_Certification",
                                                    "text": "Companion Animal Certification"
                                                },
                                                {
                                                    "value": "Neutered_records1",
                                                    "text": "Neutered records"
                                                },
                                                {
                                                    "value": "Shot_record",
                                                    "text": "Shot records"
                                                }
                                            ]
                                        }
                                    ]
                                },
                                {
                                    "type": "panel",
                                    "name": "Financial and legal background",
                                    "assessmentSectionAnchor": "Financial and legal background",
                                    "isExpandable": true,
                                    "state": "expanded",
                                    "title": "Financial and legal background",
                                    "elements": [
                                        {
                                            "type": "radiogroup",
                                            "name": "Do_you_currently_receive_income?",
                                            "title": "Do you currently receive income?",
                                            "hideNumber": true,
                                            "isRequired": true,
                                            "choices": [
                                                {
                                                    "value": "item1",
                                                    "text": "Yes"
                                                },
                                                {
                                                    "value": "item2",
                                                    "text": "No"
                                                }
                                            ],
                                            "renderAs": "prettycheckbox"
                                        },
                                        {
                                            "type": "multipletext",
                                            "name": "Include_Amounts_on_Each_Line_That_Apply",
                                            "visibleIf": "{Do_you_currently_receive_income?} = ''item1''",
                                            "title": "Include Amounts on Each Line That Apply",
                                            "hideNumber": true,
                                            "items": [
                                                {
                                                    "name": "Employment",
                                                    "title": "Employment, $",
                                                    "maxLength": 12
                                                },
                                                {
                                                    "name": "Unemployment",
                                                    "title": "Unemployment Ins, $",
                                                    "maxLength": 12
                                                },
                                                {
                                                    "name": "Workers_Comp",
                                                    "title": "Workers Comp, $",
                                                    "maxLength": 12
                                                },
                                                {
                                                    "name": "Private_Disability",
                                                    "title": "Private Disability, $",
                                                    "maxLength": 12
                                                },
                                                {
                                                    "name": "SSDI_(Disability)",
                                                    "title": "SSDI (Disability), $",
                                                    "maxLength": 12
                                                },
                                                {
                                                    "name": "SSI",
                                                    "title": "SSI, $",
                                                    "maxLength": 12
                                                },
                                                {
                                                    "name": "SSA(Retirement)",
                                                    "title": "SSA (Retirement), $",
                                                    "maxLength": 12
                                                },
                                                {
                                                    "name": "Pension_Former_Job",
                                                    "title": "Pension Former Job, $",
                                                    "maxLength": 12
                                                },
                                                {
                                                    "name": "VAPension(Non-Service)",
                                                    "title": "VA Pension (Non-Service), $",
                                                    "maxLength": 12
                                                },
                                                {
                                                    "name": "TANF/Cal_Works",
                                                    "title": "TANF/Cal Works, $",
                                                    "maxLength": 12
                                                },
                                                {
                                                    "name": "GA",
                                                    "title": "GA, $",
                                                    "maxLength": 12
                                                },
                                                {
                                                    "name": "Alimony/Spousal",
                                                    "title": "Alimony/Spousal, $",
                                                    "maxLength": 12
                                                },
                                                {
                                                    "name": "Child_Support",
                                                    "title": "Child Support, $",
                                                    "maxLength": 12
                                                },
                                                {
                                                    "name": "Other/Kids",
                                                    "title": "Other/Kids, $",
                                                    "maxLength": 12
                                                }
                                            ]
                                        },
                                        {
                                            "type": "radiogroup",
                                            "name": "Do_you_have_any_savings_for_moving?",
                                            "title": "Do you have any savings for moving? (ex. Deposit, first month?s rent, moving truck)",
                                            "hideNumber": true,
                                            "isRequired": true,
                                            "choices": [
                                                {
                                                    "value": "item1",
                                                    "text": "Yes"
                                                },
                                                {
                                                    "value": "item2",
                                                    "text": "No"
                                                }
                                            ],
                                            "renderAs": "prettycheckbox"
                                        },
                                        {
                                            "type": "text",
                                            "name": "What_is_needed_to_move?",
                                            "title": "What is needed to move? ",
                                            "hideNumber": true,
                                            "maxLength": 256
                                        },
                                        {
                                            "type": "radiogroup",
                                            "name": "Credit_Status",
                                            "title": "Credit Status",
                                            "hideNumber": true,
                                            "isRequired": true,
                                            "choices": [
                                                {
                                                    "value": "item1",
                                                    "text": "Good"
                                                },
                                                {
                                                    "value": "item2",
                                                    "text": "Fair"
                                                },
                                                {
                                                    "value": "item3",
                                                    "text": "Poor"
                                                },
                                                {
                                                    "value": "item4",
                                                    "text": "No Credit"
                                                }
                                            ],
                                            "renderAs": "prettycheckbox"
                                        },
                                        {
                                            "type": "radiogroup",
                                            "name": "Is_there_current_credit_report_in_LSS_file?",
                                            "title": "Is there a current credit report in LSS file?",
                                            "hideNumber": true,
                                            "isRequired": true,
                                            "choices": [
                                                {
                                                    "value": "item1",
                                                    "text": "Yes"
                                                },
                                                {
                                                    "value": "item2",
                                                    "text": "No"
                                                }
                                            ],
                                            "renderAs": "prettycheckbox"
                                        },
                                        {
                                            "type": "radiogroup",
                                            "name": "Do_you_owe_any_utilities_eviction_costs",
                                            "title": "Do you owe any utilities, eviction costs, or child support?",
                                            "hideNumber": true,
                                            "isRequired": true,
                                            "choices": [
                                                "Yes",
                                                "No"
                                            ],
                                            "renderAs": "prettycheckbox"
                                        },
                                        {
                                            "type": "comment",
                                            "name": "who_do_you_owe_and_how_much?",
                                            "visibleIf": "{Do_you_owe_any_utilities_eviction_costs} = ''Yes''",
                                            "isRequired": true,
                                            "title": "If yes, who do you owe and how much?",
                                            "hideNumber": true,
                                            "maxLength": 256
                                        },
                                        {
                                            "type": "matrix",
                                            "name": "Do_you_have_any_of_the_following?",
                                            "title": "Do you have any of the following?",
                                            "hideNumber": true,
                                            "isRequired": true,
                                            "columns": [
                                                "Yes",
                                                "No"
                                            ],
                                            "rows": [
                                                "Pending Legal Case",
                                                "Criminal Convictions",
                                                "Open Legal Case",
                                                "Registered 290"
                                            ]
                                        },
                                        {
                                            "type": "comment",
                                            "name": "If_yes_please_provide_where_when_and_what_offense?",
                                            "visibleIf": "{Do_you_have_any_of_the_following?.Criminal Convictions} = ''Yes'' or {Do_you_have_any_of_the_following?.Open Legal Case} = ''Yes'' or {Do_you_have_any_of_the_following?.Pending Legal Case} = ''Yes'' or {Do_you_have_any_of_the_following?.Registered 290} = ''Yes''",
                                            "isRequired": true,
                                            "title": "If yes, please provide where, when, and what offense?",
                                            "hideNumber": true
                                        }
                                    ]
                                },
                                {
                                    "type": "panel",
                                    "name": "Activities of daily living (ADLs)",
                                    "assessmentSectionAnchor": "Activities of daily living (ADLs)",
                                    "isExpandable": true,
                                    "state": "expanded",
                                    "title": "Activities of daily living (ADLs)",
                                    "elements": [
                                        {
                                            "type": "radiogroup",
                                            "name": "Bathe_as_in_washing_your_face_and_body_in_the_bath_or_shower.",
                                            "title": "Bathe, as in washing your face and body in the bath or shower. ",
                                            "hideNumber": true,
                                            "isRequired": true,
                                            "choices": [
                                                {
                                                    "value": "Independent",
                                                    "text": "Independent "
                                                },
                                                {
                                                    "value": "Some Assistance",
                                                    "text": "Some Assistance"
                                                },
                                                "Total Assistance"
                                            ],
                                            "renderAs": "prettycheckbox"
                                        },
                                        {
                                            "type": "radiogroup",
                                            "name": "Do_you_use_a_shower_or_a_tub?",
                                            "visibleIf": "{Bathe_as_in_washing_your_face_and_body_in_the_bath_or_shower.} = ''Some Assistance'' or {Bathe_as_in_washing_your_face_and_body_in_the_bath_or_shower.} = ''Total Assistance''",
                                            "isRequired": true,
                                            "title": "Do you use a shower or a tub?  ",
                                            "hideNumber": true,
                                            "validators": [
                                                {
                                                    "type": "expression"
                                                }
                                            ],
                                            "choices": [
                                                "Yes",
                                                "No"
                                            ],
                                            "renderAs": "prettycheckbox"
                                        },
                                        {
                                            "type": "radiogroup",
                                            "name": "Do you have any equipment you should_use_such_as_a_tub_seat_or_handheld_shower?",
                                            "visibleIf": "{Bathe_as_in_washing_your_face_and_body_in_the_bath_or_shower.} = ''Some Assistance'' or {Bathe_as_in_washing_your_face_and_body_in_the_bath_or_shower.} = ''Total Assistance''",
                                            "isRequired": true,
                                            "title": "Do you have any equipment you should use such as a tub seat or handheld shower?  ",
                                            "hideNumber": true,
                                            "validators": [
                                                {
                                                    "type": "expression"
                                                }
                                            ],
                                            "choices": [
                                                "Yes",
                                                "No"
                                            ],
                                            "renderAs": "prettycheckbox"
                                        },
                                        {
                                            "type": "radiogroup",
                                            "name": "Dress_and_groom_as_in_selecting_clothes.",
                                            "title": "Dress and groom, as in selecting clothes, putting them on and adequately managing your personal appearance.",
                                            "hideNumber": true,
                                            "isRequired": true,
                                            "choices": [
                                                "Independent",
                                                {
                                                    "value": "Some Assistance",
                                                    "text": "Some Assistance"
                                                },
                                                "Total Assistance"
                                            ],
                                            "renderAs": "prettycheckbox"
                                        },
                                        {
                                            "type": "radiogroup",
                                            "name": "Do_you_have_any_trouble_putting_on_clothes?",
                                            "visibleIf": "{Dress_and_groom_as_in_selecting_clothes.} = ''Some Assistance'' or {Dress_and_groom_as_in_selecting_clothes.} = ''Total Assistance''",
                                            "isRequired": true,
                                            "title": "Do you have any trouble putting on clothes?  ",
                                            "hideNumber": true,
                                            "validators": [
                                                {
                                                    "type": "expression"
                                                }
                                            ],
                                            "choices": [
                                                "Yes",
                                                "No"
                                            ],
                                            "renderAs": "prettycheckbox"
                                        },
                                        {
                                            "type": "radiogroup",
                                            "name": "Do_you_have_pain_when_dressing?",
                                            "visibleIf": "{Dress_and_groom_as_in_selecting_clothes.} = ''Some Assistance'' or {Dress_and_groom_as_in_selecting_clothes.} = ''Total Assistance''",
                                            "isRequired": true,
                                            "title": "Do you have pain when dressing?  ",
                                            "hideNumber": true,
                                            "validators": [
                                                {
                                                    "type": "expression"
                                                }
                                            ],
                                            "choices": [
                                                "Yes",
                                                "No"
                                            ],
                                            "renderAs": "prettycheckbox"
                                        },
                                        {
                                            "type": "radiogroup",
                                            "name": "Do_you_wish_you_had_help?",
                                            "visibleIf": "{Dress_and_groom_as_in_selecting_clothes.} = ''Some Assistance'' or {Dress_and_groom_as_in_selecting_clothes.} = ''Total Assistance''",
                                            "isRequired": true,
                                            "title": "Do you wish you had help?   ",
                                            "hideNumber": true,
                                            "validators": [
                                                {
                                                    "type": "expression"
                                                }
                                            ],
                                            "choices": [
                                                "Yes",
                                                "No"
                                            ],
                                            "renderAs": "prettycheckbox"
                                        },
                                        {
                                            "type": "radiogroup",
                                            "name": "Toileting_as_in_getting_to_and_from_the_toilet.",
                                            "title": "Toileting, as in getting to and from the toilet, using it appropriately, and cleaning yourself.",
                                            "hideNumber": true,
                                            "isRequired": true,
                                            "choices": [
                                                "Independent",
                                                {
                                                    "value": "Some Assistance",
                                                    "text": "Some Assistance"
                                                },
                                                "Total Assistance"
                                            ],
                                            "renderAs": "prettycheckbox"
                                        },
                                        {
                                            "type": "radiogroup",
                                            "name": "Do_you_have_any_problems_with_getting_to_the_bathroom_on_time?",
                                            "visibleIf": "{Toileting_as_in_getting_to_and_from_the_toilet.} = ''Some Assistance'' or {Toileting_as_in_getting_to_and_from_the_toilet.} = ''Total Assistance''",
                                            "isRequired": true,
                                            "title": "Do you have any problems with getting to the bathroom on time? ",
                                            "hideNumber": true,
                                            "validators": [
                                                {
                                                    "type": "expression"
                                                }
                                            ],
                                            "choices": [
                                                "Yes",
                                                "No"
                                            ],
                                            "renderAs": "prettycheckbox"
                                        },
                                        {
                                            "type": "radiogroup",
                                            "name": "Problems_getting_on_off_the_toilet?",
                                            "visibleIf": "{Toileting_as_in_getting_to_and_from_the_toilet.} = ''Some Assistance'' or {Toileting_as_in_getting_to_and_from_the_toilet.} = ''Total Assistance''",
                                            "isRequired": true,
                                            "title": "Problems getting on/off the toilet?",
                                            "hideNumber": true,
                                            "validators": [
                                                {
                                                    "type": "expression"
                                                }
                                            ],
                                            "choices": [
                                                "Yes",
                                                "No"
                                            ],
                                            "renderAs": "prettycheckbox"
                                        },
                                        {
                                            "type": "radiogroup",
                                            "name": "Do_you_have_grab_bars_or_equipment_that_help?",
                                            "visibleIf": "{Toileting_as_in_getting_to_and_from_the_toilet.} = ''Some Assistance'' or {Toileting_as_in_getting_to_and_from_the_toilet.} = ''Total Assistance''",
                                            "isRequired": true,
                                            "title": "Do you have grab bars or equipment that help? ",
                                            "hideNumber": true,
                                            "validators": [
                                                {
                                                    "type": "expression"
                                                }
                                            ],
                                            "choices": [
                                                "Yes",
                                                "No"
                                            ],
                                            "renderAs": "prettycheckbox"
                                        },
                                        {
                                            "type": "radiogroup",
                                            "name": "Eating_as_in_being_able_to_get_food_from_a_plate_into_ones_mouth.",
                                            "title": "Eating, as in being able to get food from a plate into one?s mouth. ",
                                            "hideNumber": true,
                                            "isRequired": true,
                                            "choices": [
                                                "Independent",
                                                {
                                                    "value": "Some Assistance",
                                                    "text": "Some Assistance"
                                                },
                                                "Total Assistance"
                                            ],
                                            "renderAs": "prettycheckbox"
                                        },
                                        {
                                            "type": "radiogroup",
                                            "name": "Do_you_have_any_trouble_chewing_or_swallowing_your_food?",
                                            "visibleIf": "{Eating_as_in_being_able_to_get_food_from_a_plate_into_ones_mouth.} = ''Some Assistance'' or {Eating_as_in_being_able_to_get_food_from_a_plate_into_ones_mouth.} = ''Total Assistance''",
                                            "isRequired": true,
                                            "title": "Do you have any trouble chewing or swallowing your food?",
                                            "hideNumber": true,
                                            "validators": [
                                                {
                                                    "type": "expression"
                                                }
                                            ],
                                            "choices": [
                                                "Yes",
                                                "No"
                                            ],
                                            "renderAs": "prettycheckbox"
                                        },
                                        {
                                            "type": "radiogroup",
                                            "name": "Any_problem_with_gripping_or_holding_onto_utensils_or_bringing_your_hand_to_your_mouth?",
                                            "visibleIf": "{Eating_as_in_being_able_to_get_food_from_a_plate_into_ones_mouth.} = ''Some Assistance'' or {Eating_as_in_being_able_to_get_food_from_a_plate_into_ones_mouth.} = ''Total Assistance''",
                                            "isRequired": true,
                                            "title": "Any problem with gripping or holding onto utensils or bringing your hand to your mouth?  ",
                                            "hideNumber": true,
                                            "validators": [
                                                {
                                                    "type": "expression"
                                                }
                                            ],
                                            "choices": [
                                                "Yes",
                                                "No"
                                            ],
                                            "renderAs": "prettycheckbox"
                                        },
                                        {
                                            "type": "comment",
                                            "name": "Comment",
                                            "title": "Comment",
                                            "hideNumber": true,
                                            "maxLength": 256
                                        }
                                    ]
                                },
                                {
                                    "type": "panel",
                                    "name": "Instrumental activities of daily living (IADLs)",
                                    "assessmentSectionAnchor": "Instrumental activities of daily living (IADLs)",
                                    "isExpandable": true,
                                    "state": "expanded",
                                    "title": "Instrumental activities of daily living (IADLs)",
                                    "elements": [
                                        {
                                            "type": "radiogroup",
                                            "name": "Medications_which_covers_obtaining_medications.",
                                            "title": "Medications: which covers obtaining medications and taking them as directed.",
                                            "hideNumber": true,
                                            "isRequired": true,
                                            "choices": [
                                                "Independent",
                                                {
                                                    "value": "Some Assistance",
                                                    "text": "Some Assistance"
                                                },
                                                "Total Assistance"
                                            ],
                                            "renderAs": "prettycheckbox"
                                        },
                                        {
                                            "type": "radiogroup",
                                            "name": "Can_you_open_your_pill_bottles?",
                                            "title": "Can you open your pill bottles?",
                                            "hideNumber": true,
                                            "isRequired": true,
                                            "validators": [
                                                {
                                                    "type": "expression"
                                                }
                                            ],
                                            "choices": [
                                                "Yes",
                                                "No"
                                            ],
                                            "renderAs": "prettycheckbox"
                                        },
                                        {
                                            "type": "radiogroup",
                                            "name": "Do_you_use_a_pill_box",
                                            "title": "Do you use a pillbox?",
                                            "hideNumber": true,
                                            "isRequired": true,
                                            "validators": [
                                                {
                                                    "type": "expression"
                                                }
                                            ],
                                            "choices": [
                                                "Yes",
                                                "No"
                                            ],
                                            "renderAs": "prettycheckbox"
                                        },
                                        {
                                            "type": "radiogroup",
                                            "name": "Do_you_wish_you_had_more_help_with_organizing_your_medication?",
                                            "title": "Do you wish you had more help with organizing your medication so you can take them easier?",
                                            "hideNumber": true,
                                            "isRequired": true,
                                            "validators": [
                                                {
                                                    "type": "expression"
                                                }
                                            ],
                                            "choices": [
                                                "Yes",
                                                "No"
                                            ],
                                            "renderAs": "prettycheckbox"
                                        },
                                        {
                                            "type": "radiogroup",
                                            "name": "Can_you_see_the_labels_on_the_pill_bottles?",
                                            "title": "Can you see the labels on the pill bottles?",
                                            "hideNumber": true,
                                            "isRequired": true,
                                            "validators": [
                                                {
                                                    "type": "expression"
                                                }
                                            ],
                                            "choices": [
                                                "Yes",
                                                "No"
                                            ],
                                            "renderAs": "prettycheckbox"
                                        },
                                        {
                                            "type": "radiogroup",
                                            "name": "Housekeeping_cleaning_kitchens_after_eating_keeping_tidy",
                                            "title": "Housekeeping. This means cleaning kitchens after eating, keeping one?s living space reasonably clean and tidy, and keeping up with home maintenance. ",
                                            "hideNumber": true,
                                            "choices": [
                                                "Independent",
                                                {
                                                    "value": "Some Assistance",
                                                    "text": "Some Assistance"
                                                },
                                                "Total Assistance"
                                            ],
                                            "renderAs": "prettycheckbox"
                                        },
                                        {
                                            "type": "rating",
                                            "name": "Range_of_cleanliness",
                                            "title": "Range of cleanliness (Rate 1 (poor) to 10 (pristine)",
                                            "hideNumber": true,
                                            "isRequired": true,
                                            "validators": [
                                                {
                                                    "type": "expression"
                                                }
                                            ],
                                            "rateValues": [
                                                "1",
                                                "2",
                                                "3",
                                                "4",
                                                "5",
                                                "6",
                                                "7",
                                                "8",
                                                "9",
                                                "10"
                                            ],
                                            "rateMax": 10
                                        },
                                        {
                                            "type": "radiogroup",
                                            "name": "Do_you_get_help_cleaning_your_home?",
                                            "title": "Do you get help cleaning your home?",
                                            "hideNumber": true,
                                            "isRequired": true,
                                            "validators": [
                                                {
                                                    "type": "expression"
                                                }
                                            ],
                                            "choices": [
                                                "Yes",
                                                "No"
                                            ],
                                            "renderAs": "prettycheckbox"
                                        },
                                        {
                                            "type": "radiogroup",
                                            "name": "Do_you_wish_you_had_help",
                                            "title": "Do you wish you had help?",
                                            "hideNumber": true,
                                            "isRequired": true,
                                            "validators": [
                                                {
                                                    "type": "expression"
                                                }
                                            ],
                                            "choices": [
                                                "Yes",
                                                "No"
                                            ],
                                            "renderAs": "prettycheckbox"
                                        },
                                        {
                                            "type": "radiogroup",
                                            "name": "Meal_Preparation_Cooking",
                                            "title": "Meal Preparation/Cooking: ",
                                            "hideNumber": true,
                                            "isRequired": true,
                                            "choices": [
                                                "Independent",
                                                {
                                                    "value": "Some Assistance",
                                                    "text": "Some Assistance"
                                                },
                                                "Total Assistance"
                                            ],
                                            "renderAs": "prettycheckbox"
                                        },
                                        {
                                            "type": "radiogroup",
                                            "name": "Do_you_use_a_stove?",
                                            "title": "Do you use a stove?",
                                            "hideNumber": true,
                                            "isRequired": true,
                                            "validators": [
                                                {
                                                    "type": "expression"
                                                }
                                            ],
                                            "choices": [
                                                "Yes",
                                                "No"
                                            ],
                                            "renderAs": "prettycheckbox"
                                        },
                                        {
                                            "type": "radiogroup",
                                            "name": "Do_you_use_an_oven?",
                                            "title": "Do you use an oven?",
                                            "hideNumber": true,
                                            "isRequired": true,
                                            "validators": [
                                                {
                                                    "type": "expression"
                                                }
                                            ],
                                            "choices": [
                                                "Yes",
                                                "No"
                                            ],
                                            "renderAs": "prettycheckbox"
                                        },
                                        {
                                            "type": "radiogroup",
                                            "name": "Do_you_use_a_microwave?",
                                            "title": "Do you use a microwave?",
                                            "hideNumber": true,
                                            "isRequired": true,
                                            "validators": [
                                                {
                                                    "type": "expression"
                                                }
                                            ],
                                            "choices": [
                                                "Yes",
                                                "No"
                                            ],
                                            "renderAs": "prettycheckbox"
                                        },
                                        {
                                            "type": "radiogroup",
                                            "name": "Do_you_get_meals_on_wheels?",
                                            "title": "Do you get meals on wheels?",
                                            "hideNumber": true,
                                            "isRequired": true,
                                            "validators": [
                                                {
                                                    "type": "expression"
                                                }
                                            ],
                                            "choices": [
                                                "Yes",
                                                "No"
                                            ],
                                            "renderAs": "prettycheckbox"
                                        },
                                        {
                                            "type": "radiogroup",
                                            "name": "Do_you_wish_you_had_help_with_meal_prep?",
                                            "title": "Do you wish you had more help with meal prep? ",
                                            "hideNumber": true,
                                            "isRequired": true,
                                            "validators": [
                                                {
                                                    "type": "expression"
                                                }
                                            ],
                                            "choices": [
                                                "Yes",
                                                "No"
                                            ],
                                            "renderAs": "prettycheckbox"
                                        },
                                        {
                                            "type": "radiogroup",
                                            "name": "Laundry",
                                            "title": "Laundry:",
                                            "hideNumber": true,
                                            "isRequired": true,
                                            "choices": [
                                                "Independent",
                                                {
                                                    "value": "Some Assistance",
                                                    "text": "Some Assistance"
                                                },
                                                "Total Assistance"
                                            ],
                                            "renderAs": "prettycheckbox"
                                        },
                                        {
                                            "type": "radiogroup",
                                            "name": "Does_anyone_help_you_with_your_laundry?",
                                            "title": "Does anyone help you with your laundry?",
                                            "hideNumber": true,
                                            "isRequired": true,
                                            "validators": [
                                                {
                                                    "type": "expression"
                                                }
                                            ],
                                            "choices": [
                                                "Yes",
                                                "No"
                                            ],
                                            "renderAs": "prettycheckbox"
                                        },
                                        {
                                            "type": "radiogroup",
                                            "name": "Do_you_feel_you_need_help?",
                                            "title": "Do you feel you need help? ",
                                            "hideNumber": true,
                                            "isRequired": true,
                                            "validators": [
                                                {
                                                    "type": "expression"
                                                }
                                            ],
                                            "choices": [
                                                "Yes",
                                                "No"
                                            ],
                                            "renderAs": "prettycheckbox"
                                        },
                                        {
                                            "type": "radiogroup",
                                            "name": "Telephone",
                                            "title": "Telephone:",
                                            "hideNumber": true,
                                            "isRequired": true,
                                            "choices": [
                                                "Independent",
                                                {
                                                    "value": "Some Assistance",
                                                    "text": "Some Assistance"
                                                },
                                                "Total Assistance"
                                            ],
                                            "renderAs": "prettycheckbox"
                                        },
                                        {
                                            "type": "radiogroup",
                                            "name": "Adequately_hear_on_your_phone?",
                                            "title": "Are you able to adequately hear on your phone? ",
                                            "hideNumber": true,
                                            "isRequired": true,
                                            "validators": [
                                                {
                                                    "type": "expression"
                                                }
                                            ],
                                            "choices": [
                                                "Yes",
                                                "No"
                                            ],
                                            "renderAs": "prettycheckbox"
                                        },
                                        {
                                            "type": "radiogroup",
                                            "name": "Have_trouble_dialing_numbers?",
                                            "title": "Do you have trouble dialing numbers?",
                                            "hideNumber": true,
                                            "isRequired": true,
                                            "validators": [
                                                {
                                                    "type": "expression"
                                                }
                                            ],
                                            "choices": [
                                                "Yes",
                                                "No"
                                            ],
                                            "renderAs": "prettycheckbox"
                                        },
                                        {
                                            "type": "radiogroup",
                                            "name": "Do_you_own_a_cell_phone?",
                                            "title": "Do you own a cellphone?",
                                            "hideNumber": true,
                                            "isRequired": true,
                                            "validators": [
                                                {
                                                    "type": "expression"
                                                }
                                            ],
                                            "choices": [
                                                "Yes",
                                                "No"
                                            ],
                                            "renderAs": "prettycheckbox"
                                        },
                                        {
                                            "type": "radiogroup",
                                            "name": "Shopping",
                                            "title": "Shopping:",
                                            "hideNumber": true,
                                            "isRequired": true,
                                            "choices": [
                                                "Independent",
                                                {
                                                    "value": "Some Assistance",
                                                    "text": "Some Assistance"
                                                },
                                                "Total Assistance"
                                            ],
                                            "renderAs": "prettycheckbox"
                                        },
                                        {
                                            "type": "radiogroup",
                                            "name": "Finances_paying_bills_and_managing_financial_assets.",
                                            "title": "Finances, such as paying bills and managing financial assets. ",
                                            "hideNumber": true,
                                            "isRequired": true,
                                            "choices": [
                                                "Independent",
                                                {
                                                    "value": "Some Assistance",
                                                    "text": "Some Assistance"
                                                },
                                                "Total Assistance"
                                            ],
                                            "renderAs": "prettycheckbox"
                                        },
                                        {
                                            "type": "radiogroup",
                                            "name": "Receive_help_with_paying_your_bills?",
                                            "title": "Do you receive help with paying your bills? ",
                                            "hideNumber": true,
                                            "isRequired": true,
                                            "validators": [
                                                {
                                                    "type": "expression"
                                                }
                                            ],
                                            "choices": [
                                                "Yes",
                                                "No"
                                            ],
                                            "renderAs": "prettycheckbox"
                                        },
                                        {
                                            "type": "radiogroup",
                                            "name": "Struggle_to_write_checks_or_balance_checkbook?",
                                            "title": "Do you struggle to write checks or balance your checkbook? ",
                                            "hideNumber": true,
                                            "isRequired": true,
                                            "validators": [
                                                {
                                                    "type": "expression"
                                                }
                                            ],
                                            "choices": [
                                                "Yes",
                                                "No"
                                            ],
                                            "renderAs": "prettycheckbox"
                                        },
                                        {
                                            "type": "radiogroup",
                                            "name": "Transportation_via_driving_other_means_of_transport.",
                                            "title": "Transportation, either via driving or by organizing other means of transport. ",
                                            "hideNumber": true,
                                            "isRequired": true,
                                            "choices": [
                                                "Independent",
                                                {
                                                    "value": "Some Assistance",
                                                    "text": "Some Assistance"
                                                },
                                                "Total Assistance"
                                            ],
                                            "renderAs": "prettycheckbox"
                                        },
                                        {
                                            "type": "radiogroup",
                                            "name": "Make_Medical_Appointments",
                                            "title": "Make Medical Appointments:",
                                            "hideNumber": true,
                                            "isRequired": true,
                                            "choices": [
                                                "Independent",
                                                {
                                                    "value": "Some Assistance",
                                                    "text": "Some Assistance"
                                                },
                                                "Total Assistance"
                                            ],
                                            "renderAs": "prettycheckbox"
                                        },
                                        {
                                            "type": "radiogroup",
                                            "name": "question4",
                                            "title": "Do you get help making appointments such as doctor, dentist, etc? ",
                                            "hideNumber": true,
                                            "isRequired": true,
                                            "validators": [
                                                {
                                                    "type": "expression"
                                                }
                                            ],
                                            "choices": [
                                                "Yes",
                                                "No"
                                            ],
                                            "renderAs": "prettycheckbox"
                                        },
                                        {
                                            "type": "radiogroup",
                                            "name": "Mobility_walking_inside_outside_home",
                                            "title": "Mobility: walking inside/outside home",
                                            "hideNumber": true,
                                            "isRequired": true,
                                            "choices": [
                                                "Independent",
                                                {
                                                    "value": "Some Assistance",
                                                    "text": "Some Assistance"
                                                },
                                                "Total Assistance"
                                            ],
                                            "renderAs": "prettycheckbox"
                                        },
                                        {
                                            "type": "radiogroup",
                                            "name": "Fallen_in_the_last_year?",
                                            "title": "Have you fallen in the last year? ",
                                            "hideNumber": true,
                                            "isRequired": true,
                                            "validators": [
                                                {
                                                    "type": "expression"
                                                }
                                            ],
                                            "choices": [
                                                "Yes",
                                                "No"
                                            ],
                                            "renderAs": "prettycheckbox"
                                        },
                                        {
                                            "type": "radiogroup",
                                            "name": "Have_difficulty_leaving_the_house_changing_positions_in_bed?",
                                            "title": "Do you have difficulty leaving the house; changing positions in bed?",
                                            "hideNumber": true,
                                            "isRequired": true,
                                            "validators": [
                                                {
                                                    "type": "expression"
                                                }
                                            ],
                                            "choices": [
                                                "Yes",
                                                "No"
                                            ],
                                            "renderAs": "prettycheckbox"
                                        },
                                        {
                                            "type": "radiogroup",
                                            "name": "Able_to_walk_up_a_flight_of_stairs?",
                                            "title": "Are you able to walkup a flight of stairs? ",
                                            "hideNumber": true,
                                            "isRequired": true,
                                            "validators": [
                                                {
                                                    "type": "expression"
                                                }
                                            ],
                                            "choices": [
                                                "Yes",
                                                "No"
                                            ],
                                            "renderAs": "prettycheckbox"
                                        },
                                        {
                                            "type": "radiogroup",
                                            "name": "Walk_a_city_block",
                                            "title": "Are you able to walk a city block (250-350 ft)?",
                                            "hideNumber": true,
                                            "isRequired": true,
                                            "validators": [
                                                {
                                                    "type": "expression"
                                                }
                                            ],
                                            "choices": [
                                                "Yes",
                                                "No"
                                            ],
                                            "renderAs": "prettycheckbox"
                                        },
                                        {
                                            "type": "checkbox",
                                            "name": "Please_mark_all_applicable",
                                            "title": "Please mark all applicable:",
                                            "renderAs": "prettycheckbox",
                                            "hideNumber": true,
                                            "choices": [
                                                "Cane",
                                                "Walker",
                                                "Crutches",
                                                "Grab bars",
                                                "Wheelchair",
                                                "Proper lighting"
                                            ],
                                            "showSelectAllItem": true,
                                            "selectAllText": "Cane"
                                        },
                                        {
                                            "type": "radiogroup",
                                            "name": "Friends_or_family_members_express_concerns_about_ability_to_care_for_yourself?",
                                            "title": "Do friends or family members express concerns about your ability to care for yourself?",
                                            "hideNumber": true,
                                            "isRequired": true,
                                            "validators": [
                                                {
                                                    "type": "expression"
                                                }
                                            ],
                                            "choices": [
                                                "Yes",
                                                "No"
                                            ],
                                            "renderAs": "prettycheckbox"
                                        },
                                        {
                                            "type": "radiogroup",
                                            "name": "If_Member_is_currently_housed",
                                            "title": "If Member is currently housed?",
                                            "hideNumber": true,
                                            "isRequired": true,
                                            "validators": [
                                                {
                                                    "type": "expression"
                                                }
                                            ],
                                            "choices": [
                                                "Yes",
                                                "No"
                                            ],
                                            "renderAs": "prettycheckbox"
                                        }
                                    ]
                                },
                                {
                                    "type": "panel",
                                    "name": "Home Evaluation",
                                    "assessmentSectionAnchor": "Home Evaluation",
                                    "visibleIf": "{If_Member_is_currently_housed} = ''Yes''",
                                    "isExpandable": true,
                                    "state": "expanded",
                                    "title": "Home Evaluation",
                                    "elements": [
                                        {
                                            "type": "radiogroup",
                                            "name": "Grab_bars_in_the_tub_shower_toilet?",
                                            "visibleIf": "{If_Member_is_currently_housed} = ''Yes''",
                                            "isRequired": true,
                                            "title": "Do you have grab bars in the tub, shower, or toilet? ",
                                            "hideNumber": true,
                                            "validators": [
                                                {
                                                    "type": "expression"
                                                }
                                            ],
                                            "choices": [
                                                "Yes",
                                                "No"
                                            ],
                                            "renderAs": "prettycheckbox"
                                        },
                                        {
                                            "type": "radiogroup",
                                            "name": "Do_you_need_grab_bars",
                                            "visibleIf": "{If_Member_is_currently_housed} = ''Yes''",
                                            "isRequired": true,
                                            "title": "Do you feel you need grab bars? ",
                                            "hideNumber": true,
                                            "validators": [
                                                {
                                                    "type": "expression"
                                                }
                                            ],
                                            "choices": [
                                                "Yes",
                                                "No"
                                            ],
                                            "renderAs": "prettycheckbox"
                                        },
                                        {
                                            "type": "text",
                                            "name": "Grab_bar_If_yes_where",
                                            "visibleIf": "{Do_you_need_grab_bars} = ''Yes''",
                                            "isRequired": true,
                                            "title": "If yes, where? ",
                                            "hideNumber": true,
                                            "maxLength": 256
                                        },
                                        {
                                            "type": "radiogroup",
                                            "name": "Have_animals_in_the_home?",
                                            "visibleIf": "{If_Member_is_currently_housed} = ''Yes''",
                                            "isRequired": true,
                                            "title": "Do you have an animals in the home? ",
                                            "hideNumber": true,
                                            "validators": [
                                                {
                                                    "type": "expression"
                                                }
                                            ],
                                            "choices": [
                                                "Yes",
                                                "No"
                                            ],
                                            "renderAs": "prettycheckbox"
                                        },
                                        {
                                            "type": "text",
                                            "name": "Animals_If_yes_list",
                                            "visibleIf": "{Have_animals_in_the_home?} = ''Yes''",
                                            "isRequired": true,
                                            "title": "If yes, list:",
                                            "hideNumber": true,
                                            "maxLength": 256
                                        },
                                        {
                                            "type": "radiogroup",
                                            "name": "Have_working_smoke_detector_carbon_monoxide_detector",
                                            "visibleIf": "{If_Member_is_currently_housed} = ''Yes''",
                                            "isRequired": true,
                                            "title": "Do you have a working smoke detector/ carbon monoxide detector?",
                                            "hideNumber": true,
                                            "validators": [
                                                {
                                                    "type": "expression"
                                                }
                                            ],
                                            "choices": [
                                                "Yes",
                                                "No"
                                            ],
                                            "renderAs": "prettycheckbox"
                                        },
                                        {
                                            "type": "radiogroup",
                                            "name": "question2",
                                            "visibleIf": "{If_Member_is_currently_housed} = ''Yes''",
                                            "isRequired": true,
                                            "title": "Are pathways clear to walk in your home? ",
                                            "hideNumber": true,
                                            "validators": [
                                                {
                                                    "type": "expression"
                                                }
                                            ],
                                            "choices": [
                                                "Yes",
                                                "No"
                                            ],
                                            "renderAs": "prettycheckbox"
                                        },
                                        {
                                            "type": "matrix",
                                            "name": "question3",
                                            "visibleIf": "{If_Member_is_currently_housed} = ''Yes''",
                                            "isRequired": true,
                                            "title": "  Please mark yes or no if you have in your home:  ",
                                            "hideNumber": true,
                                            "columns": [
                                                "Yes",
                                                "No"
                                            ],
                                            "rows": [
                                                {
                                                    "value": "Throw Rugs ",
                                                    "text": "Throw Rugs"
                                                },
                                                "Uneven flooring",
                                                "Clutter",
                                                "Trash"
                                            ]
                                        },
                                        {
                                            "type": "matrix",
                                            "name": "Water_heating_lighting:",
                                            "visibleIf": "{If_Member_is_currently_housed} = ''Yes''",
                                            "isRequired": true,
                                            "title": "Please mark yes or no if you have in your home water, heating, lighting systems: ",
                                            "hideNumber": true,
                                            "columns": [
                                                "Yes",
                                                "No"
                                            ],
                                            "rows": [
                                                {
                                                    "value": "Running Water? ",
                                                    "text": "Running Water?"
                                                },
                                                "Adequate heating and cooling?",
                                                "Electricity and lighting?"
                                            ]
                                        },
                                        {
                                            "type": "matrix",
                                            "name": "Appliances_Emergency_Response_Systems",
                                            "visibleIf": "{If_Member_is_currently_housed} = ''Yes''",
                                            "isRequired": true,
                                            "title": "Please mark yes or no if you have in your home Appliances & Emergency Response Systems:",
                                            "hideNumber": true,
                                            "columns": [
                                                "Yes",
                                                "No"
                                            ],
                                            "rows": [
                                                "Refrigerator?",
                                                "Stove?"
                                            ]
                                        },
                                        {
                                            "type": "radiogroup",
                                            "name": "Use_device_that_helps_get_in_out_bed",
                                            "visibleIf": "{If_Member_is_currently_housed} = ''Yes''",
                                            "isRequired": true,
                                            "title": "Do you use any device that helps you get in/out of bed?  ",
                                            "hideNumber": true,
                                            "validators": [
                                                {
                                                    "type": "expression"
                                                }
                                            ],
                                            "choices": [
                                                "Yes",
                                                "No"
                                            ],
                                            "renderAs": "prettycheckbox"
                                        },
                                        {
                                            "type": "radiogroup",
                                            "name": "Do_you_think_you_need_a_device?",
                                            "visibleIf": "{Use_device_that_helps_get_in_out_bed} = ''No''",
                                            "isRequired": true,
                                            "title": "Do you think you need a device?  ",
                                            "hideNumber": true,
                                            "validators": [
                                                {
                                                    "type": "expression"
                                                }
                                            ],
                                            "choices": [
                                                "Yes",
                                                "No"
                                            ],
                                            "renderAs": "prettycheckbox"
                                        }
                                    ]
                                },
                                {
                                    "type": "panel",
                                    "name": "Caregiver resources and involvement",
                                    "assessmentSectionAnchor": "Caregiver resources and involvement",
                                    "visibleIf": "{If_Member_is_currently_housed} = ''Yes''",
                                    "isExpandable": true,
                                    "state": "expanded",
                                    "title": "Caregiver resources and involvement",
                                    "elements": [
                                        {
                                            "type": "radiogroup",
                                            "name": "Have_family_member_or_caregiver_assisting_you?",
                                            "visibleIf": "{If_Member_is_currently_housed} = ''Yes''",
                                            "isRequired": true,
                                            "title": "Do you have a family member or other caregiver assisting you? ",
                                            "hideNumber": true,
                                            "validators": [
                                                {
                                                    "type": "expression"
                                                }
                                            ],
                                            "choices": [
                                                "Yes",
                                                "No"
                                            ],
                                            "renderAs": "prettycheckbox"
                                        },
                                        {
                                            "type": "radiogroup",
                                            "name": "Have_in_home_supportive_services_worker",
                                            "visibleIf": "{If_Member_is_currently_housed} = ''Yes''",
                                            "isRequired": true,
                                            "title": "Do you have an in-home supportive services (IHSS) worker? ",
                                            "hideNumber": true,
                                            "validators": [
                                                {
                                                    "type": "expression"
                                                }
                                            ],
                                            "choices": [
                                                "Yes",
                                                "No"
                                            ],
                                            "renderAs": "prettycheckbox"
                                        },
                                        {
                                            "type": "text",
                                            "name": "If_yes_how_many_hours_per_month?",
                                            "visibleIf": "{Have_in_home_supportive_services_worker} = ''Yes''",
                                            "isRequired": true,
                                            "title": "If yes, how many hours per month? ",
                                            "hideNumber": true,
                                            "maxLength": 256
                                        },
                                        {
                                            "type": "text",
                                            "name": "Name_of_the_person_helping_you_and_level_of _involvement",
                                            "visibleIf": "{Have_in_home_supportive_services_worker} = ''Yes'' or {Have_family_member_or_caregiver_assisting_you?} = ''Yes''",
                                            "isRequired": true,
                                            "title": "What is the name of the person helping you and the level of  involvement of care identified above?",
                                            "hideNumber": true,
                                            "maxLength": 256
                                        },
                                        {
                                            "type": "comment",
                                            "name": "question5",
                                            "visibleIf": "{If_Member_is_currently_housed} = ''Yes''",
                                            "title": "Comment:",
                                            "hideNumber": true,
                                            "maxLength": 256
                                        }
                                    ]
                                }
                            ]
                        }
                    ]
                }
' where id = @assessment_id