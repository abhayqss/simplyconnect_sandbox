package com.scnsoft.eldermark.services.cda.templates.header;

import com.scnsoft.eldermark.services.cda.templates.MultiHeaderFactory;
import com.scnsoft.eldermark.services.cda.templates.ParsableMultiHeader;
import org.eclipse.mdht.uml.cda.Author;

public interface AuthorFactory extends
        MultiHeaderFactory<Author, com.scnsoft.eldermark.entity.Author>,
        ParsableMultiHeader<Author, com.scnsoft.eldermark.entity.Author> {
}
