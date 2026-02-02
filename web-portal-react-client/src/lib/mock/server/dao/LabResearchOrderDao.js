class LabResearchOrderDao {
    validateUniqInOrganization() {
        return {
            requisitionNumber: true
        }
    }
}

export default new LabResearchOrderDao()