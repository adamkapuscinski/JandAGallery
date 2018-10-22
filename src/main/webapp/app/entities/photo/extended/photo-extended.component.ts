import { Component, OnInit, OnDestroy } from '@angular/core';
import { PhotoComponent } from 'app/entities/photo/photo.component';
import { PhotoService } from 'app/entities/photo/photo.service';

import { Principal } from 'app/core';
import { JhiParseLinks } from 'ng-jhipster/src/service/parse-links.service';
import { JhiEventManager } from 'ng-jhipster/src/service/event-manager.service';
import { JhiDataUtils } from 'ng-jhipster/src/service/data-util.service';
import { JhiAlertService } from 'ng-jhipster/src/service/alert.service';

@Component({
    selector: 'jhi-photo-extended',
    templateUrl: './photo-extended.component.html'
})
export class PhotoExtendedComponent extends PhotoComponent {
    constructor(
        private photoServiceExtended: PhotoService,
        private jhiAlertServiceExtended: JhiAlertService,
        private dataUtilsExtended: JhiDataUtils,
        private eventManagerExtended: JhiEventManager,
        private parseLinksExtended: JhiParseLinks,
        private principalExtended: Principal
    ) {
        super(
            photoServiceExtended,
            jhiAlertServiceExtended,
            dataUtilsExtended,
            eventManagerExtended,
            parseLinksExtended,
            principalExtended
        );
    }
}
