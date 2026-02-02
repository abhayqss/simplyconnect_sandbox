package com.scnsoft.eldermark.service.transformer;

import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.Note;
import com.scnsoft.eldermark.entity.phr.User;
import com.scnsoft.eldermark.service.BasePhrService;
import com.scnsoft.eldermark.service.transformer.populator.Populator;
import com.scnsoft.eldermark.shared.carecoordination.EmployeeDto;
import com.scnsoft.eldermark.web.entity.notes.NoteEmployeeDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class NoteEmployeeDtoTransformer extends BasePhrService implements Converter<Note, NoteEmployeeDto> {

    @Autowired
    private Populator<Employee, EmployeeDto> employeeDtoPopulator;

    @Override
    public NoteEmployeeDto convert(Note note) {
        if (note == null) {
            return null;
        }
        final NoteEmployeeDto noteEmployeeDto = new NoteEmployeeDto();

        final User user = getUser(note.getEmployee());
        if (user != null) {
            noteEmployeeDto.setId(user.getId());
        }

        employeeDtoPopulator.populate(note.getEmployee(), noteEmployeeDto);
        return noteEmployeeDto;
    }
}
