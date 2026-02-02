import { date, Shape } from './types'

const BEFORE_INQUIRY_DATE_ERROR_TEXT = 'The date you entered occurs before the inquiry date'

const ContactToIndividualFormScheme = Shape({
    contactedDate: date().nullable().when(['$included'], (included, scheme, { value }) => (
        !value ? 
            scheme.optional()
            : scheme.test('before-inquiry-date', BEFORE_INQUIRY_DATE_ERROR_TEXT, value => value > included.inquiryDate)
    ))
})

export default ContactToIndividualFormScheme