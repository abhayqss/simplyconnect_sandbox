package com.scnsoft.eldermark;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * TODO extract this component to a separate application
 * @author phomal
 * Created on 6/21/2017.
 */
@Profile("!h2")
@Component
public class ImportCcdServiceLauncher {

    @Autowired
    private ImportCcdService importCcdMain;

    // If you want to import some CCDs for existing residents
    //
    // 1. Set resident IDs here
    // 2. Set organization and community in ImportCcdService
    // 3. Add new CCDs or modify existing
    // 4. Uncomment @PostConstruct below
    // 5. Deploy application to the target web container
    //      (do not commit and push to master! it causes CI to redeploy all applications and results in repeated import)
    // 6. Import starts automatically on deployment - wait for it to complete
    // 7. Comment @PostConstruct and deploy again (or undeploy) - to make sure that import doesn't take place again

    //@PostConstruct
    public void start() {
        try {
            importCcdMain.parseFile(4975L, "ccd/Arya_Stark.xml");
            importCcdMain.parseFile(4976L, "ccd/Daenerys_Targaryen.xml");
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

}
