import { Component, OnInit, OnDestroy } from '@angular/core';
import { PhotoComponent } from 'app/entities/photo/photo.component';
import { PhotoService } from 'app/entities/photo/photo.service';

import { Principal } from 'app/core';
import { JhiParseLinks } from 'ng-jhipster/src/service/parse-links.service';
import { JhiEventManager } from 'ng-jhipster/src/service/event-manager.service';
import { JhiDataUtils } from 'ng-jhipster/src/service/data-util.service';
import { JhiAlertService } from 'ng-jhipster/src/service/alert.service';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { Photo } from 'app/shared/model/photo.model';

@Component({
    selector: 'jhi-photo-extended',
    templateUrl: './photo-extended.component.html',
    styleUrls: ['./photo-extended.component.css']
})
export class PhotoExtendedComponent extends PhotoComponent {
    selectedPhoto: Photo;
    constructor(
        private photoServiceExtended: PhotoService,
        private jhiAlertServiceExtended: JhiAlertService,
        private dataUtilsExtended: JhiDataUtils,
        private eventManagerExtended: JhiEventManager,
        private parseLinksExtended: JhiParseLinks,
        private principalExtended: Principal,
        private modalServiceExtended: NgbModal
    ) {
        super(
            photoServiceExtended,
            jhiAlertServiceExtended,
            dataUtilsExtended,
            eventManagerExtended,
            parseLinksExtended,
            principalExtended,
            modalServiceExtended
        );
        this.itemsPerPage = 8;
    }
    openModal(photo, target) {
        this.selectedPhoto = photo;
        this.modalServiceExtended.open(target, {
            size: 'lg',
            windowClass: 'full-width-modal'
        });
    }
}
