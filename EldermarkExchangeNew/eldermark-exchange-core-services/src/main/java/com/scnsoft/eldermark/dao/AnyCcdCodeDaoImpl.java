package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.InterpretiveCcdCode;
import com.scnsoft.eldermark.entity.document.AnyCcdCode;
import com.scnsoft.eldermark.entity.document.UnknownCcdCode;
import com.scnsoft.eldermark.entity.document.ccd.ConcreteCcdCode;
import com.scnsoft.eldermark.entity.document.ccd.DiagnosisCcdCode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Repository
public class AnyCcdCodeDaoImpl implements AnyCcdCodeDao {

    @Autowired
    private ConcreteCcdCodeDao concreteCcdCodeDao;

    @Autowired
    private DiagnosisCcdCodeDao diagnosisCcdCodeDao;

    @Autowired
    private UnknownCcdCodeDao unknownCcdCodeDao;

    @Autowired
    private InterpretiveCcdCodeDao interpretiveCcdCodeDao;

    @Override
    public UnknownCcdCode create(UnknownCcdCode ccdCode) {
        return unknownCcdCodeDao.save(ccdCode);
    }

    @Override
    public InterpretiveCcdCode create(InterpretiveCcdCode ccdCode) {
        return interpretiveCcdCodeDao.save(ccdCode);
    }

    @Override
    public AnyCcdCode getCcdCode(String code, String codeSystem, String displayName) {
        return getCcdCode(code, codeSystem, displayName, null);
    }

    @Override
    public AnyCcdCode getCcdCode(String code, String codeSystem, String displayName, String valueSet) {
        return findAmongConcreteOrInterpretiveCcdCodes(code, codeSystem, displayName, valueSet)
                .or(() -> findAmongDiagnosisCcdCodes(code, codeSystem, displayName))
                .or(() -> findAmongUnknownCodes(code, codeSystem, displayName))
                .orElse(null);
    }

    private Optional<AnyCcdCode> findAmongConcreteOrInterpretiveCcdCodes(String code,
                                                                         String codeSystem,
                                                                         String displayName,
                                                                         String valueSet) {
        final ConcreteCcdCode concreteCcdCode = concreteCcdCodeDao.getCcdCode(code, codeSystem, valueSet);
        if (concreteCcdCode != null) {
            if (displayName != null && !displayName.equals(concreteCcdCode.getDisplayName())) {
                InterpretiveCcdCode iCcdCode = interpretiveCcdCodeDao.getCcdCode(concreteCcdCode, displayName);
                if (iCcdCode != null) {
                    return Optional.of(iCcdCode);
                }
            }

            return Optional.of(concreteCcdCode);
        }

        return Optional.empty();
    }

    private Optional<? extends AnyCcdCode> findAmongDiagnosisCcdCodes(String code,
                                                                      String codeSystem,
                                                                      String displayName) {
        var diagnosisCodes = diagnosisCcdCodeDao.findAllByCodeAndCodeSystem(code, codeSystem);
        var diagnosisCodesWithoutSetup = diagnosisCodes.stream()
                .filter(c -> c.getDiagnosisSetupId() == null)
                .collect(Collectors.toList());

        return diagnosisCodeWithBestMatchingDisplayName(diagnosisCodesWithoutSetup, displayName)
                .or(() -> diagnosisCodeWithBestMatchingDisplayName(diagnosisCodes, displayName));

    }

    private Optional<DiagnosisCcdCode> diagnosisCodeWithBestMatchingDisplayName(Collection<DiagnosisCcdCode> codes,
                                                                                String displayName) {

        Supplier<Optional<DiagnosisCcdCode>> withPresentName = () -> codes.stream()
                .filter(diagnosisCcdCode -> StringUtils.isNotEmpty(diagnosisCcdCode.getDisplayName()))
                .findFirst();

        Optional<DiagnosisCcdCode> withNameMatch;

        if (StringUtils.isEmpty(displayName)) {
            withNameMatch = withPresentName.get();
        } else {
            withNameMatch = codes.stream()
                    .filter(diagnosisCcdCode -> displayName.equals(diagnosisCcdCode.getDisplayName()))
                    .findFirst()
                    .or(withPresentName);
        }
        return withNameMatch.or(() -> codes.stream().findFirst());
    }


    private Optional<? extends AnyCcdCode> findAmongUnknownCodes(String code, String codeSystem, String displayName) {
        return unknownCcdCodeDao.getCcdCodes(code, codeSystem).stream()
                .filter(unknownCcdCode -> StringUtils.equals(unknownCcdCode.getDisplayName(), displayName))
                .findFirst();
    }

}
