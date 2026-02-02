package com.scnsoft.eldermark.dump.facade;

import com.scnsoft.eldermark.dump.bean.DumpFilter;
import com.scnsoft.eldermark.dump.model.DumpType;
import com.scnsoft.eldermark.dump.service.generator.DumpGenerator;
import com.scnsoft.eldermark.dump.service.DumpFilterFactory;
import com.scnsoft.eldermark.dump.service.writer.DumpWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class DumpFacadeImpl implements DumpFacade {

    private static final Logger logger = LoggerFactory.getLogger(DumpFacadeImpl.class);

    private final Map<DumpType, DumpGenerator> dumpGenerators;
    private final Map<DumpType, DumpWriter> dumpWriters;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DumpFacadeImpl(List<DumpGenerator> dumpGenerators, List<DumpWriter> dumpWriters, JdbcTemplate jdbcTemplate) {
        this.dumpGenerators = dumpGenerators.stream().collect(Collectors.toMap(DumpGenerator::getDumpType, Function.identity()));
        this.dumpWriters = dumpWriters.stream().collect(Collectors.toMap(DumpWriter::getDumpType, Function.identity()));
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void generateDump(DumpFilter dumpFilter) {
        jdbcTemplate.execute("OPEN SYMMETRIC KEY SymmetricKey1 DECRYPTION BY CERTIFICATE Certificate1");

        logger.info("Generating models for dump {}", dumpFilter);
        var dumps = dumpGenerators.get(dumpFilter.getDumpType()).generateDump(dumpFilter);

        logger.info("Writing dumps to file {}", dumpFilter);
        var writer = dumpWriters.get(dumpFilter.getDumpType());

        dumps.forEach(writer::writeDump);
    }
}
