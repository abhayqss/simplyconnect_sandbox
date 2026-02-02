define(
    [
        'underscore',
        path('../Utils'),
        path('./MockUtils')
    ],
    function (_, U, MU) {
        function getSuccessResponse(data, extraBodyProps, extraProps) {
            extraProps = extraProps || {};
            extraBodyProps = extraBodyProps || {};


            var body = _.extend({
                success: true
            }, extraBodyProps);

            if (data) {
                body.data = data;
            }

            var resp = _.extend(
                {body: body},
                extraProps,
                {statusCode: 200}
            );

            resp.text = JSON.stringify(resp);
            return resp;
        }

        var usStates = [
            { id: 1, name: 'Alabama', abbr: 'AL' },
            { id: 37, name: 'Alaska', abbr: 'AK' },
            { id: 2, name: 'Arizona', abbr: 'AZ' },
            { id: 38, name: 'Arkansas', abbr: 'AR' },
            { id: 3, name: 'California', abbr: 'CA' },
            { id: 39, name: 'Colorado', abbr: 'CO' },
            { id: 4, name: 'Connecticut', abbr: 'CT' },
            { id: 40, name: 'Delaware', abbr: 'DE' },
            { id: 5, name: 'Florida', abbr: 'FL' },
            { id: 6, name: 'Georgia', abbr: 'GA' },
            { id: 41, name: 'Hawaii', abbr: 'HI' },
            { id: 7, name: 'Idaho', abbr: 'ID' },
            { id: 42, name: 'Illinois', abbr: 'IL' },
            { id: 8, name: 'Indiana', abbr: 'IN' },
            { id: 43, name: 'Iowa', abbr: 'IA' },
            { id: 9, name: 'Kansas', abbr: 'KS' },
            { id: 44, name: 'Kentucky', abbr: 'KY' },
            { id: 45, name: 'Louisiana', abbr: 'LA' },
            { id: 22, name: 'Maine', abbr: 'ME' },
            { id: 46, name: 'Maryland', abbr: 'MD' },
            { id: 23, name: 'Massachusetts', abbr: 'MA' },
            { id: 47, name: 'Michigan', abbr: 'MI' },
            { id: 24, name: 'Minnesota', abbr: 'MN' },
            { id: 48, name: 'Mississippi', abbr: 'MS' },
            { id: 25, name: 'Missouri', abbr: 'MO' },
            { id: 49, name: 'Montana', abbr: 'MT' },
            { id: 26, name: 'Nebraska', abbr: 'NE' },
            { id: 27, name: 'Nevada', abbr: 'NV' },
            { id: 10, name: 'New Hampshire', abbr: 'NH' },
            { id: 28, name: 'New Jersey', abbr: 'NJ' },
            { id: 11, name: 'New Mexico', abbr: 'NM' },
            { id: 29, name: 'New York', abbr: 'NY' },
            { id: 30, name: 'North Carolina', abbr: 'NC' },
            { id: 12, name: 'North Dakota', abbr: 'ND' },
            { id: 31, name: 'Ohio', abbr: 'OH' },
            { id: 13, name: 'Oklahoma', abbr: 'OK' },
            { id: 14, name: 'Oregon', abbr: 'OR' },
            { id: 32, name: 'Pennsylvania', abbr: 'PA' },
            { id: 15, name: 'Rhode Island', abbr: 'RI' },
            { id: 33, name: 'South Carolina', abbr: 'SC' },
            { id: 16, name: 'South Dakota', abbr: 'SD' },
            { id: 17, name: 'Tennessee', abbr: 'TN' },
            { id: 50, name: 'Texas', abbr: 'TX' },
            { id: 18, name: 'Utah', abbr: 'UT' },
            { id: 34, name: 'Vermont', abbr: 'VT' },
            { id: 19, name: 'Virginia', abbr: 'VA' },
            { id: 35, name: 'Washington', abbr: 'WA' },
            { id: 20, name: 'West Virginia', abbr: 'WV' },
            { id: 36, name: 'Wisconsin', abbr: 'WI' },
            { id: 21, name: 'Wyoming', abbr: 'WY' }
        ];

        var incidents = [
            {
                id: 0,
                level: 1,
                title: 'Death',
                incidents: [
                    {id: 1, level: 1, title: 'Accidental'},
                    {id: 2, level: 1, title: 'Suicide'},
                    {id: 3, level: 1, title: 'Unusual Circumstances'},
                    {id: 4, level: 1, title: 'Natural causes'},
                    {id: 5, level: 1, title: 'Other – unexpected or sudden death'}
                ]
            },
            {id: 6, level: 1, title: 'Suicide attempt'},
            {id: 7, level: 1, title: 'Sexual Assault – alleged victim'},
            {id: 8, level: 1, title: 'Sexual Assault – alleged perpetrator'},
            {id: 9, level: 1, title: 'Physical Assault – alleged victim'},
            {
                id: 10,
                level: 1,
                title: 'Fire',
                incidents: [
                    {id: 11, level: 1, title: 'Intentional – started by participant'}
                ]
            },
            {
                id: 12,
                level: 1,
                title: 'Criminal Activity',
                incidents: [
                    {id: 13, level: 1, title: 'Participant arrested for alleged felony'}
                ]
            },
            {
                id: 14,
                level: 1,
                title: 'Missing Person',
                incidents: [
                    {id: 15, level: 1, title: 'Law Enforcement contacted'}
                ]
            },
            {
                id: 16,
                level: 1,
                title: 'Suspected mistreatment (abuse, neglect)',
                incidents: [
                    {id: 171, level: 1, title: 'Alleged victim of neglect'},
                    {id: 181, level: 1, title: 'Alleged victim of abuse'},
                    {id: 191, level: 1, title: 'Alleged victim of physical abuse'},
                    {id: 201, level: 1, title: 'Alleged victim of sexual abuse'}
                ]
            },
            {
                id: 17,
                level: 2,
                title: 'Unexpected hospital visit/admission',
                incidents: [
                    {id: 18, level: 2, title: 'Emergency Room visit – illness (medical/psych)'},
                    {id: 19, level: 2, title: 'Emergency Room visit – injury'},
                    {id: 20, level: 2, title: 'Medical hospitalization'},
                    {id: 21, level: 2, title: 'Medication related'},
                    {id: 22, level: 2, title: 'Substance Abuse'},
                    {id: 23, level: 2, title: 'Psychiatric hospitalization'}
                ]
            },
            {
                id: 24,
                level: 2,
                title: 'Injury',
                incidents: [
                    {id: 25, level: 2, title: 'Fall'},
                    {id: 26, level: 2, title: 'Medication Related'},
                    {id: 27, level: 2, title: 'Bruising'},
                    {id: 28, level: 2, title: 'Burn'},
                    {id: 29, level: 2, title: 'Bleeding'},
                    {id: 30, level: 2, title: 'Cut or Puncture Wound'},
                    {id: 30, level: 2, title: 'Sprain/Strain'},
                    {id: 30, level: 2, title: 'Other, please specify'}
                ]
            },
            {id: 31, level: 2, title: 'Nursing Facility/SMHRF (IMD) Placement'},
            {
                id: 32,
                level: 2,
                title: 'Fire',
                incidents: [
                    {id: 33, level: 2, title: 'Intentional – not started by participant'},
                    {id: 34, level: 2, title: 'Accidental – not started by participant'}
                ]
            },
            {
                id: 35,
                level: 2,
                title: 'Behavioral incident',
                incidents: [
                    {id: 36, level: 2, title: 'Threats of injury to self/others'},
                    {id: 37, level: 2, title: 'Substance Abuse'}
                ]
            },
            {
                id: 38,
                level: 3,
                title: 'Property damage/destruction',
                incidents: [
                    {id: 39, level: 3, title: 'Damage/destruction of provider property'},
                    {id: 40, level: 3, title: 'Damage/destruction of participant property'},
                    {id: 41, level: 3, title: 'Damage/destruction of someone else’s property'},
                    {id: 42, level: 3, title: 'Fire - accidental'}
                ]
            },
            {
                id: 43,
                level: 3,
                title: 'Vehicle accident not requiring emergency department visit',
                incidents: [
                    {id: 44, level: 3, title: 'Participant/passenger vehicle'},
                    {id: 46, level: 3, title: 'Public Transportation'},
                    {id: 47, level: 3, title: 'Other vehicle (e.g. bicycle, motorcycle)'},
                    {id: 48, level: 3, title: 'Pedestrian'},
                    {id: 48, level: 3, title: 'Other'}
                ]
            },
            {
                id: 49,
                level: 3,
                title: 'Eviction for non-criminal reasons',
                incidents: [
                    {id: 50, level: 3, title: 'Refusal to pay rent'},
                    {id: 51, level: 3, title: 'Destruction of property'},
                    {id: 51, level: 3, title: 'Disturbing privacy/peace'},
                    {id: 52, level: 3, title: 'Other (e.g. unapproved occupants/lease violations)'}
                ]
            },
            {
                id: 53,
                level: 3,
                title: 'Suspected mistreatment',
                incidents: [
                    {id: 54, level: 3, title: 'Alleged victim of verbal abuse'},
                    {id: 55, level: 3, title: 'Alleged victim of financial abuse'}
                ]
            },
            {
                id: 56,
                level: 3,
                title: 'Alleged Fraud/Misuse of funds',
                incidents: [
                    {id: 57, level: 3, title: 'By Participant'},
                    {id: 58, level: 3, title: 'By Provider'},
                    {id: 59, level: 3, title: 'Other'}
                ]
            },
            {
                id: 60,
                level: 3,
                title: 'Criminal Activity',
                incidents: [
                    {id: 61, level: 3, title: 'Misdemeanor'},
                    {id: 62, level: 3, title: 'Citation'},
                    {id: 63, level: 3, title: 'Domestic'}
                ]
            },
            {
                id: 64,
                level: 3,
                title: 'Eviction for alleged criminal activity',
                incidents: [
                    {id: 65, level: 3, title: 'Physical violence/aggression'},
                    {id: 66, level: 3, title: 'Fire setting'},
                    {id: 67, level: 3, title: 'Drug trafficking'},
                    {id: 68, level: 3, title: 'Other'}
                ]
            },
            {
                id: 69,
                level: 3,
                title: 'Missing person',
                incidents: [
                    {id: 70, level: 3, title: 'Law enforcement not contacted'}
                ]
            }
        ];

        var incidentPlaces = [
            { id: 0, title: 'Participant’s apartment' },
            { id: 1, title: 'Relative’s home/apartment' },
            { id: 2, title: 'Provider/agency office' },
            { id: 3, title: 'Neighbor’s home/apartment' },
            { id: 4, title: 'Friend’s home/apartment' },
            { id: 5, name: 'OTHER', title: 'Other (specify)' }
        ];

        var incidentLevelReportingSettings = [
            {
                level: 1,
                timeLines: ['Immediate notification (no later than next working day)'],
                requirements: [
                    'For Colbert, “Root Cause” Investigation for deaths is accomplished through a Mortality Review facilitated by UIC- CON. For all other Level 1 incidents, the circumstances are reviewed and addressed on UIC CON teleconference.',
                    'For Williams, all Level 1 incidents require a Root Cause Analysis.',
                    'Sentinel Event Policy activated; Report on investigation with corrective action plan required (Williams); 30-day follow-up report required (Colbert).'
                ]
            },
            {
                level: 2,
                timeLines: ['Within 2 working days'],
                requirements: [
                    'Report submitted, Investigation required; report on investigation and corrective action plan required (Williams); 30-day follow-up report required (Colbert).'
                ]
            },
            {
                level: 3,
                timeLines: ['Within 3 working days'],
                requirements: [
                    'Report submitted may require investigation.',
                    'If investigated, report on investigation required, and corrective action plan or 30-day follow-up report may be required.'
                ]
            }
        ];

        var races = [
            { id: 0, title: 'White' },
            { id: 1, title: 'Black/African American' },
            { id: 2, title: 'Hispanic/Latino' },
            { id: 3, title: 'Asian' },
            { id: 4, title: 'American Indian/Alaskan Native' },
            { id: 5, title: 'Native Hawaiian / Pacific Islander' },
            { id: 6, name: 'OTHER', title: 'Other' }
        ];

        var problems = [
            { id: 1, problemName: 'Hair loss', problemType: 'Problem', diagnosisCodeSet: 'ICD-8-CM', diagnosisCode: '101.24', status: 'Active', ageObservation: '64 years', healthStatusObservation: 'Alive and Well' },
            { id: 2, problemName: 'Post-traumatic stress disorder', problemType: 'Problem', diagnosisCodeSet: 'ICD-2-CM', diagnosisCode: '532', status: 'Active', ageObservation: '90 years', healthStatusObservation: 'On the threshold' },

            { id: 3, problemName: 'Hematoma', problemType: 'Problem', diagnosisCodeSet: 'ICD-10-CM', diagnosisCode: '234.24', status: 'Resolved', ageObservation: '55 years', healthStatusObservation: 'Alive and Well' },
            { id: 4, problemName: 'Hypoglycaemia', problemType: 'Problem', diagnosisCodeSet: 'ICD-2-CM', diagnosisCode: 'EG2.54', status: 'Resolved', ageObservation: '55 years', healthStatusObservation: 'Alive and Well' },
            { id: 5, problemName: 'Paranoid schizophrenia', problemType: 'Problem', diagnosisCodeSet: 'ICD-11-CM', diagnosisCode: 'E12.12', status: 'Resolved', ageObservation: '82 years', healthStatusObservation: 'Alive and Well' },
            { id: 6, problemName: 'Hepatomegaly', problemType: 'Problem', diagnosisCodeSet: 'ICD-8-CM', diagnosisCode: '835.72', status: 'Resolved', ageObservation: '76 years', healthStatusObservation: 'satisfactorily' },
            { id: 7, problemName: 'Migraine', problemType: 'Problem', diagnosisCodeSet: 'ICD-11-CM', diagnosisCode: 'E12.12', status: 'Resolved', ageObservation: '82 years', healthStatusObservation: 'satisfactorily' },
            { id: 8, problemName: 'Benign brain tumor', problemType: 'Problem', diagnosisCodeSet: 'ICD-10-CM', diagnosisCode: '246.72', status: 'Resolved', ageObservation: '68 years', healthStatusObservation: 'satisfactorily' },

            { id: 9, problemName: 'Atherosclerotic heart disease of native coronary artery', problemType: 'Problem', diagnosisCodeSet: 'ICD-10-CM', diagnosisCode: '125.1', status: 'Ruled Out', ageObservation: '78 years', healthStatusObservation: 'satisfactorily' },
            { id: 10, problemName: 'Major Depression', problemType: 'Problem', diagnosisCodeSet: 'ICD-9-CM', diagnosisCode: '296.23', status: 'Intermittent', ageObservation: '63 years', healthStatusObservation: 'Alive and Well' },
            { id: 11, problemName: 'Drug or chemical induced diabetes mellitus', problemType: 'Problem', diagnosisCodeSet: 'ICD-10-CM', diagnosisCode: 'E09.33', status: 'Recurrent', ageObservation: '59 years', healthStatusObservation: 'satisfactorily' },
            { id: 12, problemName: 'Urinary Tract Infection', problemType: 'Problem', diagnosisCodeSet: 'ICD-9-CM', diagnosisCode: '599', status: 'Rule Out', ageObservation: '76 years', healthStatusObservation: 'Alive and Well' }
        ];

        var medicationNames = [
            'ABC Plus TABS',
            'Afrin 12 Hour 0.05 % SOLN',
            'Dyclon HCl-Benzeth-SD Alc 40 LIQD',
            'Isomil DF LIQD',
            'Millex-FG Filter/Teflon',
            'Orencia 125 MG/ML SOSY',
            'Orencia 250 MG SOLR',
            'Selfgrip 2"x1.9yd',
            'Yummy-C 100 MG CHEW'
        ];

        return {
            findStates: function () {
                return getSuccessResponse(usStates, { totalCount: 48 });
            },
            findGenders: function () {
                return getSuccessResponse([
                    { id: 3, name: 'MALE', title: 'Male' },
                    { id: 2, name: 'FEMALE', title: 'Female' }
                ], { totalCount: 2 });
            },
            findRaces: function () {
                return getSuccessResponse(races, { totalCount: 6 });
            },
            findDiagnoses: function () {
                return getSuccessResponse(problems, { totalCount: 12 });
            },
            findMedications: function () {
                return getSuccessResponse(_.map(medicationNames, function (o, i) {
                    return {id: i, medicationName: o};
                }), { totalCount: 9 });
            },
            findIncidentPlaces: function () {
                return getSuccessResponse(incidentPlaces, { totalCount: 6 })
            },
            findIncidents: function (params) {
                var level = params && params.level;

                return getSuccessResponse(
                    level ? _.where(incidents, {level: level}) : incidents, {totalCount: 6}
                )
            },
            findIncidentLevelReportingSettings: function () {
                return getSuccessResponse(incidentLevelReportingSettings, { totalCount: 3 });
            },
            saveIncidentReportDraft: function (report) {
                return getSuccessResponse(_.extend(report, {id: MU.getRandomInt(0, 99999999)}), {success: true})
            },
            saveIncidentReport: function (report) {
                return getSuccessResponse(_.extend(report, {id: MU.getRandomInt(0, 99999999)}), {success: true})
            }
        };
    }
);